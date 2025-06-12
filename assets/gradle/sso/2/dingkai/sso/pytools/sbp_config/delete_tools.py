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
    parser.set_defaults(func=delete)


def delete(args):
    sbp = SBPConfigUtils(logger)
    value = sbp.del_config(args.key)
    if value:
        logger.info(f"delete {args.key} success")
    else:
        logger.error("Please use set to set the default value！")
        sys.exit(1)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="sbp 删除配置工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
