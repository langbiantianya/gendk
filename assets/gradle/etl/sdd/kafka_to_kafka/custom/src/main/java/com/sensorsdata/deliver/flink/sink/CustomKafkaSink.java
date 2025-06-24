package com.sensorsdata.deliver.flink.sink;

import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import com.sensorsdata.deliver.flink.common.util.JsonUtil;
import com.sensorsdata.deliver.flink.sink.schema.ETLKafkaSinkSchema;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import java.util.Properties;

/**
 * TODO
 *
 * @author lucretia
 * @version 1.0.0
 * @since 2023/03/23 17:28
 */
@Slf4j
@UtilityClass
public class CustomKafkaSink {

  public static KafkaSink<Tuple2<String, String>> toKafkaDataStream(KafkaConfig config) throws Exception {
    Properties properties = new Properties();
    if (config.getProperties() != null) {
      properties.putAll(config.getProperties());
    }

    log.info("kafka conf is {}", JsonUtil.toJsonSting(properties));

    return KafkaSink.<Tuple2<String, String>>builder()
        .setBootstrapServers(config.getServers())
        .setRecordSerializer(new ETLKafkaSinkSchema())
        .setKafkaProducerConfig(properties)
        // 异常重启数据可能会重复，可以设置为 EXACTLY_ONCE，但是kafka 消费者要配置 isolation.level ，并且强烈建议将 Kafka 的事务超时时间调整至远大于 checkpoint 最大间隔 + 最大重启时间，否则 Kafka 对未提交事务的过期处理会导致数据丢失。
        .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
        .build();
  }
}
