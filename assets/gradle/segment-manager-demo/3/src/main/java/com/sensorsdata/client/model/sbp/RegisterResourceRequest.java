package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ：wutengfei
 * @description：注册资源位请求
 * @date ：2024-10-11 15:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResourceRequest {
  @NotNull(message = "code不能为空")
  String code;
  /**
   * 所属产品线 SA，SFN等*
   */
  @NotNull(message = "product不能为空")
  String product;

  @JsonProperty("name_i18n_message")
  @NotNull(message = "nameI18nMessage不能为空")
  I18nMessage nameI18nMessage;

  @JsonProperty("tip_i18n_message")
  I18nMessage tipI18nMessage;

  String config;

  @NotNull(message = "operations不能为空")
  List<Operation> operations;

}
