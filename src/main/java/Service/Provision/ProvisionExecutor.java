package Service.Provision;

import Service.ComputePath;
import SimulationImpl.ClockUtil;
import SimulationImpl.Tools;
import Topology.SimpleEdge;
import TrafficDescription.TransClient;

import java.util.Set;

public class ProvisionExecutor {
    ComputePath computePathThread;
    TransClient transClient;
    ClockUtil clock;

    public ProvisionExecutor(ComputePath computePathThread, ClockUtil clock) {
        this.computePathThread = computePathThread;
        this.transClient = TransClient.getInstance();
        this.clock = clock;

    }

    public void setTransClient(TransClient transClient) {
        this.transClient = transClient;
    }

    public void flushEdgeFutureLoad() {
        Set<SimpleEdge> edgeSet = this.computePathThread.graph.edgeSet();
        for (SimpleEdge currentEdge : edgeSet) {
            try {
                currentEdge.predictedEdgeTraffic = transClient.client
                        .getEdgePredictedData(Tools.inputEdgeDataFormatTrans(currentEdge.nowIntervalEdgeTraffic));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 将拿到的未来负载刷新到SimpleEdge.futureLoad属性中
            // 2018-06-11 根据时间向后选边预测数据的第i个元素(0-14)，作为futureload
            currentEdge.setFutureLoad(currentEdge.predictedEdgeTraffic.get(clock.getTimingIndexInHour()));
        }
    }
}