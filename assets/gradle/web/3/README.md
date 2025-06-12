## 部署

### 首次

```shell
dingkai install -p {{.ModuleName}} -f {{.ModuleName}}.tar --default
```

### 更新

```shell
dingkai install -p {{.ModuleName}} -f {{.ModuleName}}.tar --default --reinstall
```