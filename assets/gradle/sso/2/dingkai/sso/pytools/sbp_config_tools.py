import argparse
from sbp_config import get_tools, set_tools, delete_tools


def parser_args(parser):
    sub_parse = parser.add_subparsers(
        dest='conf',
        description="sbp 配置工具",
    )
    get_parse = sub_parse.add_parser("get", help="查看配置")
    set_parse = sub_parse.add_parser("set", help="设置配置")
    delete_parse = sub_parse.add_parser("delete", help="删除配置")
    sub_parse.required = True
    get_tools.parser_args(get_parse)
    set_tools.parser_args(set_parse)
    delete_tools.parser_args(delete_parse)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="sbp 配置工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
