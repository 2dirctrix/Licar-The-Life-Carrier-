import os

from ament_index_python.packages import get_package_share_directory
from launch import LaunchDescription
from launch.actions import IncludeLaunchDescription, SetEnvironmentVariable
from launch_ros.actions import Node
from launch.launch_description_sources import PythonLaunchDescriptionSource

# --- 1. 네트워크 및 환경 설정 ---
ROS_DOMAIN_ID = "1"
SIMULATOR_PC_IP = "192.168.100.145"   # 시뮬레이터 PC 자신의 IP
RASPBERRY_PI_IP = "192.168.100.246"   # RPi IP

# ✅ 홈 경로 + 폴더를 join으로 조립 (역슬래시 금지)
HOME = os.path.expanduser('~')
WS_ROOT_PATH = os.path.join(HOME, 'S13P21C206', 'apps', 'simulator', 'ros2_ws')
CYCLONEDDS_PATH = os.path.join(WS_ROOT_PATH, 'cyclonedds.xml')
# ✅ CYCLONEDDS_URI는 file:// 스킴 필수
CYCLONEDDS_URI = f'file://{CYCLONEDDS_PATH}'

def generate_launch_description():

    # 패키지 경로
    pkg_gazebo_ros = get_package_share_directory('gazebo_ros')

    # ✅ 월드 경로도 join으로 안전하게
    world_file_path = os.path.join(
        HOME,
        'S13P21C206', 'apps', 'simulator', 'aws-robomaker-hospital-world',
        'worlds', 'hospital_three_floors.world'
    )

    # Gazebo server/client
    gzserver_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(
            os.path.join(pkg_gazebo_ros, 'launch', 'gzserver.launch.py')
        ),
        launch_arguments={'world': world_file_path}.items()
    )

    gzclient_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(
            os.path.join(pkg_gazebo_ros, 'launch', 'gzclient.launch.py')
        )
    )

    # ROS ↔ Gazebo bridge
    elevator_bridge = Node(
        package='ros_gz_bridge',
        executable='parameter_bridge',
        arguments=['/elevator@std_msgs/msg/String@gazebo.msgs.GzString'],
        name='elevator_bridge',
        output='screen'
    )

    # 라즈베리 파이 토픽 수신 리스너
    delivery_listener_node = Node(
        package='simulator_listener_pkg',
        executable='listener',
        name='delivery_status_listener',
        output='screen'
    )

    ld = LaunchDescription()

    # ✅ 환경변수를 launch에서 "정상 경로"로 강제 주입
    ld.add_action(SetEnvironmentVariable('RMW_IMPLEMENTATION', 'rmw_cyclonedds_cpp'))
    ld.add_action(SetEnvironmentVariable('CYCLONEDDS_URI', CYCLONEDDS_URI))
    ld.add_action(SetEnvironmentVariable('ROS_DOMAIN_ID', ROS_DOMAIN_ID))
    ld.add_action(SetEnvironmentVariable('ROS_IP', SIMULATOR_PC_IP))

    # (선택) Fast-DDS 전용 변수들이라면 제거 가능. CycloneDDS는 cyclonedds.xml 권장
    # ld.add_action(SetEnvironmentVariable('ROS_AUTOMATIC_DISCOVERY_RANGE', 'SUBSCRIBER'))
    # ld.add_action(SetEnvironmentVariable('ROS_STATIC_PEERS', RASPBERRY_PI_IP))

    ld.add_action(gzserver_cmd)
    ld.add_action(gzclient_cmd)
    ld.add_action(elevator_bridge)
    ld.add_action(delivery_listener_node)

    return ld
