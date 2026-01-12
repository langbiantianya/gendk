import argparse
import sys

from interaction_uitils.logger import logger
from install_step.set_web_conf_step import SetWebConf

all_steps = [SetWebConf(logger)]
all_modules = [step.step_name.lower() for step in all_steps]


def parser_args(parser):
    parser.add_argument('-m', '--modules', required=False,
                        default=set(all_modules),
                        type=select_modules(),
                        dest='modules',
                        help=f"{','.join(all_modules)}\n指定要安装的模块，默认全部安装\n特殊参数 all .",
                        )
    parser.add_argument('-s', '--skip-modules', required=False, default=set(), dest='skipModules',
                        type=select_modules(),
                        help=f"{','.join(all_modules)}\n指定要跳过的模块，默认为空\n特殊参数 all .",
                        )
    for steps in all_steps:
        steps.add_argument(parser)
    parser.set_defaults(func=install)


def select_modules():
    """
    选择模块
    :return:
    """

    def func(input_list):
        res = set(input_list.lower().split(","))
        for r in res:
            if r not in all_modules and r != 'all':
                raise Exception(f"{r} no in {all_modules}")
        if "all" in res:
            res.remove("all")
            res.update(all_modules)
        return res

    return func


def install(args):
    logger.info("============================= sso install ==============================")
    modules = args.modules - args.skipModules
    for steps in all_steps:
        if steps.step_name.lower() in modules:
            try:
                logger.info(f"-------------------- step[{steps.step_name}] start")
                steps.execute(args)
                logger.info(f"-------------------- step[{steps.step_name}] success")
            except:
                logger.error(f"-------------------- step[{steps.step_name}] failed", exc_info=True)
                logger.error(f"详细日志见: {logger.log_file}")
                sys.exit(1)
    logger.info(f"详细日志见: {logger.log_file}")
    logger.info("======================== sso install succeeded =========================")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="sso 安装工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
