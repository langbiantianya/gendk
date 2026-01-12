package com.sensorsdata.app.handler;

import com.sensorsdata.analytics.javasdk.bean.EventRecord;
import come.sensorsdata.core.handler.AbsSdkDataHandler;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SdkMapDataHandlerImpl extends AbsSdkDataHandler<Map<String, Object>> {
    @Override
    @SneakyThrows
    public EventRecord transform(String eventName, List<Map<String, Object>> data) {
        return EventRecord.builder().build();
    }
}
