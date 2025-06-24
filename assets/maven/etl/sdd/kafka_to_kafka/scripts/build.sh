#!/bin/sh
# ${CI_PROJECT_NAME} 是建git仓库时项目名称
OUTPUT_DIR=output/${CI_PROJECT_NAME}

# 如果没有 output/${CI_PROJECT_NAME} 文件夹 就建一个
if [[ ! -d ${OUTPUT_DIR} ]];then
    mkdir -p ${OUTPUT_DIR}
fi
cp -r app/* "${OUTPUT_DIR}"
if [ ! -d "${OUTPUT_DIR}/lib" ]; then
    mkdir -p "${OUTPUT_DIR}/lib"
fi
mvn clean package -DskipTests
cp custom/target/custom-*-SNAPSHOT.jar "${OUTPUT_DIR}/lib"
if [ ! -d "${OUTPUT_DIR}/conf" ]; then
    mkdir -p "${OUTPUT_DIR}/conf"
fi
cp custom/src/main/resources/config*  "${OUTPUT_DIR}/conf/"
# 开发者自己编写
