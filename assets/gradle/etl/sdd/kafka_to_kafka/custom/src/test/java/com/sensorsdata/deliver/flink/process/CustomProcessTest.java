package com.sensorsdata.deliver.flink.process;

import com.sensorsdata.deliver.flink.common.model.KafkaMessageData;
import com.sensorsdata.deliver.flink.common.util.JobConfLoadUtil;
import com.sensorsdata.deliver.flink.parameter.JobParameters;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.OutputTag;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2023/1/10 23:42
 */
@Slf4j
public class CustomProcessTest {
  OneInputStreamOperatorTestHarness<KafkaMessageData, Tuple2<String, String>> testHarness;
  OutputTag<String> abnormalOutputTag = new OutputTag<String>("abnormal-data") {
  };

  @Before
  public void before() throws Exception {
    JobParameters jobParameters = JobConfLoadUtil.loadConf(new JobParameters());
    CustomProcess customProcess = new CustomProcess(abnormalOutputTag);
    testHarness = new OneInputStreamOperatorTestHarness<>(new ProcessOperator<>(customProcess), 1, 1, 0);
    testHarness.getExecutionConfig().setGlobalJobParameters(jobParameters);
    testHarness.setup();
    testHarness.open();
  }

  @Test
  public void testProcessElement() throws Exception {
    testHarness.processElement(getKafkaMessageData(), System.currentTimeMillis());
    log.info("out is : {}", testHarness.extractOutputValues());
  }

  @Test
  public void testAbnormalOutput() throws Exception {
    testHarness.processElement(getErrorKafkaMessageData(), System.currentTimeMillis());
    ConcurrentLinkedQueue<StreamRecord<String>> sideOutput = testHarness.getSideOutput(abnormalOutputTag);
    log.info("error out is : {}", sideOutput);
  }

  private KafkaMessageData getKafkaMessageData() {
    return KafkaMessageData.builder()
        .key("test")
        .offset(12345)
        .partition(1)
        .timestamp(System.currentTimeMillis())
        .topic("event_topic")
        .value("{\"type\":\"track\",\"time\":1679908090399,\"distinct_id\":\"test1\",\"properties\":{\"$lib\":\"python\",\"$lib_version\":\"1.10.3\",\"MDEVICEID\":\"TD_ID\",\"MTMESSAGES_APPEVENTS_ID\":\"TD_EVENT_ID\",\"page_class_name\":\"title_name_1\",\"$title\":\"title_name_2\",\"ADVERTISINGID\":\"1|mac|||tdid|IDFA||app project name\",\"DEVICENAME\":\"\",\"$ip\":\"172.30.80.191\",\"$latitude\":120.0,\"$longitude\":23.0,\"MAPPVERSIONCODE\":\"1.0.0\",\"MAPPVERSIONNAME\":\"version_name\",\"$country\":\"中国\",\"MDEVEPLOPERAPPKEY\":\"TD_id\",\"$manufacturer\":\"man\",\"$model\":\"version\",\"$os\":\"\",\"$os_version\":\"\",\"MPARTNERID\":\"\",\"$timezone_offset\":400,\"MTMESSAGES_APPEVENTS_NAME\":\"\",\"$is_login_id\":true,\"$city\":\"保留IP\",\"$province\":\"保留IP\"},\"lib\":{\"$lib\":\"iOS\",\"$lib_version\":\"1.10.3\",\"$lib_method\":\"code\",\"$lib_detail\":\"####/Users/lucretia/Documents/Company/Project/浦发银行项目/sa-sdk-python-master/sensorsanalytics/test.py##66\"},\"event\":\"TD\",\"login_id\":\"test1\",\"is_login_id\":true,\"map_id\":\"test1\",\"user_id\":34391036787,\"recv_time\":1679908090524,\"extractor\":{\"f\":\"sdf_input_topic\",\"o\":326889,\"n\":\"sdf_input_topic\",\"s\":326891,\"c\":326891,\"p\":1,\"e\":\"hybrid01.classic-tx-beijing-01.org-sep-1882dp1.deploy.sensorsdata.cloud\"},\"project_id\":1,\"project\":\"default\",\"ver\":2}")
        .build();
  }

  private KafkaMessageData getErrorKafkaMessageData(){
    return KafkaMessageData.builder()
        .key("test")
        .offset(12345)
        .partition(1)
        .timestamp(System.currentTimeMillis())
        .topic("test_topic")
        .value("{\"deviceInfo\":{\"isRoot\":\"0\",\"display\":\"width:1080height:2110\",\"ip\":\"114.242.250.15\",\"gps\":\"116.234243,39.2434344\",\"uuid\":\"947dbed577103710bae6fff6a6999557\",\"mac\":\"CE:14:BD:BO:50:76\",\"platform\":\"android\",\"uuidOldVersion\":\"27d5a30d3cc4f665\",\"osVersion\":\"10\",\"model\":\"Xiaomi M2003J15SC\",\"sdk\":\"29\",\"brand\":\"Redmi\",\"board\":\"merlin\"},\"tokenInfo\":{\"appVersion\":\"8.0.0\",\"custNo\":\"2109265748429430784\",\"versionNum\":\"90\",\"reaTime\":\"20220401151228\",\"appChannel\":\"voubank\",\"reqMsgId\":\"20220401010010151782690212112840\",\"token\":\"d2fdeaf2255248dca118c23a4951a90d\"},\"data\":{\"accNo\":\"6221881000000063966 \"},\"code\":\"000000\",\"msg\":\"交易成功\",\"reqTime\":\"20220401151228\",\"resTime\":\"20220401151228\",\"transCode\":\"T000002\"")
        .build();
  }
}