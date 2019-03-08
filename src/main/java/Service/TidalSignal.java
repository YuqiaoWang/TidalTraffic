package Service;

public class TidalSignal {
    private boolean arrive; // 潮汐是否到达

    private TidalSignal() {
        this.arrive = false;
    }

    private static class SingletonHolder {
        private static TidalSignal tidalSignal = new TidalSignal();
    }

    public synchronized static TidalSignal getInstance() {
        return SingletonHolder.tidalSignal;
    }

    /**
     * 获取当前潮汐标识状态
     */
    public boolean getStatus() {
        return this.arrive;
    }

    /**
     * 把标识调成[未到达]
     */
    public void setSignalOff() {
        this.arrive = false;
    }

    /**
     * 把标识调成[到达]
     */
    public void setSignalOn() {
        this.arrive = true;
    }
}