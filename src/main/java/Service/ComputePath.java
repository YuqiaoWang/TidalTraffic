package Service;

import Topology.SimpleEdge;
import Topology.Vertex;
import jdk.nashorn.internal.ir.BlockLexicalContext;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class ComputePath extends Thread {
    public HashMap<Service, GraphPath> serviceGraphPathHashMap = new HashMap<Service, GraphPath>();
    public BlockingQueue<Service> serviceBlockingQueue;
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph;

    public ComputePath() {

    }

    public ComputePath(BlockingQueue<Service> bq, SimpleWeightedGraph graph) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
    }

    public GraphPath findShortestPath(Service service, SimpleWeightedGraph graph) {
        Vertex srcNode = service.srcNode;
        Vertex desNode = service.desNode;
        GraphPath shortestPath = DijkstraShortestPath.findPathBetween(graph, srcNode, desNode);
        return shortestPath;
    }


    public void run() {
        while (true) {
            try {
                Service service = serviceBlockingQueue.take();
                GraphPath graphPath = findShortestPath(service, this.graph);
                serviceGraphPathHashMap.put(service, graphPath);
                System.out.printf("业务 " + service.serviceId + " 已算路: ");

                List<Vertex> vertexList = graphPath.getVertexList();
                Iterator<Vertex> iterator = vertexList.iterator();
                while (iterator.hasNext()) {
                    Vertex vertex = iterator.next();
                    if(iterator.hasNext() == true) {
                        System.out.printf(vertex.nodeId + " -> ");
                    }else {
                        System.out.printf(vertex.nodeId + "\n");
                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
