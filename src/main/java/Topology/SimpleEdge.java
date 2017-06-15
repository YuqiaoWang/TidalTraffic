package Topology;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleEdge extends DefaultWeightedEdge {
    Vertex srcVertex;
    Vertex desVertex;
    double capacity;
    int numberOfWavelenth;
    public SimpleEdge() {

    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex) {
        this(srcVertex, desVertex, 100, 16);//默认链路带宽容量和波长数
    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex, double capacity, int numberOfWavelenth) {
        this.srcVertex = srcVertex;
        this.desVertex = desVertex;
        this.capacity = capacity;
        this.numberOfWavelenth = numberOfWavelenth;
    }

}
