package com.sensorsdata.controller;

import com.sensorsdata.dto.SegmentNotifyMeta;
import com.sensorsdata.exception.BizException;
import com.sensorsdata.service.PushService;
import com.sensorsdata.util.FeishuUtil;
import kotlin.Pair;
import kotlin.collections.MapsKt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/segment_shulex")
@AllArgsConstructor
@Slf4j
public class SegmentController {
    private final PushService push;
    private final FeishuUtil feishuUtil;

    // 创建一个固定大小的线程池用于执行异步任务
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    // 创建一个调度线程池用于延迟任务
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);


    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }

    @PostMapping(value = "/acceptMessage")
    public ResponseEntity<String> acceptMessage(@RequestBody SegmentNotifyMeta meta) {
        meta = push.parseSegmentData(meta);

        // 获取用户邮箱并解密
        // 将获取用户邮箱的操作提交到线程池异步执行
        SegmentNotifyMeta finalMeta1 = meta;
        // 生成 20 到 60 秒之间的随机延迟时间（单位：毫秒）
        long delayTime = ThreadLocalRandom.current().nextLong(200, 601) * 100;
        CompletableFuture<Pair<List<String>, List<String>>> userEmailsFuture = new CompletableFuture<>();
        scheduledExecutorService.schedule(() -> {
            executorService.submit(() -> {
                int retryCount = 0;
                int maxRetries = 3;
                while (retryCount < maxRetries) {
                    try {
                        log.info("延迟结束，开始获取用户邮箱 (尝试 {}/{})", retryCount + 1, maxRetries);
                        Pair<List<String>, List<String>> result = push.getUsers(finalMeta1);
                        userEmailsFuture.complete(result);
                        return; // 成功获取到邮箱，直接返回
                    } catch (Exception e) {
                        retryCount++;
                        if (retryCount >= maxRetries) {
                            log.error("获取分群数据失败，已达到最大重试次数", e);
                            userEmailsFuture.completeExceptionally(e);
                        } else {
                            log.warn("获取分群数据失败，准备进行第 {} 次重试", retryCount + 1, e);
                            try {
                                // 每次重试前等待1秒
                                Thread.sleep(1000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                log.error("重试等待被中断", ie);
                                userEmailsFuture.completeExceptionally(ie);
                                return;
                            }
                        }
                    }
                }
            });
        }, delayTime, TimeUnit.MILLISECONDS);
        // 等待异步任务完成并获取结果
        List<String> userFileds;
        List<String> usernames;
        try {
            userFileds = userEmailsFuture.get().getFirst();
            usernames = userEmailsFuture.get().getSecond();
            log.info("获取到的用户字段信息为：{}", userFileds);
            log.info("获取到的用户名信息为：{}", usernames);
        } catch (Exception e) {
            throw new BizException("获取用户分群信息失败");
        }
        if (userFileds.isEmpty() || usernames.isEmpty()) {
            throw new BizException("获取用户分群为空");
        }

        try {
//           TODO 自行处理推送逻辑
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("msg_type", "text");
            bodyMap.put("content", MapsKt.mapOf(new kotlin.Pair<>("text", "推送成功\n" +
                    "分群名称:" + meta.getSegmentDefName() + "(" + meta.getSegmentDefDisplayName() + ")" + "\n" +
                    "分群计算结果ID:" + meta.getTaskId() + "\n" +
                    "推送时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n")));
            feishuUtil.sendFeishuRobotMessage(bodyMap);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(e);
        }
    }
}
