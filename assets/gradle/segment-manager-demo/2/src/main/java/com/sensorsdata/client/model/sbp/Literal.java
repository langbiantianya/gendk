package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：函数参数数值
 * @date ：2024-12-02 17:27
 */
@Data
public class Literal {

  @JsonProperty("date_type")
  String dataType;

  @JsonProperty("bool_value")
  boolean boolValue;
  @JsonProperty("int_value")
  Integer intValue;
  @JsonProperty("number_value")
  Double numberValue;
  @JsonProperty("string_value")
  String stringValue;
  @JsonProperty("list_value")
  List<String> listValue;
  @JsonProperty("datetime_value")
  String datetimeValue;
  @JsonProperty("date_value")
  String dateValue;
  @JsonProperty("bigint_value")
  Long bigintValue;
  @JsonProperty("decimal_value")
  String decimalValue;
}
