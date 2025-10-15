#!/bin/sh

# ${CI_PROJECT_NAME} 是建git仓库时项目名称
OUTPUT_DIR="output/${CI_PROJECT_NAME}"
#
# 如果没有 output/${CI_PROJECT_NAME} 文件夹 就建一个
if [ ! -d ${OUTPUT_DIR} ];then
    mkdir -p "${OUTPUT_DIR}"
fi

mkdir -p build
cp -r static build/
cp *.toml build
go mod tidy
CGO_ENABLED=0 go build -o build/server.exe
cp -r build/* dingkai/go_demo
rm -r build

cp -r dingkai/*  "${OUTPUT_DIR}/"
