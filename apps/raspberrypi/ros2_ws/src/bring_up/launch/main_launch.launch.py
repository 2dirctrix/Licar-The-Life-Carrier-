from launch import LaunchDescription
from launch.actions import ExecuteProcess

# def generate_launch_description():
#     return LaunchDescription([
#         ExecuteProcess(
#             cmd=[
#                 'sudo', '/home/ssafy/juno/.venv/bin/python',
#                 '/home/ssafy/S13P21C206/apps/raspberrypi/ros2_ws/src/rc522_pkg/rc522_pkg/main.py'
#             ],
#             output='screen'
#         )
#     ])