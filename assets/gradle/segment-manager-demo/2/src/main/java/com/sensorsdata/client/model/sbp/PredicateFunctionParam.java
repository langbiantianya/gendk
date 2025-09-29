package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：函数参数
 * @date ：2024-12-02 17:23
 */
@Data
public class PredicateFunctionParam {
  @JsonProperty("param_type")
  String paramType;
  String field;
  String variable;
  @JsonProperty("time_point")
  Object timePoint;
  Literal value;
  String expression;


}
