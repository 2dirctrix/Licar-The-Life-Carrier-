import os
from launch import LaunchDescription
from launch.actions import ExecuteProcess

# --- 모든 경로와 설정을 하드코딩된 절대 경로로 명확하게 정의 ---
# 이 방식은 현재 환경에서 가장 확실하게 동작합니다.
ROS_SETUP_SCRIPT = "/home/ssafy/git3/S13P21C206/apps/raspberrypi/ros2_ws/install/setup.bash"
VENV_SITE_PACKAGES = "/home/ssafy/juno/.venv/lib/python3.12/site-packages"
CYCLONEDDS_URI = "/home/ssafy/git3/S13P21C206/apps/raspberrypi/ros2_ws/cyclonedds.xml" # <--- xml 파일의 절대 경로

ROS_DOMAIN_ID = "1"
RASPBERRY_PI_IP = "192.168.100.246"
SIMULATOR_PC_IP = "192.168.100.145"


def generate_launch_description():
    # 실행할 명령어를 생성하는 헬퍼 함수
    def create_sudo_command(pkg_name, executable_name, node_name):
        # bash -c "명령1; 명령2; ..." 형태로 모든 것을 하나의 쉘 세션에서 실행
        command_string = (
            f"export RMW_IMPLEMENTATION=rmw_cyclonedds_cpp; "
            # 환경 변수에 직접 절대 경로를 삽입
            f"export CYCLONEDDS_URI='{CYCLONEDDS_URI}'; "

            f"export ROS_DOMAIN_ID={ROS_DOMAIN_ID}; "
            f"export ROS_IP={RASPBERRY_PI_IP}; "
            f"export ROS_AUTOMATIC_DISCOVERY_RANGE=SUBSCRIBER; "
            f"export ROS_STATIC_PEERS={SIMULATOR_PC_IP}; "
            
            f"source {ROS_SETUP_SCRIPT}; "
            f"export PYTHONPATH={VENV_SITE_PACKAGES}:$PYTHONPATH; "
            f"ros2 run {pkg_name} {executable_name} --ros-args -r __node:={node_name}"
        )
        return ['sudo', 'bash', '-c', command_string]

    return LaunchDescription([
        ExecuteProcess(
            cmd=create_sudo_command('nfc_pkg', 'hardware_controller_node', 'hardware_controller_node'),
            output='screen'
        ),
        ExecuteProcess(
            cmd=create_sudo_command('yolo_pkg', 'yolo_validator_node', 'yolo_validator_node'),
            output='screen'
        ),

    ])