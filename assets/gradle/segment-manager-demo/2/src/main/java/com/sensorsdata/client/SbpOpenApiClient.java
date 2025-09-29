package com.sensorsdata.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sensorsdata.client.model.SensorsApiSimpleResponse;
import com.sensorsdata.client.model.sbp.GetAccountResult;
import com.sensorsdata.client.model.sbp.GetRoleResult;
import com.sensorsdata.client.model.sbp.Role;
import com.sensorsdata.client.model.sbp.Statement;
import com.sensorsdata.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;


/**
 * @author ：wutengfei
 * @description：神策智能营销APIClient
 * @date ：2024-10-08 17:26
 */
@Slf4j
public class SbpOpenApiClient extends SensorsOpenApiClient{

  private SbpOpenApiClient() {
  }

  public static SbpOpenApiClient build(String sensorsDomain, String apiKey, String sensorsProject, String orgId) {
    SbpOpenApiClient client = new SbpOpenApiClient();
    client.setApiKey(apiKey);
    client.setSensorsProject(sensorsProject);
    client.setOrganizationId(orgId);
    client.setSensorsDomain(sensorsDomain);
    return client;
  }


  public SensorsApiSimpleResponse<GetAccountResult> getAccountById(Integer id) {

    super.checkClient();
    Assert.isTrue(id != null, "角色id不能为空");

    final HttpResponse execute =
        HttpUtil.createGet(super.getSensorsDomain() + CommonConstants.SbpConstants.getAccountById()).form("id", id).addHeaders(
            super.getHeaderMap()).execute();
    if (execute.isOk()) {
      final JSONObject jsonObject = JSONUtil.parseObj(execute.body());
      String code = jsonObject.getStr("code");
      if (CommonConstants.SUCCESS.equals(code)) {
        final JSONObject data = jsonObject.getJSONObject("data");
        return SensorsApiSimpleResponse.ok(jsonObject.getStr("request_id"), code, data.toBean(GetAccountResult.class));
      } else if (StringUtils.isNotBlank(jsonObject.getStr("error_info"))) {
        return SensorsApiSimpleResponse.sbpError(jsonObject.getStr("error_info"));
      }
    }
    log.error("账号详情获取失败, {}", execute);
    throw new HttpException("账号详情获取失败");
  }


  public SensorsApiSimpleResponse<GetRoleResult> getRoleById(Integer id) {
    super.checkClient();
    Assert.isTrue(id != null, "角色id不能为空");

    final HttpResponse execute =
        HttpUtil.createGet(super.getSensorsDomain() + CommonConstants.SbpConstants.getRoleById()).form("id", id).addHeaders(
            super.getHeaderMap()).execute();
    if (execute.isOk()) {
      final JSONObject jsonObject = JSONUtil.parseObj(execute.body());
      String code = jsonObject.getStr("code");
      if (CommonConstants.SUCCESS.equals(code)) {
        final JSONObject data = jsonObject.getJSONObject("data");
        return SensorsApiSimpleResponse.ok(jsonObject.getStr("request_id"), jsonObject.getStr("code"),
            data.toBean(GetRoleResult.class));
      }else if (StringUtils.isNotBlank(jsonObject.getStr("error_info"))) {
        return SensorsApiSimpleResponse.sbpError(jsonObject.getStr("error_info"));
      }
    }
    log.error("角色详情获取失败, {}", execute);
    throw new HttpException("角色详情获取失败");

  }


  public SensorsApiSimpleResponse<HashSet<Statement>> getPermissionByAccountId(Integer id) {

    final SensorsApiSimpleResponse<GetAccountResult> account = getAccountById(id);

    if (account.isOk()){
      final List<Role> roles = account.getData().getRelatedRoles();
      if(CollectionUtil.isNotEmpty(roles)){
        HashSet<Statement> statements = new HashSet<>();
        roles.forEach(role -> {
          final SensorsApiSimpleResponse<GetRoleResult> roleById = getRoleById(role.getId());
          if (roleById.isOk() && CollectionUtil.isNotEmpty(roleById.getData().getStatements())){
            statements.addAll(roleById.getData().getStatements());
          }
        });
        return SensorsApiSimpleResponse.ok(account.getRequestId(), account.getCode(), statements);
      }
    }
    return null;
  }
}
