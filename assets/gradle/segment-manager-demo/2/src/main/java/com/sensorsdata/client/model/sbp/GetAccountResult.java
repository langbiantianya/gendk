package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：获取账号详情
 * @date ：2024-10-10 17:37
 */
@Data
public class GetAccountResult {

  Account account;

  @JsonProperty("related_roles")
  List<Role> relatedRoles;

  @JsonProperty("related_extra_attributes")
  List<ExtraAttribute> relatedExtraAttributes;

  @JsonProperty("extra_attribute_definitions")
  List<ExtraAttributeDefinition> extraAttributeDefinitions;

}
