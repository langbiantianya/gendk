package com.sensorsdata.client.model.sbp;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author ：wutengfei
 * @description：角色权限配置
 * @date ：2024-10-11 11:22
 */
@Data
public class Statement {
  @NotNull(message = "operation不能为空")
  String operation;
  @NotNull(message = "effect不能为空")
  String effect;
  Condition condition;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Statement statement = (Statement) o;
    return Objects.equals(operation, statement.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operation);
  }
}
