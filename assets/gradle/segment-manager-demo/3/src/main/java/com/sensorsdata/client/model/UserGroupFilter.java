package com.sensorsdata.client.model;

import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：用户分群过滤器
 * @date ：2024-12-06 14:50
 */
@Data
public class UserGroupFilter {

  String relation;

  List<Condition> conditions;

}
