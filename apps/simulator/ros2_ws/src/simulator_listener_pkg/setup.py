# simulator_listener_pkg/setup.py
from setuptools import setup

package_name = 'simulator_listener_pkg'

setup(
    name=package_name,
    version='0.0.0',
    packages=[package_name],
    data_files=[
        ('share/ament_index/resource_index/packages', ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
        # launch 파일을 쓴다면 아래 폴더/파일도 실제로 존재해야 합니다
        # ('share/' + package_name + '/launch', ['launch/listener.launch.py']),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='hagyun',
    maintainer_email='squarez2g2@gmail.com',
    description='Simple ROS 2 listener package',
    license='MIT',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            # 왼쪽: 실행 파일 이름, 오른쪽: 모듈.함수
            # 'listener = simulator_listener_pkg.listener_node:main',
        ],
    },
)
