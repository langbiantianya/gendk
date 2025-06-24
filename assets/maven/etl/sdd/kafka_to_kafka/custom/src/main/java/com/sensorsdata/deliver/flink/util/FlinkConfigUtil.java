package com.sensorsdata.deliver.flink.util;


import com.sensorsdata.deliver.flink.common.util.ConfigUtil;
import com.sensorsdata.deliver.flink.common.util.JobConfLoadUtil;
import com.sensorsdata.deliver.flink.common.exceptions.JobConfloadException;
import com.sensorsdata.deliver.flink.parameter.JobParameters;
import lombok.experimental.UtilityClass;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 10:39
 */
@UtilityClass
public class FlinkConfigUtil {
  /**
   * 启动的参数，读取本地的配置文件，并且初始化类
   *
   * @param args 启动参数
   * @return job 的配置类
   * @throws FileNotFoundException
   */
  public JobParameters getParameter(String[] args) throws FileNotFoundException {
    String confFile = System.getenv("APP_CONF_FILE");
    if (confFile != null && !confFile.isEmpty()) {
      ConfigUtil.init(new File(confFile));
    } else {
      ParameterTool parameterTool = ParameterTool.fromArgs(args);
      if (parameterTool.has("conf")) {
        confFile = parameterTool.get("conf");
        // 初始化 config，读取配置文件
        ConfigUtil.init(new File(confFile));
      }
    }
    try {
      // 根据配置文件的内容，初始化 JobParameters 类
      return JobConfLoadUtil.loadConf(new JobParameters());
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      throw new JobConfloadException(e);
    }
  }
}
