package com.sensorsdata.horizon.segment.plugin.demo.proxy;

import com.sensorsdata.openapi.horizon.v1.api.SegmentJavaClientApi;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/02/02 18:56
 */
@UtilityClass
public class SegmentOpenapiClientHolder {

  private static final String HOST = "localhost";
  private static final int PORT = 8186;
  private static final String DEFAULT_SUPER_API_TOKEN_KEY = "#token#";
  private static final String HEADER_TOKEN = "token";
  // TODO 3、choice 配置 super token
  private static final String SUPER_API_TOKEN = "";

  private static final Map<String, Map<String, SegmentJavaClientApi>> CLIENT_HOLDER = new ConcurrentHashMap<>();

  public static SegmentJavaClientApi getSegmentJavaClient(String projectName) {
    return getSegmentJavaClient(projectName, DEFAULT_SUPER_API_TOKEN_KEY);
  }

  public static SegmentJavaClientApi getSegmentJavaClient(String projectName, String apiKey) {
    Map<String, SegmentJavaClientApi> clientMap = CLIENT_HOLDER.getOrDefault(projectName, new ConcurrentHashMap<>());
    SegmentJavaClientApi clientApi = clientMap.get(apiKey);
    if (null == clientApi) {
      clientApi = createClientApi(projectName, apiKey);
    }
    return clientApi;
  }

  private static synchronized SegmentJavaClientApi createClientApi(String projectName, String apiKey) {
    Map<String, SegmentJavaClientApi> clientApiMap = CLIENT_HOLDER.getOrDefault(projectName, new ConcurrentHashMap<>());
    SegmentJavaClientApi javaClientApi = clientApiMap.get(apiKey);
    if (null != javaClientApi) {
      return javaClientApi;
    }
    SegmentJavaClientApi clientApi = new SegmentJavaClientApi(apiKey, projectName, HOST, PORT);
    if (apiKey.equals(DEFAULT_SUPER_API_TOKEN_KEY)) {
      clientApi.setApiKey(null);
      clientApi.getApiClient().addDefaultHeader(HEADER_TOKEN, SUPER_API_TOKEN);
    }
    clientApiMap.put(apiKey, clientApi);
    CLIENT_HOLDER.put(projectName, clientApiMap);
    return clientApi;
  }

}
