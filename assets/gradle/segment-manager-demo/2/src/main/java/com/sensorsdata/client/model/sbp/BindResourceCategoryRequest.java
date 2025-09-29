package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sensorsdata.client.enums.ResourceGroup;
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
public class BindResourceCategoryRequest {
  @JsonProperty("category_code")
  @NotNull(message = "categoryCode不能为空")
  String categoryCode;

  @NotNull(message = "resourceCode不能为空")
  @JsonProperty("resource_code")
  String resourceCode;

  @NotNull(message = "group不能为空")
  ResourceGroup group;
}
