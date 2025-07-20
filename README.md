# gendk

如果你不知道这个是干什么的，那么你大概率也用不到这个工具。

## 这个工具能干什么

帮你快速生成定开项目，减少和和 dingkai 工具斗智斗勇调整蓝图的时间。
用该工具生成的 gradle 项目可以放心大胆的不去管 jdk 版本，gradle 会自己处理好 jdk 版本，更可以直接使用 vscode 或者 trea 这些 ide 开箱即用。

## 如何使用 gitlab 的 ci

使用下面的共享 ci 配置文件并新建 release/xxx 分支

### gradle

deliver/deliver-package.yml@general-utilities/share-cicd

### maven

product/deliver.yml@general-utilities/share-cicd

## 如何编译

### 必须的依

- golang 1.23.5+
- make

#### linux

以 debian12 为例必须以下依赖

- libgl1-mesa-dev
- xorg-dev
- mesa-utils

### 可选依赖

仅 native 与 docker web 需要

- docker

### web

#### powershell

```shell
$ENV:BUILD=1
$ENV:WEB=1
make web
```

### bash | zsh

```shell
export BUILD=1
export WEB=1
make web
```

### docker-web

```shell
docker build -t gendk-web .
```

### ~~native~~

#### ~~windows~~

```shell
make windows
```

#### ~~linux~~

```shell
make linux
```

#### ~~android~~

```shell
make android
```

#### ~~freebsd~~

```shell
make freebsd
```
