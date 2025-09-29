package com.sensorsdata.horizon.segment.plugin.demo.proxy;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/12 16:17
 */
@UtilityClass
public class OkhttpProxy {

  private static final OkHttpClient okhttpClient = new OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .retryOnConnectionFailure(false)
      .build();
  private static final MediaType JSON = MediaType.parse("application/json;charset=utf8");

  @SneakyThrows
  public static Response post(String url, String data, Map<String, String> headerMap) {
    return post(url, data, headerMap, null);
  }

  @SneakyThrows
  public static Response post(String url, String data, Map<String, String> headerMap, Map<String, String> paramMap) {
    RequestBody body = RequestBody.create(data, JSON);

    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
    if (MapUtils.isNotEmpty(paramMap)) {
      paramMap.forEach(urlBuilder::addQueryParameter);
    }

    Request.Builder builder = new Request.Builder()
        .url(urlBuilder.build())
        .post(body);

    if (MapUtils.isNotEmpty(headerMap)) {
      headerMap.forEach(builder::addHeader);
    }

    return okhttpClient.newCall(builder.build()).execute();
  }

  @SneakyThrows
  public static Response get(String url, Map<String, String> param, Map<String, String> headerMap) {

    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
    if (MapUtils.isNotEmpty(param)) {
      param.forEach(urlBuilder::addQueryParameter);
    }

    Request.Builder builder = new Request.Builder()
        .url(urlBuilder.build())
        .get();

    if (MapUtils.isNotEmpty(headerMap)) {
      headerMap.forEach(builder::addHeader);
    }
    return okhttpClient.newCall(builder.build()).execute();
  }


}
