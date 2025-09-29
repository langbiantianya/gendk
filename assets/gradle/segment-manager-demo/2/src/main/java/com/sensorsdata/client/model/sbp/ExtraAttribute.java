package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：扩展属性值
 * @date ：2024-10-10 18:18
 */
@Data
public class ExtraAttribute {

  @JsonProperty("attr_code")
  String attrCode;
  @JsonProperty("node_code")
  String nodeCode;
  @JsonProperty("node_name")
  String nodeName;
  @JsonProperty("full_name")
  String fullName;
  @JsonProperty("has_children_node")
  boolean hasChildrenNode;
  String desc;

}
