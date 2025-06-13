package com.sensorsdata.analytics.sso.openapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensorsdata.analytics.sso.openapi.entity.AccountData;
import com.sensorsdata.analytics.sso.openapi.entity.AccountItem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApiAccounts {
    @Value("${sdp.api-key}")
    private String apiKey;
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final ObjectMapper objectMapper;
    @Value("${sdp.api-domain}")
    private String apiDomain;

    public ApiAccounts(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * 获取全部用户
     */
    @SneakyThrows
    public AccountData getAccounts() {
        Request request = new Request.Builder()
                .url(apiDomain + "/sbp/accounts?is_in_project=true&sort_order=desc&token=" + apiKey)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        log.info("okhttp: {}", request);
        Response response = client.newCall(request).execute();
// 处理响应：反序列化 JSON 为 AccountData
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, AccountData.class);
        } else {
            throw new RuntimeException("请求失败，状态码：" + response.code());
        }
    }

    /**
     * 修改用户信息 目前只加了角色
     */
    @SneakyThrows
    public void updateAccount(long accountId, List<Integer> related_role_ids, int project_id) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        Map<String, Object> projectInfo = new HashMap<>();
        projectInfo.put("project_id", project_id);

        List<Map<String, Object>> roles = related_role_ids.stream().map(id -> {
            HashMap<String, Object> role = new HashMap<>();
            role.put("id", id);
            return role;
        }).collect(Collectors.toList());
        projectInfo.put("roles", roles);
        Map<String, Object> projectInfos = new HashMap<>();
        projectInfos.put("project_info", Collections.singletonList(projectInfo));

        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(projectInfos));
        log.info("RequestBody: {}", projectInfos);
        Request request = new Request.Builder()
                .url(apiDomain + "/sbp/accounts/global/" + accountId + "?token=" + apiKey)
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        log.info("okhttp: {}", request);
        Response response = client.newCall(request).execute();
// 处理响应：反序列化 JSON 为 AccountData
        if (!response.isSuccessful()) {
            throw new RuntimeException("请求失败，状态码：" + response.code());
        }
    }

}
