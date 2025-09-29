import os
import utils.sa_utils
import yaml

PROGRAM_ROOT_PATH = os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))))

def read_yaml(file_path, default=None):
    """
    读取yaml并返回文件信息
    :param file_path: 文件名
    :param default: 默认值
    :return: 字典
    """
    if default is not None and not os.path.exists(file_path):
        return default
    with open(file_path, 'rb') as f:
        application = yaml.safe_load(f)
    return application

def render_params():
    """函数名是写死的"""

    program_name = os.path.split(PROGRAM_ROOT_PATH)[1]
    module_name = program_name
    # 获取所有nginx ip地址列表
    nginx_ips = utils.sa_utils.get_hosts_by_role('sp', 'nginx', 'nginx')
    application_file = os.path.join(PROGRAM_ROOT_PATH, module_name,"resources","application.yaml")
    application = read_yaml(application_file)
    return {  # 返回所有渲染参数
        'hosts': nginx_ips,
        'port' : application.get('server').get('port'),
    }
