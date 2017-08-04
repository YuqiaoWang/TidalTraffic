package Service;

import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.Vertex;
import jdk.nashorn.internal.ir.BlockLexicalContext;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class ComputePath extends Thread {
    public long programStartTime;
    public HashMap<Service, GraphPath> serviceGraphPathHashMap = new HashMap<Service, GraphPath>();
    public Set backupEdgeSet = new HashSet<SimpleEdge>();
    public BlockingQueue<Service> serviceBlockingQueue;
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph;
    public HashMap<String, Area> areaHashMap = new HashMap<String, Area>();
    public int blockedTimes;
    public int servicesNumberInTidalMigrationPeriod;
    public int blockedTimesInTidalMigrationPeriod;
    public int countHopNumber;
    public int longTimeServiceInTidalMigrationPeriod;   //长连接
    public int shortTimeService;                        //短连接
    public int crossAreaLongTimeService;                //跨域长连接
    public int inAreaLongTimeService;                   //域内长连接
    public int inAreaShortTimeService;                  //域内短连接
    public int crossAreaShortTimeService;               //跨域短连接
    public int blockedInAreaShortTimeService;           //被阻塞域内短连接
    public int blockedInAreaLongTimeService;            //被阻塞域内长连接
    public int blockedCrossAreaShortTimeService;        //被阻塞跨域短连接
    public int blockedCrossAreaLongTimeService;         //被阻塞跨域长连接
    public int blockedLongTimeService;                  //被阻塞长连接


    public ComputePath() {

    }

    public ComputePath(BlockingQueue<Service> bq, SimpleWeightedGraph graph, long startTime) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
        this.programStartTime = startTime;
        this.blockedTimes = 0;
        this.backupEdgeSet = graph.edgeSet();
        this.countHopNumber = 0;
        this.servicesNumberInTidalMigrationPeriod = 0;
        this.blockedTimesInTidalMigrationPeriod = 0;
        this.longTimeServiceInTidalMigrationPeriod = 0;
        this.shortTimeService = 0;
        this.crossAreaLongTimeService = 0;
        this.crossAreaShortTimeService = 0;
        this.blockedCrossAreaLongTimeService = 0;
        this.blockedCrossAreaShortTimeService = 0;
        this.blockedLongTimeService = 0;
        this.inAreaLongTimeService = 0;
        this.inAreaShortTimeService = 0;
        this.blockedInAreaLongTimeService = 0;
        this.blockedInAreaShortTimeService = 0;


        //确定每个area有多少点
        Iterator<Vertex> vertexIterator = this.graph.vertexSet().iterator();
        while (vertexIterator.hasNext()) {
            Vertex currentVertex = vertexIterator.next();
            if(!areaHashMap.containsKey(currentVertex.areaId)) {
                Area area = new Area(currentVertex.areaId);
                areaHashMap.put(currentVertex.areaId, area);
            }

        }

        Iterator<SimpleEdge> simpleEdgeIterator = this.graph.edgeSet().iterator();
        while (simpleEdgeIterator.hasNext()) {
            SimpleEdge currentEdge = simpleEdgeIterator.next();
            if(currentEdge.srcVertex.areaId.equals(currentEdge.desVertex.areaId)) {
                areaHashMap.get(currentEdge.srcVertex.areaId).addNumverOfEdges();
            }else {
                areaHashMap.get(currentEdge.srcVertex.areaId).addNumverOfEdges();
                areaHashMap.get(currentEdge.desVertex.areaId).addNumverOfEdges();
            }
        }


    }

    public GraphPath findShortestPath(Service service, SimpleWeightedGraph graph) {
        Vertex srcNode = service.srcNode;
        Vertex desNode = service.desNode;
        GraphPath shortestPath = DijkstraShortestPath.findPathBetween(graph, srcNode, desNode);
        service.isComputed = true;
        return shortestPath;
    }

    /**用于用负载表征边权*/
    public void reAllocateWeight() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if(this.graph.containsEdge(currentedge)) {
                this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength);
            }
        }
    }

    /**路长当边权*/
    public void allocatedWeightBack() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if(this.graph.containsEdge(currentedge)) {
                edgeIterator = backupEdgeSet.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge backupEdge = edgeIterator.next();
                    if(backupEdge.srcVertex.equals(currentedge.srcVertex) && backupEdge.desVertex.equals(currentedge.desVertex)) {
                        this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength);
                        break;
                    }
                }

            }
        }
    }

    public void normalPeriodComputingPath(Service service) {

        //D算法算路
        GraphPath graphPath = findShortestPath(service, this.graph);
        serviceGraphPathHashMap.put(service, graphPath);
        service.isComputed = true;
        service.setGraphPath(graphPath);
        System.out.printf("业务 " + service.serviceId + " 已算路: ");
        List<Vertex> vertexList = graphPath.getVertexList();
        Iterator<Vertex> iterator = vertexList.iterator();
        this.countHopNumber += (vertexList.size() - 1);
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if(iterator.hasNext() == true) {
                System.out.printf(vertex.nodeId + " -> ");
            }else {
                System.out.printf(vertex.nodeId + "\n");
            }
        }
    }

    public void normalPeriodComputingPath(Service service, FileWriter fileWriter) throws Exception{

        //D算法算路
        GraphPath graphPath = findShortestPath(service, this.graph);
        serviceGraphPathHashMap.put(service, graphPath);
        service.isComputed = true;
        service.setGraphPath(graphPath);
        System.out.printf("业务 " + service.serviceId + " 已算路: ");
        List<Vertex> vertexList = graphPath.getVertexList();
        //统计跳数
        this.countHopNumber += (vertexList.size() - 1);
        fileWriter.write(vertexList.size() - 1 + "\n");
        fileWriter.close();

        Iterator<Vertex> iterator = vertexList.iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if(iterator.hasNext() == true) {
                System.out.printf(vertex.nodeId + " -> ");
            }else {
                System.out.printf(vertex.nodeId + "\n");
            }
        }
    }

    /**分配资源*/
    public void allocateResource(Service service) {
        try {
            GraphPath servicePath = serviceGraphPathHashMap.get(service);
            List<SimpleEdge> edgeList = servicePath.getEdgeList();
            int n = service.numberOfWavelenthes;
            /** 分配波长(满足波长一致性) */
            int count = 0;  //记录各边都满足的连续波长数
            List<Integer> freeWavelenthesNumber = new ArrayList<Integer>(); // 统计空闲波长号用
            //资源统计
            for(int i = 0; i < Tools.DEFAULTNUMBEROFWAVELENTHES; i ++) {    //对于每个波长号，统计各个边的该波长号是否都空闲
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
                    service.wavelengthesNumber.add(Integer.valueOf(currentWavelenthNumber));//将波长号放入servce对象中
                    Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                    while (edgeIterator.hasNext()) {
                        SimpleEdge currentEdge = edgeIterator.next();
                        currentEdge.wavelenthOccupation[currentWavelenthNumber] = true;
                        currentEdge.numberOfOccupatedWavelength +=1;
                    }
                    //System.out.print("[" + currentWavelenthNumber + "]");
                }
                System.out.printf("\n");
                service.isAllocated = true;

            }else {
                service.isBlocked = true;
                service.wavelengthesNumber.clear(); //如果分配资源不成功，就释放service对象占用的波长号
                //System.out.println("没有足够资源分配给业务 " + service.serviceId + " 。");
                blockedTimes +=1;
                if(System.currentTimeMillis() - this.programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        System.currentTimeMillis() - this.programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    blockedTimesInTidalMigrationPeriod +=1;
                    if(service.isLongTimeService) {
                        blockedLongTimeService += 1;
                        if(!service.srcNode.areaId.equals(service.desNode.areaId)) {
                            blockedCrossAreaLongTimeService += 1;
                        }else {
                            blockedInAreaLongTimeService += 1;
                        }
                    }else {
                        if(!service.srcNode.areaId.equals(service.desNode.areaId)) {
                            blockedCrossAreaShortTimeService += 1;
                        }else {
                            blockedInAreaShortTimeService += 1;
                        }
                    }
                }
            }




        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        while (true) {
            try {
                /**业务到来*/
                Service service = serviceBlockingQueue.take();
                if(System.currentTimeMillis() - programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        System.currentTimeMillis() - programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    this.servicesNumberInTidalMigrationPeriod +=1;
                }

                /**峰谷状态更新*/
                //各域负载初始化
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
                        currentArea.load += currentEdge.numberOfOccupatedWavelength;
                    }
                    //域间边
                    else {
                        Area srcArea = this.areaHashMap.get(currentEdge.srcVertex.areaId);
                        Area desArea = this.areaHashMap.get(currentEdge.desVertex.areaId);
                        srcArea.load += currentEdge.numberOfOccupatedWavelength;
                        desArea.load += currentEdge.numberOfOccupatedWavelength;
                    }
                }

                //各域状态判断
                areaMapIterator = this.areaHashMap.entrySet().iterator();
                //把负载值写入文件
                FileWriter areaOneLoadFileWriter, areaTwoLoadFileWriter, areaThreeLoadFileWriter, totalLoadFileWriter,
                        hopFileWriter;

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
                }
                //为计算资源利用率做统计
                long nowTime = System.currentTimeMillis();
                if(nowTime - this.programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        nowTime - this.programStartTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    double totalload = 0;
                    areaMapIterator = this.areaHashMap.entrySet().iterator();
                    while (areaMapIterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) areaMapIterator.next();
                        Area currentArea = (Area) entry.getValue();
                        totalload += currentArea.load;
                    }
                    totalLoadFileWriter.write(totalload + "\n");
                    totalLoadFileWriter.close();
                    }

                /**潮汐迁移时段算路处理方案*/
                long alreadyRunTime = System.currentTimeMillis() - this.programStartTime;
                //判断是否处于迁移时段
                if(alreadyRunTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE &&
                        alreadyRunTime < (Tools.DEFAULTWORKINGTIME + 3 * Tools.DEFAULTAVERAGESERVICETIME) * Tools.TIMESCALE) {
                    //判断业务是否为长连接
                    if(!service.isLongTimeService) {

                        shortTimeService += 1;
                        if(!service.srcNode.areaId.equals(service.desNode.areaId)) {
                            crossAreaShortTimeService += 1;
                        }else {
                            inAreaShortTimeService += 1;
                        }

                        //重新赋边权(以负载为边权)
                        //如果在对照组分支上，将这部分注释掉

                        reAllocateWeight();
                        normalPeriodComputingPath(service, hopFileWriter);
                        allocateResource(service);
                    } else {
                        longTimeServiceInTidalMigrationPeriod += 1;
                        //判断业务源宿节点是否在同一个域内
                        if(service.srcNode.areaId.equals(service.desNode.areaId)) {
                            inAreaLongTimeService += 1;
                            Area tempArea = this.areaHashMap.get(service.srcNode.areaId);
                            //判断当前是否为潮谷
                            if(tempArea.load / tempArea.totalCapacity < tempArea.threshold) {
                                allocatedWeightBack();
                                normalPeriodComputingPath(service, hopFileWriter);
                                allocateResource(service);
                            }else {
                                //判断是否峰往谷迁移
                                if(service.srcNode.areaId == "1") {
                                    allocatedWeightBack();
                                    normalPeriodComputingPath(service, hopFileWriter);
                                    allocateResource(service);
                                    //判断当前资源是否足够
                                    if(service.isResourceAllocated() != true) {
                                        reAllocateWeight();
                                        normalPeriodComputingPath(service, hopFileWriter);
                                        allocateResource(service);
                                    }
                                }else {
                                    reAllocateWeight();
                                    normalPeriodComputingPath(service, hopFileWriter);
                                    allocateResource(service);
                                }
                            }
                        }else {//源宿节点不在一个域内
                            crossAreaLongTimeService += 1;  //统计跨域长连接个数
                            edgeIterator = this.graph.edgeSet().iterator();
                            while (edgeIterator.hasNext()) {
                                SimpleEdge currentedge = edgeIterator.next();
                                if(this.graph.containsEdge(currentedge)) {
                                    if(currentedge.srcVertex.areaId == "1" && currentedge.desVertex.areaId == "1") {
                                        this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength * (1- Tools.LOADCHANGEPERCENT));
                                    }else if (currentedge.srcVertex.areaId == "3" && currentedge.desVertex.areaId == "3") {
                                        this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength * (1 + Tools.LOADCHANGEPERCENT));
                                    }else {
                                        this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength);
                                    }

                                }
                            }
                            normalPeriodComputingPath(service, hopFileWriter);
                            allocateResource(service);
                        }
                    }
                }else {
                    /**普通时段算路*/
                    /**算路*/
                    reAllocateWeight();
                    normalPeriodComputingPath(service);
                    allocateResource(service);
                }


                /**资源分配*/
                //考虑迁移时段处理的分支将此语句写在前面
                //allocateResource(service);

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
                    fw.close();
                    //System.out.println("被阻塞的业务个数为:" + this.blockedTimes);
                    System.out.println("潮汐迁移时段长连接个数：" + this.longTimeServiceInTidalMigrationPeriod);
                    //System.out.println("平均跳数：" + (double)this.countHopNumber / this.servicesNumberInTidalMigrationPeriod);
                    //System.out.println("潮汐迁移时段被阻塞业务个数：" + this.blockedTimesInTidalMigrationPeriod);
                    System.out.println("潮汐迁移时段业务个数[阻塞/全部]：" + this.blockedTimesInTidalMigrationPeriod + "/" + this.servicesNumberInTidalMigrationPeriod);
                    System.out.println("[跨域长连接]个数[阻塞/全部]：" + this.blockedCrossAreaLongTimeService + "/" + this.crossAreaLongTimeService);
                    System.out.println("[跨域短连接]个数[阻塞/全部]：" + this.blockedCrossAreaShortTimeService + "/" + this.crossAreaShortTimeService);
                    System.out.println("[域内短连接]个数[阻塞/全部]：" + this.blockedInAreaShortTimeService + "/" + this.inAreaShortTimeService);
                    System.out.println("[域内长连接]个数[阻塞/全部]：" + this.blockedInAreaLongTimeService + "/" + this.inAreaLongTimeService);
                    System.out.println("迁移时段阻塞的长连接个数："  + this.blockedLongTimeService);
                    //System.out.println("迁移时段阻塞的跨域长连接个数：" + this.blockedCrossAreaLongTimeService);
                    System.out.println("程序结束");
                }


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
