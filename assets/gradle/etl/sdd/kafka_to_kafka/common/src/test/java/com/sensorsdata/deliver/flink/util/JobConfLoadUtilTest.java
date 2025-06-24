package com.sensorsdata.deliver.flink.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensorsdata.deliver.flink.common.annotation.JobConf;
import com.sensorsdata.deliver.flink.common.exceptions.NotJsonException;
import com.sensorsdata.deliver.flink.common.model.KafkaConfig;
import com.sensorsdata.deliver.flink.common.util.JobConfLoadUtil;
import com.sensorsdata.deliver.flink.common.util.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.flink.api.common.ExecutionConfig;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/30 16:34
 */
public class JobConfLoadUtilTest {

  @Test
  public void testLoad() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, JsonProcessingException {
    JobParametersTest jobParameters = JobConfLoadUtil.loadConf(new JobParametersTest());
    Map<String, String> stringStringMap = jobParameters.toMap();
    System.out.println(JsonUtil.toJsonSting(stringStringMap));
    // 测试 bean 类型是否能成功转换，并且设置对应的值
    Assert.assertEquals("event_import_stream_new_buried_point", jobParameters.getSourceKafkaConf().getGroup());
    // 测试 copy 参数能否复制对应的值
    Assert.assertEquals(1, jobParameters.getParallelismKafkaSink());
    // 测试配置设置对应的值之后，不 copy
    Assert.assertEquals(2, jobParameters.getParallelismKafkaSource());
    // 测试配置 key 中带点，能否正常拿到配置
    Assert.assertEquals("test", jobParameters.getFilterEventName());
    // 测试 boolean 类型数据能否正常拿到配置
    Assert.assertTrue(jobParameters.isDisableOperatorChaining());
    // 测试 boolean 类型数据能否正常复制数据
    Assert.assertTrue(jobParameters.isDisableOperatorChaining());
    // 测试蛇形配置是否成功
    Assert.assertTrue(jobParameters.getSourceKafkaConf().isCoveringOffset());
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public class JobParametersTest extends ExecutionConfig.GlobalJobParameters {
    @JobConf("source.kafka")
    private KafkaConfig sourceKafkaConf;
    @JobConf("sink.kafka")
    private KafkaConfig sinkKafkaConf;

    @JobConf("parallelism.default")
    private int parallelismDefault = 10;
    @JobConf(value = "parallelism.source.kafka", copy = "parallelismDefault")
    private int parallelismKafkaSource;
    @JobConf(value = "parallelism.process.to_json", copy = "parallelismDefault")
    private int parallelismProcessToJson;
    @JobConf(value = "parallelism.process.custom", copy = "parallelismDefault")
    private int parallelismProcessCustom;
    @JobConf(value = "parallelism.sink.kafka", copy = "parallelismDefault")
    private int parallelismKafkaSink;

    @JobConf("filter.event.name")
    private String filterEventName;


    @JobConf("conf.disable_operator_chaining")
    private boolean disableOperatorChaining = false;
    @JobConf(value = "conf.skip_dirty_data", copy = "disableOperatorChaining")
    private boolean skipDirtyData = true;
    @JobConf("conf.checkpoint.interval")
    private long checkpointInterval = 3 * 60 * 1000L;
    @JobConf("conf.port")
    private int port = 10104;

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
}
