package com.sensorsdata.deliver.flink.common.sink;

import com.fasterxml.jackson.databind.JsonNode;
import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import com.sensorsdata.deliver.flink.common.sink.schema.EventKafkaSerializationSchema;
import lombok.experimental.UtilityClass;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * JsonNode 格式的 kafka sink，根据配置信息，生成 kafka sink
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 19:50
 */
@UtilityClass
public class EventKafkaSink {

  /**
   * 获取 kafka 的 sink
   *
   * @param config     kafka 配置信息
   * @param properties 其他的 kafka 配置信息
   * @param isSetKey   是否要给数据设置 key，根据数据中的 distinct_id 设置为 key
   * @return kafka 的 sink
   */
  public KafkaSink<JsonNode> getFlinkKafkaSink(KafkaConfig config, Properties properties, boolean isSetKey) {
    if (properties == null) {
      properties = new Properties();
    }

    if (config.getProperties() != null) {
      properties.putAll(config.getProperties());
    }
    KafkaSink<JsonNode> kafkaSink;
    if (isSetKey) {
      kafkaSink = KafkaSink.<JsonNode>builder()
          .setBootstrapServers(config.getServers())
          .setRecordSerializer(new EventKafkaSerializationSchema(config.getTopic().get(0)))
          .setKafkaProducerConfig(properties)
          // 异常重启数据可能会重复，可以设置为 EXACTLY_ONCE，但是kafka 消费者要配置 isolation.level ，并且强烈建议将 Kafka 的事务超时时间调整至远大于 checkpoint 最大间隔 + 最大重启时间，否则 Kafka 对未提交事务的过期处理会导致数据丢失。
          .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
          .build();
    } else {
      // 如果不是 event 数据，就没必要用 event 序列化
      kafkaSink = KafkaSink.<JsonNode>builder()
          .setBootstrapServers(config.getServers())
          .setRecordSerializer(KafkaRecordSerializationSchema.builder()
              .setTopic(config.getTopic().get(0))
              .setValueSerializationSchema((SerializationSchema<JsonNode>) jsonNode -> jsonNode.asText("").getBytes(StandardCharsets.UTF_8))
              .build())
          .setKafkaProducerConfig(properties)
          // 异常重启数据可能会重复，可以设置为 EXACTLY_ONCE，但是kafka 消费者要配置 isolation.level ，并且强烈建议将 Kafka 的事务超时时间调整至远大于 checkpoint 最大间隔 + 最大重启时间，否则 Kafka 对未提交事务的过期处理会导致数据丢失。
          .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
          .build();
    }
    return kafkaSink;
  }

  public KafkaSink<JsonNode> getFlinkKafkaSink(KafkaConfig config) {
    return getFlinkKafkaSink(config, null, true);
  }
}
