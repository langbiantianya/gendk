package come.sensorsdata.core.service.impl;

import com.sensorsdata.analytics.javasdk.ISensorsAnalytics;
import com.sensorsdata.analytics.javasdk.bean.EventRecord;

import come.sensorsdata.core.handler.IDataHandler;
import come.sensorsdata.core.service.AbsEventService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SdkEventServiceImpl<T> extends AbsEventService<EventRecord, T> {
    private final ISensorsAnalytics sensorsAnalytics;

    public SdkEventServiceImpl(IDataHandler<EventRecord, T> dataHandler, ISensorsAnalytics sensorsAnalytics) {
        super(dataHandler);
        this.sensorsAnalytics = sensorsAnalytics;
    }

    @Override
    public void track(T data) {
        EventRecord eventRecord = this.dataHandler.transform(data);
        try {
            sensorsAnalytics.track(eventRecord);
        } catch (Exception e) {
            log.error("SdkEventServiceImpl track error, eventRecord: {}", eventRecord, e);
        }
    }
}
