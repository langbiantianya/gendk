package com.sensorsdata.client.model;

import lombok.Data;

/**
 * @author ：wutengfei
 * @description：神策openapi请求失败对象
 * @date ：2024-10-08 15:58
 */
@Data
public class ErrorInfo {

  String errorType;

  String error;

  String errorDesc;

  String systemResponse;

  Integer httpStatus;
}
