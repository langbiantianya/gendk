package com.sensorsdata.client.model.sbp;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ：wutengfei
 * @description：新增角色请求入参
 * @date ：2024-10-11 14:18
 */
@Data
public class SaveRoleRequest {

  @NotNull(message = "role不能为空")
  RoleSaveBody role;
  @NotNull(message = "statements不能为空")
  List<Statement> statements;
}
