# LiCar Simulator

Gazebo 환경에서 AWS Hospital 맵을 불러와 Nav2를 수행하는 시나리오.

## Dev Env
- Ubunutu 22.04
- ROS2 Humble

## 1. ROS2 Installation
<b> [Reference Link](https://docs.ros.org/en/humble/Installation/Ubuntu-Install-Debs.html#id4) </b>
```bash
locale  # check for UTF-8

sudo apt update && sudo apt install locales
sudo locale-gen en_US en_US.UTF-8
sudo update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8
export LANG=en_US.UTF-8

locale  # verify settings
```
```bash
sudo apt install software-properties-common
sudo add-apt-repository universe
```
```bash
sudo apt update && sudo apt install curl -y
export ROS_APT_SOURCE_VERSION=$(curl -s https://api.github.com/repos/ros-infrastructure/ros-apt-source/releases/latest | grep -F "tag_name" | awk -F\" '{print $4}')
curl -L -o /tmp/ros2-apt-source.deb "https://github.com/ros-infrastructure/ros-apt-source/releases/download/${ROS_APT_SOURCE_VERSION}/ros2-apt-source_${ROS_APT_SOURCE_VERSION}.$(. /etc/os-release && echo $VERSION_CODENAME)_all.deb" # If using Ubuntu derivates use $UBUNTU_CODENAME
sudo dpkg -i /tmp/ros2-apt-source.deb
```
```bash
sudo apt update
sudo apt upgrade

sudo apt install ros-humble-desktop
```


## 2. AWS Hospital Map installation
<b> [Reference Link](https://github.com/aws-robotics/aws-robomaker-hospital-world/tree/ros2) </b>
```bash
cd ~/S13P21C206/apps/simulator/aws-robomaker-hospital-world
rosdep install --from-paths . --ignore-src -r -y

sudo apt install ros-humble-gazebo-ros-pkgs
sudo apt install ros-humble-ros-gz-bridge

chmod +x setup.sh
./setup.sh
colcon build
```

## 3. TurtleSim Installation (HUMBLE)
[Reference Link](https://kimbrain.tistory.com/entry/%ED%84%B0%ED%8B%80%EB%B4%87-%EC%8A%A4%ED%84%B0%EB%94%94-1%EC%A3%BC%EC%B0%A8-%ED%84%B0%ED%8B%80%EB%B4%873-%EC%8B%9C%EB%AE%AC%EB%A0%88%EC%9D%B4%EC%85%98-%EB%93%80%ED%86%A0%EB%A6%AC%EC%96%BC-%EC%A7%84%ED%96%89%ED%95%98%EA%B8%B0)
```bash
cd ~
mkdir -p turtle_ws/src && cd turtle_ws/src

git clone https://github.com/ROBOTIS-GIT/turtlebot3.git -b humble
git clone https://github.com/ROBOTIS-GIT/turtlebot3_msgs.git -b humble
git clone https://github.com/ROBOTIS-GIT/turtlebot3_simulations.git -b humble


sudo apt install python3-rosdep2
sudo apt install python3-colcon-common-extensions

cd ~/S13P21C206/apps/simulator/turtle_ws

rosdep init
rosdep update
rosdep install --from-paths src --ignore-src -r -y

colcon build --symlink-install && source install/local_setup.bash
```

<b> 라이다 센서 최대 범위 수정 </b>
```bash
code $HOME/turtle_ws/src/turtlebot3_simulations/turtlebot3_gazebo/models/turtlebot3_burger/model.sdf

# 156번줄 <max>200.0</max> 으로 수정

cd ~/turtle_ws
colcon build --symlink-install
turtlebot
```

## 4. Cartographer
- 맵 생성
```bash
ros2 launch turtlebot3_cartographer cartographer.launch.py use_sim_time:=True
```
- 터틀봇 조작
```bash
ros2 run turtlebot3_teleop teleop_keyboard
```
- 맵 저장
```bash
ros2 run nav2_map_server map_saver_cli -f $HOME/S13P21C206/apps/simulator/maps/MMDD_HHMM/map
```

## 5. 자율 주행
```bash
ros2 launch turtlebot3_navigation2 navigation2.launch.py use_sim_time:=True map:=$HOME/S13P21C206/apps/simulator/maps/MMDD_HHMM/map.yaml
```

## bashrc
```bash
# 환경 변수
export ROS2_WS_PATH=$HOME/S13P21C206/apps/simulator/ros2_ws
export AWS_HOSPITAL_WS_PATH=$HOME/S13P21C206/apps/simulator/aws-robomaker-hospital-world
export TURTLE_WS_PATH=$HOME/S13P21C206/apps/simulator/turtle_ws
export TURTLEBOT3_MODEL=burger
export ROS_DOMAIN_ID=1
export LIBGL_ALWAYS_SOFTWARE=1
export QT_QPA_PLATFORM=xcb
export GAZEBO_PLUGIN_PATH=/opt/ros/humble/lib:$GAZEBO_PLUGIN_PATH

# Gazebo
export GAZEBO_MODEL_PATH=${AWS_HOSPITAL_WS_PATH}/models:${AWS_HOSPITAL_WS_PATH}/fuel_models
source /usr/share/gazebo/setup.sh

# ROS2
source /opt/ros/humble/setup.bash
source ${AWS_HOSPITAL_WS_PATH}/install/local_setup.bash
source ${ROS2_WS_PATH}/install/local_setup.bash
source ${TURTLE_WS_PATH}/install/local_setup.bash

# Alias
alias sb="source ~/.bashrc; echo \"bashrc is reloaded.\""
alias cb='cd ${ROS2_WS_PATH} && colcon build --symlink-install && source install/local_setup.bash && echo "ros2 build success"'
alias killgazebo="killall gzserver gzclient"
alias humble="source /opt/ros/humble/setup.bash; echo \"ROS2 Humble is activated.\""
alias turtlebot="humble; source ${TURTLE_WS_PATH}/install/local_setup.bash; echo \"turtlebot workspace is activated.\""
```
# 실행 순서(순서대로)

### 첫번째 터미널
 - gazebo 실행
```
cd S13P21C206/apps/simulator/ros2_ws/
ros2 launch licar_bringup test.launch.py
```
### 두번째 터미널
 - nav2(rviz) 실행
```
cd S13P21C206/apps/simulator/ros2_ws/
ros2 launch licar_bringup navigation2_with_initial_pose.launch.py \
    use_sim_time:=True \
    map:=$HOME/S13P21C206/apps/simulator/maps/0915_1714/map.yaml
```
### 새번째 터미널
 - 카메라 센서 뷰
```
ros2 run rqt_image_view rqt_image_view
```
### 네번째 터미널
 - ros2 data 송출
```
ros2 topic pub --once /turtlebot_target std_msgs/msg/Int32 "data: 301"
```
### 기타 명령
 - 엘리베이터 이동 명령 (새 터미널 혹은 네번째 터미널에서)
```
gz topic -p /gazebo/world/elevator -m 'data: "0"'  # 1층 이동
gz topic -p /gazebo/world/elevator -m 'data: "1"'  # 2층 이동
gz topic -p /gazebo/world/elevator -m 'data: "2"'  # 3층 이동
```
 - 워크스페이스(ws) 수정 후 다시 빌드하는 법
```
cd (수정한 워크스페이스의 위치)
colcon build --symlink-install
source install/local-setup.bash
ros2 launch licar_bringup ..런치 파일..
```
 - gazebo 프로젝트 구조 및 구성하는 법

    [Reference Link](https://mkdrone.tistory.com/14?category=1142298)