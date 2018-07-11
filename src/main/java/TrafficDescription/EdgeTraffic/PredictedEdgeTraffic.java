package TrafficDescription.EdgeTraffic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/5/10.
 */
public class PredictedEdgeTraffic {
    public List<Double> predictedIntervalTraffic = new ArrayList<Double>();

    public PredictedEdgeTraffic() {

    }

    public PredictedEdgeTraffic(List<Double> predictedIntervalTraffic) {
        this.predictedIntervalTraffic = predictedIntervalTraffic;
    }

}
