#!/bin/sh

BASEDIR=$(cd $(dirname "$0")/../ && pwd)

export PROFILE_ACTIVE="prod"

# 定义应用名称作为进程关键字
APP_NAME="{{.ModuleName}}_server"
# 二进制文件路径

BIN_PATH="${BASEDIR}/server.exe"


# 检查二进制文件是否存在
if [ ! -f "$BIN_PATH" ]; then
    echo "错误：二进制文件 $BIN_PATH 不存在"
    exit 1
fi

# 启动应用，通过环境变量和命令行参数添加标识
exec $BIN_PATH -app="$APP_NAME" APP_KEYWORD="$APP_NAME"
