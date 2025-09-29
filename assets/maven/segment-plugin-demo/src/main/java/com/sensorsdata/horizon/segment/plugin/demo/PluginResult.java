package com.sensorsdata.horizon.segment.plugin.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyuan
 * @version 1.0.0
 * @since 2023/01/17 16:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PluginResult {
  private PluginResultStatus status;
  private String message;
}
