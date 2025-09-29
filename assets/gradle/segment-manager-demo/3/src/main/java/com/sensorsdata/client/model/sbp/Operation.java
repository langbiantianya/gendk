package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sensorsdata.client.enums.OperationType;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：操作权限
 * @date ：2024-10-11 15:39
 */
@Data
public class Operation {
  String code;
  OperationType type;

  @JsonProperty("name_i18n_message")
  I18nMessage nameI18nMessage;
  @JsonProperty("tip_i18n_message")
  I18nMessage tipI18nMessage;
  @JsonProperty("desc_i18n_message")
  I18nMessage descI18nMessage;

  String config;
  boolean checkable;
  boolean disable;
  @JsonProperty("module_name")
  String moduleName;
  Integer sort;

  @JsonProperty("data_resources")
  List<Resource> dataResources;

}
