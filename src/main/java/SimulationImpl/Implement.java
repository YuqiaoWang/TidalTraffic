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

        //业务发生 与 初步算路
        BlockingQueue<Service> servicesToComputePath = new ArrayBlockingQueue<Service>(10);
        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath);
        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph);

        poissionStreamThread.start();
        computePathThread.start();



    }
}
