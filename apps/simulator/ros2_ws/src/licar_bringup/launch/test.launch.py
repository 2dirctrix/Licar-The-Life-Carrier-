import os
from ament_index_python.packages import get_package_share_directory
from launch import LaunchDescription
from launch.actions import IncludeLaunchDescription, TimerAction, DeclareLaunchArgument
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node
from launch.substitutions import LaunchConfiguration

def generate_launch_description():
    # 패키지 경로
    pkg_gazebo_ros = get_package_share_directory('gazebo_ros')
    pkg_turtlebot3_gazebo = get_package_share_directory('turtlebot3_gazebo')
    pkg_elevator = get_package_share_directory('elevator_pkg')
    pkg_turtlebot = get_package_share_directory('turtlebot_pkg')

    # Launch Arguments
    use_sim_time = LaunchConfiguration('use_sim_time', default='true')

    # Gazebo world 파일 체크
    HOME_PATH = os.environ['HOME']
    world_hospital_third = HOME_PATH + '/S13P21C206/apps/simulator/aws-robomaker-hospital-world/worlds/hospital_three_floors.world'
    
    # World 파일이 없으면 기본 world 사용
    launch_arguments = {}
    if os.path.exists(world_hospital_third):
        launch_arguments['world'] = world_hospital_third
        launch_arguments['verbose'] = 'true'
        print("Hospital world 로드")
    else:
        print("Hospital world 파일을 찾을 수 없어 기본 world를 사용합니다.")
        launch_arguments['verbose'] = 'true'

    # Gazebo 실행
    gzserver_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(pkg_gazebo_ros, 'launch', 'gzserver.launch.py')),
        launch_arguments=launch_arguments.items()
    )

    gzclient_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(pkg_gazebo_ros, 'launch', 'gzclient.launch.py'))
    )

    # Robot State Publisher (TF 정보 제공)
    robot_state_publisher = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(pkg_turtlebot3_gazebo, 'launch', 'robot_state_publisher.launch.py')),
        launch_arguments={'use_sim_time': use_sim_time}.items()
    )
    
    # 터틀봇 스폰 (지연 실행)
    turtlebot_spawn = TimerAction(
        period=3.0,  # 3초 후 실행
        actions=[
            Node(
                package='gazebo_ros',
                executable='spawn_entity.py',
                arguments=[
                    '-entity', 'turtlebot3_burger',
                    '-file', os.path.join(pkg_turtlebot3_gazebo, 'models', 'turtlebot3_burger', 'model.sdf'),
                    '-x', '-0.15',
                    '-y', '2.5',
                    '-z', '0.4',
                    '-unpause'
                ],
                output='screen'
            )
        ]
    )

    # Elevator 노드
    elevator_node = Node(
        package='elevator_pkg',
        executable='door_state_converter_node',
        name='door_state_converter',
        output='screen',
        parameters=[{'use_sim_time': use_sim_time}]
    )

    # Turtlebot 노드들 (Robot State Publisher 시작 후)
    turtlebot_main = TimerAction(
        period=5.0,  # Robot State Publisher 시작 후
        actions=[
            Node(
                package='turtlebot_pkg',
                executable='main',
                name='main',
                output='screen',
                parameters=[{'use_sim_time': use_sim_time}]
            )
        ]
    )

    turtlebot_mover = TimerAction(
        period=5.0,  # Robot State Publisher 시작 후
        actions=[
            Node(
                package='turtlebot_pkg',
                executable='turtlebot_mover',
                name='turtlebot_mover',
                output='screen',
                parameters=[{'use_sim_time': use_sim_time}]
            )
        ]
    )

    # Launch Arguments 선언
    declare_use_sim_time_cmd = DeclareLaunchArgument(
        'use_sim_time',
        default_value='true',
        description='Use simulation (Gazebo) clock if true')

    # LaunchDescription에 추가
    ld = LaunchDescription()
    
    # Launch Arguments 추가
    ld.add_action(declare_use_sim_time_cmd)
    
    # 실행 순서: Gazebo → Robot State → TurtleBot Spawn → User Nodes
    ld.add_action(gzserver_cmd)
    ld.add_action(gzclient_cmd)
    ld.add_action(robot_state_publisher)
    ld.add_action(turtlebot_spawn)
    ld.add_action(elevator_node)
    ld.add_action(turtlebot_main)
    ld.add_action(turtlebot_mover)

    return ld