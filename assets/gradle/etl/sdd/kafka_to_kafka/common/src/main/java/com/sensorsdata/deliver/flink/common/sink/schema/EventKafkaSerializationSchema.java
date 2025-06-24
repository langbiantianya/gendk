package com.sensorsdata.deliver.flink.common.sink.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sensorsdata.deliver.flink.common.util.JsonUtil;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * 自定义 kafka 序列化类，用于处理 event 数据
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 20:12
 */
public class EventKafkaSerializationSchema implements KafkaRecordSerializationSchema<JsonNode> {
  private final String topic;

  public EventKafkaSerializationSchema(String topic) {
    this.topic = topic;
  }

  /**
   * 自定义序列化方式，将 json 转成 string，并将用户 id 作为 key，用于分区，如果 distinct_id 没有值，直接取时间戳做 key
   * Serializes given element and returns it as a {@link ProducerRecord}.
   *
   * @param element   element to be serialized
   * @param context   context to possibly determine target partition
   * @param timestamp timestamp
   * @return Kafka {@link ProducerRecord}
   */
  @Override
  public ProducerRecord<byte[], byte[]> serialize(JsonNode element, KafkaSinkContext context, Long timestamp) {
    JsonNode distinctId = JsonUtil.getJsonValue("distinct_id", element);
    String key = null;
    if (distinctId != null) {
      String s = distinctId.asText("");
      if (!s.isEmpty()) {
        key = s;
      }
    }
    String value = element.toString();
    if (key != null) {
      return new ProducerRecord<>(this.topic, key.getBytes(), value.getBytes());
    } else {
      return new ProducerRecord<>(this.topic, value.getBytes());
    }
  }
}
