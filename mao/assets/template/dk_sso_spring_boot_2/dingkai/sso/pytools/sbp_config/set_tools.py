import argparse
import sys
import os

sys.path.append(os.path.join(os.path.abspath(os.path.dirname(os.path.abspath(__file__))), '../'))
from interaction_uitils.logger import logger
from interaction_uitils.sbp_util import SBPConfigUtils


def parser_args(parser):
    parser.add_argument('-n', '--key', required=True,
                        type=str,
                        dest='key',
                        help="配置名",
                        )
    parser.add_argument('-v', '--value', required=True,
                        type=str,
                        dest='value',
                        help="配置值",
                        )
    parser.set_defaults(func=get)


def get(args):
    sbp = SBPConfigUtils(logger)
    sbp.set_config(args.key, args.value)
    logger.info(f"set {args.key} success")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="sbp 删除配置工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
