package come.sensorsdata.core.config;

import come.sensorsdata.core.handler.AbsSdkDataHandler;
import come.sensorsdata.core.service.impl.SdkEventServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

@Slf4j
public class EventServiceAutoConfig implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
        // 检查registry是否是ConfigurableListableBeanFactory的实例
        if (registry instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) registry;
            Environment environment = beanFactory.getBean(Environment.class);
            String reportType = environment.getProperty("event.report-type");
            if (EventProperties.ReportType.SDK.name().equals(reportType)) {
                // 获取所有AbsSdkDataHandler的实现类
                Map<String, AbsSdkDataHandler> dataHandlers = beanFactory.getBeansOfType(AbsSdkDataHandler.class);

                // 为每个实现类创建并注册对应的SdkEventServiceImpl
                for (Map.Entry<String, AbsSdkDataHandler> entry : dataHandlers.entrySet()) {
                    String dataHandlerName = entry.getKey();

                    // 构建BeanDefinition
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SdkEventServiceImpl.class);
                    builder.addConstructorArgReference(dataHandlerName); // 注入dataHandler
                    builder.addConstructorArgReference("initSdk"); // 注入ISensorsAnalytics

                    // 注册Bean
                    String eventServiceName = dataHandlerName.replace("DataHandler", "EventService");
                    registry.registerBeanDefinition(eventServiceName, builder.getBeanDefinition());
                    log.info("注册Bean {} 成功", eventServiceName);
                }
            }
        }
    }
}
