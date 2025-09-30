## ros2_ws launch 명령어
# 워크스페이스 루트 폴더로 이동
cd ~/git/S13P21C206/apps/raspberrypi/ros2_ws/

# 다시 빌드
colcon build --symlink-install

# 환경 설정
source install/setup.bash

# 런치 파일 실행
ros2 launch bring_up validation_system.launch.py