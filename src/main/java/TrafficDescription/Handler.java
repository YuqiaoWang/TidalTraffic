package TrafficDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/5/3.
 */
public class Handler implements TrafficDataService.Iface {
    public PredictedIntervalTrafficData getPredictedData(NowIntervalTrafficData nowIntervalTrafficData) {
        List<Double> testTrafficData = new ArrayList<>();
        testTrafficData.add(0.2);
        testTrafficData.add(0.3);
        testTrafficData.add(0.5);
        PredictedIntervalTrafficData testOutput = new PredictedIntervalTrafficData(0.6, testTrafficData);
        return testOutput;
    }
}
