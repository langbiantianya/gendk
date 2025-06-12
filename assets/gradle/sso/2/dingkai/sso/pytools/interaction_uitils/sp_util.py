# -*- coding:utf-8 -*-
"""
@Time  : 2022/1/13 2:59 下午
@Author: zhaozhiqi@sensorsdata.cn
@File  : sp_util.py
"""
import utils.sa_utils


def get_hosts_by_role(product, module, role):
    return utils.sa_utils.get_hosts_by_role(product, module, role)


def get_host_list():
    return utils.sa_utils.get_host_list()


def get_client_conf(model, product) -> dict:
    from hyperion_client.hyperion_inner_client.inner_config_manager import InnerConfigManager
    return InnerConfigManager.get_instance().get_client_conf(module_name=model, product_name=product)


def get_mysql_master():
    from hyperion_client.hyperion_inner_client.inner_config_manager import InnerConfigManager
    return InnerConfigManager.get_instance().get_mysql_master()


def get_url_by_role_list(product, module, role, port=None):
    role_list = get_hosts_by_role(product, module, role)
    port = port or get_client_conf(module, product)["port"]
    return [f"http://{role}:{port}" for role in role_list]


if __name__ == '__main__':
    print(get_url_by_role_list("sm", "pushgateway", "pushgateway"))
