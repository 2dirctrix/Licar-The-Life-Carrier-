#!/home/ssafy/juno/.venv/bin/python

import rclpy
from rclpy.node import Node
from std_msgs.msg import String, Bool, Int32
from ultralytics import YOLO
import paho.mqtt.client as mqtt
import cv2
import json
import torch
import os
import threading

class SystemState:
    IDLE = 0
    WAITING_FOR_DRUGS = 1 # MQTT 응답 대기 상태
    VALIDATING = 2
    VALIDATION_DONE = 3
    SIMULATOR_RUNNING = 4
    MANUAL_OPEN = 5
    MANUAL_OPEN_DONE = 6

# MQTT 설정
MQTT_WS_URL = "j13c206.p.ssafy.io"
MQTT_WS_PORT = 9001
MQTT_WS_PATH = "/mqtt"
MQTT_TOPIC_REQUEST_DRUGS = "robots/status/1"
MQTT_TOPIC_RESPONSE_DRUGS = "robots/response/1"
MQTT_TOPIC_RECOGNITION_UPDATE = "transports/recognition/update"
MQTT_USERNAME = "robot"
MQTT_PASSWORD = "robot1004"

class YoloValidatorNode(Node):
    def __init__(self):
        super().__init__('yolo_validator_node')
        
        self.nfc_tag_subscription = self.create_subscription(String, 'nfc_tag', self.nfc_tag_callback, 10)

        self.simulator_status_subscription = self.create_subscription(Bool, 'simulator_status', self.simulator_status_callback, 10)
        
        self.open_publisher = self.create_publisher(Bool, 'open_door', 10)
        self.complete_publisher = self.create_publisher(Bool, 'validation_complete_signal', 10)
        self.close_publisher = self.create_publisher(Int32, 'close_door', 10)
        
        # 상태 변수와 lock 이름을 통일하여 관리 (언더스코어 사용)
        self._current_state = SystemState.IDLE
        self._lock = threading.Lock()
        
        self.transport_id = None
        self.drug_id_map = {}
        self.target_pills = {}

        # MQTT 클라이언트 설정
        self.mqtt_client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, transport="websockets")
        self.mqtt_client.ws_set_options(path=MQTT_WS_PATH)
        self.mqtt_client.on_connect = self.on_mqtt_connect
        self.mqtt_client.on_message = self.on_mqtt_message
        self.mqtt_client.username_pw_set(MQTT_USERNAME, MQTT_PASSWORD)

        try:
            self.get_logger().info(f"Connecting to MQTT Broker at wss://{MQTT_WS_URL}:{MQTT_WS_PORT}{MQTT_WS_PATH}...")
            self.mqtt_client.connect(MQTT_WS_URL, MQTT_WS_PORT, 60)
            self.mqtt_client.loop_start()
        except Exception as e:
            self.get_logger().error(f"Failed to connect to MQTT Broker: {e}")

        try:
            self.device = 'cpu'
            model_path = '/home/ssafy/Desktop/best.pt'
            self.model = YOLO(model_path)
            self.model.to(self.device)
            self.class_names = self.model.names
            self.get_logger().info(f"Model loaded successfully from {model_path}")
        except Exception as e:
            self.get_logger().error(f"FATAL: Failed to load YOLO model: {e}")
            return
            
        self.cap = None
        try:
            self.cap = cv2.VideoCapture(0)
            if not self.cap.isOpened(): self.cap = None
        except Exception: self.cap = None
        
        self.timer = self.create_timer(0.1, self.process_frame)
        self.get_logger().info('YOLO Validator Node has been started. Current state: IDLE')

    def simulator_status_callback(self, msg):
        """시뮬레이터로부터 현재 운행 상태를 수신합니다."""
        with self.lock:
            if msg.data: # True = 시뮬레이터가 작동 시작
                self.get_logger().info("Simulator has started. Locking NFC for validation.")
                self.current_state = SystemState.SIMULATOR_RUNNING
            else: # False = 시뮬레이터가 작동 중지
                self.get_logger().info("Simulator has stopped. Enabling manual open mode.")
                self.current_state = SystemState.MANUAL_OPEN


    # 상태를 안전하게 읽고 쓰는 함수
    def get_state(self):
        with self._lock:
            return self._current_state

    def set_state(self, new_state):
        with self._lock:
            if self._current_state != new_state:
                self.get_logger().info(f"State changed from {self._current_state} to {new_state}")
                self._current_state = new_state

    def on_mqtt_connect(self, client, userdata, flags, rc, properties):
        if rc == 0:
            self.get_logger().info("Connected successfully to MQTT Broker.")
            client.subscribe(MQTT_TOPIC_RESPONSE_DRUGS)
            self.get_logger().info(f"Subscribed to MQTT topic: {MQTT_TOPIC_RESPONSE_DRUGS}")
        else:
            self.get_logger().error(f"Failed to connect to MQTT Broker, return code {rc}")

    def on_mqtt_message(self, client, userdata, msg):
        if self.get_state() == SystemState.WAITING_FOR_DRUGS:
            try:
                payload_str = msg.payload.decode('utf-8')
                self.get_logger().info(f"Received message from MQTT topic '{msg.topic}': {payload_str}")
                data = json.loads(payload_str)
                
                # with self._lock 안에서 데이터 처리 및 상태 변경
                with self._lock:
                    self.transport_id = data.get('transportId')
                    drugs_list = data.get('drugs', [])
                    
                    if not self.transport_id or not drugs_list:
                        self.get_logger().error("Invalid data format from server.")
                        self._current_state = SystemState.IDLE
                        return

                    drug_names = []
                    for drug in drugs_list:
                        drug_name, drug_id = drug.get('name'), drug.get('drugId')
                        if drug_name and drug_id:
                            drug_names.append(drug_name)
                            self.drug_id_map[drug_name] = drug_id
                    
                    self.target_pills = {name: False for name in drug_names}
                    self._current_state = SystemState.VALIDATING
                    self.get_logger().info(f"Validation started. Transport ID: {self.transport_id}")
                
                open_msg = Bool(); open_msg.data = True
                self.open_publisher.publish(open_msg)
            except Exception as e:
                self.get_logger().error(f"Error processing MQTT message: {e}")
                self.set_state(SystemState.IDLE)

    def nfc_tag_callback(self, msg):
        state = self.get_state()

        if self.get_state() == SystemState.IDLE:
            if self.cap is None:
                self.get_logger().error("Camera is not available, cannot start validation.")
                return
            self.get_logger().info("First NFC tag received. Requesting drug list via MQTT...")
            self.mqtt_client.publish(MQTT_TOPIC_REQUEST_DRUGS, payload="get_list")
            self.set_state(SystemState.WAITING_FOR_DRUGS)

        elif self.get_state() == SystemState.VALIDATION_DONE:
            self.get_logger().info("Second NFC tag received. Commanding door to close.")

            if self.transport_id is not None:
                close_msg = Int32()
                close_msg.data = self.transport_id
                self.close_publisher.publish(close_msg)
                self.get_logger().info(f"Published transport_id {self.transport_id} to /close_door topic.")
                self.set_state(SystemState.SIMULATOR_RUNNING) # 이제 시뮬레이터가 달릴 차례
                self.get_logger().info("Delivery started. Locking NFC.")
            else:
                self.get_logger().error("transport_id is not available to send. Cannot close door.")
                # 문제가 생겼으므로 IDLE 상태로 리셋하여 다시 시도할 수 있게 함
                self.set_state(SystemState.IDLE)

            
            # with self._lock:
            #     # 1번 상태로 돌아가는 코드
            #     # self._current_state = SystemState.IDLE
            #     # self.transport_id = None
            #     # self.drug_id_map = {}
            #     # self.target_pills = {}

        elif state == SystemState.SIMULATOR_RUNNING:
            # 3번 플래그: 시뮬레이터 작동 중 -> NFC 무시
            self.get_logger().info("NFC tag ignored: Simulator is currently running.")
            return

        elif state == SystemState.MANUAL_OPEN:
            # 4번 플래그: 시뮬레이터 정지 후 태그 -> 문만 열기
            self.get_logger().info("Manual open tag received. Opening the door without validation.")
            open_msg = Bool(); open_msg.data = True
            self.open_publisher.publish(open_msg)
            # 5번 플래그 상태로 전환
            self.current_state = SystemState.MANUAL_OPEN_DONE

        elif state == SystemState.MANUAL_OPEN_DONE:
            # 5번 플래그: 수동으로 문이 열린 상태에서 태그 -> 문 닫고 초기화
            self.get_logger().info("Manual close tag received. Closing the door and resetting.")
            close_msg = Bool(); close_msg.data = True
            self.close_publisher.publish(close_msg)
            # 모든 상태를 초기화
            with self.lock:
                self.current_state = SystemState.IDLE
                self.transport_id = None
                self.drug_id_map = {}
                self.target_pills = {}

                
    def process_frame(self):
        current_state = self.get_state()
        if current_state != SystemState.VALIDATING or not self.cap:
            return
        
        ret, frame = self.cap.read()
        if not ret: return
        
        results_generator = self.model(frame, stream=True, verbose=False, conf=0.25, device=self.device, imgsz=192)
        
        newly_found_pills = []
        for results in results_generator:
            for box in results.boxes:
                class_id = int(box.cls[0])
                pill_name = self.class_names.get(class_id, "Unknown")
                
                if pill_name in self.target_pills and not self.target_pills.get(pill_name):
                    newly_found_pills.append(pill_name)

        if newly_found_pills:
            with self._lock:
                for pill_name in newly_found_pills:
                    if self.target_pills.get(pill_name) == False:
                        self.target_pills[pill_name] = True
                        self.get_logger().info(f"Found Target: {pill_name}! Status: {self.target_pills}")
                        
                        drug_id_to_send = self.drug_id_map.get(pill_name)
                        if self.transport_id is not None and drug_id_to_send is not None:
                            payload = {"transportId": self.transport_id, "drugId": drug_id_to_send}
                            self.mqtt_client.publish(MQTT_TOPIC_RECOGNITION_UPDATE, json.dumps(payload))
                            self.get_logger().info(f"Published recognition update: {payload}")

        all_found = False
        # lock 범위 최소화
        with self._lock:
            if self.target_pills and all(self.target_pills.values()):
                all_found = True
        
        if all_found:
            self.get_logger().info('SUCCESS: All target drugs have been detected! Waiting for final tag.')
            self.set_state(SystemState.VALIDATION_DONE)
            
            complete_msg = Bool(); complete_msg.data = True
            self.complete_publisher.publish(complete_msg)

    def destroy_node(self):
        self.mqtt_client.loop_stop()
        if self.cap: self.cap.release()
        super().destroy_node()

def main(args=None):
    rclpy.init(args=args)
    yolo_validator_node = YoloValidatorNode()
    try: 
        rclpy.spin(yolo_validator_node)
    except KeyboardInterrupt: pass
    finally:
        yolo_validator_node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    main()