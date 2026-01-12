# mao

本模块为实际的模板渲染引擎，借助webassembly在浏览器中提供支持，也可编译为可执行文件在其他地方使用。

## todo

- [x] 基础模板渲染
- [ ] 提供接口给js调用
- [ ] 文件以二进制的方式保存而不是文本

## js如何传递数据到引擎

### json定义

```json
{
    "name":"模板名称",
    "data":{
        "在rules中定义的key":{
            "type":"string",
            "value":"在rules中定义的key对应的value"
        }
    }
}
```
