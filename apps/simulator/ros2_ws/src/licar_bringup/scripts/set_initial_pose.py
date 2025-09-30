#!/usr/bin/env python3
import rclpy
from rclpy.node import Node
from geometry_msgs.msg import PoseWithCovarianceStamped
import sys

class InitialPoseSetter(Node):
    def __init__(self, x, y, yaw):
        super().__init__('initial_pose_setter')
        self.publisher = self.create_publisher(PoseWithCovarianceStamped, '/initialpose', 10)

        # 1초 후 초기 위치 발행
        self.timer = self.create_timer(1.0, lambda: self.publish_initial_pose(x, y, yaw))

    def publish_initial_pose(self, x, y, yaw):
        msg = PoseWithCovarianceStamped()
        msg.header.frame_id = 'map'
        msg.header.stamp = self.get_clock().now().to_msg()

        msg.pose.pose.position.x = float(x)
        msg.pose.pose.position.y = float(y)
        msg.pose.pose.position.z = 0.0

        # yaw를 quaternion으로 변환
        import math
        msg.pose.pose.orientation.z = math.sin(float(yaw)/2)
        msg.pose.pose.orientation.w = math.cos(float(yaw)/2)

        self.publisher.publish(msg)
        self.get_logger().info(f'Initial pose set: x={x}, y={y}, yaw={yaw}')

        # 한 번만 발행하고 종료
        self.timer.cancel()
        rclpy.shutdown()

def main():
    if len(sys.argv) != 4:
        print("Usage: set_initial_pose.py <x> <y> <yaw>")
        return

    rclpy.init()
    node = InitialPoseSetter(sys.argv[1], sys.argv[2], sys.argv[3])
    rclpy.spin(node)

if __name__ == '__main__':
    main()