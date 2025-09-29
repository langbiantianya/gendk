package com.sensorsdata.client.model;

import com.sensorsdata.client.enums.FilterRelation;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：过滤器
 * @date ：2024-12-05 11:12
 */
@Data
public class Filter {
  FilterRelation relation;

  List<Condition> conditions;

  List<Filter> filters;

}
