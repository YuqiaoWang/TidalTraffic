package Topology;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.*;
import java.util.*;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleGraph {
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph= new SimpleWeightedGraph<Vertex, SimpleEdge>(SimpleEdge.class);
    public HashMap<String, Vertex> vertexHashMap = new HashMap<String, Vertex>();
    public HashMap<String, Area> areaHashMap = new HashMap<String, Area>();

    public SimpleWeightedGraph<Vertex,SimpleEdge> parseJsonToGraph() {

        try{
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(
                    //new FileReader("src/main/java/Topology/DefaultTopology.json"));
                    new FileReader("src/main/java/Topology/NsfnetTopology.json"));
            JsonObject jsonGraph = jsonObject.getAsJsonObject("Graph");
            JsonArray vertexArray = jsonGraph.getAsJsonArray("vertex");
            /*List<String> nodeIdList = new ArrayList<String>();
            for(int i = 0; i < vertexArray.size(); i++) {
                nodeIdList.add(vertexArray.)
            }*/

            /** 加点*/
            Iterator<JsonElement> vertexIterator = vertexArray.iterator();
            while (vertexIterator.hasNext()) {
                JsonObject vertexObj = (JsonObject) vertexIterator.next();
                String nodeId = vertexObj.get("nodeId").toString();
                //Vertex currentNode = vertexHashMap.get(nodeId);
                Vertex currentNode = new Vertex(nodeId);
                currentNode.areaId = vertexObj.get("areaId").toString();
                graph.addVertex(currentNode);
                vertexHashMap.put(currentNode.nodeId, currentNode);

                //下面的信息没有传过去
                if(!areaHashMap.containsKey(currentNode.areaId)) {
                    Area area = new Area(currentNode.areaId);
                    areaHashMap.put(currentNode.areaId, area);
                }
                Area currentArea = areaHashMap.get(currentNode.areaId);
                currentArea.vertices.add(currentNode);
            }

            /**加边*/
            JsonArray jsonEdge = jsonGraph.getAsJsonArray("edge");
            Iterator edgeIterator = jsonEdge.iterator();
            while (edgeIterator.hasNext()) {
                JsonObject edgeObject = (JsonObject)edgeIterator.next();
                String srcId = edgeObject.get("srcId").toString();
                String desId = edgeObject.get("desId").toString();
                double metric = Double.valueOf(edgeObject.get("metric").toString());
                //Vertex srcNode = new Vertex(srcId);
                //Vertex desNode = new Vertex(desId);
                Vertex srcNode = vertexHashMap.get(srcId);
                Vertex desNode = vertexHashMap.get(desId);
                //vertexHashMap.put(srcId, srcNode);
                //vertexHashMap.put(desId, desNode);
                //graph.addVertex(srcNode);
                //graph.addVertex(desNode);
                SimpleEdge simpleEdge = new SimpleEdge(srcNode, desNode);
                //SimpleEdge edge =
                graph.addEdge(srcNode, desNode, simpleEdge);
                graph.setEdgeWeight(simpleEdge, metric);
                //往每个域中加边
                if(areaHashMap.get(srcNode.areaId) != areaHashMap.get(desNode.areaId)) {
                    Area srcArea = areaHashMap.get(srcNode.areaId);
                    Area desArea = areaHashMap.get(desNode.areaId);
                    srcArea.edges.add(simpleEdge);
                    desArea.edges.add(simpleEdge);

                    srcArea.addNumverOfEdges();
                    desArea.addNumverOfEdges();
                }else {
                    Area area = areaHashMap.get(srcNode.areaId);
                    area.edges.add(simpleEdge);
                    area.addNumverOfEdges();
                }
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
