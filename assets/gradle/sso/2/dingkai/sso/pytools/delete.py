import argparse
import sys

from interaction_uitils.logger import logger
from interaction_uitils.base import BaseStep
from interaction_uitils.sbp_util import SBPConfigUtils


class DeleteWebStep(BaseStep):
    def __init__(self, log):
        super().__init__("webConf", log)

    def execute(self, args):
        """
        删除 web conf 配置
        """
        sbp = SBPConfigUtils(self.log)
        if not sbp.del_config("login_user_info_api"):
            sbp.set_config("login_user_info_api", "")


all_steps = [DeleteWebStep(logger)]
all_modules = [step.step_name.lower() for step in all_steps]


def parser_args(parser):
    parser.add_argument('-m', '--modules', required=False,
                        default=set(all_modules),
                        type=select_modules(),
                        dest='modules',
                        help=f"{','.join(all_modules)}\n指定要删除的操作，默认为 all\n特殊参数 all .",
                        )
    for steps in all_steps:
        steps.add_argument(parser)
    parser.set_defaults(func=delete)


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


def delete(args):
    logger.info("============================= sso delete ==============================")
    modules = args.modules
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
    logger.info("======================== sso delete succeeded =========================")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="sso 卸载工具", formatter_class=argparse.RawTextHelpFormatter)
    parser_args(parser)
    args = parser.parse_args()
    args.func(args)
