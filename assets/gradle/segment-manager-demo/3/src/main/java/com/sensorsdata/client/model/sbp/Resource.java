package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sensorsdata.client.enums.ConditionType;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：数据资源
 * @date ：2024-10-11 15:49
 */
@Data
public class Resource {

  @JsonProperty("operation_code")
  String operationCode;

  String code;
  @JsonProperty("name_i18n_message")
  I18nMessage nameI18nMessage;
  @JsonProperty("desc_i18n_message")
  I18nMessage descI18nMessage;
  @JsonProperty("deny_desc_i18n_message")
  I18nMessage denyDescI18nMessage;

  @JsonProperty("condition_type")
  ConditionType conditionType;
  @JsonProperty("is_default")
  boolean isDefault;
  String api;
  @JsonProperty("display_column")
  String displayColumn;
  @JsonProperty("value_column")
  String valueColumn;
  String config;
}
