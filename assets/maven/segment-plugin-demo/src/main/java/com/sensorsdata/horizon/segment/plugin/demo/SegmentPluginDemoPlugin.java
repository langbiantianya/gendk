package com.sensorsdata.horizon.segment.plugin.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensorsdata.horizon.plugin.apis.segment.notify.extension.SegmentNotifyExtension;
import com.sensorsdata.horizon.plugin.apis.segment.notify.model.SegmentNotifyMeta;
import com.sensorsdata.horizon.plugin.apis.segment.notify.model.SegmentNotifyResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/06 17:04
 */
@Slf4j
public class SegmentPluginDemoPlugin implements SegmentNotifyExtension {
  private static final OkHttpClient httpClient = new OkHttpClient();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  @Override
  public String getName() {
    return "{{.PluginCName}}";
  }

  @Override
  public SegmentNotifyResult sendNotify(SegmentNotifyMeta metadata) {
    log.info("segment plugin shulex start.[metadata={}]", metadata);
    if (PluginResultStatus.SUCCEED.name().equals(metadata.getStatus())) {
      if (metadata.getCount() > 0) {
        // 新增：使用 OkHttp 发送 POST 请求
        // 启动异步任务
        CompletableFuture.runAsync(() -> {
          try {
            log.info("plugin send message to Shulex service start. metadata: {}", metadata);
            // 1. 序列化 metadata 为 JSON
            String metadataJson = objectMapper.writeValueAsString(metadata);

            // 2. 构建请求体（application/json 格式）
            RequestBody requestBody = RequestBody.create(metadataJson, MediaType.get("application/json"));

            // 3. 构建 POST 请求
            Request request = new Request.Builder()
                //.url("http://localhost:8107/api/segment_shulex/acceptMessage")
                .url("{{.WebServerUrl}}")
                .post(requestBody)
                .build();

            // 4. 发送请求并忽略结果（使用 try-with-resources 自动关闭响应流）
            try (Response response = httpClient.newCall(request).execute()) {
              if (response.body() != null) {
                log.info("plugin send message to Shulex service end. result: {}", response.body().string());
              } else {
                log.error("plugin send message to Shulex service end. result: {}", response);
              }
            }
          } catch (JsonProcessingException e) {
            log.error("metadata 序列化失败", e);
          } catch (Exception e) {
            log.error("plugin send message to Shulex service err", e);
          }
        }, executor);
        log.info("segment push meta plugin end.[metadata={}]", metadata);
        return PluginResultFactory.succeed();
      } else {
        log.error("segment count is zero");
        return PluginResultFactory.failed(PluginResultStatus.FAILED, "segment count is zero");
      }
    } else {
      log.error("illegal task status");
      return PluginResultFactory.failed(PluginResultStatus.FAILED,
          String.format("illegal task status: %s", metadata.getStatus()));
    }
  }
}
