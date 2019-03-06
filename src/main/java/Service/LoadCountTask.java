package Service;

import Service.Reconfiguration.ReconfigStatistic;
import Service.Reconfiguration.Trigger;
import SimulationImpl.ClockUtil;
import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import TrafficDescription.EdgeTraffic.NowIntervalEdgeTraffic;
import TrafficDescription.AreaTraffic.NowIntervalTraffic;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.*;
import java.util.*;

/**
 * Created by yuqia_000 on 2017/12/6.
 */

/**
 * 流量统计任务
 */
public class LoadCountTask extends TimerTask {
    FileWriter area1LoadCountFileWriter;
    FileWriter area2LoadCountFileWriter;
    FileWriter area3LoadCountFileWriter;
    double totalUsedWavelength; // 统计资源利用率
    int writeTimes; // 记录写入文件的次数

    SimpleWeightedGraph<Vertex, SimpleEdge> graph;
    Area area1;
    Area area2;
    Area area3;
    Set<SimpleEdge> edgeSet;
    Iterator<SimpleEdge> edgeIterator;
    HashMap<SimpleEdge, FileWriter> edgeLoadCountMap;
    List<Double> usedWavelengthCountList; // 统计资源利用率用的集合
    boolean timeInfoPrinted;

    /** 重构用到的属性 */
    List<Trigger> listenerList; // 监听器列表
    List<NowIntervalTraffic> nowIntervalTrafficList; // 面向每个area的1h流量统计值
    NowIntervalTraffic area1NowIntervalTraffic; // area1的1h流量的包装类
    NowIntervalTraffic area3NowIntervalTraffic; // area3的1h流量的包装类
    ReconfigStatistic reconfigStatistic; // 重构统计器
    Map<String, NowIntervalEdgeTraffic> nowTrafficForEdges; // 此map用来存储各个边1h的流量统计
    ClockUtil clock; // 计时器

    public LoadCountTask() {

    }

    public LoadCountTask(SimpleWeightedGraph<Vertex, SimpleEdge> graph, HashMap<String, Area> areaHashMap,
            List<Trigger> listenerList, ReconfigStatistic reconfigStatistic, ClockUtil clock) throws Exception {
        FileWriter fw1 = new FileWriter("data/load/area1loadCount.txt");
        fw1.write("");
        fw1.close();
        FileWriter fw2 = new FileWriter("data/load/area2loadCount.txt");
        fw2.write("");
        fw2.close();
        FileWriter fw3 = new FileWriter("data/load/area3loadCount.txt");
        fw3.write("");
        fw3.close();

        area1LoadCountFileWriter = new FileWriter("data/load/area1loadCount.txt", true);
        area2LoadCountFileWriter = new FileWriter("data/load/area2loadCount.txt", true);
        area3LoadCountFileWriter = new FileWriter("data/load/area3loadCount.txt", true);

        this.totalUsedWavelength = 0;
        this.writeTimes = 0;
        this.graph = graph;
        this.area1 = areaHashMap.get("1");
        this.area2 = areaHashMap.get("2");
        this.area3 = areaHashMap.get("3");
        this.edgeSet = graph.edgeSet();
        this.edgeIterator = edgeSet.iterator();
        this.edgeLoadCountMap = new HashMap<SimpleEdge, FileWriter>();
        this.usedWavelengthCountList = new ArrayList<>();
        this.timeInfoPrinted = false;
        // 判断目录是否存在，若不存在，则新建目录
        File fileParent = new File("data/load/edgeload/x.txt").getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        while (edgeIterator.hasNext()) {
            SimpleEdge currentEdge = edgeIterator.next();
            String edgeId = currentEdge.toString();
            FileWriter currentEdgeLoadWriter = new FileWriter("data/load/edgeload/" + edgeId + ".txt");
            currentEdgeLoadWriter.write("");
            currentEdgeLoadWriter.close();
            currentEdgeLoadWriter = new FileWriter("data/load/edgeload/" + edgeId + ".txt", true);
            edgeLoadCountMap.put(currentEdge, currentEdgeLoadWriter);
        }

        // 重构属性相关的初始化
        /** 201805015 注释为了统计数据 */

        this.listenerList = listenerList;
        this.reconfigStatistic = reconfigStatistic;
        this.nowIntervalTrafficList = new ArrayList<NowIntervalTraffic>();
        this.area1NowIntervalTraffic = new NowIntervalTraffic(area1.areaId);
        this.area3NowIntervalTraffic = new NowIntervalTraffic(area3.areaId);
        this.nowIntervalTrafficList.add(area1NowIntervalTraffic);
        this.nowIntervalTrafficList.add(area3NowIntervalTraffic);
        this.nowTrafficForEdges = new HashMap<>();
        this.edgeIterator = edgeSet.iterator();
        // 初始化这个map
        /*
         * while(edgeIterator.hasNext()) { SimpleEdge currentEdge = edgeIterator.next();
         * NowIntervalEdgeTraffic currentEdgeTraffic = new
         * NowIntervalEdgeTraffic(currentEdge.srcVertex.nodeId,
         * currentEdge.desVertex.nodeId);
         * this.nowTrafficForEdges.put(currentEdge.toString(), currentEdgeTraffic); }
         */
        this.clock = clock;

    }

    public void run() {
        try {
            area1.flushLoad();
            area2.flushLoad();
            area3.flushLoad();
            if (this.writeTimes < Tools.COUNT_TIMES) {
                clock.setTimingIndexInHour(writeTimes % 15);
                /** 将负载信息写入文件 */
                area1LoadCountFileWriter.write(area1.load / area1.totalCapacity + "\n");
                area1LoadCountFileWriter.flush();
                area2LoadCountFileWriter.write(area2.load / area2.totalCapacity + "\n");
                area2LoadCountFileWriter.flush();
                area3LoadCountFileWriter.write(area3.load / area2.totalCapacity + "\n");
                area3LoadCountFileWriter.flush();
                // 统计资源利用率
                this.totalUsedWavelength = area1.load + area2.load + area3.load;
                this.usedWavelengthCountList.add(this.totalUsedWavelength);

                Iterator<SimpleEdge> edgeIterator;

                // TODO : 加入重构触发用的流量统计
                /** 全局负载均衡指标 */
                this.reconfigStatistic.computeLoadBalanceTarget();
                // 此处遍历各边，为了将负载写入到SimpleEdge的属性里
                edgeIterator = edgeSet.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    FileWriter currentEdgeLoadWriter = edgeLoadCountMap.get(currentEdge);
                    currentEdgeLoadWriter.write(
                            (double) currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth + "\n");
                    currentEdgeLoadWriter.flush();
                    /** 201805015 注释为了统计数据 */
                    // 重构用到的每条边的1h流量数据
                    NowIntervalEdgeTraffic currentEdgeTraffic = currentEdge.nowIntervalEdgeTraffic;
                    currentEdgeTraffic.nowIntervalTraffic
                            .add((double) currentEdge.numberOfOccupatedWavelength / currentEdge.numberOfWavelenth);
                }

                /** 20181115 摘除重构触发环节，专注生成数据 */
                /** 为向tensorflow传数据进行包装 */
                /*
                 * area1NowIntervalTraffic.nowIntervalTraffic.add(area1.load /
                 * area1.totalCapacity);
                 * area3NowIntervalTraffic.nowIntervalTraffic.add(area3.load /
                 * area3.totalCapacity); if (this.writeTimes % 15 == 14 && this.writeTimes > 28)
                 * {
                 * 
                 * for(Trigger trigger : listenerList) {
                 * trigger.flushTraffic(nowIntervalTrafficList); } // 域的1h流量包装
                 * area1NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                 * area1NowIntervalTraffic.removeOneHourTrafficData();
                 * area3NowIntervalTraffic.setTimeOfHour((writeTimes/15)/24.0);
                 * area3NowIntervalTraffic.removeOneHourTrafficData(); }
                 */

                // 此处遍历边，为了将前1h的流量清除
                edgeIterator = edgeSet.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    // 重构用到的每条边的1h流量数据
                    NowIntervalEdgeTraffic currentEdgeTraffic = currentEdge.nowIntervalEdgeTraffic;
                    currentEdgeTraffic.setTimeOfHour((writeTimes / 15) / 24.0);
                    if (this.writeTimes % 15 == 14 && this.writeTimes > 28) {
                        currentEdgeTraffic.removeOneHourTrafficData();
                    }
                }
                this.writeTimes++;

            } else {
                area1LoadCountFileWriter.close();
                area2LoadCountFileWriter.close();
                area3LoadCountFileWriter.close();
                Iterator<SimpleEdge> edgeIterator = edgeSet.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    FileWriter currentEdgeLoadWriter = edgeLoadCountMap.get(currentEdge);
                    currentEdgeLoadWriter.close();
                }
                double sumOfUsedWavelengthCount = 0;
                int countZero = 0;
                for (Double d : usedWavelengthCountList) {
                    sumOfUsedWavelengthCount += d;
                    if (Math.abs(d - 0) < 50) {
                        countZero++;
                    }
                }
                if (!timeInfoPrinted) {
                    double averageUsedWavelength = sumOfUsedWavelengthCount
                            / (usedWavelengthCountList.size() - countZero);
                    double totalCapacity = area1.totalCapacity + area2.totalCapacity + area3.totalCapacity;
                    double averageUsedRatio = averageUsedWavelength / (totalCapacity);
                    System.out.println("resource use ratio: " + averageUsedRatio);
                    timeInfoPrinted = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
