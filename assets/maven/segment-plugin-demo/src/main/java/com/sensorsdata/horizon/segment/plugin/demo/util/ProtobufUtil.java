package com.sensorsdata.horizon.segment.plugin.demo.util;

import com.sensorsdata.openapi.client.common.HttpApiResult;
import com.sensorsdata.openapi.client.common.SensorsOpenApiConstants;

import com.google.protobuf.Empty;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.Timestamps;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@UtilityClass
@Slf4j
public class ProtobufUtil {

  private static final JsonFormat.Printer JSON_FORMAT_PRINTER =
      JsonFormat.printer().omittingInsignificantWhitespace().preservingProtoFieldNames().includingDefaultValueFields();

  public Date toDate(Timestamp timestamp) {
    return new Date(Timestamps.toMillis(timestamp));
  }

  public Timestamp toTimestamp(Date date) {
    return Timestamps.fromMillis(date.getTime());
  }

  public static String printToString(MessageOrBuilder message) throws InvalidProtocolBufferException {
    return JSON_FORMAT_PRINTER.print(message);
  }

  private final JsonFormat.Parser parser = JsonFormat.parser().ignoringUnknownFields();

  public static <T extends Message> T toProto(String json, Class<T> typeClass) {
    Message.Builder builder = Internal.getDefaultInstance(typeClass).newBuilderForType();
    try {
      parser.merge(json, builder);
    } catch (InvalidProtocolBufferException e) {
      String protoName = builder.getDescriptorForType().getName();
      throw new IllegalStateException(
          String.format("json to proto serialize error.[json=%s,protoName=%s]", json, protoName), e);
    }
    Message result = builder.build();
    return typeClass.cast(result);
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public static <T extends Message> T handle(HttpApiResult result, Class<T> clazz) {
    log.info("get result {}", result);
    if (result.getData() != null) {
      String responseJson = SensorsOpenApiConstants.MAPPER.writeValueAsString(result.getData());
      return toProto(responseJson, clazz);
    }
    if (Empty.class == clazz) {
      return null;
    }
    throw new RuntimeException(
        String.format("invoke with error response.[code=%s, message=%s]", result.getCode(), //NOSONAR
            result.getMessage()));
  }

}
