package Topology;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleGraph {
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph= new SimpleWeightedGraph<Vertex, SimpleEdge>(SimpleEdge.class);
    public HashMap<String, Vertex> vertexHashMap = new HashMap<String, Vertex>();

    public SimpleWeightedGraph<Vertex,SimpleEdge> parseJsonToGraph() {

        try{
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(
                    new FileReader("src/main/java/Topology/DefaultTopology.json"));
            JsonObject jsonGraph = jsonObject.getAsJsonObject("Graph");
            JsonArray vertexArray = jsonGraph.getAsJsonArray("vertex");
            JsonArray jsonEdge = jsonGraph.getAsJsonArray("edge");
            Iterator edgeIterator = jsonEdge.iterator();
            while (edgeIterator.hasNext()) {
                JsonObject edgeObject = (JsonObject)edgeIterator.next();
                String srcId = edgeObject.get("srcId").toString();
                String desId = edgeObject.get("desId").toString();
                double metric = Double.valueOf(edgeObject.get("metric").toString());
                Vertex srcNode = new Vertex(srcId);
                Vertex desNode = new Vertex(desId);
                vertexHashMap.put(srcId, srcNode);
                vertexHashMap.put(desId, desNode);
                graph.addVertex(srcNode);
                graph.addVertex(desNode);
                SimpleEdge simpleEdge = new SimpleEdge(srcNode, desNode);
                //SimpleEdge edge =
                graph.addEdge(srcNode, desNode, simpleEdge);
                graph.setEdgeWeight(simpleEdge, metric);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return this.graph;
    }


    /*
    public static void main(String[] args) {
        SimpleGraph mysimpleGraph = new SimpleGraph();
        mysimpleGraph.parseJsonToGraph();
    }*/
}
