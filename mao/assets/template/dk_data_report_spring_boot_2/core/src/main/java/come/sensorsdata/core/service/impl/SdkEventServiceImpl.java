package come.sensorsdata.core.service.impl;

import com.sensorsdata.analytics.javasdk.ISensorsAnalytics;
import com.sensorsdata.analytics.javasdk.bean.EventRecord;
import come.sensorsdata.core.handler.IDataHandler;
import come.sensorsdata.core.service.AbsEventService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SdkEventServiceImpl<T> extends AbsEventService<EventRecord, T> {
    private final ISensorsAnalytics sensorsAnalytics;

    public SdkEventServiceImpl(IDataHandler<List<EventRecord>, List<T>> dataHandler, ISensorsAnalytics sensorsAnalytics) {
        super(dataHandler);
        this.sensorsAnalytics = sensorsAnalytics;
    }

    @Override
    public void track(String eventName, List<T> data) {
        List<EventRecord> eventRecord = this.dataHandler.transform(eventName, data);
        for (EventRecord record : eventRecord) {
            try {
                sensorsAnalytics.track(record);
            } catch (Exception e) {
                log.error("SdkEventServiceImpl track error, eventRecord: {}", eventRecord, e);
            }

        }

    }
}
