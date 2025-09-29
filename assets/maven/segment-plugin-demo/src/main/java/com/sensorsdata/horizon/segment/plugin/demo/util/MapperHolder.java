package com.sensorsdata.horizon.segment.plugin.demo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/10 18:55
 */
@UtilityClass
public class MapperHolder {

  // 容忍json中出现未知的列，兼容java中的驼峰的字段名命名
  private static final JsonMapper MAPPER =
      JsonMapper.builder()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
          .build();

  // 容忍json中出现未知的列，使用驼峰式 + 开头下划线保留的方式
  private static final JsonMapper KAFKA_MAPPER =
      JsonMapper.builder()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .propertyNamingStrategy(new CamelCaseToLowerCasePermitUnderscorePrefix())
          .build();

  private static final JsonMapper API_MAPPER =
      JsonMapper.builder()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
          .serializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
          .build();

  /**
   * 默认的 JsonMapper
   */
  public static JsonMapper get() {
    return MAPPER;
  }

  /**
   * 给 Batch Loader 和 Data Loader 用的
   */
  public static JsonMapper getKafka() {
    return KAFKA_MAPPER;
  }

  /**
   * 给 web 请求用的
   */
  public static JsonMapper getApi() {
    return API_MAPPER;
  }

  private static class CamelCaseToLowerCasePermitUnderscorePrefix
      extends PropertyNamingStrategies.NamingBase {

    @Override
    public String translate(String input) {
      if (input == null) {
        return input;
      } else {
        boolean startWithUnderscore = false;
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        if (length > 0 && input.charAt(0) == 95) {
          startWithUnderscore = true;
        }
        for (int i = 0; i < length; ++i) {
          char c = input.charAt(i);
          if (i > 0 || c != 95) {
            if (Character.isUpperCase(c)) {
              if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != 95) {
                result.append('_');
                ++resultLength;
              }

              c = Character.toLowerCase(c);
              wasPrevTranslated = true;
            } else {
              wasPrevTranslated = false;
            }

            result.append(c);
            ++resultLength;
          }
        }

        if (resultLength > 0) {
          if (startWithUnderscore) {
            result.insert(0, '_');
          }
          return result.toString();
        } else {
          return input;
        }
      }
    }
  }
}
