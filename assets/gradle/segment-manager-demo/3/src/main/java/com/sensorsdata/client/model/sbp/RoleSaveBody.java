package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：新增角色请求实体
 * @date ：2024-10-11 14:19
 */
@Data
public class RoleSaveBody {

  Integer id;
  String name;
  String cname;
  String description;
  @JsonProperty("is_project_customize_default_role")
  boolean isProjectCustomizeDefaultRole;

}
