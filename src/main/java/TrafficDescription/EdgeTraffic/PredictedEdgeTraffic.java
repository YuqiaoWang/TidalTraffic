package TrafficDescription.EdgeTraffic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/5/10.
 */
public class PredictedEdgeTraffic {
    public boolean migration;
    public List<Double> predictedIntervalTraffic = new ArrayList<Double>();

    public PredictedEdgeTraffic() {

    }

    public PredictedEdgeTraffic(double migration, List<Double> predictedIntervalTraffic) {
        this.migration = (migration > 0.5) ? true : false;
        this.predictedIntervalTraffic = predictedIntervalTraffic;
    }

}
