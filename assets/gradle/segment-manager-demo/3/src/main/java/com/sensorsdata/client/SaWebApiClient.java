package com.sensorsdata.client;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sensorsdata.client.model.*;
import com.sensorsdata.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author ：wutengfei
 * @description：神策分析webapi客户端
 * @date ：2024-12-05 15:38
 */
@Slf4j
public class SaWebApiClient extends SensorsWebApiClient  {

  private static final String USER_LOOK_UP = "/api/v2/sa/user_lookup/report";

  private static final String SUCCESS = "SUCCESS";

  private SaWebApiClient(){

  }

  public static SaWebApiClient build(String sensorsDomain, String sensorsProject, String token) {
    SaWebApiClient client = new SaWebApiClient();
    client.setSensorsProject(sensorsProject);
    client.setSuperToken(token);
    client.setSensorsDomain(sensorsDomain);
    return client;
  }

  public SensorsApiSimpleResponse<UserLookUpResult> getUserListByGroupName(String groupName, List<String> profiles,
                                                                           Integer pageNum, Integer pageSize) throws BizException {
    Condition condition = new Condition();
    condition.setName(groupName);
    condition.setFunction("isTrue");
    UserGroupFilter userGroupFilter = new UserGroupFilter();
    userGroupFilter.setRelation("and");
    userGroupFilter.setConditions(Collections.singletonList(condition));

    UserLookUpRequest body = new UserLookUpRequest();
    body.setSearchWay("by_condition");
    body.setUserGroupFilter(userGroupFilter);
    body.setUseCache(true);

    body.setProfiles(profiles.stream().map(profile -> "user." + profile).collect(Collectors.toList()));
    body.setLimit(2000000000);
    body.setPage(pageNum - 1);
    body.setNumPerPage(pageSize);
    body.setSubTaskType("USER_LOOKUP");
    body.setAllPage(false);
    body.setSortByField(profiles.get(0));


    final HttpResponse execute =
        HttpUtil.createPost(super.getSensorsDomain() + USER_LOOK_UP).body(
            JSONUtil.toJsonStr(body)).addHeaders(super.getHeaderMap()).execute();

    if (execute.isOk()){
      return SensorsApiSimpleResponse.ok(UUID.randomUUID().toString(), SUCCESS,JSONUtil.toBean(execute.body(), UserLookUpResult.class));
    }

    log.error("获取用户细查列表失败, {}", execute);
    throw new BizException("获取用户细查列表失败");
  }
}
