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
    List<Vertex> exportVertexList;

    public Area(String areaId) {
        this.areaId = areaId;
    }

}
