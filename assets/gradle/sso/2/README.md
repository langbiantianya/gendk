# {{.ProjectName}}

## tips

1. idp metadata 地址客户提供
2. entityId 自己编，生成 metadata 提供给客户他们注册
3. [`sensorsKeystore.jks`](src/main/resources/saml/sensorsKeystore.jks)需要使用jdk生成，然后导入客户给的密钥
4. idp_metadata.xml 和 sp_metadata.xml 请放在`resources/saml`下面
5. 自己在 sp 的 nginx 配置里面加个配置代理 sp

```nginx
location /saml/metadata.xml {
     alias /sensorsdata/main/dingkai/program/dk_sso/sso/conf/saml/idp_metadata.xml;
     try_files $uri =404;
     expires 30d;
     access_log off;
}
```

### 坑：

1. idp 侧的 metadata 中 IDPSSODescriptor 元素下缺失 WantAuthnRequestsSigned =true
   ，导致发送 saml 请求的时候不加密，下载到本地后手动添加 WantAuthnRequestsSigned=true 后，
   上传到服务器，用 nginx 代理到文件读取 url 为 https://aimashiec.cloud.sensorsdata.cn/saml/metadata.xml
2. saml response 中本应该是 Assertion 元素，但是传递来的是 EncryptedAssertion，opensaml 版本不适配，适配成当前程序的版本。

### 生成 sensorsKeystore.jks 并导入密钥

```shell
keytool -genkeypair -alias tempkey -keyalg RSA -keystore sensorsKeystore.jks -keysize 2048 -sigalg SHA256withRSA  -storepass '2wsx#EDC' -keypass '3edc$RFV' -validity 1825

keytool -import -alias sensorsKey -file SensorsDataWebAnalytics-RAW.cer  -keystore sensorsKeystore.jks
```

### 导出证书 cer

```shell
keytool -exportcert -alias sensorsKey -file sensors.crt -keystore sensorsKeystore.jks
```

### 查看证书内容

```shell
keytool -list -v -keystore sensorsKeystore.jks -storepass '2wsx#EDC'

```

## 部署

### 部署前配置

#### 配置sa默认登录页面

```shell
sbpadmin business_config set -p sbp -k front_auto_logout_config -v "{'redirect_type': 'URL','redirect_value' : 'https://客户域名/api/sso/login'}"
```

#### 配置 userinfo 地址（固定配置，无需修改）

```shell
sbpadmin business_config -a set -p sbp -k login_user_info_api -v  "http://127.0.0.1:8112/sso/userinfo"
```

#### 自动创建账号

##### 开启

```shell
sbpadmin business_config set -p sbp -k enable_oauth_auto_create_user -v true
```

##### 关闭

```shell
sbpadmin business_config set -p sbp -k enable_oauth_auto_create_user -v false
```

### 首次

```shell
dingkai install -p {{.ProjectName}} -f sso.tar --default

```

### 更新

```shell
dingkai install -p {{.ProjectName}} -f sso.tar --default --reinstall
```