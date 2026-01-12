package com.sensorsdata.analytics.sso.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 该工具类主要用于调用客户接口
 * 如果客户未提供可以调用他们接口的maven依赖或者jar包
 * 可以调用本工具类的doGet或doPost方法来调用客户接口
 *
 */
@Slf4j
public class HttpClientUtil {


    private HttpClientUtil() {
    }

    private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";
    private static int timeout = 60000;


    static {
        RequestConfig config =
                RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(
                        timeout).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }


    /**
     * HTTP Get 获取内容
     *
     * @param url    请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> headers, Map<String, String> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            HttpGet httpGet = new HttpGet(url);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        httpGet.setHeader(entry.getKey(), value);
                    }
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("HTTP连接异常    HttpClient,error status code={}", statusCode);
                }
                HttpEntity entity = response.getEntity();
                String result = null;
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }

                return result;
            } finally {
                if (httpGet != null) {
                    httpGet.abort();
                }
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                }
            }
        } catch (Exception e) {
            log.error("HTTP连接异常", e);
        }
        return null;
    }


    /**
     * HTTP Post 获取内容
     *
     * @param url    请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (!pairs.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("HTTP连接异常HttpClient,error status code is {}" , statusCode);
                }
                HttpEntity entity = response.getEntity();
                String result = null;
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
                return result;
            } finally {
                if (httpPost != null) {
                    httpPost.abort();
                }
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                }
            }
        } catch (Exception e) {
            log.error("HTTP连接异常  ", e);
        }
        return null;
    }

}

