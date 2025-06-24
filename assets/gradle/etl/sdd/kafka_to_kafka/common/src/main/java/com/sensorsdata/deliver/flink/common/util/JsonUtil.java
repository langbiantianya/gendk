package com.sensorsdata.deliver.flink.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 19:08
 */
@UtilityClass
public class JsonUtil {
  private final ObjectMapper _mapper = new ObjectMapper();
  private final String DOT = "\\.";

  static {
    _mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public String toJsonSting(Object object) throws JsonProcessingException {
    return _mapper.writeValueAsString(object);
  }


  public <T> T fromBean(String s, Class<T> var2) throws JsonProcessingException {
    return _mapper.readValue(s, var2);
  }

  public <T> T fromBean(Object o, Class<T> var2) {
    return _mapper.convertValue(o, var2);
  }

  public JsonNode toJsonNode(String s) throws JsonProcessingException {
    return _mapper.readTree(s);
  }

  public JsonNode toJsonNode(Object o) {
    return _mapper.valueToTree(o);
  }

  public ObjectNode getObjectNode() {
    return _mapper.createObjectNode();
  }

  public JsonNode getJsonValue(String key, JsonNode o) {
    JsonNode tmp = o;
    for (String field : key.split(DOT)) {
      if (tmp.has(field)) {
        tmp = tmp.get(field);
      } else {
        return null;
      }
    }
    return tmp;
  }

  public Object getMapValue(String key, Object o) {
    Object tmp = o;
    StringBuilder tmpKey = new StringBuilder();
    for (String field : key.split(DOT)) {
      if (tmp instanceof Map) {
        tmpKey.append(field);
        Map<?, ?> map = (Map<?, ?>) tmp;
        if (map.containsKey(tmpKey.toString())) {
          tmp = map.get(tmpKey.toString());
          tmpKey.setLength(0);
        } else {
          tmpKey.append(".");
        }
      } else {
        return null;
      }
    }
    if (tmpKey.length() == 0) {
      return tmp;
    } else {
      return null;
    }
  }

}
