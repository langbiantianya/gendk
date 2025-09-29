package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：用户对象
 * @date ：2024-10-10 17:38
 */
@Data
public class Account {
  Integer id;
  String uuid;
  String name;
  String cname;
  String email;
  String phone;
  @JsonProperty("expire_time")
  String expireTime;
  @JsonProperty("is_global")
  Boolean isGlobal;
  Boolean disabled;
  String position;
  @JsonProperty("create_time")
  String creatTime;
}
