package Topology;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.File;
import java.io.FileReader;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleGraph {
    private SimpleWeightedGraph<Vertex, SimpleEdge> graph= new SimpleWeightedGraph(SimpleEdge.class);


    public SimpleWeightedGraph<Vertex,SimpleEdge> parseJsonToGraph() {
        try{
            File file = new File("DefaultTopology.json");
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(
                    new FileReader(file));
            JsonArray array = jsonObject.getAsJsonArray("Graph");
        }catch (Exception e) {
            e.printStackTrace();
        }

        return this.graph;
    }

    public static void main(String[] args) {
        SimpleGraph mysimpleGraph = new SimpleGraph();
        mysimpleGraph.parseJsonToGraph();
    }
}
