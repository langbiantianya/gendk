# {{.ProjectName}}

## 如何使用

1. 将nginx配置文件放在/sensorsdata/main/program/sp/nginx/conf/web_locations下面
2. 重启nginx
3. 使用get 请求 `/{{.Redirect}}?redirectUrl=xxxsx&ssoToken=xxxx`

## 用途

当客户用某种奇怪的方式自己做单点登入时可用

## 原理

通过nginx重定向可以将外部获取到的token设置到系统域名下的cookie中来设置登入状态。
