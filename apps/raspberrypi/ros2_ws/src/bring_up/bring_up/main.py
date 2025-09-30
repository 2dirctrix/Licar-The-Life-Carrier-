import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32  # <- Int32 메시지 import
import time

class IntPublisher(Node):
    def __init__(self):
        super().__init__('int_publisher')
        self.publisher_ = self.create_publisher(Int32, '/turtlebot_command', 10)

        # 메시지 생성 및 데이터 설정
        msg = Int32()
        msg.data = 2  # 전송할 정수 값

        # 메시지 발행
        self.publisher_.publish(msg)
        self.get_logger().info(f'Publishing Int32: {msg.data}')

        # 전송 안정화 시간 확보
        time.sleep(0.5)
        rclpy.shutdown()


def main(args=None):
    rclpy.init(args=args)
    IntPublisher()


if __name__ == '__main__':
    main()
