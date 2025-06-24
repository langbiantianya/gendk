package com.sensorsdata.deliver.flink.common.source;

import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import com.sensorsdata.deliver.flink.common.model.KafkaMessageData;
import com.sensorsdata.deliver.flink.common.source.schema.MyKafkaDeserializationSchema;
import lombok.experimental.UtilityClass;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Properties;

/**
 * kafka source，根据配置信息，生成 kafka source
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 10:14
 */
@UtilityClass
public class MyKafkaSource {

  /**
   * 获取 kafka 初始偏移量设置
   *
   * @param reset            最新或者最早
   * @param isCoveringOffset 是否覆盖掉消费者组的提交
   * @return 偏移量设置
   */
  private OffsetsInitializer getOffsetsInitializer(String reset, boolean isCoveringOffset) {
    OffsetsInitializer offsetsInitializer = null;
    if ("earliest".equals(reset)) {
      offsetsInitializer = OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
      if (isCoveringOffset) {
        offsetsInitializer = OffsetsInitializer.earliest();
      }
    } else if ("latest".equals(reset)) {
      offsetsInitializer = OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
      if (isCoveringOffset) {
        offsetsInitializer = OffsetsInitializer.latest();
      }
    }
    return offsetsInitializer;
  }

  public KafkaSource<KafkaMessageData> getFlinkKafkaSource(KafkaConfig config, Properties properties) {
    if (properties == null) {
      properties = new Properties();
    }
    // 设置 kafka 的地址
    if (config.getServers() != null) {
      properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getServers());
    }
    // 设置 kafka 的消费者组
    if (config.getGroup() != null) {
      properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, config.getGroup());
    }
    // 设置 kafka 的初始的偏移量
    OffsetsInitializer offsetsInitializer = getOffsetsInitializer(config.getReset(), config.isCoveringOffset());

    // 如果有些自定义的配置，可以直接加在里面
    if (config.getProperties() != null) {
      properties.putAll(config.getProperties());
    }

    // 创建 kafka 的 source
    return KafkaSource.<KafkaMessageData>builder()
        .setTopics(config.getTopic())
        .setStartingOffsets(offsetsInitializer)
        .setDeserializer(KafkaRecordDeserializationSchema.of(new MyKafkaDeserializationSchema()))
        .setProperties(properties)
        .build();
  }

  public KafkaSource<KafkaMessageData> getFlinkKafkaSource(KafkaConfig config) {
    return getFlinkKafkaSource(config, null);
  }
}
