#!/usr/bin/env bash
shell_path=$(cd $(dirname "$0")/../ && pwd)
# 产品组件名称，根绝实际写
PRODUCT_NAME=$(basename $(dirname "$shell_path"))
# 模块名，根绝实际写
MODULE_NAME=$(basename "$shell_path")

log_path=${DINGKAI_BASE}/logs/${PRODUCT_NAME}/${MODULE_NAME}
nohup sh -x "${shell_path}"/bin/start_web.sh "${log_path}" >>"${log_path}"/service.out 2>>"${log_path}"/service.err </dev/null &
# sleep一下 避免某些场景后台进程退出
sleep 2
