package com.sensorsdata.analytics.sso.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2023/5/6 15:16
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginApiRequest {
  private String accountName;
  private String password;

  @Override
  public String toString() {
    return "LoginApiRequest(" +
            "accountName=" + accountName + ", " +
            ", password=******)";
  }
}
