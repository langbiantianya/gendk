# gendk

如果你不知道这个是干什么的，那么你大概率也用不到这个工具。

## 这个工具能干什么

帮你快速生成定开项目，减少和和 dingkai 工具斗智斗勇调整蓝图的时间。

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

### native

#### windows

```shell
make windows
```

#### linux

```shell
make linux
```

#### android

```shell
make android
```

#### freebsd

```shell
make freebsd
```
