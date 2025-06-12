package com.sensorsdata.analytics.sso.controller;

import com.sensorsdata.analytics.sso.entity.RoleInfo;
import com.sensorsdata.analytics.sso.openapi.api.ApiAccounts;
import com.sensorsdata.analytics.sso.openapi.entity.AccountItem;
import com.sensorsdata.analytics.sso.service.SsoServerImpl;
import com.sensorsdata.analytics.sso.service.SsoVerifier;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.core.impl.NameIDImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collections;

/**
 * 可以实现自己的Controller，用来满足特殊情况的需求
 */
@RestController
@Slf4j
public class TestController {

    ResourceLoader resourceLoader;
    private final JdbcTemplate jdbcTemplate;
    private final ApiAccounts apiAccounts;

    private final SsoServerImpl ssoVerifier;

    public TestController(ResourceLoader resourceLoader, DataSource dataSource, ApiAccounts apiAccounts, SsoVerifier ssoVerifier) {
        this.resourceLoader = resourceLoader;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.apiAccounts = apiAccounts;

        this.ssoVerifier = (SsoServerImpl) ssoVerifier;
    }

    @Value("${saml2.sp-metadata-url}")
    private String spMetadataPath;


    @GetMapping("/saml/sp/metadata")
    public ResponseEntity<Resource> getSpMetadata() throws MalformedURLException {
//        classpath:saml/sensorsKeystore.jks

        File file = new File(spMetadataPath);
        // 读取文件为资源
        Path filePath = file.toPath();
        Resource resource = new UrlResource(filePath.toUri());

        // 设置内容类型为 XML
        String contentType = "application/xml";


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"") // 设置为 inline 可直接在浏览器中查看
                .body(resource);

    }

    @GetMapping("/saml/user/current")
    public ResponseEntity<String> getCurrentUser() {
        log.info("try get current user");
        // 获取邮箱信息
        NameIDImpl principal = (NameIDImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getValue();
        log.info("email: {}", email);
        return ResponseEntity.ok(email);

    }
}
