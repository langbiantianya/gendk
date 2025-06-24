package com.sensorsdata.deliver.flink.common.source.schema;

import com.sensorsdata.deliver.flink.common.model.KafkaMessageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

/**
 * 自定义 kafka 数据的 schema 信息，把数据的元数据信息也拿到
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 10:50
 */
@Slf4j
public class MyKafkaDeserializationSchema implements KafkaDeserializationSchema<KafkaMessageData> {
  /*是否流结束，比如读到一个key为end的字符串结束，这里不再判断，直接返回false 不结束*/
  @Override
  public boolean isEndOfStream(KafkaMessageData kafkaMessageData) {
    return false;
  }

  /**
   * 序列化消费到的 kafka 数据
   *
   * @param consumerRecord kafka 数据信息
   * @return 序列化好的数据
   */
  @Override
  public KafkaMessageData deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) {
    String value = byteToString(consumerRecord.value());
    int partition = consumerRecord.partition();
    long offset = consumerRecord.offset();
    String topic = consumerRecord.topic();
    String key = byteToString(consumerRecord.key());
    long timestamp = consumerRecord.timestamp();
    return new KafkaMessageData(value, key, offset, partition, topic, timestamp);
  }

  private String byteToString(byte[] b) {
    if (b == null) return "";
    return new String(b, StandardCharsets.UTF_8);
  }

  /*用于获取反序列化对象的类型*/
  @Override
  public TypeInformation<KafkaMessageData> getProducedType() {
    return TypeInformation.of(new TypeHint<KafkaMessageData>() {
    });
  }
}
