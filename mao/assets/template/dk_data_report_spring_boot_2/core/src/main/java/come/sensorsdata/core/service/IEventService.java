package come.sensorsdata.core.service;

import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface IEventService<T> {
    @Async
    void track(String eventName, List<T> data);
}
