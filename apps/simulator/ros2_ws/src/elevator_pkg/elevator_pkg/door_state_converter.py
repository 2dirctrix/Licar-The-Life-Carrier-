#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from sensor_msgs.msg import JointState
from std_msgs.msg import Bool

class DoorStateConverterNode(Node):
    def __init__(self):
        super().__init__('door_state_converter_node')
        self.subscription = self.create_subscription(
            JointState,
            '/elevator/joint_states',
            self.listener_callback,
            10
        )
        self.publisher = self.create_publisher(Bool, '/elevator/door_state', 10)
        self.last_state = None  # 이전 상태 기억 (True/False)

    def listener_callback(self, msg):
        if "elevator::door" in msg.name:
            idx = msg.name.index("elevator::door")
            pos = msg.position[idx]

            # 기준치 설정
            if pos < 0.1:
                state = False  # 닫힘
            elif pos > 0.7:
                state = True   # 열림
            else:
                return  # 움직이는 중이면 무시

            # 상태 변화가 있을 때만 publish
            if state != self.last_state:
                self.last_state = state
                msg_out = Bool()
                msg_out.data = state
                self.publisher.publish(msg_out)
                self.get_logger().info(f"Door state changed → {state}")

def main(args=None):
    rclpy.init(args=args)
    node = DoorStateConverterNode()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()
