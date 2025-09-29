package com.sensorsdata.controller;

import com.sensorsdata.exception.BizException;
import com.sensorsdata.util.FeishuUtil;
import kotlin.collections.MapsKt;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/🤣🤣/test")
@AllArgsConstructor
public class TestController {
    private final FeishuUtil feishuUtil;
    @GetMapping("/send")
    public ResponseEntity<String> send() {
        throw new BizException("测试飞书异常告警");
    }

    @GetMapping("/sendOK")
    public ResponseEntity<String> sendOK() {
        //            发送飞书成功消息
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("msg_type", "text");
        bodyMap.put("content", MapsKt.mapOf(new kotlin.Pair<>("text", "shulex 工单创建成功\n" +
                "分群名称:" + "测试" + "(" + "测试" + ")" + "\n" +
                "分群计算结果ID:" + 233 + "\n" +
                "模板ID:" + "321,231" + "\n" +
                "邮箱数量:" + 666 + "\n" +
                "推送时间:" + dateStr + "\n" +
                "shulex响应详情:" + "nmsl")));
        feishuUtil.sendFeishuRobotMessage(bodyMap);
        return ResponseEntity.ok("发送成功");
    }
}
