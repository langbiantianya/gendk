package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author ：wutengfei
 * @description：注册资源位请求
 * @date ：2024-10-11 15:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCategoryRequest {
  @NotNull(message = "code不能为空")
  String code;

  @JsonProperty("name_i18n_message")
  @NotNull(message = "nameI18nMessage不能为空")
  I18nMessage nameI18nMessage;

  Integer sort;
}
