package Service;

import Service.Reconfiguration.ReconfigStatistic;
import Service.Reconfiguration.Trigger;
import SimulationImpl.ClockUtil;
import Topology.Area;
import Topology.SimpleEdge;
import Topology.SimpleGraph;
import Topology.Vertex;
import SimulationImpl.Tools;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.SimpleWeightedGraph;
import DataProcess.FigureGenerate;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Deprecated;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class ComputePath extends Thread {
    private ServiceTransMsg msg; // 业务传输完毕标识
    public long programStartTime; // 程序启动时间
    public HashMap<Service, GraphPath<Vertex, SimpleEdge>> serviceGraphPathHashMap = new HashMap<Service, GraphPath<Vertex, SimpleEdge>>(); // 业务-最短路
                                                                                                                                            // map
    public BlockingQueue<Service> serviceBlockingQueue; // 阻塞队列，用于业务发生器线程与算路分配资源线程之间的资源分配
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph; // 拓扑
    public SimpleWeightedGraph<Vertex, SimpleEdge> loadGraph; // 负载拓扑，用于过程中更改权重
    public HashMap<String, Area> areaHashMap; // 标号与域的map
    public ScheduledExecutorService scheduExec; // 线程池，用来存放timertask的执行线程
    /**
     * 统计指标
     */
    public int blockedTimes; // 业务阻塞次数
    public int servicesNumberInTidalMigrationPeriod; // 潮汐时段业务个数
    public int blockedTimesInTidalMigrationPeriod; // 潮汐时段业务阻塞个数
    public String lastServiceIDInTidalMigrationPeriod; // 潮汐时段最后一个业务的ID
    public int countHopNumber; // 跳数统计
    public ClockUtil clock; // 计时相关
    public long totalCalculateTime; // 算路时间统计

    /**
     * 重构相关的属性
     */
    public List<Trigger> listenerList; // 监听者列表，用于通知重构触发器
    public ReconfigStatistic reconfigStatistic; // 重构统计器

    public ComputePath() {

    }

    public ComputePath(BlockingQueue<Service> bq, SimpleWeightedGraph<Vertex, SimpleEdge> graph,
            HashMap<String, Area> areaHashMap, ServiceTransMsg msg, ClockUtil clock) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
        // this.loadGraph = (SimpleWeightedGraph<Vertex, SimpleEdge>) graph.clone();
        this.loadGraph = SimpleGraph.cloneGraph(graph);
        this.blockedTimes = 0;
        this.programStartTime = clock.getStartTime();
        this.servicesNumberInTidalMigrationPeriod = 0;
        this.blockedTimesInTidalMigrationPeriod = 0;
        this.lastServiceIDInTidalMigrationPeriod = "0";
        this.countHopNumber = 0;
        this.totalCalculateTime = 0;
        this.areaHashMap = areaHashMap;
        this.scheduExec = Executors.newScheduledThreadPool(Tools.CORE_POOL_SIZE);
        this.listenerList = new ArrayList<>();
        this.reconfigStatistic = new ReconfigStatistic(areaHashMap, graph);
        this.clock = clock;
        this.msg = msg;
        this.setName("compute_path_thread");
    }

    /**
     * 最短路算路
     * 
     * @param service 业务
     * @param graph   拓扑
     * @return 算出来的最短路
     */
    public GraphPath<Vertex, SimpleEdge> findShortestPath(Service service,
            SimpleWeightedGraph<Vertex, SimpleEdge> graph) {
        Vertex srcNode = service.srcNode;
        Vertex desNode = service.desNode;
        // TODO: 以后加入KSP算路方法，并通过配置文件灵活变更
        GraphPath<Vertex, SimpleEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, srcNode, desNode);
        service.isComputed = true; // 业务标记为已算路
        service.setGraphPath(shortestPath); // 将业务路径记录
        return shortestPath;
    }

    /**
     * 将边权赋值为[距离（拓扑中两点间长度）]
     */
    public void setWeightAsMetric() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if (this.graph.containsEdge(currentedge)) {
                this.graph.setEdgeWeight(currentedge, currentedge.metric);
            }
        }
    }

    /**
     * 将边权赋值成[当前负载值]
     */
    @Deprecated
    public void reAllocateWeight() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentedge = edgeIterator.next();
            if (this.graph.containsEdge(currentedge)) {
                this.graph.setEdgeWeight(currentedge, currentedge.numberOfOccupatedWavelength);
            }
        }
    }

    /**
     * 将各边的权重赋值成[预测到的负载值]
     */
    // TODO:等边预测模型完成后，使用本方法
    public void reAllocatedWeightAsFutureTraffic() {
        Iterator<SimpleEdge> edgeIterator = this.graph.edgeSet().iterator();
        while (edgeIterator.hasNext()) {
            SimpleEdge currentEdge = edgeIterator.next();
            if (this.graph.containsEdge(currentEdge)) {
                // TODO:将来改成预测的边负载
                this.graph.setEdgeWeight(currentEdge, currentEdge.getFutureLoad());
            }
        }
    }

    /**
     * 资源分配（在业务算完路后调用）
     * 
     * @param service 业务
     * @return 是否分配成功
     */
    public boolean allocateResource(Service service) {
        try {
            // GraphPath<Vertex, SimpleEdge> servicePath =
            // serviceGraphPathHashMap.get(service);
            GraphPath<Vertex, SimpleEdge> servicePath = service.getGraphPath();
            if (servicePath == null) { // 判断是否算路
                throw new Exception("业务[" + service.serviceId + "]还未算路！");
            }
            List<SimpleEdge> edgeList = servicePath.getEdgeList(); // 得到路径的link序列
            int n = service.numberOfWavelenthes;
            /** 分配波长(满足波长一致性) */
            int count = 0; // 记录各边都满足的连续波长数
            List<Integer> freeWavelengthesNumber = new ArrayList<Integer>(); // 统计空闲波长号用
            // 资源统计
            for (int i = 0; i < Tools.DEFAULTNUMBEROFWAVELENTHES; i++) { // 对于每个波长号，统计各个边的该波长号是否都空闲
                int edgeCount = 0;
                Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    if (currentEdge.wavelenthOccupation[i] == false) {
                        edgeCount++;
                    }
                }
                if (edgeCount == edgeList.size()) {
                    count++;
                    freeWavelengthesNumber.add(Integer.valueOf(i));
                }
            }
            // 分配资源
            if (count >= n) { // 如果[空闲波长]不少于[需要的波长]
                // TODO:今后在这里加入配置文件，灵活变更分配策略
                if (Tools.RESOURCE_ALLOCATION_STRATEGY == 1) {
                    AllocationStrategy.firstFit(service, freeWavelengthesNumber, edgeList);
                } else if (Tools.RESOURCE_ALLOCATION_STRATEGY == 2) {
                    AllocationStrategy.randomFit(service, freeWavelengthesNumber, edgeList);
                }

            } else { // 若波长资源不够用
                service.isBlocked = true;
                service.isOutOfTime = true;
                service.wavelengthesNumber.clear(); // 如果分配资源不成功，就释放service对象占用的波长号
                // System.out.println("没有足够资源分配给业务 " + service.serviceId + " 。");
                blockedTimes += 1;
                long programRunningTime = System.currentTimeMillis() - this.clock.getStartTime(); // 程序运行时间
                if (programRunningTime > Tools.DEFAULTWORKINGTIME && programRunningTime < Tools.DEFAULTTIDALENDTIME) {
                    blockedTimesInTidalMigrationPeriod += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 如果抛异常则直接返回false
        }
        return service.isAllocated;
    }

    /**
     * 重构相关的方法 注册监听器(也就是重构的trigger)，让trigger决定是否当前进行重构 （目前已将通知逻辑转移到Loa CountTask）
     */
    public void regist(Trigger trigger) {
        this.listenerList.add(trigger);
    }

    public void unregist(Trigger trigger) {
        this.listenerList.remove(trigger);
    }

    public void run() {
        int serviceNum = 0;
        try {
            LoadCountTask loadCountTask = new LoadCountTask(graph, areaHashMap, listenerList, reconfigStatistic, clock); // 新建统计任务
            this.scheduExec.scheduleAtFixedRate(loadCountTask, Tools.COUNT_DELAY, Tools.COUNT_PERIOD * Tools.TIMESCALE,
                    TimeUnit.MILLISECONDS); // 定时器执行统计
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (System.currentTimeMillis() - programStartTime < Tools.DEFAULTSERVICEENDTIME) { // 当业务未发生完毕时
            if (this.msg.getStatus()) { // 若发生业务状态变为true即完成，则中断
                break;
            }
            try {
                /** 业务到来 */
                Service service = serviceBlockingQueue.take(); // 从阻塞队列拿到业务
                serviceNum++;
                long programRunningTime = System.currentTimeMillis() - this.clock.getStartTime(); // 程序运行时间
                if (programRunningTime > Tools.DEFAULTWORKINGTIME && programRunningTime < Tools.DEFAULTTIDALENDTIME) { // 潮汐时段内
                    this.servicesNumberInTidalMigrationPeriod += 1; // 统计潮汐迁移时段内的业务个数
                    lastServiceIDInTidalMigrationPeriod = service.serviceId;
                }
                /** 算路 */
                // 重新赋边权(以负载为边权)
                // TODO:如果在对照组分支上，将这部分注释掉
                // reAllocateWeight();
                // D算法算路
                long calculateStartTime = System.currentTimeMillis(); // 算路起始时间
                GraphPath<Vertex, SimpleEdge> graphPath = findShortestPath(service, this.loadGraph); // 最短路路径(使用loadGraph做计算)
                serviceGraphPathHashMap.put(service, graphPath); // 将路径放入map中
                // System.out.printf("service No." + service.serviceId + " has been calulated a
                // path: ");
                List<Vertex> vertexList = graphPath.getVertexList();
                // 统计跳数
                long nowTime = System.currentTimeMillis();
                if (nowTime - this.programStartTime > Tools.DEFAULTWORKINGTIME
                        && nowTime - this.programStartTime < Tools.DEFAULTTIDALENDTIME) {
                    countHopNumber += (vertexList.size() - 1);
                }
                /*
                 * Iterator<Vertex> iterator = vertexList.iterator(); while (iterator.hasNext())
                 * { Vertex vertex = iterator.next(); if(iterator.hasNext() == true) {
                 * System.out.printf(vertex.nodeId + " -> "); }else {
                 * System.out.printf(vertex.nodeId + "\n"); } }
                 */
                /** 资源分配 */
                boolean allocated = allocateResource(service);

                /** 统计算路时常 */
                long calculateEndTime = System.currentTimeMillis(); // 分配资源结束时间
                long calculateCost = calculateEndTime - calculateStartTime;
                if (allocated) {
                    this.totalCalculateTime += calculateCost;
                }

                /** 业务离去 */
                if (service.isResourceAllocated() == true) { // 如果分配了资源
                    ServiceLeavingTask serviceLeavingTask = new ServiceLeavingTask(service);
                    this.scheduExec.schedule(serviceLeavingTask, service.serviceTime * Tools.TIMESCALE,
                            TimeUnit.MILLISECONDS);// 业务时间结束后离去
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter("data/load/blockedTimes.txt");
            fw.write(Integer.toString(this.blockedTimes));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** 统计 */
        System.out.println("number of blocked services:" + this.blockedTimes);
        System.out.println("blocked services in tidal period:" + this.blockedTimesInTidalMigrationPeriod);
        System.out.println("number of services in tidal period:" + this.servicesNumberInTidalMigrationPeriod);
        double averageHop = (double) countHopNumber / this.servicesNumberInTidalMigrationPeriod;
        System.out.println("average hop:" + averageHop);
        // System.out.println("the last service ID in tidal period:" +
        // lastServiceIDInTidalMigrationPeriod);
        double averageCalculateTime = (double) this.totalCalculateTime / (serviceNum - this.blockedTimes);
        // System.out.println("average calculate path time: " + averageCalculateTime);
        System.out.println("mapping the calculate time to reality: " + 1200 * averageCalculateTime);

        // System.out.println("*****statistic about reconstruction*******");
        // System.out.println("times of reconstruction:" +
        // reconfigStatistic.reconfigTimes);
        // System.out.println("number of success reconstruction:" +
        // reconfigStatistic.numberOfReconfigedServices);
        // System.out.println("number of failure reconstruction:" +
        // reconfigStatistic.numberOfFailedServices);

        while (System.currentTimeMillis() - this.programStartTime < Tools.PROGRAM_EXECUTE_TIME + 1000) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        this.scheduExec.shutdown();
        System.out.println("Program ending");
        /*
         * try { FigureGenerate.generateJson(this.blockedTimes,
         * this.blockedTimesInTidalMigrationPeriod, averageHop);
         * FigureGenerate.generateFigure(); } catch (Exception e) { // TODO: handle
         * exception e.printStackTrace(); }
         */

    }
}
