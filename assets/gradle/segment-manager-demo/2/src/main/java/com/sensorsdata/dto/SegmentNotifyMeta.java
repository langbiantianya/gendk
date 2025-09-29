package com.sensorsdata.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SegmentNotifyMeta {
    // 项目ID
    private int projectId;
    // 项目名称
    private String projectName;

    // 实体 name
    private String entityName;
    // 分群 name
    private String segmentDefName;
    // 分群展示名
    private String segmentDefDisplayName;

    // 计算 id
    private int taskId;
    // 计算结果状态
    private String status;
    // 基准时间
    private Date baseTime;
    // 计算任务在状态为计算中的开始时间
    private Date startTime;
    // 计算任务在状态为计算中的结束时间
    private Date endTime;
    // 计算成功的实体个数
    private long count;

    // 操作用户 id
    private String userId;
    // 操作用户名称
    private String userName;

    // 推送参数 入参这里取
    private String param;
    // 触发方式， 计算完成之后自动触发：AFTER_COMPUTE，手动立即推送：MANUAL
    private String triggerType;
    /**
     * 解析后填入的邮箱模板id
     */
    private NotifyMeatDataDto notifyMeatData;
}
