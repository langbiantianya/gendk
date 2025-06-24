package com.sensorsdata.deliver.flink.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensorsdata.deliver.flink.common.annotation.JobConf;
import com.sensorsdata.deliver.flink.common.util.JsonUtil;
import com.sensorsdata.deliver.flink.common.exceptions.NotJsonException;
import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig.GlobalJobParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义配置类
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 18:36
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class JobParameters extends GlobalJobParameters {
  // 关闭算子聚合，用于测试
  @JobConf("conf.disable_operator_chaining")
  private boolean disableOperatorChaining = false;
  // check point 的间隔时间
  @JobConf("conf.checkpoint.interval")
  private long checkpointInterval = 3 * 60 * 1000L;
  //程序端口，用于探活
  @JobConf("conf.port")
  private int port = 10104;

  // kafka 数据源
  @JobConf("source.kafka")
  private KafkaConfig sourceKafkaConf;
  // kafka 输出
  @JobConf("sink.kafka")
  private KafkaConfig sinkKafkaConf;

  @JobConf("sink.fail.kafka")
  private KafkaConfig sinkFailKafkaConf;

  // 设置默认并行度
  @JobConf("parallelism.default")
  private int parallelismDefault = 10;
  // 设置 kafka 数据源并行度，如果不设置，则走默认配置
  @JobConf(value = "parallelism.source.kafka", copy = "parallelismDefault")
  private int parallelismKafkaSource;
  // 设置自定义算子并行度，如果不设置，则走默认配置
  @JobConf(value = "parallelism.process.custom", copy = "parallelismDefault")
  private int parallelismProcessCustom;
  // 设置 kafka 输出并行度，如果不设置，则走默认配置
  @JobConf(value = "parallelism.sink.kafka", copy = "parallelismDefault")
  private int parallelismKafkaSink;
  // 设置异常 kafka 输出并行度，如果不设置，则走默认配置
  @JobConf(value = "parallelism.sink.fail.kafka", copy = "parallelismDefault")
  private int parallelismFailKafkaSink;

  // 自定义的配置
  @JobConf("project.name")
  private String projectName;

  @JobConf("sink-topic")
  private Map<String,String> topicMap;

  @JobConf("filter.type")
  private String filterType;

  @Override
  public Map<String, String> toMap() {
    Map<String, Object> map = JsonUtil.fromBean(this, Map.class);
    Map<String, String> ret = new HashMap<>(map.size());

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      try {
        ret.put(entry.getKey(), JsonUtil.toJsonSting(entry.getValue()));
      } catch (JsonProcessingException e) {
        throw new NotJsonException(e.getMessage());
      }
    }
    return ret;
  }
}
