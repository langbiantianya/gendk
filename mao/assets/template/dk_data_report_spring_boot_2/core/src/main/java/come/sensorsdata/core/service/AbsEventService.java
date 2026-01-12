package come.sensorsdata.core.service;


import come.sensorsdata.core.handler.IDataHandler;

import java.util.List;

public abstract class AbsEventService<R, T> implements IEventService<T> {
    protected final IDataHandler<List<R>, List<T>> dataHandler;

    protected AbsEventService(IDataHandler<List<R>, List<T>> dataHandler) {
        this.dataHandler = dataHandler;
    }
}
