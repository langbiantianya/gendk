package com.sensorsdata.deliver.flink.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 20:24
 */
@Slf4j
@UtilityClass
public class ConfigUtil {
  private Map<String, Object> config;

  public void init(String path) {
    log.info("config file is {}", path);
    Yaml yaml = new Yaml();
    InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream(path);
    config = yaml.load(in);
  }

  public void init(File file) throws FileNotFoundException {
    log.info("config file is {}", file);
    Yaml yaml = new Yaml();
    config = yaml.load(new FileReader(file));
  }

  public void init() {
    if (config == null) {
      init("config.yaml");
    }
  }

  public <T> T getConf(String key, Class<T> tClass) {
    init();
    Object mapValue = JsonUtil.getMapValue(key, config);
    if (mapValue == null) {
      return null;
    }
    return JsonUtil.fromBean(mapValue, tClass);
  }


}
