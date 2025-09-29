package com.sensorsdata.client.model.sbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ：wutengfei
 * @description：TODO
 * @date ：2024-10-11 15:36
 */
@Data
public class I18nMessage {

  @JsonProperty("zh_cn")
  String zhCn;
  @JsonProperty("en_us")
  String enUs;
  @JsonProperty("zh_tw")
  String zhTw;
  String ja;
  String th;
}
