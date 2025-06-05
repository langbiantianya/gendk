#!/usr/bin/env bash
log_path=${DINGKAI_BASE}/logs/{{.ProjectName}}/{{.ModuleName}}
shell_path=${DINGKAI_BASE}/program/{{.ProjectName}}/{{.ModuleName}}
source /home/sa_cluster/.bash_profile
nohup sh -x ${shell_path}/bin/start_web.sh ${log_path} >>${log_path}/start.out 2>>${log_path}/start.err </dev/null &
# sleep一下 避免某些场景后台进程退出
sleep 10s