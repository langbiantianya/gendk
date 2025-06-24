package com.sensorsdata.deliver.flink.common.annotation;

import java.lang.annotation.*;

/**
 * 读取配置文件，给属性赋值的注解，类似于 sparing 的 @value
 *
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/28 19:56
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobConf {
  // 读取配置文件中的 key
  String value();

  // 复制其他的属性值，必须保证属性值类型一致
  String copy() default "";
}
