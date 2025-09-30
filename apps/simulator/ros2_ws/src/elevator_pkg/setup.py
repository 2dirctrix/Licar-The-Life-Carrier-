from setuptools import find_packages, setup

package_name = 'elevator_pkg'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
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
            'elevator_controller_node = elevator_pkg.elevator_controller:main',
            'door_state_converter_node = elevator_pkg.door_state_converter:main',
        ],
    },
)