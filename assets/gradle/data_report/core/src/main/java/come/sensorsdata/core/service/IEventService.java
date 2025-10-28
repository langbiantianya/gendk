package come.sensorsdata.core.service;

import org.springframework.scheduling.annotation.Async;

public interface IEventService<T> {
    @Async
    void track(String eventName,T data);
}
