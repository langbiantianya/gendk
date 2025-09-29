package com.sensorsdata.horizon.segment.plugin.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/10 18:58
 */
@UtilityClass
public class JsonUtil {

  @SneakyThrows
  public String toString(Object object) {
    return MapperHolder.get().writeValueAsString(object);
  }

  @SneakyThrows
  public <T> T toObject(String json, Class<T> tClass) {
    return MapperHolder.get().readValue(json, tClass);
  }

  @SneakyThrows
  public <T> T toObject(String json, TypeReference<T> typeReference) {
    return MapperHolder.get().readValue(json, typeReference);
  }
}
