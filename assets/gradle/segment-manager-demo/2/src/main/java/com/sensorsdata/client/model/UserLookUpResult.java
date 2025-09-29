package com.sensorsdata.client.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ：wutengfei
 * @description：用户细查返回结果
 * @date ：2024-12-05 16:58
 */
@Data
public class UserLookUpResult {

  List<String> columnName;

  List<String> permittedProperties;

  String reportUpdateTime;

  Integer pageNum;

  Integer size;

  List<User> users;

  @Data
  public class User{

    String id;
    String firstId;
    String secondId;
    Map<String, Object> profiles;

  }
}
