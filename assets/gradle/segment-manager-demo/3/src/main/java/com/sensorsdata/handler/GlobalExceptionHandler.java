package com.sensorsdata.handler;

import com.sensorsdata.exception.BizException;
import com.sensorsdata.util.FeishuUtil;
import kotlin.collections.MapsKt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 标记为全局异常处理类（自动被 Spring 扫描）
@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final FeishuUtil feishuUtil;

    // 捕获所有 RuntimeException 类型的异常（可根据需求扩展其他异常类型）
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 状态码为 500
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        // 将异常信息包装成统一的 Result 对象
        log.error(e.getMessage(), e);
        return new ResponseEntity<>("全局异常捕获：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 业务异常返回 400
    public ResponseEntity<?> handleBizException(BizException e) {
        log.error(e.getMessage(), e);
        //       发送飞书告警
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("msg_type", "text");
        bodyMap.put("content", MapsKt.mapOf(new kotlin.Pair<>(
                "text", "分群推送告警：\n" + e.getMessage())));
        feishuUtil.sendFeishuRobotMessage(bodyMap);
        return new ResponseEntity<>("业务异常：" + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}