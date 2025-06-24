package com.sensorsdata.deliver.flink.common.exceptions;

/**
 * 读取配置文件报错的自定义异常类
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 14:44
 */
public class JobConfloadException extends RuntimeException {
  public JobConfloadException(Exception e) {
    super(e);
  }

  public JobConfloadException(String key) {
    super(String.format("conf [%s] not load!", key));
  }
}
