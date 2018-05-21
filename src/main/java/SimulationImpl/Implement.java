package SimulationImpl;
import Service.*;
import Service.Reconfiguration.ReconfigExecutor;
import Service.Reconfiguration.Trigger;
import Topology.*;


import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class Implement {
    public static void main(String[] args) {
        //拓扑读取
        SimpleGraph simpleGraph = new SimpleGraph();
        simpleGraph.parseJsonToGraph();

        //初始时间
        long startTime = System.currentTimeMillis();

        //业务发生 与 初步算路
        BlockingQueue<Service> servicesToComputePath = new ArrayBlockingQueue<Service>(10);
        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath, startTime);
        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph,
                simpleGraph.areaHashMap, startTime);
        /**201805015 注释为了统计数据*/
        ReconfigExecutor reconfigExecutor = new ReconfigExecutor(computePathThread, poissionStreamThread.servicesMap);
        Trigger trigger = new Trigger(computePathThread, reconfigExecutor);


        poissionStreamThread.start();
        computePathThread.start();

        /*
        Timer timer = new Timer();

        try{
            LoadCountTask loadCountTask = new LoadCountTask(simpleGraph.areaHashMap.get("1"),
                    simpleGraph.areaHashMap.get("2"), simpleGraph.areaHashMap.get("3"));
            timer.schedule(loadCountTask, 20, 200);
        }catch (Exception e) {

        }*/




    }
}
