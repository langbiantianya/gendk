package come.sensorsdata.core.handler;

import com.sensorsdata.analytics.javasdk.bean.EventRecord;

import java.util.List;

public abstract class AbsSdkDataHandler<T> implements IDataHandler<List<EventRecord>, List<T>> {
}
