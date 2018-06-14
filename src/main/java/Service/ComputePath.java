package Service;

import Service.Reconfiguration.ReconfigStatistic;
import Service.Reconfiguration.Trigger;
import SimulationImpl.ClockUtil;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import SimulationImpl.Tools;
import jdk.nashorn.internal.ir.BlockLexicalContext;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class ComputePath extends Thread {
    public long programStartTime;
    public HashMap<Service, GraphPath> serviceGraphPathHashMap = new HashMap<Service, GraphPath>();
    public BlockingQueue<Service> serviceBlockingQueue;     //阻塞队列，用于业务发生器线程与算路分配资源线程之间的资源分配
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph;
    public HashMap<String, Area> areaHashMap;               //标号与域的映射
    public int blockedTimes;
    public int servicesNumberInTidalMigrationPeriod;
    public int blockedTimesInTidalMigrationPeriod;
    public String lastServiceIDInTidalMigrationPeriod;
    public int countHopNumber;
    public ClockUtil clock;

    /**
     * 重构相关的属性
     */
    public List<Trigger> listenerList;  //监听者列表，用于通知重构触发器
    public ReconfigStatistic reconfigStatistic;

    public ComputePath() {

    }

    public ComputePath(BlockingQueue<Service> bq, SimpleWeightedGraph graph,
                       HashMap<String, Area> areaHashMap, ClockUtil clock) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
        this.blockedTimes = 0;
        this.programStartTime = clock.getStartTime();
        this.servicesNumberInTidalMigrationPeriod = 0;
        this.blockedTimesInTidalMigrationPeriod = 0;
        this.lastServiceIDInTidalMigrationPeriod = "0";
        this.countHopNumber = 0;
        this.areaHashMap = areaHashMap;
        this.listenerList = new ArrayList<Trigger>();
        this.reconfigStatistic = new ReconfigStatistic(areaHashMap, graph);
        this.clock = clock;
    }

    public GraphPath findShortestPath(Service service, SimpleWeightedGraph graph) {
        Vertex srcNode = service.srcNode;
        Vertex desNode = service.desNode;
        GraphPath shortestPath = DijkstraShortestPath.findPathBetween(graph, srcNode, desNode);
        service.isComputed = true;
        return shortestPath;
    }

    public void setWeightAsMetric() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if(this.graph.containsEdge(currentedge)) {
                this.graph.setEdgeWeight(currentedge, currentedge.metric);
            }
        }
    }

    //TODO：等边预测模型完成后，放弃本方法
    public void reAllocateWeight() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if(this.graph.containsEdge(currentedge)) {
                this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength);
            }
        }
    }

    /**
     * 本方法将拓扑各边的权重赋值成预测到的负载值
     */
    //TODO:等边预测模型完成后，使用本方法
    public void reAllocatedWeightAsFutureTraffic() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while(edgeIterator.hasNext()) {
            SimpleEdge currentEdge = edgeIterator.next();
            if(this.graph.containsEdge(currentEdge)) {
                //TODO:将来改成预测的边负载
                this.graph.setEdgeWeight(currentEdge, currentEdge.getFutureLoad());
            }
        }
    }



    public boolean allocateResource(Service service) {
        try {
            GraphPath servicePath = serviceGraphPathHashMap.get(service);
            List<SimpleEdge> edgeList = servicePath.getEdgeList();
            int n = service.numberOfWavelenthes;
            /** 分配波长(满足波长一致性) */
            int count = 0;  //记录各边都满足的连续波长数
            List<Integer> freeWavelenthesNumber = new ArrayList<Integer>(); // 统计空闲波长号用
            //资源统计
            for(int i = 0; i < Tools.DEFAULTNUMBEROFWAVELENTHES; i++) {    //对于每个波长号，统计各个边的该波长号是否都空闲
                int edgeCount = 0;
                Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    if(currentEdge.wavelenthOccupation[i] == false) {
                        edgeCount++;
                    }
                }
                if(edgeCount == edgeList.size()) {
                    count++;
                    freeWavelenthesNumber.add(Integer.valueOf(i));
                }
            }
            //分配资源
            if(count >= n) {
                //System.out.printf("分配的波长资源：");
                for(int i = 0; i < n; i++) {
                    int currentWavelenthNumber = freeWavelenthesNumber.get(i).intValue();   //取出波长号
                    service.wavelengthesNumber.add(Integer.valueOf(currentWavelenthNumber));//将波长号放入service对象中
                    Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                    while (edgeIterator.hasNext()) {
                        SimpleEdge currentEdge = edgeIterator.next();
                        currentEdge.wavelenthOccupation[currentWavelenthNumber] = true;
                        currentEdge.serviceOnWavelength[currentWavelenthNumber] = service.serviceId;    //将每个波长跑的什么业务记录下来
                        currentEdge.numberOfOccupatedWavelength +=1;
                    }
                    //System.out.print("[" + currentWavelenthNumber + "]");
                }
                //System.out.printf("\n");
                service.isAllocated = true;
            }else {
                service.isBlocked = true;
                service.isOutOfTime = true;
                service.wavelengthesNumber.clear(); //如果分配资源不成功，就释放service对象占用的波长号
                //System.out.println("没有足够资源分配给业务 " + service.serviceId + " 。");
                blockedTimes +=1;
                if(System.currentTimeMillis() - this.programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        System.currentTimeMillis() - this.programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    blockedTimesInTidalMigrationPeriod +=1;
                 }
                //FileWriter fileWriter = new FileWriter("src/main/java/Service/blockedNumber.txt");
                //BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                //bufferedWriter.write();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return service.isAllocated;
    }

    /**
     * 重构相关的方法
     * 注册监听器(也就是重构的trigger)
     */
    public void regist(Trigger trigger) {
        this.listenerList.add(trigger);
    }

    public void unregist(Trigger trigger) {
        this.listenerList.remove(trigger);
    }

    /**
     * 重构相关的方法
     * 通知所有list中的trigger，让trigger决定是否当前进行重构
     * 目前已将通知逻辑转移到LoadCountTask
     */
    public void reConfigNotify() throws Exception{
        if(!listenerList.isEmpty()) {
            for(Trigger trigger : listenerList) {
                //TODO:入参还未确定，应该怎样把流量传过去
                //trigger.flushTraffic();
            }
        }else {
            throw new Exception("监听列表为空");
        }

    }


    public void run() {
        int serviceNum = 0;
        try{
            LoadCountTask loadCountTask = new LoadCountTask(graph, areaHashMap, listenerList, reconfigStatistic, clock);
            Timer timer = new Timer();
            timer.schedule(loadCountTask, Tools.COUNT_DELAY, Tools.COUNT_PERIOD * Tools.TIMESCALE);


        }catch (Exception e) {
            e.printStackTrace();
        }
        while (serviceNum < Tools.DEFAULTSERVICENUMBER) {
            try {

                /**业务到来*/
                Service service = serviceBlockingQueue.take();
                serviceNum++;
                if(System.currentTimeMillis() - programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        System.currentTimeMillis() - programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    this.servicesNumberInTidalMigrationPeriod +=1;
                    lastServiceIDInTidalMigrationPeriod = service.serviceId;
                }

                /**峰谷状态更新*/
                //各域负载初始化
                /*
                Iterator areaMapIterator = this.areaHashMap.entrySet().iterator();
                while (areaMapIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) areaMapIterator.next();
                    Area areaToInit = (Area) entry.getValue();
                    areaToInit.initialLoad();
                }
                //计算各域负载
                Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    //域内部边
                    if(currentEdge.srcVertex.areaId == currentEdge.desVertex.areaId) {
                        Area currentArea = this.areaHashMap.get(currentEdge.srcVertex.areaId);
                        //currentArea.load += currentEdge.numberOfOccupatedWavelength;
                    }
                    //域间边
                    else {
                        Area srcArea = this.areaHashMap.get(currentEdge.srcVertex.areaId);
                        Area desArea = this.areaHashMap.get(currentEdge.desVertex.areaId);
                        //srcArea.load += currentEdge.numberOfOccupatedWavelength;
                        //desArea.load += currentEdge.numberOfOccupatedWavelength;
                    }
                }*/
                //各域状态判断
                //areaMapIterator = this.areaHashMap.entrySet().iterator();
                //把负载值写入文件
                //FileWriter areaOneLoadFileWriter, areaTwoLoadFileWriter, areaThreeLoadFileWriter, totalLoadFileWriter,
                //        hopFileWriter;
                /*
                if(Integer.valueOf(service.serviceId) == 0) {
                    areaOneLoadFileWriter = new FileWriter("target/generated-sources/area1.txt", false);
                    areaTwoLoadFileWriter = new FileWriter("target/generated-sources/area2.txt", false);
                    areaThreeLoadFileWriter = new FileWriter("target/generated-sources/area3.txt", false);
                    totalLoadFileWriter = new FileWriter("target/generated-sources/total.txt", false);
                    hopFileWriter = new FileWriter("target/generated-sources/hop.txt", false);

                }else {
                    areaOneLoadFileWriter = new FileWriter("target/generated-sources/area1.txt", true);
                    areaTwoLoadFileWriter = new FileWriter("target/generated-sources/area2.txt", true);
                    areaThreeLoadFileWriter = new FileWriter("target/generated-sources/area3.txt", true);
                    totalLoadFileWriter = new FileWriter("target/generated-sources/total.txt", true);
                    hopFileWriter = new FileWriter("target/generated-sources/hop.txt", true);

                }
                while (areaMapIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) areaMapIterator.next();
                    Area currentArea = (Area) entry.getValue();

                    switch (Integer.valueOf(currentArea.areaId)) {
                        case 1 :
                            areaOneLoadFileWriter.write(currentArea.load + "\n");
                            areaOneLoadFileWriter.close();
                            break;
                        case 2 :
                            areaTwoLoadFileWriter.write(currentArea.load + "\n");
                            areaTwoLoadFileWriter.close();
                            break;
                        case 3 :
                            areaThreeLoadFileWriter.write(currentArea.load + "\n");
                            areaThreeLoadFileWriter.close();
                    }
                    if(currentArea.load / currentArea.totalCapacity >= currentArea.threshold) {
                        //System.out.println("[area " + currentArea.areaId + "] 当前处于潮峰区,load:" + currentArea.load + "/"+ currentArea.totalCapacity);
                    }else {
                        //System.out.println("[area " + currentArea.areaId + "] 当前处于潮谷区,load:" + currentArea.load + "/"+ currentArea.totalCapacity);
                    }
                }*/

                /**算路*/
                //重新赋边权(以负载为边权)
                //TODO:如果在对照组分支上，将这部分注释掉
                //reAllocateWeight();
                //D算法算路
                GraphPath graphPath = findShortestPath(service, this.graph);
                serviceGraphPathHashMap.put(service, graphPath);
                service.isComputed = true;
                service.setGraphPath(graphPath);
                System.out.printf("业务 " + service.serviceId + " 已算路: ");
                List<Vertex> vertexList = graphPath.getVertexList();

                //统计跳数
                long nowTime = System.currentTimeMillis();
                if(nowTime - this.programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        nowTime - this.programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    countHopNumber += (vertexList.size() - 1);
                    //hopFileWriter.write(vertexList.size() - 1 + "\n");
                    //hopFileWriter.close();
                }
                /*
                Iterator<Vertex> iterator = vertexList.iterator();
                while (iterator.hasNext()) {
                    Vertex vertex = iterator.next();
                    if(iterator.hasNext() == true) {
                        System.out.printf(vertex.nodeId + " -> ");
                    }else {
                        System.out.printf(vertex.nodeId + "\n");
                    }
                }*/
                /**资源分配*/
                boolean allocated = allocateResource(service);
                /**业务离去*/
                if(service.isResourceAllocated() == true) {     //如果分配了资源
                    Timer leavingTimer = new Timer();
                    ServiceLeavingTask serviceLeavingTask = new ServiceLeavingTask(service);
                    leavingTimer.schedule(serviceLeavingTask, service.serviceTime * Tools.TIMESCALE);  //业务时间结束后离去
                }
                int num = Integer.valueOf(service.serviceId);
                if(num == Tools.DEFAULTSERVICENUMBER - 1) {
                    FileWriter fw = new FileWriter("target/generated-sources/blockedTimes.txt");
                    fw.write(Integer.toString(this.blockedTimes));
                    System.out.println("被阻塞的业务个数为:" + this.blockedTimes);
                    System.out.println("潮汐迁移时段被阻塞业务个数：" + this.blockedTimesInTidalMigrationPeriod);
                    System.out.println("潮汐迁移时段业务个数：" + this.servicesNumberInTidalMigrationPeriod);
                    System.out.println("平均跳数：" + (double)countHopNumber/this.servicesNumberInTidalMigrationPeriod);
                    System.out.println("迁移时段最后一个业务ID：" + lastServiceIDInTidalMigrationPeriod);
                    System.out.println("*****重构相关统计*******");
                    System.out.println("重构次数:" + reconfigStatistic.reconfigTimes);
                    System.out.println("重构成功业务个数：" + reconfigStatistic.numberOfReconfigedServices);
                    System.out.println("重构失败业务个数：" + reconfigStatistic.numberOfFailedServices);
                    System.out.println("程序结束");
                    fw.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
