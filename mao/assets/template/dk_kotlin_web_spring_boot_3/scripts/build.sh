#!/bin/sh

# ${CI_PROJECT_NAME} 是建git仓库时项目名称
OUTPUT_DIR="output/${CI_PROJECT_NAME}"
#
# 如果没有 output/${CI_PROJECT_NAME} 文件夹 就建一个
if [ ! -d ${OUTPUT_DIR} ];then
    mkdir -p "${OUTPUT_DIR}"
fi
echo $(java --version)
chmod +x ./gradlew && ./gradlew clean && ./gradlew build

cp -r dingkai/*  "${OUTPUT_DIR}/"

