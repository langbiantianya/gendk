# {{.ProjectName}}

## 说明

[[SDH] 分群推送插件开发指南](https://doc.sensorsdata.cn/pages/viewpage.action?pageId=600646486)

## 部署

### 安装

``` shell
horizonadmin segment_plugin offline -m INSTALL -p $(pwd)/{{.ProjectName}}.jar
```

### 更新

```shell
horizonadmin segment_plugin offline -m UPDATE -p  $(pwd)/{{.ProjectName}}.jar
```

### 重启 `horizon` 模块

需要重启 web 和 jobscheduler 才会生效

```shell
aradmin restart -p horizon -m web
aradmin restart -p horizon -m jobscheduler
```
