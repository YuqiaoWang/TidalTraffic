package Service.Reconfiguration;

import Service.Service;
import Service.ComputePath;
import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import TrafficDescription.PredictedIntervalTraffic;
import org.jgrapht.GraphPath;

import java.util.*;

/**
 * Created by yuqia_000 on 2018/5/10.
 */
public class ReconfigExecutor {
    ComputePath computePathThread;
    Map<String, Service> serviceMap;            //业务集
    static TreeSet<Service> servicesToReconfig; //待重构的业务集

    double loadBalanceTarget;                   //TODO:全局负载均衡指标，目前没想好是啥

    public ReconfigExecutor(ComputePath computePathThread, Map<String, Service> serviceMap) {
        this.computePathThread = computePathThread;
        this.serviceMap = serviceMap;
        servicesToReconfig = new TreeSet<Service>(new ServiceComparator());
        loadBalanceTarget = Tools.INIT_LOAD_BALANCE_TARGET;
    }

    //TODO:重构执行体
    public void doReconfig(Area area, PredictedIntervalTraffic predictedIntervalTraffic) {

        //对于高负载的每一条链路，将这些链路上跑的业务放入待重构的业务集（这个集合是按占用波长数和跳数降序排列的）
        for(SimpleEdge currentEdge : area.edges) {
            //TODO:对area里每条边，都去拿其预测结果，判断是否为高负载
            //TODO：将拿到的未来负载刷新到SimpleEdge.futureLoad属性中
            //现在暂时用当前负载
            //simpleEdge.predictedEdgeTraffic =
            if(currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth > Tools.DEFAULTTHRESHOLD) {
                for(String serviceId : currentEdge.serviceOnWavelength) {
                    Service currentService = serviceMap.get(serviceId);
                    if(!currentService.isReconfiged()) {            //若该业务没有被重构过
                        servicesToReconfig.add(serviceMap.get(serviceId));
                    }

                }
            }
        }

        //TODO: 当【某个全局负载指标】高于门限时，一直执行重构，直到低于门限

        while(loadBalanceTarget < Tools.THREASHOLD_LOAD_BALANCE_TARGET) {
            //从待排序业务集中最前面的业务开始排序
            Service currentService = servicesToReconfig.first();
            servicesToReconfig.remove(currentService);              //从待重构业务集中移除正在重构的业务
            //Vertex srcNode = currentService.srcNode;
            //Vertex desNode = currentService.desNode;
            computePathThread.reAllocatedWeightAsFutureTraffic();   //重定义各边权重
            //TODO:算路（目前暂时用D算法，将来有可能改成K算法）
            //GraphPath graphPath = computePathThread.findShortestPath(currentService, computePathThread.graph);  //算路
            boolean isReAllocated = computePathThread.allocateResource(currentService);
            if(isReAllocated) {
                System.out.println("[service " + currentService.serviceId + "] 已重构并重新分配资源");
            }else {
                System.out.println("[service " + currentService.serviceId + "] 在新路径上没有找到可用资源，不重构");
            }
        }

        //跳出循环即重构结束

    }
}
