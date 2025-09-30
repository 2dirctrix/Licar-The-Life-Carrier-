#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32, Bool

class TurtlebotMain(Node):
    def __init__(self):
        super().__init__('turtlebot_main_node')

        # === 구독자 (Subscribers) ===
        self.create_subscription(Int32, '/turtlebot_target', self.target_callback, 10)
        self.create_subscription(Bool, '/turtlebot/move_complete', self.move_complete_callback, 10)

        # === 퍼블리셔 (Publishers) ===
        self.mover_pub = self.create_publisher(Int32, '/turtlebot_command', 10)

        # === 상태 변수 (State Variables) ===
        self.route = []
        self.current_step_index = 0
        self.state = "IDLE"  # IDLE, MOVING, WAITING

        self.wait_time = 3.0         # 대기 시간(초)
        self.wait_timer = None       # 타이머 객체 저장용

        self.get_logger().info("✅ Main node ready, waiting for target...")

    # === 목적지 신호 수신 ===
    def target_callback(self, msg: Int32):
        dest = msg.data
        self.get_logger().info(f"🎯 목적지 요청 수신: {dest}")

        # 허용된 목적지만 처리
        if dest not in [1000, 301]:
            self.get_logger().warn(f"등록되지 않은 목적지: {dest}")
            return

        # === 현재 작업 무시하고 새 목적지로 강제 전환 ===
        if self.wait_timer:
            self.wait_timer.cancel()
            self.wait_timer = None

        self.get_logger().info(f"⚡ 새로운 목적지 [{dest}] 요청 → 현재 작업 중단 후 재시작")
        self.state = "IDLE"
        self.route = []
        self.current_step_index = 0

        # 새 경로 세팅 및 실행
        self.setup_route(dest)
        self.process_next_step()

    # === 경로 세팅 ===
    def setup_route(self, dest):
        self.get_logger().info(f"🗺️ 목적지 [{dest}] 경로 설정 시작")

        if dest == 1:
            self.route = [11, 12, 1]
        elif dest == 301:
            self.route = [11, 12, 301]

        self.current_step_index = 0

    # === 이동 완료 콜백 ===
    def move_complete_callback(self, msg: Bool):
        if not msg.data or self.state != "MOVING":
            return

        step_id = self.route[self.current_step_index - 1]
        self.get_logger().info(f"✅ Step {step_id} 이동 완료!")

        # 11번 / 12번 도착 시 3초 대기
        if step_id in [11, 12]:
            self.get_logger().info(f"⏳ 대기 지점({step_id}) 도착. {self.wait_time}초 대기...")
            self.state = "WAITING"
            self.wait_timer = self.create_timer(self.wait_time, self.timer_callback)
        else:
            self.process_next_step()

    # === 대기 타이머 콜백 ===
    def timer_callback(self):
        if self.wait_timer:
            self.wait_timer.cancel()
            self.wait_timer = None

        if self.state == "WAITING":
            self.get_logger().info("⏰ 대기 완료! 다음 스텝으로 진행합니다.")
            self.process_next_step()

    # === 다음 스텝 실행 ===
    def process_next_step(self):
        if self.current_step_index >= len(self.route):
            self.get_logger().info("🎉 최종 목적지 도착 완료!")
            self.state = "IDLE"
            self.route = []
            return

        step_id = self.route[self.current_step_index]
        self.get_logger().info(f"🚀 Step {step_id} 이동 시작...")

        self.state = "MOVING"
        msg = Int32()
        msg.data = step_id
        self.mover_pub.publish(msg)

        self.current_step_index += 1


def main(args=None):
    rclpy.init(args=args)
    node = TurtlebotMain()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()


if __name__ == "__main__":
    main()
