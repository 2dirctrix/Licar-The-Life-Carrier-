#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32, Bool, String

class TurtlebotMain(Node):
    def __init__(self):
        super().__init__('turtlebot_main_node')

        # 목적지 신호를 수신하는 구독자
        self.create_subscription(Int32, '/turtlebot_target', self.target_callback, 10)
        # 엘리베이터 문 상태를 수신하는 구독자
        self.create_subscription(Bool, '/elevator/door_state', self.door_callback, 10)

        # 목적지 신호를 mover로 보내는 퍼블리셔
        self.mover_pub = self.create_publisher(Int32, '/turtlebot_command', 10)
        # 도착 신호 퍼블리셔
        # self.elevator_pub = self.create_publisher(String, '/gazebo/world/elevator', 10)
        # self.arrival_pub = self.create_publisher(Bool, '/turtlebot/arrived', 10)

        # 상태 변수
        self.door_triggered = False
        self.route = []
        self.current_step = 0
        self.pending_target = None

        self.get_logger().info("✅ Main node ready, waiting for target...")

    def target_callback(self, msg: Int32):
        dest = msg.data
        self.get_logger().info(f"🎯 목적지 요청: {dest}")

        if dest not in [1, 301, 302]:  # 1번, 301번, 302번만 처리
            self.get_logger().warn(f"등록되지 않은 목적지: {dest}")
            return

        # 목적지가 이미 이동 중이면 대기
        if self.route:
            self.get_logger().info(f"이동 중 → 요청 저장: {dest}")
            self.pending_target = dest
            return

        # 1번, 301번, 302번은 모두 엘리베이터를 경유해야 함
        self.setup_route(dest)

    def door_callback(self, msg: Bool):
        if msg.data and not self.door_triggered:  # 문 열림 신호
            self.door_triggered = True
            self.get_logger().info("🚪 문 열림 신호 수신")
            self.process_next_step()

        # else:  # 문 닫힘
        #     if self.door_open:  # 직전에 열렸었음 → 이제 닫힘 감지
        #         self.door_open = False
        #         self.get_logger().info("🚪 문 닫힘 감지 → 엘리베이터 3층 이동 명령 전송")
        #         self.elevator_pub.publish(String(data="2"))

    def setup_route(self, dest):
        # 301, 302번 병실도 엘리베이터 경유
        if dest == 1:  # 의국은 엘리베이터를 거쳐야 한다.
            self.route = [11, 12, 1]
        elif dest == 301:  # 301호도 엘리베이터를 거쳐야 한다.
            self.route = [11, 12, 301]
        elif dest == 302:  # 302호도 엘리베이터를 거쳐야 한다.
            self.route = [11, 12, 302]

        self.current_step = 0
        self.door_triggered = False
        self.process_next_step()

    def process_next_step(self):
        if self.current_step >= len(self.route):
            self.get_logger().info("✅ 목적지 도착 완료!")
            self.arrival_pub.publish(Bool(data=True))
            self.route = []
            self.current_step = 0
            self.door_triggered = False

            if self.pending_target:
                target = self.pending_target
                self.pending_target = None
                self.setup_route(target)

            return

        # 목적지 이동 신호를 퍼블리시
        step_id = self.route[self.current_step]
        self.get_logger().info(f"🚀 Step {step_id} 으로 이동 시작")
        self.mover_pub.publish(Int32(data=step_id))
        self.current_step += 1

def main(args=None):
    rclpy.init(args=args)
    node = TurtlebotMain()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()

if __name__ == "__main__":
    main()
