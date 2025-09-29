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
public abstract class SensorsWebApiClient {

  /**
   * 神策域名*
   */
  String sensorsDomain;

  /**
   * 神策项目名称*
   */
  String sensorsProject;

  /**
   * token*
   */
  String superToken;

  protected void checkClient(){
    Assert.isTrue(StringUtils.isNotBlank(superToken), "superToken不能为空");
    Assert.isTrue(StringUtils.isNotBlank(sensorsProject), "project不能为空");
  }

  protected Map<String, String> getHeaderMap() {
    Map<String ,String> header = new HashMap<>();

    header.put("token", superToken);
    header.put("Sensorsdata-Project", sensorsProject);
    return header;
  }

}
