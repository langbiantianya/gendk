# -*- coding:utf-8 -*-
"""
@Time  : 2022/1/17 5:08 下午
@Author: zhaozhiqi@sensorsdata.cn
@File  : logger.py
"""
import logging
import logging.handlers
import os
from interaction_uitils.colour import ColouredFormatter
from interaction_uitils.config_util import module_name, product_name

sdd_log_path = os.getenv('SENSORS_DELIVER_DEV_LOG_DIR')
dk_path = os.getenv('DINGKAI_BASE')
common_path = os.path.split(os.path.split(os.path.realpath(__file__))[0])[0]


def get_logger(log_file):
    log = logging.getLogger(__name__)
    log.setLevel(logging.DEBUG)
    log.addHandler(get_colour_console_handler())
    log.addHandler(get_file_handler(log_file))
    return log


def get_console_handler():
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    console.setFormatter(logging.Formatter('%(asctime)s %(levelname)s: \n%(message)s\n'))
    return console


def get_colour_console_handler():
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    console.setFormatter(ColouredFormatter('%(asctime)s %(levelname)s: %(message)s'))
    return console


def get_file_handler(log_file):
    fa = logging.handlers.RotatingFileHandler(log_file, mode='a', maxBytes=5 * 1024 * 1024, backupCount=3)
    fa.setLevel(logging.DEBUG)
    file_format = logging.Formatter('%(asctime)s %(levelname)s: %(message)s')
    fa.setFormatter(file_format)
    return fa


def get_log_file(file_name):
    log_file = os.path.join(common_path, f'logs/{file_name}')
    if sdd_log_path:
        log_file = os.path.join(sdd_log_path, f'{module_name}/{file_name}')
    elif dk_path:
        log_file = os.path.join(dk_path, f'logs/{product_name}/{module_name}/{file_name}')
    log_path_dir = os.path.dirname(log_file)
    if not os.path.exists(log_path_dir):
        os.makedirs(log_path_dir)
    return log_file


class BaseLogger:
    def __init__(self):
        self.log_file = get_log_file("sso_install.log")
        self.logger = get_logger(self.log_file)

    def info(self, msg, *args, **kwargs):
        self.logger.info(msg, *args, **kwargs)

    def debug(self, msg, *args, **kwargs):
        self.logger.debug(msg, *args, **kwargs)

    def warning(self, msg, *args, **kwargs):
        self.logger.warning(msg, *args, **kwargs)

    def error(self, msg, *args, **kwargs):
        self.logger.error(msg, *args, **kwargs)

    def set_log_file(self, log_name, file_name):
        self.log_file = get_log_file(file_name)
        log = logging.getLogger(log_name)
        log.setLevel(logging.DEBUG)
        log.addHandler(get_file_handler(self.log_file))
        self.logger = log

    def set_log_colour_console(self, log_name):
        self.log_file = None
        log = logging.getLogger(log_name)
        log.setLevel(logging.DEBUG)
        log.addHandler(get_colour_console_handler())
        self.logger = log

    def set_log_console(self, log_name):
        self.log_file = None
        log = logging.getLogger(log_name)
        log.setLevel(logging.DEBUG)
        log.addHandler(get_console_handler())
        self.logger = log

    def set_log_console_file(self, log_name, file_name):
        self.log_file = get_log_file(file_name)
        log = logging.getLogger(log_name)
        log.setLevel(logging.DEBUG)
        log.addHandler(get_console_handler())
        log.addHandler(get_file_handler(self.log_file))
        self.logger = log

    def set_log_colour_console_file(self, log_name, file_name):
        self.log_file = get_log_file(file_name)
        log = logging.getLogger(log_name)
        log.setLevel(logging.DEBUG)
        log.addHandler(get_colour_console_handler())
        log.addHandler(get_file_handler(file_name))
        self.logger = log


logger = BaseLogger()
