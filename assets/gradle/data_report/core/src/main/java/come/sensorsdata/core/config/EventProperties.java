package come.sensorsdata.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "event")
@Configuration
public class EventProperties {
    /**
     * 上报类型
     */
    private ReportType reportType;

    private SDK sdk;

    public enum ReportType {
        SDK
    }

    @Data
    @Configuration
    public static class SDK {
        /**
         * 事件上报地址
         */
        private String serverUrl;
        /**
         * debugger是否写入数据库只在dev配置文件生效
         */
        private boolean writeData;
    }
}
