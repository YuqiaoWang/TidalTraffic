package Service.Reconfiguration;

/**
 * Created by yuqia_000 on 2018/5/21.
 */

import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;

import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.HashMap;
import java.util.Set;

/**
 * 本类的目的仅是做重构时的指标统计，不涉及业务逻辑方法
 */
public class ReconfigStatistic {
    public int reconfigTimes; // 重构次数
    public int numberOfReconfigedServices; // 重构成功业务数
    public int numberOfFailedServices; // 重构失败业务数

    public double loadBalanceTarget; // TODO:全局负载均衡指标，目前没想好是啥
    // public HashMap<String, Double> loadBalanceHashMap; //TODO:存的是全局负载均衡指标，目前没想好是啥
    // public HashMap<String, Area> areaHashMap;
    public Set<SimpleEdge> edgeSet;

    public ReconfigStatistic(HashMap<String, Area> areaHashMap, SimpleWeightedGraph<Vertex, SimpleEdge> graph) {
        this.reconfigTimes = 0;
        this.numberOfReconfigedServices = 0;
        this.numberOfFailedServices = 0;
        this.loadBalanceTarget = Tools.INIT_LOAD_BALANCE_TARGET;
        /*
         * this.loadBalanceHashMap = new HashMap<>(); this.areaHashMap = areaHashMap;
         * Set<String> areaIdSet = areaHashMap.keySet(); for(String areaId : areaIdSet)
         * { loadBalanceHashMap.put(areaId, Tools.INIT_LOAD_BALANCE_TARGET); }
         */
        this.edgeSet = graph.edgeSet();
    }

    // TODO:重新计算全局负载均衡指标（应该是实时的）
    // TODO:暂时将全局负载均衡变量定义为均方差
    public void computeLoadBalanceTarget(Area area) {

        double meanEdgeLoad = area.load / area.numberOfEdges;
        double meanSquareError = 0;
        for (SimpleEdge currentEdge : area.edges) {
            double diff = meanEdgeLoad
                    - (double) currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth;
            meanSquareError += diff * diff;
        }
        /*
         * double loadBalanceTarget = meanSquareError / area.numberOfEdges;
         * loadBalanceHashMap.put(area.areaId, loadBalanceTarget);
         */
        this.loadBalanceTarget = meanSquareError;
    }

    public void computeLoadBalanceTarget() {
        double meanEdgeLoad = 0;
        double meanSquareError = 0;

        for (SimpleEdge currentEdge : this.edgeSet) {
            double diff = meanEdgeLoad
                    - (double) currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth;
            meanSquareError += diff * diff;
        }

        /*
         * double loadBalanceTarget = meanSquareError / area.numberOfEdges;
         * loadBalanceHashMap.put(area.areaId, loadBalanceTarget);
         */
        this.loadBalanceTarget = meanSquareError;
    }

}
