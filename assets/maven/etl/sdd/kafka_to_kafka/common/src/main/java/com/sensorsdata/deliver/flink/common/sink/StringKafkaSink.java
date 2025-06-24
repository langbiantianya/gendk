package com.sensorsdata.deliver.flink.common.sink;

import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import lombok.experimental.UtilityClass;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import java.util.Properties;

/**
 * String 数据输出到 kafka 的工具类。根据配置信息，生成 kafka sink
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2023/3/13 10:50
 */
@UtilityClass
public class StringKafkaSink {

  public KafkaSink<String> getFlinkKafkaSink(KafkaConfig config, Properties properties) {
    if (properties == null) {
      properties = new Properties();
    }

    if (config.getProperties() != null) {
      properties.putAll(config.getProperties());
    }
    KafkaSink<String> kafkaSink;
    // 如果不是 event 数据，就没必要用 event 序列化
    kafkaSink = KafkaSink.<String>builder()
        .setBootstrapServers(config.getServers())
        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
            .setTopic(config.getTopic().get(0))
            .setValueSerializationSchema(new SimpleStringSchema())
            .build())
        .setKafkaProducerConfig(properties)
        // 异常重启数据可能会重复，可以设置为 EXACTLY_ONCE，但是kafka 消费者要配置 isolation.level ，并且强烈建议将 Kafka 的事务超时时间调整至远大于 checkpoint 最大间隔 + 最大重启时间，否则 Kafka 对未提交事务的过期处理会导致数据丢失。
        .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
        .build();

    return kafkaSink;
  }

  public KafkaSink<String> getFlinkKafkaSink(KafkaConfig config) {
    return getFlinkKafkaSink(config, null);
  }
}
