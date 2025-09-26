package com.sensorsdata.horizon.segment.plugin.demo;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/12 14:41
 */
public enum PluginResultStatus {

  SUCCEED("成功"),
  SKIP_PROCESS("不满足处理条件，不处理"),
  CALL_BACK_FAILED("回调中间件失败"),
  INVALID_PARAM("无效的推送参数"),
  FAILED("失败")
  ;

  private String desc;

  PluginResultStatus(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
