package Service;

public class ServiceTransMsg {
    private boolean transCompleted; // 业务传输完成标识

    private int serviceIndex; // 已生成的业务数

    private ServiceTransMsg() {
        this.transCompleted = false;
        this.serviceIndex = 0;
    }

    /**
     * 将传输状态更改为已完成
     */
    public synchronized void complete() {
        this.transCompleted = true;
    }

    /**
     * 传输完成一个业务
     */
    public synchronized void addIndex() {
        this.serviceIndex++;
    }

    private static class SingletonHolder {
        private static ServiceTransMsg serviceTransMsg = new ServiceTransMsg();
    }

    // 获取单例的ServiceTransMsg
    public synchronized static ServiceTransMsg getInstance() {
        return SingletonHolder.serviceTransMsg;
    }

    /**
     * 获取当前传输状态
     */
    public boolean getStatus() {
        return this.transCompleted;
    }

    /**
     * 获取当前已生成的业务数量
     */
    public int getIndex() {
        return this.serviceIndex;
    }

}