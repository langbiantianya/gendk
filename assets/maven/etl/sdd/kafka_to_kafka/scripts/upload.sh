#!/bin/sh
#
# Copyright (c) 2018 SensorsData, Inc. All Rights Reserved
# @file upload.sh
# @date 2020-07-15
# @author padme(mailto: jinsilan@sensorsdata.cn)
# @description:
# 这个文件直接拷贝到新环境上 不可修改

export RSYNC_PASSWORD="eD32I9"
RELEASE_SERVER="10.1.1.247:28123"
RSYNC_PREFIX="deliver@10.1.1.247::deliver"


CUSTOMER_NAME=$1
MODULE_NAME=${CI_PROJECT_NAME}
COMMIT=`git log -n 1 --format='%H %aI'`
MAJOR_VERSION=`echo $CI_COMMIT_BRANCH | awk -F '/' '{print $2}'`
VERSION=${MAJOR_VERSION}.${CI_JOB_ID}
OUTPUT_DIR=output/${MODULE_NAME}


# 必须先将要打包的东西放到output/<模块名>目录下
if not test -d "${OUTPUT_DIR}"
then
    echo 'output dir does not exists!'
    exit 1
fi


cat << EOF > ${OUTPUT_DIR}/version_info.yml
customer_name: ${CUSTOMER_NAME}
commit: ${COMMIT}
build_time: `date`
version: ${VERSION}
build_by: ${GITLAB_USER_LOGIN}
EOF

cd output
tar czf ${MODULE_NAME}.tar ${MODULE_NAME}
cd -

MD5=`md5sum ${OUTPUT_DIR}.tar | awk '{print $1}'`

# 上传
# ['customer_name', 'module_name', 'version', 'build_by', 'build_url', 'build_by', 'md5','package_file_name']
cat << EOF > ./post_params
{
"customer_name": "${CUSTOMER_NAME}",
"module_name": "${MODULE_NAME}",
"version": "${VERSION}",
"build_by": "${GITLAB_USER_LOGIN}",
"build_url": "${CI_PIPELINE_URL}",
"md5": "${MD5}",
"package_file_name": "${MODULE_NAME}.tar"
}
EOF
# 发给release server新建一个条目
OUTPUT=`curl -d@./post_params "http://$RELEASE_SERVER/api/deliver_dev_package/new"`
RSYNC_PATH=`python -c "a=$OUTPUT;print(a['package_path'])"`
ID=`python -c "a=$OUTPUT;print(a['id'])"`
# 上传
rsync -vz ${OUTPUT_DIR}.tar ${RSYNC_PREFIX}/${RSYNC_PATH}
# 标记上传成功
curl -d '{"status": "SUCCEED"}' "http://${RELEASE_SERVER}/api/deliver_dev_package/${ID}"

# 输出下载链接
echo "dowload url: http://download.sensorsdata.cn/release/deliver/${RSYNC_PATH}"


# vim:expandtab shiftwidth=4 softtabstop=4
# 新制品库
curl -f -v -u ${NEXUS_USER}:${NEXUS_PASSWD} --upload-file ${OUTPUT_DIR}.tar https://nexus-deliver-internal.sensorsdata.cn/repository/deliver-test/${CUSTOMER_NAME}/${MODULE_NAME}/${VERSION}/${MODULE_NAME}.tar