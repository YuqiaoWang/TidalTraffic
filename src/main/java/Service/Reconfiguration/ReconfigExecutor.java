package Service.Reconfiguration;

import Service.Service;
import Service.ComputePath;
import SimulationImpl.ClockUtil;
import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import TrafficDescription.PredictedIntervalTraffic;
import TrafficDescription.TrafficDataService;
import TrafficDescription.TransClient;
import org.jgrapht.EdgeFactory;
import org.jgrapht.GraphPath;
import java.lang.Deprecated;
import java.util.*;

/**
 * Created by yuqia_000 on 2018/5/10.
 */
public class ReconfigExecutor {
    ComputePath computePathThread;
    Map<String, Service> serviceMap; // 业务集
    static TreeSet<Service> servicesToReconfig; // 待重构的业务集
    TransClient transClient;
    ReconfigStatistic reconfigStatistic;
    ClockUtil clock;

    public ReconfigExecutor(ComputePath computePathThread, Map<String, Service> serviceMap, ClockUtil clock) {
        this.computePathThread = computePathThread;
        this.serviceMap = serviceMap;
        servicesToReconfig = new TreeSet<Service>(new ServiceComparator());
        reconfigStatistic = computePathThread.reconfigStatistic;
        this.clock = clock;
        this.transClient = TransClient.getInstance();
    }

    @Deprecated
    public void setTransClient(TransClient transClient) {
        this.transClient = transClient;
    }

    // TODO:重构执行体
    public void doReconfig(Area area, PredictedIntervalTraffic predictedIntervalTraffic) {
        // 对于高负载的每一条链路，将这些链路上跑的业务放入待重构的业务集（这个集合是按占用波长数和跳数降序排列的）
        for (SimpleEdge currentEdge : computePathThread.graph.edgeSet()) {
            // for(SimpleEdge currentEdge : area.edges) {
            // 对area里每条边，都去拿其预测结果，判断是否为高负载
            try {
                currentEdge.predictedEdgeTraffic = transClient.client
                        .getEdgePredictedData(Tools.inputEdgeDataFormatTrans(currentEdge.nowIntervalEdgeTraffic));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 将拿到的未来负载刷新到SimpleEdge.futureLoad属性中
            // 2018-06-11 根据时间向后选边预测数据的第i个元素(0-14)，作为futureload
            currentEdge.setFutureLoad(currentEdge.predictedEdgeTraffic.get(clock.getTimingIndexInHour()));
            // if((double)currentEdge.numberOfOccupatedWavelength /
            // currentEdge.numberOfWavelenth > Tools.DEFAULTTHRESHOLD) {
            if (currentEdge.getFutureLoad() > Tools.DEFAULTTHRESHOLD) { // 对于未来负载超过门限值的link
                for (String serviceId : currentEdge.serviceOnWavelength) { // 边上的每个业务
                    if (serviceId != null) {
                        Service currentService = serviceMap.get(serviceId);
                        if (!currentService.isReconfiged()) { // 若该业务没有被重构过
                            servicesToReconfig.add(serviceMap.get(serviceId)); // 就将其放入待重构的业务集合中
                        }
                    }
                }
            }
        }

        // TODO: 当【某个全局负载指标】高于门限时，一直执行重构，直到低于门限
        while (reconfigStatistic.loadBalanceTarget >= Tools.THREASHOLD_LOAD_BALANCE_TARGET
                && !servicesToReconfig.isEmpty()) {
            // 从待排序业务集中最前面的业务开始排序
            Service currentService = servicesToReconfig.first();
            servicesToReconfig.remove(currentService); // 从待重构业务集中移除正在重构的业务
            // TODO: 等边预测模型完成后，改为预测负载做边权
            computePathThread.reAllocatedWeightAsFutureTraffic(); // 重定义各边权重
            // computePathThread.reAllocateWeight(); //暂时使用当前负载做边权
            // TODO:算路（目前暂时用D算法，将来有可能改成K算法）
            GraphPath<Vertex, SimpleEdge> graphPath = computePathThread.findShortestPath(currentService,
                    computePathThread.graph); // 算路
            computePathThread.serviceGraphPathHashMap.put(currentService, graphPath);
            List<Integer> originOccupiedWavelengths = currentService.wavelengthesNumber; // 该业务搬移前占用的波长号
            try {
                GraphPath<Vertex, SimpleEdge> originPath = currentService.getGraphPath(); // 该业务搬移前的路由
                boolean isReAllocated = computePathThread.allocateResource(currentService);
                if (isReAllocated) {
                    leave(currentService, originPath, originOccupiedWavelengths);
                    System.out.println("[service " + currentService.serviceId + "] 已重构并重新分配资源");
                    currentService.reconfiged = true; // 标记该业务为已搬移
                    reconfigStatistic.numberOfReconfigedServices++; // 重构业务数+1
                } else {
                    System.out.println("[service " + currentService.serviceId + "] 在新路径上没有找到可用资源，不重构");
                    reconfigStatistic.numberOfFailedServices++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            reconfigStatistic.computeLoadBalanceTarget();
        }
        // 跳出循环即重构结束
        computePathThread.setWeightAsMetric(); // 重构完后将边权改为路长

    }

    public static void leave(Service service, GraphPath<Vertex, SimpleEdge> originPath,
            List<Integer> originOccupiedWavelengths) {
        for (int i = 0; i < originOccupiedWavelengths.size(); i++) {
            int currentWavelenthNumber = originOccupiedWavelengths.get(i);
            Iterator<SimpleEdge> edgeIterator = originPath.getEdgeList().iterator();
            while (edgeIterator.hasNext()) {
                SimpleEdge currentEdge = edgeIterator.next();
                currentEdge.wavelenthOccupation[currentWavelenthNumber] = false;
                currentEdge.numberOfOccupatedWavelength -= 1;
                currentEdge.serviceOnWavelength[currentWavelenthNumber] = null; // 把"该link的该波长号是哪个业务占用的"这个信息清除
            }

        }
        service.wavelengthesNumber.clear();
        System.out.println("[service " + service.serviceId + "] has released the resource");
    }

}
