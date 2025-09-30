## RaspberryPi
<b> IP Address : 192.168.100.246 </b>
```bash
ssh ssafy@192.168.100.246
# password : 1
``` 
```
cd /home/ssafy/S13P21C206/apps/raspberrypi
pip install -r requirements.txt
cd ros2_ws
colcon build
source install/local_setup.bash
ros2 run rc522_pkg rc522_node
```

