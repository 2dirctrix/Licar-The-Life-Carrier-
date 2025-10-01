# ros2_ws launch 명령어
## 워크스페이스 루트 폴더로 이동
cd ../apps/raspberrypi/ros2_ws/

## 다시 빌드
colcon build --symlink-install

## 환경 설정
source install/setup.bash

## 런치 파일 실행
ros2 launch bring_up validation_system.launch.py

<hr>

시뮬레이터에서는 ros2 humble을 사용했고 raspberry에서는 ros2 jazzy를 사용했기 때문에 dds를 통일하는 과정에서 cyclone dds 로 통합하려고 했으나 완성하지 못해 과정중에 남은 코드들이 일부 남아있음

## 경로 수정

* \apps\raspberrypi\ros2_ws\src\bring_up\launch\validation_system.launch.py 의 경로 수정

    * ROS_SETUP_SCRIPT = "{파일 경로}/apps/raspberrypi/ros2_ws/install/setup.bash" 
    * VENV_SITE_PACKAGES = "{파일 경로}/.venv/lib/python3.12/site-packages" # 가상환경의 절대 경로
    * CYCLONEDDS_URI = "{파일 경로}/apps/raspberrypi/ros2_ws/cyclonedds.xml" # xml 파일의 절대 경로

* \apps\raspberrypi\ros2_ws\src\yolo_pkg\yolo_pkg\yolo_validator_node.py 의 경로 수정g

    * MQTT_WS_URL = "j13c206.p.ssafy.io" # mqtt통신할 주소를 작성해 준다,
    * model_path = '/home/Desktop/best.pt' # ros2_ws에 있는 pt 파일의 위치를 작성해 준다.