package com.sensorsdata.client.model;

import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：条件参数
 * @date ：2024-12-05 11:28
 */
@Data
public class Condition {
  String field;
  String function;
  List<Object> params;
  String name;
}
