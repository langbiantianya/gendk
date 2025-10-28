package come.sensorsdata.core.handler;

public interface IDataHandler<R,T> {
    /**
     * 转换上报事件
     */
    R transform(T data);
}
