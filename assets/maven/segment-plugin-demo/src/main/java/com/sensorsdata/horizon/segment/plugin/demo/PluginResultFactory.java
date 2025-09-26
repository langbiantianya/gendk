package com.sensorsdata.horizon.segment.plugin.demo;

import com.sensorsdata.horizon.plugin.apis.segment.notify.model.SegmentNotifyResult;
import com.sensorsdata.horizon.plugin.apis.segment.notify.model.SegmentNotifyResultFactory;
import com.sensorsdata.horizon.segment.plugin.demo.util.JsonUtil;

import lombok.experimental.UtilityClass;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/12 14:41
 */
@UtilityClass
public class PluginResultFactory {

  public SegmentNotifyResult succeed() {
    return SegmentNotifyResultFactory.succeed(
        JsonUtil.toString(PluginResult.builder().status(PluginResultStatus.SUCCEED).build()));
  }

  public SegmentNotifyResult failed(String message) {
    return failed(PluginResultStatus.FAILED, message);
  }

  public SegmentNotifyResult failed(PluginResultStatus status, String message) {
    return SegmentNotifyResultFactory.failed(
        JsonUtil.toString(PluginResult.builder().status(status).message(message).build()));
  }

  public SegmentNotifyResult failed(PluginResultStatus status, String message, Exception e) {
    return SegmentNotifyResultFactory.failed(
        JsonUtil.toString(PluginResult.builder().status(status).message(message).build()), e);
  }
}
