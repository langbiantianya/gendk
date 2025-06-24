# {{.ProjectName}}

实时数据导入 demo

## 使用文档
- [开发说明](https://doc.sensorsdata.cn/pages/viewpage.action?pageId=337746168)
- [部署说明](https://doc.sensorsdata.cn/pages/viewpage.action?pageId=337746174)

## 目录介绍
- app: 线上的程序目录
- scripts: 打包脚本
- common: 工具项目，一般不需要改动
- custom: 自定义程序项目，开发着这里写逻辑

## custom 程序文件介绍
```
├── FlinkStreamApp.java                         # 程序启动类，flink 程序的处理流程在这里定义
├── parameter                                   # 自定义的 flink 配置
│   └── JobParameters.java                          # 配置类
├── process                                     # 自定义的数据转换算子放在这个目录下
│   └── CustomProcess.java                          # 自定义的算子
└── util                                        # 工具类目录
    └── FlinkConfigUtil.java                        # 读启动参数的工具类
```

## common 程序文件介绍
```
├── annotation                                  # 自定义的注解目录
│   └── JobConf.java                                # 读配置的注解
├── exceptions                                  # 自定义的异常类目录
│   ├── JobConfloadException.java                   # 解析配置遇到异常
│   └── NotJsonException.java                       # json 不合法的异常
├── model                                       # 自定义的 model
│   ├── KafkaConfig.java                            # kafka 配置
│   └── KafkaMessageData.java                       # 消费到的 kafka 数据
├── sink                                        # 自定义 sink 目录
│   ├── EventKafkaSink.java                         # 事件 kafka sink
│   ├── StringKafkaSink.java                        # 字符串类型的 kafka sink
│   └── schema
│       └── EventKafkaSerializationSchema.java          # 自定义的 kafka schema
├── source                                      # 自定义 source 目录 
│   ├── MyKafkaSource.java                          # kafka source
│   └── schema
│       └── MyKafkaDeserializationSchema.java           # 自定义的 kafka schema
└── util                                        # 工具类目录
    ├── ConfigUtil.java                             # 读配置的工具类
    ├── JobConfLoadUtil.java                        # 初始化配置信息的工具类
    └── JsonUtil.java                               # josn 处理的工具类

```

## 线上的程序的程序文件介绍
```
├── bin                     # 脚本目录
│   └── run.sh                  # 启动脚本
├── lib                     # 程序包目录
│   └── xxxx.jar                # jar 包，可以有多个
├── conf                    # 配置文件目录
│   ├── flink-app-conf.yaml     # flink 的配置文件，配置内容可以看 flink 官网
│   ├── logback-cli.xml         # 本地客户的日志配置
│   └── logback.xml             # flink 程序的日志配置
└── stream_import.yml       # 模块监控配置
```

## 常用命令
```shell
# 创建 kafka topic
kafka-topics --zookeeper 127.0.0.1:2181 --create --topic flink_event_data --partitions 10 --replication-factor 2
# 查看 topic 详情
kafka-topics --zookeeper 127.0.0.1:2181 --describe --topic flink_event_data
# 查看所有的 topic
kafka-topics --zookeeper 127.0.0.1:2181 --list
```
```shell
# 查看 kafka 的连接信息
spadmin config get client -m kafka -p sp -n broker_list
# 消费 kafka 数据
/sensorsdata/main/program/sp/sdp/current/kafka-broker/bin/kafka-console-consumer.sh --bootstrap-server hybrid02:9092,hybrid03:9092,hybrid01:9092 --topic youbank_events_data
# 生产 kafka 数据
/sensorsdata/main/program/sp/sdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list hybrid02:9092,hybrid03:9092,hybrid01:9092 --topic youbank_interface_log_data
```