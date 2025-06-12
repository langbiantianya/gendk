import yaml
import os
import json
import socket
import time

common_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../"))
module_name = os.path.basename(common_path)
product_name = os.path.basename(os.path.dirname(common_path))
local_host_name = socket.getfqdn()
this_time = time.strftime("%Y%m%d%H%M%S", time.localtime())
tmp_path = os.path.join('/tmp', f'sso_install_{this_time}/')


def read_yaml(filename, default=None):
    """
    读取yaml文件，并返回文件的信息
    :param filename: 文件名
    :param default: 默认值
    :return: 字典
    """
    if default is not None and not os.path.exists(filename):
        return default
    with open(filename, 'rb') as f:
        # yaml文件通过---分节，多个节组合成一个列表
        data = yaml.safe_load(f)
    return data


def writ_yaml(data, filename):
    """
    写 yaml
    :param data:
    :param filename:
    :return:
    """
    stra = data
    if isinstance(data, dict):
        stra = json.dumps(data)
    with open(filename, 'w', encoding='utf-8') as f:
        yaml.dump(stra, f, allow_unicode=True)


def get_file_path(file):
    return os.path.join(common_path, file)


def get_tmp_file(file):
    tmp_file = os.path.join(tmp_path, file)
    if not os.path.exists(os.path.dirname(tmp_file)):
        os.makedirs(os.path.dirname(tmp_file))
    return tmp_file


def get_config_file_path():
    return os.path.join(common_path, "conf/application.yml")


conf_name = get_config_file_path()
conf = read_yaml(conf_name, {})


def get_json_value_by_key(data: dict, key, default=None):
    """
    取字典中的某个字段，可以嵌套
    :param data: 字典
    :param key: key
    :param default: 默认值
    :return: 取到的内容
    """
    tmp = data
    for _ in key.split("."):
        if isinstance(tmp, list):
            tmp = tmp[int(_)]
        elif isinstance(tmp, dict):
            tmp = tmp.get(_)
        else:
            return None
    return tmp or default


def get_conf(key, default=None):
    return get_json_value_by_key(conf, key, default) or default
