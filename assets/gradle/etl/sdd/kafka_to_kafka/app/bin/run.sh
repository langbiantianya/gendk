#!/bin/sh
# 产品线名称，固定为 sdd
PRODUCT_NAME=sdd
# 模块名，写你 git 仓库名
MODULE_NAME={{.ProjectName}}
# 监控用的进程关键字
PROCESS_KEYWORD="proc_${PRODUCT_NAME}_${MODULE_NAME}"
# 启动类，写你程序启动类
MAIN_CLASS=com.sensorsdata.deliver.flink.FlinkStreamApp
# 配置文件
CONF_FILE=${SENSORS_DELIVER_DEV_HOME}/${MODULE_NAME}/conf/config.yaml
#jaas配置文件
JAAS_CONF_FILE=${SENSORS_DELIVER_DEV_HOME}/${MODULE_NAME}/conf/kafka_client_jaas.conf

export JVM_ARGS="$JVM_ARGS -D${PROCESS_KEYWORD}"
export FLINK_ENV_JAVA_OPTS_CLI='-Xmx256m'
export APP_CONF_FILE="${CONF_FILE}"
export APP_JAAS_CONF_FILE="${JAAS_CONF_FILE}"

hdfs dfs -test -e /sa/flink/${PRODUCT_NAME}/${MODULE_NAME}/savepoints
if [ $? -ne 0 ]; then
  hdfs dfs -mkdir -p /sa/flink/${PRODUCT_NAME}/${MODULE_NAME}/savepoints
fi
hdfs dfs -test -e /sa/flink/${PRODUCT_NAME}/${MODULE_NAME}/checkpoints
if [ $? -ne 0 ]; then
  hdfs dfs -mkdir -p /sa/flink/${PRODUCT_NAME}/${MODULE_NAME}/checkpoints
fi

bin=$(dirname "$0")

cd $bin/ && jar xf ../lib/*.jar flink-app-control && cd -
chmod +x $bin/flink-app-control

# 1.1.11 版本开始可通过 APP_FLINK_HOME 配置 flink 包位置。
exec env APP_FLINK_HOME=${SENSORS_DELIVER_DEV_HOME}/flink \
  $bin/flink-app-control \
  run -p "${PRODUCT_NAME}" \
  -m "${MODULE_NAME}" \
  -c "${MAIN_CLASS}" >>"${SENSORS_DELIVER_DEV_HOME}/${MODULE_NAME}/${MODULE_NAME}.out" \
  2>>"${SENSORS_DELIVER_DEV_HOME}/${MODULE_NAME}/${MODULE_NAME}.err" &
# sleep一下 避免某些场景后台进程退出
sleep 1