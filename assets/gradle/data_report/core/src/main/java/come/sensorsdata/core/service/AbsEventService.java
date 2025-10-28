package come.sensorsdata.core.service;


import come.sensorsdata.core.handler.IDataHandler;

public abstract class AbsEventService<R, T> implements IEventService<T> {
    protected final IDataHandler<R, T> dataHandler;

    protected AbsEventService(IDataHandler<R, T> dataHandler) {
        this.dataHandler = dataHandler;
    }
}
