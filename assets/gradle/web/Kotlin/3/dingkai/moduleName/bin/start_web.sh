#!/bin/sh
WEB_JVM_MEMORY=512
SPRING_OPTIONS="--spring.profiles.active=prod"
JAVA_OPTIONS="-D{{.ModuleName}}_server"
LOG_FILE_NAME="service.log"


LOG_PATH="."
if [ $# -ge 1 ]; then
    LOG_PATH=$1
fi

HEAP_OPTIONS="-Xms${WEB_JVM_MEMORY}m -Xmx${WEB_JVM_MEMORY}m"
BASEDIR=$(cd $(dirname "$0")/../ && pwd)

LOG_OPTIONS="-Dsdd.root.logger=INFO,DRFA -Dsdd.custom.logger=INFO,DRFA -Dsdd.log.dir=${LOG_PATH} -Dsdd.log.file=${LOG_FILE_NAME}"

# 提取主版本号
# 处理不同格式，如1.8.0_301、17.0.1、11.0.12等
if [[ $java_version == 1.* ]]; then
    # 处理1.x格式
    major_version=$(echo "$java_version" | cut -d '.' -f 2)
else
    # 处理10+格式
    major_version=$(echo "$java_version" | cut -d '.' -f 1)
fi

# 比较版本号
if [[ $major_version -ge 17 ]]; then
    echo "当前Java版本为 $java_version，满足大于等于JDK 17的要求"
    JAVA="java"
else
    echo "当前Java版本为 $java_version，不满足大于等于JDK 17的要求"
    # 设置jdk17环境变量如果报错请手动修改
    export JAVA_HOME=/sensorsdata/main/program/armada/jdk17/jdk17
    JAVA="${JAVA_HOME}/bin/java"
fi

exec $JAVA -jar "${BASEDIR}/server.jar" "$SPRING_OPTIONS" "$JAVA_OPTIONS" "$LOG_OPTIONS" "$HEAP_OPTIONS" -XX:OnOutOfMemoryError="kill -9 %p"
