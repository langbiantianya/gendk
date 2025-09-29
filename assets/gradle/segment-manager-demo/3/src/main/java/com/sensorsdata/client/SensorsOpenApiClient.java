package com.sensorsdata.client;

import cn.hutool.core.lang.Assert;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：wutengfei
 * @description：神策openapi通用client
 * @date ：2024-10-08 15:54
 */
@Data
public abstract class SensorsOpenApiClient {

  /**
   * 神策域名*
   */
  String sensorsDomain;

  /**
   * 神策api key*
   */
  String apiKey;

  /**
   * 神策项目名称*
   */
  String sensorsProject;

  /**
   * 神策租户id*
   */
  String organizationId;

  protected void checkClient(){
    Assert.isTrue(StringUtils.isNotBlank(apiKey), "apiKey不能为空");
    Assert.isTrue(StringUtils.isNotBlank(sensorsProject), "project不能为空");
    Assert.isTrue(StringUtils.isNotBlank(organizationId), "orgId不能为空");
  }

  protected Map<String, String> getHeaderMap() {
    Map<String ,String> header = new HashMap<>();

    header.put("api-key", apiKey);
    header.put("sensorsdata-project", sensorsProject);
    header.put("X-Organization-Id", organizationId);
    return header;
  }

}
