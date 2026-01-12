package com.sensorsdata.analytics.sso.api;

import com.sensorsdata.analytics.sso.model.LoginServerUserInfo;
import com.sensorsdata.analytics.sso.utils.Constants;
import com.sensorsdata.analytics.sso.utils.HttpUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 校验账号密码 api，要求 sbp 版本要高于 1.4.0.3726
 * @description: sensorsdata code
 * @author: zhaozhiqi
 * @email: zhaozhiqi@sensorsdata.cn
 * @date: 2023/5/6 15:14
 */
@Slf4j
@UtilityClass
public class LoginApi {
  private final String LOGIN_API_URI = "/api/v2/sbp/auth/login/simple";

  public LoginApiResponse postApi(LoginServerUserInfo userInfo, String project, boolean isGlobal, String url) throws LoginApiException {
    LoginApiRequest loginApiRequest = LoginApiRequest.builder()
            .accountName(userInfo.getUsername())
            .password(userInfo.getPassword()).build();
    String apiUri = getLoginApiUri(project, isGlobal, url);
    try {
      return HttpUtils.post(apiUri, loginApiRequest, LoginApiResponse.class, 0, 0).getBody();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().is4xxClientError()) {
        return null;
      } else {
        throw new LoginApiException(e.getMessage());
      }
    } catch (InterruptedException e) {
      log.error("InterruptedException !!", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      throw new LoginApiException(e.getMessage());
    }
    return null;
  }

  private String getLoginApiUri(String project, boolean isGlobal, String url) {
    if (url == null || url.isEmpty()) {
      // 如果没有配置url，先拼接处IP地址：端口
      // 其中IP是本机IP地址，因为单点登录是部署在神策分析所在的机器上，所以可以直接请求本地IP地址+8107 端口+xxxx
      url = "http://" + Constants.IP + ":8107";
    }
    // 拼接 url，url 中的参数有projectName，可能有is_global，表示是否是全局账号，ssoProperty中的is_global默认是true，表示全局
    if (!isGlobal) {
      // 如果没有指定任何的项目，则设置为默认项目
      url = url + LOGIN_API_URI + "?project=" + project + "&is_global=false";
    } else {
      url = url + LOGIN_API_URI + "?is_global=true";
    }
    return url;
  }
}
