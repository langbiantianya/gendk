## 部署

### 首次

```shell
dingkai install -p {{.ProjectName}} -f {{.ModuleName}}.tar --default
```

### 更新

```shell
dingkai install -p {{.ProjectName}} -f {{.ModuleName}}.tar --default --reinstall
```

## 注册资源树

1. 第一步

```shell
curl --location 'http://xxx:8107/api/v2/sbp/resources/register?token=%247f61a95a684725d4f8049b0fcf629706' \
--header 'Content-Type: application/json' \
--header 'Cookie: sbp_web=dea4c0efc6dd60a844d5b5ad2f840ab2' \
--data '{
    "code": "GROUP_SHULEX_PUSH_MANAGEMENT",
    "product": "SBP",
    "name_i18n_message": {
        "zh_cn": "Shulex 分群推送管理",
        "en_us": "Shulex push management",
        "zh_tw": "Shulex 分群推送管理"
    },
    "tip_i18n_message": {
        "zh_cn": "Shulex 分群推送管理",
        "en_us": "Shulex push management",
        "zh_tw": "Shulex 分群推送管理"
    },
    "config": "string",
    "operations": [
        {
            "name_i18n_message": {
                "zh_cn": "Shulex 通道",
                "en_us": "Shulex Channel",
                "zh_tw": "Shulex 頻道"
            },
            "code": 98206,
            "type": "BASE_PRINCIPAL",
            "desc_i18n_message": {
                "zh_cn": "可使用「全部」通道账号",
                "en_us": "All channel accounts can be used",
                "zh_tw": "可使用「全部」頻道帳號"
            },
            "tip_i18n_message": {
                "zh_cn": "可使用「全部」通道账号",
                "en_us": "All channel accounts can be used",
                "zh_tw": "可使用「全部」頻道帳號"
            },
            "config": "{\"related_info\":[\"event_filter\",\"profile_filter\",\"access_data\"],\"operation_key\":\"1\"}",
            "checkable": true,
            "visible": true,
            "disable": false,
            "module_name": "BASIC_MODULE",
            "sort": 3
        }
    ]
}'
```

2. 第二步

```shell
curl --location 'http://xxx:8107/api/v3/portal/v2/identity/resource/category/bind' \
--header 'api-key: #K-Dwnx65TTeMixzveBUhuT8ZyMswkfbMfc' \
--header 'Content-Type: application/json' \
--header 'Cookie: sbp_web=dea4c0efc6dd60a844d5b5ad2f840ab2' \
--data '{
    "group": "FEATURE_PERMISSION",
    "category_code": "feature_horizon_entity_model",
    "resource_code": "GROUP_SHULEX_PUSH_MANAGEMENT"
}'
```
