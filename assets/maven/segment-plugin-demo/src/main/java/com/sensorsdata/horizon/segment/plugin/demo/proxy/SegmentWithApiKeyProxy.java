package com.sensorsdata.horizon.segment.plugin.demo.proxy;

import com.sensorsdata.horizon.segment.plugin.demo.util.ProtobufUtil;
import com.sensorsdata.horizon.v1.CancelSegmentTaskRequest;
import com.sensorsdata.horizon.v1.EvaluateSegmentRequest;
import com.sensorsdata.horizon.v1.GetLastedSegmentItemResponse;
import com.sensorsdata.horizon.v1.GetSegmentTaskRequest;
import com.sensorsdata.horizon.v1.GetSegmentTaskResponse;
import com.sensorsdata.horizon.v1.SegmentDefinition;
import com.sensorsdata.horizon.v1.SegmentItem;
import com.sensorsdata.horizon.v1.SegmentTask;
import com.sensorsdata.openapi.client.common.HttpApiResult;
import com.sensorsdata.openapi.client.common.SensorsOpenApiConstants;
import com.sensorsdata.openapi.horizon.v1.api.SegmentJavaClientApi;
import com.sensorsdata.openapi.horizon.v1.model.SensorsdataHorizonV1CancelSegmentTaskRequest;
import com.sensorsdata.openapi.horizon.v1.model.SensorsdataHorizonV1EvaluateSegmentRequest;
import com.sensorsdata.openapi.horizon.v1.model.SensorsdataHorizonV1GetLastedSegmentItemRequest;
import com.sensorsdata.openapi.horizon.v1.model.SensorsdataHorizonV1GetSegmentDefinitionRequest;
import com.sensorsdata.openapi.horizon.v1.model.SensorsdataHorizonV1GetSegmentTaskRequest;

import com.google.common.collect.Lists;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/02/01 16:21
 */
@UtilityClass
@Slf4j
public class SegmentWithApiKeyProxy {

  private static final String SUCCESS_CODE = "SUCCESS";

  private SegmentJavaClientApi getSegmentJavaClientApi(String projectName, String apiKey) {
    return SegmentOpenapiClientHolder.getSegmentJavaClient(projectName, apiKey);
  }

  @SneakyThrows
  public void evaluateSegment(String projectName, String apiKey,
      EvaluateSegmentRequest request) {
    String jsonStr = JsonFormat.printer().preservingProtoFieldNames().includingDefaultValueFields().print(request);
    SensorsdataHorizonV1EvaluateSegmentRequest v1EvaluateSegmentRequest =
        SensorsOpenApiConstants.MAPPER.readValue(jsonStr, SensorsdataHorizonV1EvaluateSegmentRequest.class);
    HttpApiResult httpApiResult =
        getSegmentJavaClientApi(projectName, apiKey).evaluateSegment(v1EvaluateSegmentRequest);
    checkResult(httpApiResult, request);
  }

  @SneakyThrows
  public List<SegmentTask> getSegmentTask(String projectName, String apiKey, GetSegmentTaskRequest request) {
    String jsonStr = JsonFormat.printer().preservingProtoFieldNames().includingDefaultValueFields().print(request);
    SensorsdataHorizonV1GetSegmentTaskRequest v1GetSegmentTaskRequest =
        SensorsOpenApiConstants.MAPPER.readValue(jsonStr, SensorsdataHorizonV1GetSegmentTaskRequest.class);
    HttpApiResult httpApiResult = getSegmentJavaClientApi(projectName, apiKey).getSegmentTask(v1GetSegmentTaskRequest);
    checkResult(httpApiResult, request);
    GetSegmentTaskResponse response = ProtobufUtil.handle(httpApiResult, GetSegmentTaskResponse.class);
    return Lists.newArrayList(response.getTasksList());
  }

  @SneakyThrows
  public void cancelSegmentTask(String projectName, String apiKey, CancelSegmentTaskRequest request) {
    String jsonStr = JsonFormat.printer().preservingProtoFieldNames().includingDefaultValueFields().print(request);
    SensorsdataHorizonV1CancelSegmentTaskRequest v1CancelSegmentTaskRequest =
        SensorsOpenApiConstants.MAPPER.readValue(jsonStr, SensorsdataHorizonV1CancelSegmentTaskRequest.class);
    HttpApiResult httpApiResult =
        getSegmentJavaClientApi(projectName, apiKey).cancelSegmentTask(v1CancelSegmentTaskRequest);
    checkResult(httpApiResult, request);
  }

  @SneakyThrows
  public Map<String, SegmentItem> getLatestSegmentItem(String projectName, String apiKey, SensorsdataHorizonV1GetLastedSegmentItemRequest request) {
    HttpApiResult httpApiResult =
        getSegmentJavaClientApi(projectName, apiKey).getLastedSegmentItem(request);
    checkResult(httpApiResult, request);
    GetLastedSegmentItemResponse response = ProtobufUtil.handle(httpApiResult, GetLastedSegmentItemResponse.class);
    return response.getLastedSegmentItemMapMap();
  }

  @SneakyThrows
  public static SegmentDefinition getSegmentDefinition(String projectName, String apiKey,
      SensorsdataHorizonV1GetSegmentDefinitionRequest request) {
    HttpApiResult httpApiResult = getSegmentJavaClientApi(projectName, apiKey).getSegmentDefinition(request);
    checkResult(httpApiResult, request);
    return ProtobufUtil.handle(httpApiResult, SegmentDefinition.class);
  }

  private static void checkResult(HttpApiResult httpApiResult, Object request) {
    if (!SUCCESS_CODE.equals(httpApiResult.getCode())) {
      log.error("grpc request error.[httpApiResult={}, request={}]", httpApiResult, request);
      throw new RuntimeException("grpc request error.");
    }
  }
}
