package com.sensorsdata.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "feishu")
@Configuration
public class FeishuProperties {
    /**
     * webhookUrl 地址
     */
    private String webhookUrl;
    /**
     * 消息类型
     */
    private String msg_type;
    /**
     * secret
     */
    private String secret;
    private FeishuCardProperties card;

    @Data
    public static class FeishuCardProperties {
        /**
         * 模板类型
         */
        private String type;
        /**
         * 模板id
         */
        private String template_id;
        /**
         * 模板版本号
         */
        private String template_version_name;
    }
}
