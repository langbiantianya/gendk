import socket
import hyperion_client.deploy_topo
import re
import os
import yaml

all_host = hyperion_client.deploy_topo.DeployTopo().get_all_host_list()
all_host_brief = {host.split('.')[0]: host for host in all_host}
base_path = os.path.realpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../../"))
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


def get_default_host_name_list(data) -> list:
    host_name = data.get("host_name")
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


def get_re_host_name_list(data) -> list:
    host_name = data.get("host_name")
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


def get_local_host_name_list(data) -> list:
    return [get_local_host_name()]


def get_prot(data):
    port = data.get("port")
    if port:
        return port
    port_conf = data.get("port_conf")
    if port_conf:
        conf = read_yaml(os.path.join(base_path, port_conf))
        return conf["server"]["port"]
    raise Exception("port or port_conf no configuration")


def get_liveness_probe(data):
    prot = get_prot(data)
    if not isinstance(prot, int):
        raise Exception("Port is %s, not int" % prot)
    from hyperion_utils.shell_utils import run_cmd
    out = run_cmd('aradmin version -f', print_fun=lambda x: None)['stdout']
    version = level = None
    for line in out.split('\n'):
        if 'hyperion' in line:
            _, version, level, *_ = [i.strip() for i in line.split('│') if i.strip()]
    if not version or (version_tuple(version) < version_tuple('0.2.0.7257')):
        liveness_probe = {
            'port': {
                'port': prot,
                'process_keyword': data['process_keyword']
            }
        }
    else:
        liveness_probe = {
            'http_get': {
                'path': data["get_path"],
                'port': prot,
            }
        }
    return liveness_probe


def version_tuple(_version):
    return tuple(map(int, _version.split('.')))


fun = {
    "default": get_default_host_name_list,
    "re": get_re_host_name_list,
    "local": get_local_host_name_list,
    "liveness": get_liveness_probe,
}


def get_conf(data):
    return str(fun[data.get("type", "default")](data))


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
    return {k: get_conf(v) for k, v in data.items()}
