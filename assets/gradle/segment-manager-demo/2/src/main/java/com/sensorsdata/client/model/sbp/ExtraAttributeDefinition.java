package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：扩展属性值
 * @date ：2024-10-10 18:18
 */
@Data
public class ExtraAttributeDefinition {
  Integer id;
  String code;
  String name;
  String type;
  String desc;
  @JsonProperty("attr_value_scope")
  boolean attrValueScope;

}
