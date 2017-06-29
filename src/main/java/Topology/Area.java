package Topology;

import java.util.List;

/**
 * Created by yuqia on 2017/6/20.
 */
public class Area {
    public String areaId;
    public double threshold;
    public double load;
    public double totalCapacity;
    public int numberOfEdges;
    public static double DEFAULTTHRESHOLD = 0.7;
    List<Vertex> exportVertexList;

    public Area(String areaId) {
        this.areaId = areaId;
        this.load = 0;
        this.numberOfEdges = 0;
        this.threshold = DEFAULTTHRESHOLD;
        this.totalCapacity = 0;
    }

    public void initialLoad() {
        this.load = 0;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public void addNumverOfEdges() {
        this.numberOfEdges +=1;
        this.totalCapacity = numberOfEdges * SimpleEdge.DEFAULTNUMBEROFWAVELENTHES;
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
