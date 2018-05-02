package Service;

import Service.Reconfiguration.Trigger;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import TrafficDescription.NowIntervalTraffic;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

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

    //重构用到的属性
    List<Trigger> listenerList;     //监听器列表
    List<NowIntervalTraffic> nowIntervalTrafficList;    //
    NowIntervalTraffic area1NowIntervalTraffic;
    NowIntervalTraffic area3NowIntervalTraffic;

    public LoadCountTask(){

    }

    public LoadCountTask(SimpleWeightedGraph graph, Area area1, Area area2, Area area3, List<Trigger> listenerList) throws Exception{
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

        //重构属性相关的初始化
        this.listenerList = listenerList;
        this.nowIntervalTrafficList = new ArrayList<NowIntervalTraffic>();
        this.area1NowIntervalTraffic = new NowIntervalTraffic();
        this.area3NowIntervalTraffic = new NowIntervalTraffic();
        this.nowIntervalTrafficList.add(area1NowIntervalTraffic);
        this.nowIntervalTrafficList.add(area3NowIntervalTraffic);

    }

    public void run() {
        try{
            area1.flushLoad();
            area2.flushLoad();
            area3.flushLoad();
            if(this.writeTimes < 360) {
                area1LoadCountFileWriter.write(area1.load / area1.totalCapacity + "\n");
                area1LoadCountFileWriter.flush();
                area2LoadCountFileWriter.write(area2.load / area2.totalCapacity+ "\n");
                area2LoadCountFileWriter.flush();
                area3LoadCountFileWriter.write(area3.load / area2.totalCapacity + "\n");
                area3LoadCountFileWriter.flush();

                Iterator<SimpleEdge> edgeIterator = edgeSet.iterator();
                while(edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    FileWriter currentEdgeLoadWriter = edgeLoadCountMap.get(currentEdge);
                    currentEdgeLoadWriter.write((double)currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth + "\n");
                    currentEdgeLoadWriter.flush();
                }


                //TODO:加入重构触发用的流量统计
                /*
                if(this.writeTimes % 15 != 14) {
                    area1NowIntervalTraffic.nowIntervalTraffic.add(area1.load / area1.totalCapacity);
                    area3NowIntervalTraffic.nowIntervalTraffic.add(area3.load / area3.totalCapacity);
                }else {
                    //刷新流量
                    for(Trigger trigger : listenerList) {
                        trigger.flushTraffic(nowIntervalTrafficList);
                    }

                    area1NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                    area1NowIntervalTraffic.setNowIntervalTraffic(new ArrayList<Double>());
                    area3NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                    area3NowIntervalTraffic.setNowIntervalTraffic(new ArrayList<Double>());
                }*/
                //刷新流量
                area1NowIntervalTraffic.nowIntervalTraffic.add(area1.load / area1.totalCapacity);
                area3NowIntervalTraffic.nowIntervalTraffic.add(area3.load / area3.totalCapacity);
                if(this.writeTimes % 15 == 14) {
                    for(Trigger trigger : listenerList) {
                        trigger.flushTraffic(nowIntervalTrafficList);
                    }

                    area1NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                    area1NowIntervalTraffic.setNowIntervalTraffic(new ArrayList<Double>());
                    area3NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                    area3NowIntervalTraffic.setNowIntervalTraffic(new ArrayList<Double>());
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
