package com.sensorsdata.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.util.ObjectUtil;
import com.sensorsdata.client.SaWebApiClient;
import com.sensorsdata.client.SbpOpenApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ：wutengfei
 * @description：TODO
 * @date ：2024-12-09 15:17
 */
@Slf4j
@Component
public class SensorsApiClientUtils {

  static String sensorsDomain;

  static String superToken;

  static String apiKey;

  static String orgId;

  @Value("${system.request.domain}")
  public void setSensorsDomain(String domain){
    SensorsApiClientUtils.sensorsDomain = domain;
  }

  @Value("${system.request.super_token}")
  public void setSuperToken(String superToken){
    SensorsApiClientUtils.superToken = superToken;
  }

  @Value("${system.request.api_key}")
  public void setApiKey(String apiKey){
    SensorsApiClientUtils.apiKey = apiKey;
  }

  @Value("${system.request.org_id}")
  public void setOrgId(String orgId){
    SensorsApiClientUtils.orgId = orgId;
  }


  static LFUCache<String, SaWebApiClient> saWebApiClientLFUCache = CacheUtil.newLFUCache(10, 60 * 60 * 1000);

  static LFUCache<String, SbpOpenApiClient> sbpOpenApiClients = CacheUtil.newLFUCache(10, 60 * 60 * 1000);

  public static SaWebApiClient getSaWebApiClient(String projectName) {
    final SaWebApiClient client = saWebApiClientLFUCache.get(projectName);
    if (ObjectUtil.isNotNull(client)) {
      return client;
    }
    final SaWebApiClient saWebApiClient = SaWebApiClient.build(sensorsDomain, projectName, superToken);
    if (ObjectUtil.isNotNull(saWebApiClient)) {
      saWebApiClientLFUCache.put(projectName, saWebApiClient);
      return saWebApiClient;
    }
    throw new RuntimeException("获取saWebApiClient失败");
  }

  public static SbpOpenApiClient getSbpOpenApiClient(String projectName) {
    final SbpOpenApiClient client = sbpOpenApiClients.get(projectName);
    if (ObjectUtil.isNotNull(client)) {
      return client;
    }
    final SbpOpenApiClient sbpOpenApiClient = SbpOpenApiClient.build(sensorsDomain, apiKey, projectName, orgId);
    if (ObjectUtil.isNotNull(sbpOpenApiClient)) {
      sbpOpenApiClients.put(projectName, sbpOpenApiClient);
      return sbpOpenApiClient;
    }
    throw new RuntimeException("获取sbpOpenApiClient失败");
  }

}
