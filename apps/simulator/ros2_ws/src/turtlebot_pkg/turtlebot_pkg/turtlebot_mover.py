#!/usr/bin/env python3
import math
import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32, Bool
from geometry_msgs.msg import PoseStamped
from nav2_simple_commander.robot_navigator import BasicNavigator, TaskResult

class TurtlebotMover(Node):
    def __init__(self):
        super().__init__('turtlebot_mover_node')
        self.navigator = BasicNavigator()

        self.create_subscription(Int32, '/turtlebot_command', self.command_callback, 10)
        self.completion_pub = self.create_publisher(Bool, '/turtlebot/move_complete', 10)
        
        # ⭐ 네비게이션 상태와 타이머를 관리할 변수 추가
        self.is_navigating = False
        self.nav_timer = None
        
        self.get_logger().info("✅ Mover node ready")

        self.waypoints = {
            1:  {"x": -0.15, "y": 2.5},
            11: {"x": 1.8, "y": 20.0},
            12: {"x": 1.8, "y": 21.7}, 
            301: {"x": -2.2, "y": -7.3},
            302: {"x": 9.0, "y": -7.3},
        }

    def command_callback(self, msg: Int32):
        # 이미 이동 중인 경우, 기존 작업을 취소합니다.
        if self.is_navigating:
            self.get_logger().info('Canceling previous task to start a new one...')
            self.navigator.cancelTask()
            if self.nav_timer:
                self.nav_timer.cancel()
        
        cmd = msg.data
        if cmd == 0:
            self.get_logger().info('Canceling current task...')
            self.navigator.cancelTask()
            self.is_navigating = False
            if self.nav_timer:
                self.nav_timer.cancel()
            return
        
        waypoint = self.waypoints.get(cmd)
        if not waypoint:
            self.get_logger().error(f"알 수 없는 명령: {cmd}")
            return

        x, y = waypoint['x'], waypoint['y']
        self.get_logger().info(f"🚀 {cmd}번 지점 ({x}, {y})으로 이동을 시작합니다.")
        
        goal_pose = self.create_pose(x, y)
        
        # goToPose는 명령 전달 성공 여부를 bool로 반환합니다.
        # 더 이상 future 객체를 사용하지 않습니다.
        self.navigator.goToPose(goal_pose)
        
        self.is_navigating = True
        # 1초마다 check_nav_status 함수를 호출하는 타이머를 시작합니다.
        self.nav_timer = self.create_timer(1.0, self.check_nav_status)
    
    def check_nav_status(self):
        if not self.is_navigating:
            return

        # isTaskComplete()로 네비게이션 완료 여부를 확인합니다.
        if self.navigator.isTaskComplete():
            # 작업이 완료되면 타이머를 멈춥니다.
            if self.nav_timer:
                self.nav_timer.cancel()
            self.is_navigating = False

            result = self.navigator.getResult()
            if result == TaskResult.SUCCEEDED:
                self.get_logger().info("✨ Move complete")
                completion_msg = Bool()
                completion_msg.data = True
                self.completion_pub.publish(completion_msg)
            elif result == TaskResult.CANCELED:
                self.get_logger().warn("⚠️ Goal was canceled!")
            elif result == TaskResult.FAILED:
                self.get_logger().error("❌ Goal failed!")
            else:
                self.get_logger().error(f"❓ Move failed with unknown result: {result}")

    def create_pose(self, x, y, yaw=0.0):
        pose = PoseStamped()
        pose.header.frame_id = 'map'
        pose.header.stamp = self.get_clock().now().to_msg()
        pose.pose.position.x = x
        pose.pose.position.y = y
        pose.pose.orientation.z = math.sin(yaw/2)
        pose.pose.orientation.w = math.cos(yaw/2)
        return pose

def main(args=None):
    rclpy.init(args=args)
    node = TurtlebotMover()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()

if __name__ == "__main__":
    main()