package SimulationImpl;

/**
 * Created by yuqia_000 on 2018/6/12.
 */
public class ClockUtil {
    private long startTime; //系统初试时间
    private int timingIndexInHour; //1h内时间推移次数（即统计写入次数）参数

    public ClockUtil() {
        startTime = System.currentTimeMillis();
        timingIndexInHour = 0;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setTimingIndexInHour(int times) {
        timingIndexInHour = times;
    }

    public int getTimingIndexInHour() {
        return timingIndexInHour;
    }
}
