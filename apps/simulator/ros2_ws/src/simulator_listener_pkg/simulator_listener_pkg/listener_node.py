import rclpy
from rclpy.node import Node
from std_msgs.msg import Int32

class DeliveryStatusListener(Node):
    def __init__(self):
        super().__init__('delivery_status_listener')
        self.subscription = self.create_subscription(
            Int32,
            'delivery_status',
            self.listener_callback,
            10)
        self.get_logger().info('Delivery status listener has been started.')

    def listener_callback(self, msg):
        self.get_logger().info(f'Received delivery status code from HW: {msg.data}')

def main(args=None):
    rclpy.init(args=args)
    node = DeliveryStatusListener()
    rclpy.spin(node)
    node.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()