#!/bin/sh
WEB_JVM_MEMORY=512
SPRING_OPTIONS="--spring.profiles.active=prod"
JAVA_OPTIONS="-Dservice"
LOG_FILE_NAME="service.log"


LOG_PATH="."
if [ $# -ge 1 ]; then
    LOG_PATH=$1
fi

HEAP_OPTIONS="-Xms${WEB_JVM_MEMORY}m -Xmx${WEB_JVM_MEMORY}m"
BASEDIR=$(cd $(dirname "$0")/../ && pwd)

LOG_OPTIONS="-Dsdd.root.logger=INFO,DRFA -Dsdd.custom.logger=INFO,DRFA -Dsdd.log.dir=${LOG_PATH} -Dsdd.log.file=${LOG_FILE_NAME}"

JAVA="java"

exec $JAVA -jar "${BASEDIR}/server.jar" "$SPRING_OPTIONS" "$JAVA_OPTIONS" "$LOG_OPTIONS" "$HEAP_OPTIONS" -XX:OnOutOfMemoryError="kill -9 %p"