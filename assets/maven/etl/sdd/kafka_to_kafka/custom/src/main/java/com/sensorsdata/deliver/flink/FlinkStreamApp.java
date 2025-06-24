package com.sensorsdata.deliver.flink;

import com.sensorsdata.airline.guidance.flink.GuidanceFlinkConfigFactory;
import com.sensorsdata.deliver.flink.common.model.KafkaMessageData;
import com.sensorsdata.deliver.flink.common.sink.StringKafkaSink;
import com.sensorsdata.deliver.flink.common.source.MyKafkaSource;
import com.sensorsdata.deliver.flink.parameter.JobParameters;
import com.sensorsdata.deliver.flink.process.CustomProcess;
import com.sensorsdata.deliver.flink.sink.CustomKafkaSink;
import com.sensorsdata.deliver.flink.util.FlinkConfigUtil;
import com.sensorsdata.platform.flink.common.SensorsFlinkJobOwnerInfo;
import com.sensorsdata.platform.flink.common.SensorsFlinkStreamApp;
import com.sensorsdata.platform.flink.job.JobManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;

/**
 * 一个基于 Flink 流处理模块的开发例子
 *
 * @author cfreely
 * @version 1.0.0
 * @since 2020/07/26 23:29
 */
@Slf4j
public class FlinkStreamApp extends SensorsFlinkStreamApp {
  private static JobParameters jobParameters;

  public static void main(String[] args) throws Exception {
    jobParameters = FlinkConfigUtil.getParameter(args);
//    if (!isIdeaEnv()) {
//      JobManager.init(GuidanceFlinkConfigFactory.create("ResourceOwner", "ResourceName"),
//          SensorsFlinkJobOwnerInfo.Builder.builder()
//              .product(System.getenv("FLINK_APP_PRODUCT_NAME"))
//              .module(System.getenv("FLINK_APP_MODULE_NAME")).build());
//      // 用于 Monitor 探活的端口，每个模块需要不一样，提前找云平台注册
//      // 如果在 IDEA 中运行会自动启用本地调试模式(目前 break）
//      // killExistApplication 为 true，这样在启动之前会先删除 yarn 上正在运行的同名任务。避免上次退出有没有清理干净/卡死的任务。
//      new FlinkStreamApp().runApp(jobParameters.getPort(), true);
//    } else {
      // idea 本地调试运行，走这里
      StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
      new FlinkStreamApp().configureApp(env);
      env.execute();
//    }
  }

  /**
   * 提交 flink 任务的设置，可以在这里设置自己的 flink 执行流程
   *
   * @param env 上线文
   * @throws Exception
   */
  @Override
  public void configureApp(StreamExecutionEnvironment env) throws Exception {
    // 设置 flink 环境参数
    setFlinkEnv(env);

    KafkaSource<KafkaMessageData> flinkKafkaSource = MyKafkaSource.getFlinkKafkaSource(jobParameters.getSourceKafkaConf());

    // 设置数据源（kafka）
    DataStream<KafkaMessageData> dataKafkaSource = env.fromSource(flinkKafkaSource, WatermarkStrategy.noWatermarks(), "dataKafkaSource")
        .uid("dataKafkaSource").setParallelism(jobParameters.getParallelismKafkaSource());


    // 这需要是一个匿名的内部类，以便我们分析类型
    final OutputTag<String> abnormalOutputTag = new OutputTag<String>("abnormal-data") {
    };

    // 处理数据
    SingleOutputStreamOperator<Tuple2<String, String>> customProcess = dataKafkaSource.process(new CustomProcess(abnormalOutputTag))
        .name("customProcess").uid("customProcess")
        .setParallelism(jobParameters.getParallelismProcessCustom());

    // 异常数据输出流
    customProcess.getSideOutput(abnormalOutputTag).sinkTo(StringKafkaSink.getFlinkKafkaSink(jobParameters.getSinkFailKafkaConf()))
        .name("sinkFailKafka").uid("sinkFailKafka")
        .setParallelism(jobParameters.getParallelismFailKafkaSink());

    // 设置输出 （kafka）
    customProcess.sinkTo(CustomKafkaSink.toKafkaDataStream(jobParameters.getSinkKafkaConf()))
        .name("sinkKafka").uid("sinkKafka")
        .setParallelism(jobParameters.getParallelismKafkaSink());

  }

  /**
   * 设置 flink 的参数，可以看看官方文档提供的参数，结合自己需求配置
   *
   * @param env 上下文
   */
  private static void setFlinkEnv(StreamExecutionEnvironment env) {
    // 开启 checkpoint，这种情况下 Kafka 的订阅进度会保存在 Checkpoint 里
    env.enableCheckpointing(jobParameters.getCheckpointInterval());
    if (jobParameters.isDisableOperatorChaining()) {
      // 关闭算子合并，排查问题可以用
      env.disableOperatorChaining();
    }
    // 设置全局的配置，算子可以在 open 方法里拿到这个配置
    env.getConfig().setGlobalJobParameters(jobParameters);
  }

  private static boolean isIdeaEnv() {
    return System.getProperty("java.class.path").contains("idea_rt.jar");
  }
}
