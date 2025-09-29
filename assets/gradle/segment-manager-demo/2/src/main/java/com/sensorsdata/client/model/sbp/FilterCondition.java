package com.sensorsdata.client.model.sbp;

import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：TODO
 * @date ：2024-12-02 17:22
 */
@Data
public class FilterCondition {

  String function;
  List<PredicateFunctionParam> params;
  Integer index;

}
