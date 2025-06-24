package com.sensorsdata.deliver.flink.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sensorsdata.deliver.flink.common.exceptions.NotJsonException;
import com.sensorsdata.deliver.flink.common.model.KafkaMessageData;
import com.sensorsdata.deliver.flink.common.util.JsonUtil;
import com.sensorsdata.deliver.flink.parameter.JobParameters;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 自定义处理类，一个 demo
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 21:04
 */
@Slf4j
public class CustomProcess extends ProcessFunction<KafkaMessageData, Tuple2<String, String>> {
  private transient JobParameters jobParameters;
  private final OutputTag<String> abnormalOutputTag;

  public CustomProcess(OutputTag<String> outputTag) {
    this.abnormalOutputTag = outputTag;
  }

  /**
   * 算子启动时，会先调用这个方法
   *
   * @param parameters 参数
   */
  @Override
  public void open(Configuration parameters) {
    jobParameters = (JobParameters) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
  }

  @Override
  @SneakyThrows
  public void processElement(KafkaMessageData kafkaMessageData, ProcessFunction<KafkaMessageData, Tuple2<String, String>>.Context context, Collector<Tuple2<String, String>> collector) {
//   这里将全部处理逻辑去掉直接发送
    try {
      JsonNode jsonMessage = getJsonMessage(kafkaMessageData);
      if (!jsonMessage.isEmpty()
          && jsonMessage.hasNonNull("type")
          && !jobParameters.getFilterType().equals(jsonMessage.get("type").asText())) {
        // 不做任何处理，直接转发原始数据
        sendMsg(kafkaMessageData, collector);
      } else if (!jsonMessage.isEmpty()
          && !jsonMessage.hasNonNull("type")) {
        // 不做任何处理，直接转发原始数据
        sendMsg(kafkaMessageData, collector);
      }
    } catch (JsonProcessingException | NotJsonException e) {
      log.error("data process exception, data is [{}] .", kafkaMessageData, e);
      // 不做任何处理，直接转发原始数据
      sendMsg(kafkaMessageData, collector);
    } catch (Exception e) {
      log.error("data process exception, data is [{}] .", kafkaMessageData, e);
      throw e;
    }
  }

  private void sendMsg(KafkaMessageData kafkaMessageData, Collector<Tuple2<String, String>> collector) {
    String topic = jobParameters.getTopicMap().get(kafkaMessageData.getTopic());
    if (topic != null) {
      // 确保 value 字段为 String 类型（即使为空也显式赋值空字符串）
      String value = kafkaMessageData.getValue() != null ? kafkaMessageData.getValue() : "";
      // 显式指定 Tuple2<String, String> 类型
      collector.collect(new Tuple2<>(topic, value));
    }
  }

  /**
   * json 解析，把 string 转换成 json
   *
   * @param kafkaMessageData string 数据
   * @return 结果数据
   */
  private JsonNode getJsonMessage(KafkaMessageData kafkaMessageData) throws JsonProcessingException {
    JsonNode jsonNode = JsonUtil.toJsonNode(kafkaMessageData.getValue());
    // 如果 jsonNode 只是一个值，或者为空，则说明这不是一个 json
    if (jsonNode.isValueNode() || jsonNode.isMissingNode()) {
      log.warn("data is not json, data is [{}]", jsonNode);
      throw new NotJsonException("data is not json");
    }
    return jsonNode;
  }

}



