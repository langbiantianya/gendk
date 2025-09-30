# TODO 这个文件名得改一下，把 dk_sso_demo 换成你自己的组件名
import socket
import hyperion_client.deploy_topo
import re
import os
import yaml

all_host = hyperion_client.deploy_topo.DeployTopo().get_all_host_list()
all_host_brief = {host.split('.')[0]: host for host in all_host}
base_path = os.path.realpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../../../../"))
yaml_file = os.path.join(base_path, "construction_blueprint/blueprint_2_1/config.yaml")


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
    if isinstance(host_name, dict):
        for k, v in host_name.items():
            host_name_list = get_default_host_name(v)
            if host_name_list:
                return host_name_list
    elif isinstance(host_name, list):
        host_name_list = get_default_host_name(host_name)
        if host_name_list:
            return host_name_list
    raise RuntimeError(f"{host_name} not in {all_host}")


def get_re_host_name_list(host_name) -> list:
    if isinstance(host_name, dict):
        for k, v in host_name.items():
            host_name_list = get_re_host_name(v)
            if host_name_list:
                return host_name_list
    elif isinstance(host_name, str):
        host_name_list = get_re_host_name(host_name)
        if host_name_list:
            return host_name_list
    raise RuntimeError(f"{host_name} not in {all_host}")


def get_local_host_name_list(host_name) -> list:
    return [get_local_host_name()]


fun = {
    "default": get_default_host_name_list,
    "re": get_re_host_name_list,
    "local": get_local_host_name_list,
    "liveness": lambda x: None,
}


def get_prot(data):
    port = data.get("port")
    if port:
        return port
    port_conf = data.get("port_conf")
    if port_conf:
        conf = read_yaml(os.path.join(base_path, port_conf))
        return conf["server"]["port"]
    raise Exception("port or port_conf no configuration")


def get_host_name_list(data):
    name_list = fun[data.get("type", "default")](data.get("host_name"))
    if name_list is None:
        return None
    prot = get_prot(data)
    if not isinstance(prot, int):
        raise Exception("Port is %s, not int" % prot)
    return '\n'.join([f"server {name}:{prot};" for name in name_list])


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
