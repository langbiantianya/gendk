from argparse import ArgumentParser

from interaction_uitils.logger import BaseLogger
from interaction_uitils.config_util import get_file_path, get_tmp_file, module_name, this_time


class BaseStep:
    def __init__(self, step_name, log: BaseLogger):
        self.log = log
        self.step_name = step_name
        self.module_name = module_name
        self.install_time = this_time

    def add_argument(self, parser):
        """
        添加自定义参数
        :param parser: 解析参数的对象
        :return:
        """

    def execute(self, args):
        """
        处理逻辑
        :param args: 参数
        :return:
        """
        raise NotImplementedError("子类必须实现此方法")

    def _get_real_path(self, path):
        """
        通过相对路径，转换成真实路径
        :param path:
        :return:
        """
        return get_file_path(path)

    def _get_tmp_file(self, file_path):
        """
        通过相对路径，获取真实的临时文件及路径
        :param file_path:
        :return:
        """
        return get_tmp_file(file_path)
