package com.sensorsdata.client.model.sbp;

import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：获取角色详情返回结果
 * @date ：2024-10-11 11:28
 */
@Data
public class GetRoleResult {

  Role role;
  List<Statement> statements;

}
