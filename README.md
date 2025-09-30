# gendk

本工具适合神策交付(外包)使用，如果你不知道这个是干什么的，那么你大概率也用不到这个工具。

## 这个工具能干什么

帮你快速生成定开项目，减少和和 dingkai 工具斗智斗勇调整蓝图的时间。
用该工具生成的 gradle 项目可以放心大胆的不去管 jdk 版本，gradle 会自己处理好 jdk 版本，更可以直接使用 vscode 或者 trea 这些 ide 开箱即用。

## 如何使用 gitlab 的 ci

使用下面的共享 ci 配置文件并新建 release/xxx 分支

### gradle

deliver/deliver-package.yml@general-utilities/share-cicd

### maven

product/deliver.yml@general-utilities/share-cicd

## 关于java 17

sa版本是新版本且对于某些有漏洞扫描的客户无法通过升级依赖修复漏洞，或者单纯不想用java 1.8都可以使用。

### 查找jdk17并使用

1. 使用`which java`查看Java运行时的目录，再同级目录中有个java17的目录。
2. 修改`dingkai/dk_segment_push_shulex_manager/bin/run.sh`在`source /home/sa_cluster/.bash_profile`之后添加上一步获取到的路径设为`JAVA_HOME`并加上`export PATH=$JAVA_HOME/bin:$PATH`

## 关于koltin
多提供一种选择，用不用都行。

## 如何编译

### 必须的依

- golang 1.23.5+
- make

### 可选依赖

仅 docker web 需要

- docker

### web

#### powershell

```shell
$ENV:BUILD=1
make
```

### bash | zsh

```shell
export BUILD=1
make
```

### docker-web

```shell
docker build -t gendk-web .
```
