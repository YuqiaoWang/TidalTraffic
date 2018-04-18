package Topology;

import SimulationImpl.Tools;

import java.util.ArrayList;
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
    //public static double DEFAULTTHRESHOLD = 0.7;
    List<Vertex> exportVertexList;

    List<SimpleEdge> edges;
    List<Vertex> vertices;

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
