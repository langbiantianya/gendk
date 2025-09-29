package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：过滤条件
 * @date ：2024-10-11 11:23
 */
@Data
public class Condition {
  String operator;
  List<FilterCondition> conditions;
  @JsonProperty("compound_conditions")
  Object compoundConditions;
  Integer index;
}
