package com.sensorsdata.deliver.flink.common.exceptions;

/**
 * json 解析失败的自定义异常类
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/12/1 18:37
 */
public class NotJsonException extends RuntimeException{
  public NotJsonException(String msg){
    super(msg);
  }
}
