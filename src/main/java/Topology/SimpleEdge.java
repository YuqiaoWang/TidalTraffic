package Topology;

import SimulationImpl.Tools;
import TrafficDescription.EdgeTraffic.NowIntervalEdgeTraffic;
import TrafficDescription.EdgeTraffic.PredictedEdgeTraffic;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia on 2017/6/15.
 */

/**
 * 边
 */
public class SimpleEdge extends DefaultWeightedEdge {
    public Vertex srcVertex;              //源节点
    public Vertex desVertex;              //宿节点
    public double capacity;               //容量
    public double unitWavelength = 6.25;  //每个波长的固定带宽
    public double metric;                 //路长（用作边权）
    public int numberOfWavelenth;         //一条link的波长总数
    public boolean[] wavelenthOccupation; //波长占用情况（true为占用，false为空闲）
    public String[] serviceOnWavelength;    //该link的该波长号是哪个业务占用的
    public int numberOfOccupatedWavelength; //被占用的波长个数
    //public static int DEFAULTNUMBEROFWAVELENTHES = 30;

    /**重构用到的属性*/
    public NowIntervalEdgeTraffic nowIntervalEdgeTraffic;   //边2h的包装的流量
    public List<Double> predictedEdgeTraffic;               //预测到的下1h的流量
    private double futureLoad;                              //未来时刻的负载

    public SimpleEdge() {

    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex, double metric) {
        this(srcVertex, desVertex, 100, Tools.DEFAULTNUMBEROFWAVELENTHES, metric);//默认链路带宽容量和波长数
    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex, double capacity, int numberOfWavelenth, double metric) {
        this.srcVertex = srcVertex;
        this.desVertex = desVertex;
        this.capacity = capacity;
        this.metric = metric;
        this.numberOfWavelenth = numberOfWavelenth;
        this.wavelenthOccupation = new boolean[numberOfWavelenth];
        this.serviceOnWavelength = new String[numberOfWavelenth];
        this.numberOfOccupatedWavelength = 0;
        for(int i = 0; i < numberOfWavelenth; i++) {
            wavelenthOccupation[i] = false;
        }
        this.nowIntervalEdgeTraffic = new NowIntervalEdgeTraffic(this.srcVertex.nodeId, this.desVertex.nodeId);
        this.predictedEdgeTraffic = new ArrayList<>();
        this.futureLoad = 0;
    }

    /*
    @Override
    public int hashCode() {
        int x = srcVertex.hashCode();
        int y = desVertex.hashCode();
        return (x * y + x + y);
    }*/

    public double getFutureLoad() {
        return this.futureLoad;
    }

    public void setFutureLoad(double futureLoad) {
        this.futureLoad = futureLoad;
    }

    @Override
    public boolean equals(Object obj) {
        SimpleEdge x = (SimpleEdge)obj;
        if(x.srcVertex.nodeId.equals(this.srcVertex.nodeId) &&
                x.desVertex.nodeId.equals(this.desVertex.nodeId)) {
            return true;
            //}else if(x.srcVertex.nodeId.equals(this.desVertex.nodeId) || x.desVertex.nodeId.equals(this.srcVertex.nodeId)) {
        }else if(x.srcVertex.nodeId.equals(this.desVertex.nodeId) &&
                x.desVertex.nodeId.equals(this.srcVertex.nodeId)) {
            return true;
        }else {
            return false;
        }

    }

    @Override
    public String toString() {
        String srcNodeId = this.srcVertex.getNodeId();
        String desNodeId = this.desVertex.getNodeId();
        String edgeId = (Integer.valueOf(srcNodeId) < Integer.valueOf(desNodeId)) ?
                "edge" + srcNodeId + desNodeId : "edge" + desNodeId + srcNodeId;
        return edgeId;
    }

}
