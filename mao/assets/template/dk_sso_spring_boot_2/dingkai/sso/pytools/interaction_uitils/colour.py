#!/bin/env python
# -*- coding: UTF-8 -*-

"""
Copyright (c) 2015 SensorsData, Inc. All Rights Reserved
@author padme(jinsilan@sensorsdata.cn)
@brief

打印出有颜色的输出
python2/3兼容
"""
from __future__ import print_function  # 兼容print
import logging
import sys
import datetime


class colour:
    """
    打印颜色
    """
    BLUE = '\033[94m'
    GREEN = '\033[92m'
    DARK_GREEN = '\033[36m'
    RED = '\033[91m'
    YELLOW = '\033[93m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


BACK_ONE_LINE = "\033[F"
CLEAR_ONE_LINE = "\033[K"


def __print(header, msg, tee):
    print(header + msg + colour.ENDC)
    if tee:
        tee(msg + '\n')


def print_green(msg, tee=None):
    __print(colour.GREEN, msg, tee)


def print_blue(msg, tee=None):
    __print(colour.BLUE, msg, tee)


def print_red(msg, tee=None):
    __print(colour.RED, msg, tee)


def print_yellow(msg, tee=None):
    __print(colour.YELLOW, msg, tee)


class ColouredFormatter(logging.Formatter):
    COLOR_TO_LEVEL = {
        'DEBUG': colour.BLUE,
        'INFO': colour.GREEN,
        'WARNING': colour.YELLOW,
        'CRITICAL': colour.YELLOW,
        'ERROR': colour.RED,
    }

    def __init__(self, msg):
        logging.Formatter.__init__(self, msg)

    def format(self, record):
        levelname = record.levelname
        levelname_color = ColouredFormatter.COLOR_TO_LEVEL[levelname.upper()] + levelname + colour.ENDC
        record.levelname = levelname_color
        ret = logging.Formatter.format(self, record)
        record.levelname = levelname
        return ret


class MultiLineLogger:
    """建议多行进度条"""
    flags = ['|', '-']  # 滚动效果

    def __init__(self, time_colour=colour.GREEN, process_name_color=colour.DARK_GREEN):
        self.flag_index = 0
        self.time_colour = time_colour
        self.process_name_color = process_name_color
        self.is_first = True

    def log(self, title, process_dict):
        if not self.is_first:
            # 清理之前的输出
            sys.stdout.write(BACK_ONE_LINE * (len(process_dict) + 1))
            sys.stdout.flush()
        else:
            self.is_first = False
        # 更新滚动效果
        self.flag_index ^= 1

        # 第一行：打印时间（带颜色） + title
        sys.stdout.write('\r' + CLEAR_ONE_LINE + self.time_colour + '[' + datetime.datetime.now().strftime(
            '%Y-%m-%d %H:%M:%S') + ']' + colour.ENDC + ' ' + title + ' [' + self.flags[self.flag_index] + ']\n')
        # 对每行打印 带颜色的进程名 + 不带颜色的内容
        for process_name in sorted(process_dict.keys()):
            process_log = process_dict[process_name]
            sys.stdout.write(
                '\r' + CLEAR_ONE_LINE + self.process_name_color + '[' + process_name + '] ' + colour.ENDC + process_log + '\n')
        sys.stdout.flush()


if __name__ == '__main__':
    import time

    p = MultiLineLogger()
    for i in range(10):
        title = 'test test'
        d = {'client %d' % j: 'xxx %d' % (j + 1) for j in range(3)}
        p.log(title, d)
        time.sleep(1)
