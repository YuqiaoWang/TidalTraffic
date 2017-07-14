package SimulationImpl;
import Service.*;
import Topology.*;

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
        BlockingQueue<Service> servicesToComputePath = new ArrayBlockingQueue<Service>(20);
        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath, simpleGraph.graph, startTime);
        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph, startTime);

        poissionStreamThread.start();
        computePathThread.start();



    }
}
