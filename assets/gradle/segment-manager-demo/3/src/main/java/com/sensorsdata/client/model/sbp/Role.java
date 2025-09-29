package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：角色
 * @date ：2024-10-10 17:43
 */
@Data
public class Role {
  Integer id;
  String name;
  String cname;
  String description;
  @JsonProperty("create_user_name")
  String createUserName;
  @JsonProperty("create_time")
  String createTime;
  @JsonProperty("update_time")
  String updateTime;
  @JsonProperty("role_type")
  String roleType;
  @JsonProperty("type_name")
  String typeName;
  @JsonProperty("is_project_customize_default_role")
  boolean isProjectCustomizeDefaultRole;
}
