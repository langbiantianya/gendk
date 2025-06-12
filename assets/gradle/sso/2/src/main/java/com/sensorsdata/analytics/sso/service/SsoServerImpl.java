package com.sensorsdata.analytics.sso.service;

import com.sensorsdata.analytics.sso.api.LoginApi;
import com.sensorsdata.analytics.sso.api.LoginApiException;
import com.sensorsdata.analytics.sso.api.LoginApiResponse;
import com.sensorsdata.analytics.sso.config.ApiConfig;
import com.sensorsdata.analytics.sso.entity.RoleInfo;
import com.sensorsdata.analytics.sso.model.CheckPasswordResult;
import com.sensorsdata.analytics.sso.model.CheckTokenResult;
import com.sensorsdata.analytics.sso.model.LoginServerUserInfo;
import com.sensorsdata.analytics.sso.openapi.api.ApiAccounts;
import com.sensorsdata.analytics.sso.openapi.entity.AccountItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SsoServerImpl implements SsoVerifier {

    private final ApiConfig apiConfig;
    private final JdbcTemplate jdbcTemplate;
    private final ApiAccounts apiAccounts;

    @Value("${sensors.login.defaultRole}")
    private String defaultRole;

    public SsoServerImpl(ApiConfig apiConfig, DataSource dataSource, ApiAccounts apiAccounts) {
        this.apiConfig = apiConfig;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.apiAccounts = apiAccounts;
    }

    @Nullable
    public RoleInfo getRole(String project, String roleName) {
        String queryProjectSql = "select id from project where name = ?";
        RoleInfo roleInfo = null;
        Integer projectId = null;
        try {
            // 查询 name 字段，参数为 cname（防止 SQL 注入）
            projectId = jdbcTemplate.queryForObject(queryProjectSql, Integer.class, project);

        } catch (Exception e) {
            log.error("查询用户 name 失败，project={}", project, e);
        }
        if (projectId != null) {
            String querySql = "select id, name, project_id from role where cname = ? and project_id = ?";
            try {
                // 查询 name 字段，参数为 cname（防止 SQL 注入）
                roleInfo = jdbcTemplate.queryForObject(querySql,
                        (rs, rowNum) -> new RoleInfo(rs.getInt("id"), rs.getString("name"), rs.getInt("project_id")),
                        roleName, projectId);

            } catch (Exception e) {
                log.error("查询用户 name 失败，cname={}", roleName, e);
            }
        }
        return roleInfo;
    }

    /**
     * 该方法用于需要根据客户提供的接口文档，
     * 去调用客户接口获取登录用户的信息，并把用户信息封装成CheckTokenResult对象返回。
     * <p>
     * 当客户点击链接 http://ip地址:8107端口/api/sso/login 时
     * nginx会监听到路径 /api/sso/login，然后会将请求转发到 /sso/login
     * 在/sso/login接口中会调用SsoVerifier接口的getUserInformation方法
     * getUserInformation方法就是返回登录用户信息封装而成的CheckTokenResult对象
     * 而该类就实现了SsoVerifier接口，因此开发者需要在该getUserInformation方法中返回
     * 获取到的登录用户信息封装的CheckTokenResult对象
     * 至于如果获取登录用户信息，需要结合客户的接口文档来看如何调用客户接口，从而获取登录用户信息
     *
     * @param request 客户平台请求sso/login接口时的request，
     *                可能会携带token或code等信息，通过token或code来获取登录用户信息
     * @return 返回用登录用户信息封装的对象
     */
    @Override
    public CheckTokenResult getUserInformation(HttpServletRequest request, HttpServletResponse response) {
        String project = request.getParameter("project");
        log.info("start get user info");
        log.info("project: {}", project);
        // 获取邮箱信息
        DefaultSaml2AuthenticatedPrincipal principal =
                (DefaultSaml2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                // 替换为idp中实际的值
        String email = principal.getFirstAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
        List<String> groups = principal.getAttribute("http://schemas.microsoft.com/ws/2008/06/identity/claims/groups");
        String role = principal.getFirstAttribute("http://schemas.microsoft.com/ws/2008/06/identity/claims/role");
        log.info("email: {}", email);
        log.info("role: {}", role);
        log.info("group: {}", groups);
// 这一块如果没有角色映射需求可简化直接返回 CheckTokenResult(email)
        CheckTokenResult result = null;

//        判断用户是否存在
        AccountItem account = apiAccounts.getAccounts().getAccounts().stream().filter(accountItem -> accountItem.getAccount().getUsername().equals(email)).findFirst().orElse(null);

        if (account == null) {
            // 封装从客户平台获取的登录用户信息
            // 设置用户名&角色，标识该登录用户的账号
            if (groups != null && groups.contains("按需修改 允许授权组")) {
                if (groups.size() > 1 && StringUtils.hasText(project)) {
                    RoleInfo roleInfo = getRole(project, groups.get(1));
                    if (roleInfo != null) {
                        result = new CheckTokenResult(email, roleInfo.getName());
                    } else {
                        result = new CheckTokenResult(email);
                    }
                } else {
                    result = new CheckTokenResult(email);
                }
            }

        } else {
            if (groups != null && groups.contains("按需修改 允许授权组")) {
                if (groups.size() > 1 && StringUtils.hasText(project)) {
//                      修改用户角色
                    RoleInfo roleInfo = getRole(project, groups.get(1));
                    if (roleInfo != null) {
                        apiAccounts.updateAccount(account.getAccount().getId(), Collections.singletonList(roleInfo.getId()), roleInfo.getProjectId());
                    }
                } else if (groups.size() == 1 && StringUtils.hasText(project) && StringUtils.hasText(defaultRole)) {
                    RoleInfo roleInfo = getRole(project, defaultRole);
                    if (roleInfo != null) {
                        apiAccounts.updateAccount(account.getAccount().getId(), Collections.singletonList(roleInfo.getId()), roleInfo.getProjectId());
                    }
                }
                result = new CheckTokenResult(email);
            }
        }

        // 打印 result 日志
        log.info("result: {}", result);
        if (StringUtils.hasText(project) && result != null) {
            result.setProjectName(project);
        }

        return result;
    }

    /**
     * 该方法用于通过神策登录页面进行登录账号和密码的校验，如果在神策系统验证，需要有mysql相关配置
     * <p>
     * 1、如果客户不想支持神策账号密码登录，那需要将application.yml中的checkPassword配置项设置为false，checkPassword方法直接返回null就可以了。
     * 2、客户想同时支持神策账号密码登录，需要将application.yml中的checkPassword配置项设置为true，checkPassword方法里调用JDBCLogin.checkPassword()方法就可以。
     * 3、如果客户有其它需求，例如携带账号密码去客户的登录认证接口进行校验
     *
     * @param userInfo userInfo参数封装了用户在神策登录界面输入的账号和密码
     * @param project  项目名
     * @return 返回用登录用户信息封装的对象
     */
    @Override
    public CheckPasswordResult checkPassword(LoginServerUserInfo userInfo, String project) {
        //日志打印
        log.info("enter postLoginApi method success");
        CheckPasswordResult checkPasswordResult = new CheckPasswordResult();
        LoginApiResponse loginApiResponse = null;
        try {
            loginApiResponse = LoginApi.postApi(userInfo, project, apiConfig.isGlobal(), apiConfig.getUrl());
        } catch (LoginApiException e) {
            checkPasswordResult.setErrorMessage("校验用户时出现服务端异常!");
            return checkPasswordResult;
        }
        if (loginApiResponse == null) {
            return null;
        }
        log.info("loginApiResponse is {}", loginApiResponse);
        checkPasswordResult.setUsername(loginApiResponse.getUsername());
        return checkPasswordResult;
    }

}
