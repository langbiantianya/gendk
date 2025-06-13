import socket
import hyperion_client.deploy_topo
import re
import os
import yaml
import sys

all_host = hyperion_client.deploy_topo.DeployTopo().get_all_host_list()
all_host_brief = {host.split('.')[0]: host for host in all_host}
yaml_file = os.path.abspath(os.path.join(os.path.dirname(__file__), '../../../host.yaml'))


def get_local_host_name():
    return socket.getfqdn()


def get_re_host_name(host_pattern: str) -> list:
    return list(filter(lambda host: re.fullmatch(host_pattern, host), all_host))


def get_host_in_all_host(host: str) -> str:
    if '.' in host:
        if host in all_host:
            return host
    else:
        if host in all_host_brief:
            return all_host_brief[host]
    return ''


def get_default_host_name(host_list: list) -> list:
    host_name_list = list()
    for host in host_list:
        host_name = get_host_in_all_host(host)
        if host_name:
            host_name_list.append(host_name)
        else:
            return []
    return host_name_list


def get_default_host_name_list(host_name) -> list:
    if type(host_name) == dict:
        for k, v in host_name.items():
            host_name_list = get_default_host_name(v)
            if host_name_list:
                return host_name_list
    elif type(host_name) == list:
        host_name_list = get_default_host_name(host_name)
        if host_name_list:
            return host_name_list
    raise RuntimeError(f"{host_name} not in {all_host}")


def get_re_host_name_list(host_name) -> list:
    if type(host_name) == dict:
        for k, v in host_name.items():
            host_name_list = get_re_host_name(v)
            if host_name_list:
                return host_name_list
    elif type(host_name) == str:
        host_name_list = get_re_host_name(host_name)
        if host_name_list:
            return host_name_list
    raise RuntimeError(f"{host_name} not in {all_host}")


def get_local_host_name_list(host_name) -> list:
    return [get_local_host_name()]


def get_sso_host_list(host_name):
    """
    单点登录专门做的一个配置方式，读取单点登录的配置文件，如果是高可用模式，则部署在所有的 sbp web 节点上，如果不是，则走 local
    :return:
    """
    sys.path.append(os.path.join(os.path.abspath(os.path.dirname(os.path.abspath(__file__))), '../../../../../sso/pytools/'))
    from interaction_uitils.config_util import get_conf
    from interaction_uitils.sp_util import get_hosts_by_role
    if get_conf('sensors.conf.high_availability', False):
        return get_hosts_by_role("sbp", "web", "web")
    else:
        return get_local_host_name_list(host_name)


fun = {
    "default": get_default_host_name_list,
    "re": get_re_host_name_list,
    "local": get_local_host_name_list,
    "sso": get_sso_host_list,
}


def get_host_name_list(data):
    name_list = fun[data.get("type", "default")](data.get("host_name"))
    return '\n'.join([f"server {name}:{data['port']};" for name in name_list])


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


def render_params():
    data = read_yaml(yaml_file)
    result = {k: get_host_name_list(v) for k, v in data.items()}
    dk_base_dir = os.getenv('DINGKAI_BASE')
    result["dk_base_dir"] = dk_base_dir
    return result
