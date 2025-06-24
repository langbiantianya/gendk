package com.sensorsdata.deliver.flink.common.util;

import com.sensorsdata.deliver.flink.common.annotation.JobConf;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2022/11/30 14:46
 */
@UtilityClass
public class JobConfLoadUtil {
  private String getFieldSetMethodName(String fieldName) {
    return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  private String getFieldGetMethodName(String fieldName, Class<?> type) {
    if (type == boolean.class) {
      return "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  private void setFieldValue(Object o, String fieldName, Class<?> c, Object value, Class<?> type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = c.getMethod(getFieldSetMethodName(fieldName), type);
    method.invoke(o, value);
  }

  private void copyFiledValue(Object o, String fieldName, String copyFieldName, Class<?> c, Class<?> type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method getMethod = c.getMethod(getFieldGetMethodName(copyFieldName, type));
    Method setMethod = c.getMethod(getFieldSetMethodName(fieldName), getMethod.getReturnType());
    setMethod.invoke(o, getMethod.invoke(o));
  }

  /**
   * 根据配置文件的内容，初始化配置类
   *
   * @param tClass 配置类
   * @param <T>    配置类
   * @return 配置类示例对象
   */
  public <T> T loadConf(T confObject) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    // 先初始化
    Class<?> tClass = confObject.getClass();
    // 拿到类里所有的属性
    Field[] fields = tClass.getDeclaredFields();
    List<Field> copFields = new ArrayList<>();
    for (Field field : fields) {
      // 取到属性上的注解
      JobConf annotation = field.getAnnotation(JobConf.class);
      if (annotation == null) continue;
      String confKey = annotation.value();
      String copy = annotation.copy();
      Class<?> type = field.getType();
      String name = field.getName();
      // 根据注解上写的配置key，从配置文件中找到对应的值
      Object confValue = ConfigUtil.getConf(confKey, type);
      if (confValue != null) {
        // 调用 set 方法，将值 set 到属性上
        setFieldValue(confObject, name, tClass, confValue, type);
      } else {
        // 如果取不到对应的值，但是这个属性配置的 copy，则先放到列表里
        if (!copy.isEmpty()) {
          copFields.add(field);
        }
      }
    }
    // 遍历所有要 copy 的属性列表
    for (Field field : copFields) {
      JobConf annotation = field.getAnnotation(JobConf.class);
      String copy = annotation.copy();
      // 将被 copy 的属性值赋予当前属性
      copyFiledValue(confObject, field.getName(), copy, tClass, field.getType());
    }
    return confObject;
  }
}
