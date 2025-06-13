# 作者 ： 胡振华
# 时间 ： 2021/12/27 12:12 下午
from string import Template
import socket

from interaction_uitils.config_util import get_conf, module_name
from interaction_uitils.sp_util import get_hosts_by_role


class customTemple(Template):
    """
    doc for LinduoTemple
    """
    # modify delimiter
    delimiter = '$$'


format_args = {}


def get_format_args():
    """
    该方法主要是获取一个map对象
    包含localIp：本地ip，类似于
    host_names: 本地ip:8112端口，类似于 ：['hybrid01.debugresetreset25487.sensorsdata.cloud:8112']
    nginx_ips: 机器主机名列表，类似于：
                ['hybrid01.debugresetreset25487.sensorsdata.cloud',
                 'hybrid02.debugresetreset25487.sensorsdata.cloud',
                  'hybrid03.debugresetreset25487.sensorsdata.cloud']
    module_name: 单点登录模块的名称，相当于 $SENSORS_DELIVER_DEV_HOME/sso/ 目录中的 sso
    sdd_home_path: sdd安装目录，类似于： $SENSORS_DELIVER_DEV_HOME/sso/
    :return: 包含localIp、host_names、nginx_ips、module_name、hosts、sdd_home_path的字典
    """
    if format_args:
        return format_args
    # 获取所有nginx ip地址列表
    high_availability = get_conf('sensors.conf.high_availability', False)
    port = get_conf('server.port', 8112)
    max_num = 1
    min_num = 1
    stateful = True
    local_ip = socket.getfqdn()
    host_names = str([local_ip + ':' + str(port)])
    if high_availability:
        # 高可用模式， 选所有的 sbp web 节点作为部署节点
        nginx_ips = get_hosts_by_role("sbp", "web", "web")
        host_names = str([ip + ':' + str(port) for ip in nginx_ips])
        max_num = len(nginx_ips)
        min_num = len(nginx_ips)
        local_ip = "127.0.0.1"

    format_args.update({
        # sddServer
        'module_name': module_name,
        "max_num": max_num,
        "min_num": min_num,
        "stateful": stateful,
        'host_names': host_names,
        'local_ip': local_ip,
        'port': port,
    })
    return format_args


def format_file(file_path, tmp_file_path):
    """
    替换文件中的一些占位符
    :param file_path: 原始文件路径
    :param tmp_file_path: 经过替换后，生成的文件路径
    :return:
    """
    if not format_args:
        get_format_args()
    with open(file_path, 'r') as sso_server_yml_file:
        c = customTemple(sso_server_yml_file.read())
    with open(tmp_file_path, 'w+') as tmp_sso_server_yml_file:
        tmp_sso_server_yml_file.write(c.substitute(format_args))
