#!/home/ssafy/juno/.venv/bin/python

import rclpy
from rclpy.node import Node
from std_msgs.msg import String, Bool, Int32
from mfrc522 import SimpleMFRC522
import RPi.GPIO as GPIO
import time
import threading
import requests

CARD_ID_TO_USE = "673075365188"
API_BASE_URL = "http://192.168.100.157:8080/api/v1/transports"

class HardwareControllerNode(Node):
    def __init__(self):
        super().__init__('hardware_controller_node')
        
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)

        # NFC 기능 (Publisher만)
        self.tag_publisher_ = self.create_publisher(String, 'nfc_tag', 10)
        self.reader = None
        self.nfc_thread = threading.Thread(target=self.nfc_reading_loop, daemon=True)
        self.nfc_thread.start()

        # 서보 & LED 기능 (Subscriber와 Publisher)
        self.open_subscription = self.create_subscription(Bool, 'open_door', self.open_door_callback, 10)
        self.complete_subscription = self.create_subscription(Bool, 'validation_complete_signal', self.validation_complete_callback, 10)
        self.close_subscription = self.create_subscription(Int32, 'close_door', self.close_door_callback, 10)
        self.delivery_status_publisher_ = self.create_publisher(Int32, 'delivery_status', 10)
            
        # 하드웨어 초기화
        self.servo1_pin = 13
        self.servo2_pin = 19
        GPIO.setup(self.servo1_pin, GPIO.OUT)
        GPIO.setup(self.servo2_pin, GPIO.OUT)
        self.pwm1 = GPIO.PWM(self.servo1_pin, 50)
        self.pwm2 = GPIO.PWM(self.servo2_pin, 50)
        self.pwm1.start(0)
        self.pwm2.start(0)
        self.set_angles(90, 90)

        self.LED_GREEN_PIN = 20
        GPIO.setup(self.LED_GREEN_PIN, GPIO.OUT)
        GPIO.output(self.LED_GREEN_PIN, GPIO.LOW)
        
        self.get_logger().info('Hardware Controller Node (NFC + Servo) has been started.')

    def nfc_reading_loop(self):
        time.sleep(1.0)
        self.reader = SimpleMFRC522()
        self.get_logger().info('NFC reader initialized inside the thread.')
        
        while rclpy.ok():
            self.get_logger().info('Ready to read a card.')
            try:
                id = self.reader.read_id()
                if id and str(id) == CARD_ID_TO_USE:
                    self.get_logger().info(f'Valid card tagged: {id}. Publishing to /nfc_tag.')
                    msg = String()
                    msg.data = str(id)
                    self.tag_publisher_.publish(msg)
                    time.sleep(2.0)
            except Exception:
                time.sleep(0.5)

    def set_angles(self, angle1, angle2):
        duty1 = 2 + (angle1 / 18)
        duty2 = 2 + (angle2 / 18)
        self.pwm1.ChangeDutyCycle(duty1)
        self.pwm2.ChangeDutyCycle(duty2)
        time.sleep(1.5)
        self.pwm1.ChangeDutyCycle(0)
        self.pwm2.ChangeDutyCycle(0)

    def open_door_callback(self, msg):
        if msg.data:
            self.get_logger().info('Open command received. Opening the door.')
            GPIO.output(self.LED_GREEN_PIN, GPIO.LOW)
            self.set_angles(0, 180)

    def validation_complete_callback(self, msg):
        if msg.data:
            self.get_logger().info('Validation complete signal received. Turning on green light.')
            GPIO.output(self.LED_GREEN_PIN, GPIO.HIGH)

    def close_door_callback(self, msg):
        transport_id = msg.data
        if transport_id > 0:
            self.get_logger().info(f'Close command received with Transport ID: {transport_id}. Closing the door.')
            GPIO.output(self.LED_GREEN_PIN, GPIO.LOW)
            self.set_angles(90, 90)
            try:
                api_url = f"{API_BASE_URL}/{transport_id}/start"
                self.get_logger().info(f"Sending PATCH request to: {api_url}")
                headers = {'Content-Type': 'application/json'}
                response = requests.patch(api_url, headers=headers, timeout=5)

                if 200 <= response.status_code < 300:
                    self.get_logger().info("Successfully sent start signal to BE server.")
                    status_code_msg = Int32()
                    status_code_msg.data = 301 
                    self.delivery_status_publisher_.publish(status_code_msg)
                    self.get_logger().info(f'Published delivery status code: {status_code_msg.data}')
                else:
                    self.get_logger().error(f"Failed to send start signal. Status Code: {response.status_code}")
            except requests.exceptions.RequestException as e:
                self.get_logger().error(f"PATCH request failed: {e}")

    def destroy_node(self):
        self.pwm1.stop()
        self.pwm2.stop()
        GPIO.cleanup()
        super().destroy_node()

def main(args=None):
    rclpy.init(args=args)
    hardware_node = HardwareControllerNode()
    try:
        rclpy.spin(hardware_node)
    except KeyboardInterrupt: pass
    finally:
        hardware_node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    main()