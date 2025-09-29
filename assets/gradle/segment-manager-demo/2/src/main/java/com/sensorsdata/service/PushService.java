package com.sensorsdata.service;

import com.sensorsdata.dto.SegmentNotifyMeta;
import kotlin.Pair;

import java.util.List;


public interface PushService {
    /**
     * 解析分群插件传入的数据
     */
    SegmentNotifyMeta parseSegmentData(SegmentNotifyMeta meta);

    /**
     * 获取用户加密后的邮箱
     */
    Pair<List<String>, List<String>> getUsers(SegmentNotifyMeta meta);
}
