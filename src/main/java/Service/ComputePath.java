package Service;

import Topology.SimpleEdge;
import Topology.Vertex;
import jdk.nashorn.internal.ir.BlockLexicalContext;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
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
        service.isComputed = true;
        return shortestPath;
    }

    public void allocateResource(Service service) {
        try {
            GraphPath servicePath = serviceGraphPathHashMap.get(service);
            List<SimpleEdge> edgeList = servicePath.getEdgeList();
            Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
            int n = service.numberOfWavelenthes;
            //分配波长(满足波长一致性)
            int i = 0; //第i个Set
            HashSet<Integer>[] wavelenthSet = new HashSet[n]; //每个set存放这条边的未被占用的波长
            while (edgeIterator.hasNext()) {// 对每条边
                SimpleEdge currentEdge = edgeIterator.next();
                for(int j = 0; j < currentEdge.wavelenthOccupation.length; j++) {
                    if(currentEdge.wavelenthOccupation[j] == false) {
                        wavelenthSet[i].add(Integer.valueOf(j));
                    }
                }
            }
            //找波长序号相同的各个边的连续波长序号




        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        while (true) {
            try {
                Service service = serviceBlockingQueue.take();
                GraphPath graphPath = findShortestPath(service, this.graph);
                serviceGraphPathHashMap.put(service, graphPath);
                service.isComputed = true;
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

                //资源分配


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
