package Topology;

import SimulationImpl.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia on 2017/6/20.
 */

/**
 * 域
 */
public class Area {
    public String areaId;               //域id
    public double threshold;            //门限
    public volatile double load;        //域负载
    public double totalCapacity;        //总容量
    public int numberOfEdges;           //域内link个数
    //public static double DEFAULTTHRESHOLD = 0.7;
    List<Vertex> exportVertexList;

    public List<SimpleEdge> edges;      //link 列表
    public List<Vertex> vertices;       //节点 列表

    public Area(String areaId) {
        this.areaId = areaId;
        this.load = 0;
        this.numberOfEdges = 0;
        this.threshold = Tools.DEFAULTTHRESHOLD;
        this.totalCapacity = 0;

        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<SimpleEdge>();
    }

    public void initialLoad() {
        this.load = 0;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public void addNumverOfEdges() {
        this.numberOfEdges +=1;
        this.totalCapacity = numberOfEdges * Tools.DEFAULTNUMBEROFWAVELENTHES;
    }

    /**
     * 更新area的负载值（即累加各link负载）
     */
    public void flushLoad() {
        this.load = 0;
        for(SimpleEdge edge : edges) {
            this.load += edge.numberOfOccupatedWavelength;
        }
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.areaId);
    }

    @Override
    public boolean equals(Object obj) {
        Area x = (Area)obj;
        if(x.areaId.equals(this.areaId)) {
            return true;
        }else {
            return false;
        }
    }

}
