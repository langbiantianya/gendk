package come.sensorsdata.core.config;

import com.sensorsdata.analytics.javasdk.ISensorsAnalytics;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.sensorsdata.analytics.javasdk.consumer.BatchConsumer;
import com.sensorsdata.analytics.javasdk.consumer.DebugConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;


@Configuration
@Slf4j
@Import(EventServiceAutoConfig.class)
public class EventConfig {
    @Bean(destroyMethod = "shutdown")
    @Conditional(ReportTypeSdkCondition.class)
    public ISensorsAnalytics initSdk(EventProperties eventProperties, Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0 && activeProfiles[0].equals("dev")) {
            //debug 模式(此模式只适用于测试集成 SDK 功能，千万不要使用到生产环境！！！)
            log.info("Init debug consumer.");
            return new SensorsAnalytics(new DebugConsumer(eventProperties.getSdk().getServerUrl(), eventProperties.getSdk().isWriteData()));
        } else {
            //网络批量发送模式（此模式在容器关闭的时候，如果存在数据还没有发送完毕，就会丢失未发送的数据！！！）
            log.info("Init batch consumer.");
            return new SensorsAnalytics(new BatchConsumer(eventProperties.getSdk().getServerUrl()));
        }
    }

    static class ReportTypeSdkCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Environment env = context.getEnvironment();
            String reportType = env.getProperty("event.report-type"); // 读取配置
            return "SDK".equals(reportType);
        }
    }
}
