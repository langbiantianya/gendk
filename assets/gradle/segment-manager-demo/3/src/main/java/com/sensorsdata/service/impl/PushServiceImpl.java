package com.sensorsdata.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensorsdata.client.SaWebApiClient;
import com.sensorsdata.client.model.SensorsApiSimpleResponse;
import com.sensorsdata.client.model.UserLookUpResult;
import com.sensorsdata.conf.SystemProperties;
import com.sensorsdata.dto.NotifyMeatDataDto;
import com.sensorsdata.dto.SegmentNotifyMeta;
import com.sensorsdata.exception.BizException;
import com.sensorsdata.service.PushService;
import com.sensorsdata.util.SensorsApiClientUtils;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.sensorsdata.constant.ErrorConstants.SEGMENT_GET_ERROR;

@Service
@Slf4j
public class PushServiceImpl implements PushService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SystemProperties.SystemProfileProperties profile;

    public PushServiceImpl(SystemProperties systemProperties) {
        this.profile = systemProperties.getProfile();
    }

    @Override
    public SegmentNotifyMeta parseSegmentData(SegmentNotifyMeta meta) {
        log.info("SegmentNotifyMeta: {}", meta);
        if (StringUtils.hasText(meta.getParam())) {
            try {
                NotifyMeatDataDto notifyMeatDataDto = objectMapper.readValue(meta.getParam(), new TypeReference<NotifyMeatDataDto>() {
                });
                meta.setNotifyMeatData(notifyMeatDataDto);
            } catch (Exception e) {
                throw new BizException("param json解析失败请检查 SegmentNotifyMeta 数据", e);
            }

        } else {
            throw new BizException("数据解析失败 param 为空或者不存在");
        }
        return meta;
    }

    @Override
    public Pair<List<String>, List<String>> getUsers(SegmentNotifyMeta meta) {  // 保持返回Flux<String>
        meta = parseSegmentData(meta);
        final SaWebApiClient saWebApiClient = SensorsApiClientUtils.getSaWebApiClient(meta.getProjectName());
        String segmentDefName = meta.getSegmentDefName();
        String userFiled = profile.getUserFiled();
        String usernameField = profile.getUsername();
        int batchSize = profile.getBatchSize();
        int page = 1;
        List<String> userFileds = new java.util.ArrayList<>();
        List<String> usernames = new java.util.ArrayList<>();


        while (true) {
            SensorsApiSimpleResponse<UserLookUpResult> sensorsApiSimpleResponse;
            try {
                sensorsApiSimpleResponse = saWebApiClient.getUserListByGroupName(segmentDefName, Arrays.asList(userFiled, usernameField), page,
                        batchSize);
            } catch (BizException e) {
                log.error("获取用户列表失败，page: {}", page, e);
                // 可根据需求决定是否继续下一页
                page++;
                continue;
            }
            if (sensorsApiSimpleResponse.isOk()) {
                List<UserLookUpResult.User> users = sensorsApiSimpleResponse.getData().getUsers();
                if (CollectionUtils.isEmpty(users)) {
                    break;
                }
                page += 1;
                for (UserLookUpResult.User user : users) {
                    Object dataObject = user.getProfiles().get(userFiled);
                    String data = dataObject instanceof java.util.List
                            ? String.valueOf(((java.util.List<?>) dataObject).get(0))
                            : String.valueOf(dataObject);
                    if (org.springframework.util.StringUtils.hasText(data)) {
                        userFileds.add(data);
                    }
                    dataObject = user.getProfiles().get(usernameField);
                    data = dataObject instanceof java.util.List
                            ? String.valueOf(((java.util.List<?>) dataObject).get(0))
                            : String.valueOf(dataObject);
                    if (org.springframework.util.StringUtils.hasText(data)) {
                        usernames.add(data);
                    }
                }
            } else {
                log.error("{}. {}", String.format(SEGMENT_GET_ERROR, segmentDefName), sensorsApiSimpleResponse);
                // 错误时仍尝试下一页（可根据需求调整）
                throw new BizException(String.format(SEGMENT_GET_ERROR, segmentDefName));
            }
        }
        return new Pair<>(userFileds, usernames);
    }

}
