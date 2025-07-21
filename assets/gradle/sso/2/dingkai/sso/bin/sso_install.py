# coding:utf-8
"""
@author FONF(zhaozhiqi@sensorsdata.cn)
@brief

单点登录一键部署脚本
"""
import argparse
import sys
import os

ROOT_PATH = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(os.path.join(ROOT_PATH, 'pytools'))
from delete import parser_args as delete_parser_args
from install import parser_args as install_parser_args
from sbp_config_tools import parser_args as config_parser_args


def parser_args(parser):
    sub_parse = parser.add_subparsers(
        dest='sso_install',
        description="单点登录工具",
    )
    install_parse = sub_parse.add_parser("install", help="单点登录一键部署")
    delete_parse = sub_parse.add_parser("delete", help="删除单点登录服务，务必谨慎，删除不备份")
    config_parse = sub_parse.add_parser("config", help="sbp 配置工具")
    sub_parse.required = True
    delete_parser_args(delete_parse)
    install_parser_args(install_parse)
    config_parser_args(config_parse)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="单点登录工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
