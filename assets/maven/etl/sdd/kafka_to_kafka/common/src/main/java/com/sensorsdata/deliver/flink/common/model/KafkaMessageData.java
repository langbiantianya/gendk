package com.sensorsdata.deliver.flink.common.model;

import lombok.*;

/**
 * 消费到的 kafka 数据信息
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 10:30
 */
@Data
@ToString
@AllArgsConstructor
@Builder
public class KafkaMessageData {
  private String value;
  private String key;
  private long offset;
  private int partition;
  private String topic;
  private long timestamp;
}