from setuptools import find_packages, setup
import os
from glob import glob

package_name = 'turtlebot_pkg'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
        (os.path.join('share', package_name, 'launch'), glob('launch/*.launch.py')),
        # ('share/' + package_name + '/config', ['config/cases.yaml']), 
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='juno',
    maintainer_email='juno980220@naver.com',
    description='TODO: Package description',
    license='TODO: License declaration',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            'turtlebot_mover = turtlebot_pkg.turtlebot_mover:main',
            'main = turtlebot_pkg.main:main',
            'main_test = turtlebot_pkg.main_test:main',
        ],
    },
)
