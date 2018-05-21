package TrafficDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/4/27.
 */
public class PredictedIntervalTraffic {
    public boolean migration;
    public List<Double> predictedIntervalTraffic = new ArrayList<Double>();

    public PredictedIntervalTraffic() {

    }

    public PredictedIntervalTraffic(double migration, List<Double> predictedIntervalTraffic) {
        this.migration = (migration >= 0.5) ? true : false;
        this.predictedIntervalTraffic = predictedIntervalTraffic;
    }
}
