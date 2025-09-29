package com.sensorsdata.client.model;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sensorsdata.constant.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：wutengfei
 * @description：神策openapi通用返回对象
 * @date ：2024-10-08 15:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorsApiSimpleResponse<T> {
  /**
   * 返回码，成功：SUCCESS 失败见：https://manual.sensorsdata.cn/sa/2.4/zh_cn/%E9%94%99%E8%AF%AF%E7%A0%81%E8%AF%B4%E6%98%8E-111247453.html*
   */
  String code;

  /**
   * 返回对象实体*
   */
  T data;

  /**
   * 相应码*
   */
  String requestId;

  /**
   * 返回失败对象*
   */
  ErrorInfo errorInfo;

  public static <T> SensorsApiSimpleResponse<T> sbpError(String errorInfo) {
    final JSONObject errorObject = JSONUtil.parseObj(errorInfo);
    SensorsApiSimpleResponse<T> sensorsApiSimpleResponse = new SensorsApiSimpleResponse<T>();
    ErrorInfo resultErrorInfo = new ErrorInfo();
    resultErrorInfo.setError(errorObject.getStr("description"));
    resultErrorInfo.setErrorDesc(errorObject.getJSONObject("context").getStr("error_extend_desc"));
    resultErrorInfo.setSystemResponse(errorObject.getStr("system_response"));
    sensorsApiSimpleResponse.setCode(errorObject.getStr("code"));
    sensorsApiSimpleResponse.setErrorInfo(resultErrorInfo);
    sensorsApiSimpleResponse.setRequestId(errorObject.getJSONObject("context").getStr("request_id"));
    return sensorsApiSimpleResponse;
  }

  public static <T> SensorsApiSimpleResponse<T> saError(String errorInfo) {
    final JSONObject errorObject = JSONUtil.parseObj(errorInfo);
    SensorsApiSimpleResponse<T> sensorsApiSimpleResponse = new SensorsApiSimpleResponse<T>();
    ErrorInfo resultErrorInfo = new ErrorInfo();
    resultErrorInfo.setError(errorObject.getStr("description"));
    resultErrorInfo.setErrorDesc(errorObject.getJSONObject("error_causes").getStr("error_cause"));
    resultErrorInfo.setSystemResponse(errorObject.getStr("system_response"));
    sensorsApiSimpleResponse.setCode(errorObject.getStr("code"));
    sensorsApiSimpleResponse.setErrorInfo(resultErrorInfo);
    sensorsApiSimpleResponse.setRequestId(errorObject.getJSONObject("context").getStr("request_id"));
    return sensorsApiSimpleResponse;
  }

  public static <T> SensorsApiSimpleResponse<T> ok(String requestId, String code, T data) {
    SensorsApiSimpleResponse<T> sensorsApiSimpleResponse = new SensorsApiSimpleResponse<>();
    sensorsApiSimpleResponse.setRequestId(requestId);
    sensorsApiSimpleResponse.setCode(code);
    sensorsApiSimpleResponse.setData(data);
    return sensorsApiSimpleResponse;
  }

  public boolean isOk(){
    return CommonConstants.SUCCESS.equals(this.getCode());
  }

}
