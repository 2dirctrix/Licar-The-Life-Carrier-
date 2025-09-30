import os

from ament_index_python.packages import get_package_share_directory
from launch import LaunchDescription
from launch.actions import DeclareLaunchArgument, IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch.substitutions import LaunchConfiguration


def generate_launch_description():

    # ----------------------------
    # 런치 인자 정의
    # ----------------------------
    use_sim_time_arg = DeclareLaunchArgument(
        'use_sim_time',
        default_value='True'
    )

    # map 경로 절대경로, 존재 여부 체크
    default_map_path = os.path.expanduser('~/S13P21C206/apps/simulator/maps/0912_1130/map.yaml')
    if not os.path.isfile(default_map_path):
        raise FileNotFoundError(f"Map file not found: {default_map_path}")

    x_pose_arg = DeclareLaunchArgument(
        'x_pose',
        default_value='-2.0'
    )

    y_pose_arg = DeclareLaunchArgument(
        'y_pose',
        default_value='-0.5'
    )

    # ----------------------------
    # 경로 설정
    # ----------------------------
    HOME_PATH = os.environ['HOME']

    turtlebot3_gazebo_dir = os.path.join(get_package_share_directory('turtlebot3_gazebo'), 'launch')
    pkg_gazebo_ros = get_package_share_directory('gazebo_ros')

    world_hospital =  HOME_PATH + '/S13P21C206/apps/simulator/aws-robomaker-hospital-world/worlds/hospital.world'
    world_hospital_third =  HOME_PATH + '/S13P21C206/apps/simulator/aws-robomaker-hospital-world/worlds/hospital_three_floors.world'

    # ----------------------------
    # Gazebo 서버/클라이언트 런치
    # ----------------------------
    launch_gzserver = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(pkg_gazebo_ros, 'launch', 'gzserver.launch.py')),
        launch_arguments={'world': world_hospital_third}.items()
    )

    launch_gzclient = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(pkg_gazebo_ros, 'launch', 'gzclient.launch.py'))
    )

    # ----------------------------
    # Robot State Publisher
    # ----------------------------
    launch_robot_state_publisher = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(turtlebot3_gazebo_dir, 'robot_state_publisher.launch.py')),
        launch_arguments={'use_sim_time': LaunchConfiguration('use_sim_time')}.items()
    )

    # ----------------------------
    # TurtleBot3 스폰
    # ----------------------------
    launch_spawn_turtlebot = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(turtlebot3_gazebo_dir, 'spawn_turtlebot3.launch.py')),
        launch_arguments={
            'x_pose': LaunchConfiguration('x_pose'),
            'y_pose': LaunchConfiguration('y_pose')
        }.items()
    )

    ld = LaunchDescription()
    
    ld.add_action(use_sim_time_arg)
    ld.add_action(x_pose_arg)
    ld.add_action(y_pose_arg)

    ld.add_action(launch_gzserver)
    ld.add_action(launch_gzclient)
    ld.add_action(launch_robot_state_publisher)
    ld.add_action(launch_spawn_turtlebot)

    return ld
