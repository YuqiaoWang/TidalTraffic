package TrafficDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/4/27.
 */
public class NowIntervalTraffic {
    public String areaId;
    public double timeOfHour;
    public List<Double> nowIntervalTraffic;

    public NowIntervalTraffic(String areaId) {
        this.areaId = areaId;
        this.timeOfHour = 0.0;
        this.nowIntervalTraffic = new ArrayList<Double>();
    }

    public NowIntervalTraffic(String areaId, double timeOfHour, List<Double> nowIntervalTraffic) {
        this.timeOfHour = timeOfHour;
        this.nowIntervalTraffic = nowIntervalTraffic;
    }

    public double getTimeOfHour() {
        return timeOfHour;
    }

    public void setTimeOfHour(double timeOfHour) {
        this.timeOfHour = timeOfHour;
    }

    public List<Double> getNowIntervalTraffic() {
        return nowIntervalTraffic;
    }

    public void setNowIntervalTraffic(List<Double> nowIntervalTraffic) {
        this.nowIntervalTraffic = nowIntervalTraffic;
    }

    public void removeOneHourTrafficData() {
        if(this.nowIntervalTraffic.size() > 15) {
            for(int i = 0; i < 15; i++) {
                this.nowIntervalTraffic.remove(0);
            }

        }
    }
}
