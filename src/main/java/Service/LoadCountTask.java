package Service;

import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TimerTask;

/**
 * Created by yuqia_000 on 2017/12/6.
 */
public class LoadCountTask extends TimerTask{

    FileWriter area1LoadCountFileWriter;
    FileWriter area2LoadCountFileWriter;
    FileWriter area3LoadCountFileWriter;
    int writeTimes;                     //记录写入文件的次数

    SimpleWeightedGraph<Vertex, SimpleEdge> graph;
    Area area1;
    Area area2;
    Area area3;
    Set<SimpleEdge> edgeSet;
    Iterator<SimpleEdge> edgeIterator;
    HashMap<SimpleEdge, FileWriter> edgeLoadCountMap;

    public LoadCountTask(){

    }

    public LoadCountTask(SimpleWeightedGraph graph, Area area1, Area area2, Area area3) throws Exception{
        FileWriter fw1 = new FileWriter("target/generated-sources/area1loadCount.txt");
        fw1.write("");
        fw1.close();
        FileWriter fw2 = new FileWriter("target/generated-sources/area2loadCount.txt");
        fw2.write("");
        fw2.close();
        FileWriter fw3 = new FileWriter("target/generated-sources/area3loadCount.txt");
        fw3.write("");
        fw3.close();

        area1LoadCountFileWriter = new FileWriter("target/generated-sources/area1loadCount.txt", true);
        area2LoadCountFileWriter = new FileWriter("target/generated-sources/area2loadCount.txt", true);
        area3LoadCountFileWriter = new FileWriter("target/generated-sources/area3loadCount.txt", true);

        this.writeTimes = 0;
        this.graph = graph;
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
        this.edgeSet = graph.edgeSet();
        this.edgeIterator = edgeSet.iterator();
        this.edgeLoadCountMap = new HashMap<SimpleEdge, FileWriter>();
        while(edgeIterator.hasNext()) {
            SimpleEdge currentEdge = edgeIterator.next();
            String edgeId = currentEdge.toString();
            FileWriter currentEdgeLoadWriter = new FileWriter(
                    "target/generated-sources/edgeload/"+ edgeId + ".txt");
            currentEdgeLoadWriter.write("");
            currentEdgeLoadWriter.close();
            currentEdgeLoadWriter = new FileWriter(
                    "target/generated-sources/edgeload/"+ edgeId + ".txt", true);
            edgeLoadCountMap.put(currentEdge, currentEdgeLoadWriter);
        }



    }

    public void run() {
        try{
            area1.flushLoad();
            area2.flushLoad();
            area3.flushLoad();
            if(this.writeTimes < 360) {
                area1LoadCountFileWriter.write(area1.load + "\n");
                area1LoadCountFileWriter.flush();
                area2LoadCountFileWriter.write(area2.load + "\n");
                area2LoadCountFileWriter.flush();
                area3LoadCountFileWriter.write(area3.load + "\n");
                area3LoadCountFileWriter.flush();

                Iterator<SimpleEdge> edgeIterator = edgeSet.iterator();
                while(edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    FileWriter currentEdgeLoadWriter = edgeLoadCountMap.get(currentEdge);
                    currentEdgeLoadWriter.write((double)currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth + "\n");
                    currentEdgeLoadWriter.flush();
                }
                this.writeTimes++;
            }else {
                area1LoadCountFileWriter.close();
                area2LoadCountFileWriter.close();
                area3LoadCountFileWriter.close();
                Iterator<SimpleEdge> edgeIterator = edgeSet.iterator();
                while(edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    FileWriter currentEdgeLoadWriter = edgeLoadCountMap.get(currentEdge);
                    currentEdgeLoadWriter.close();
                }



            }



        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
