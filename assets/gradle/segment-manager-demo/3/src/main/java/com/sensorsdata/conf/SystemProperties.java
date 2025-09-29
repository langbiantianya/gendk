package com.sensorsdata.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "system")
@Configuration
public class SystemProperties {
    private SystemProfileProperties profile;

    @Data
    @Configuration
    public static class SystemProfileProperties {
        /**用户id字段*/
        private String userFiled;
        /**用户名字段*/
        private String username;
        /**分群批大小*/
        private int batchSize;
        /**sa权限id*/
        private String operationCode;
    }
}
