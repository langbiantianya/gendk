package com.sensorsdata.deliver.flink.common.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * kafka 配置信息的 model 类
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 10:16
 */
@Data
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KafkaConfig implements Serializable {
  private String servers;
  private List<String> topic;
  private String group;
  /* 消费 kafka 从什么位置开始 */
  private String reset = "earliest";
  /* 是否不走 group 提交的偏移量 */
  private boolean coveringOffset = false;
  /* 一些自定义的 kafka 配置信息 */
  private Map<String, String> properties;
}
