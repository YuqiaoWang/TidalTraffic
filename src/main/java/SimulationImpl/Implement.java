package SimulationImpl;

import Service.*;
import Service.Provision.ProvisionExecutor;
import Service.Reconfiguration.ReconfigExecutor;
import Service.Reconfiguration.Trigger;
import Topology.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class Implement {
    public static void main(String[] args) {
        // 拓扑读取
        SimpleGraph simpleGraph = new SimpleGraph();
        simpleGraph.parseJsonToGraph();
        // 初始化时间
        ClockUtil clock = new ClockUtil();
        // 业务发生 与 初步算路
        BlockingQueue<Service> servicesToComputePath = new ArrayBlockingQueue<Service>(10);
        ServiceTransMsg msg = ServiceTransMsg.getInstance();
        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath, msg, clock.getStartTime());
        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph,
                simpleGraph.areaHashMap, msg, clock);
        /** 201805015 注释为了统计数据 */
        ReconfigExecutor reconfigExecutor = new ReconfigExecutor(computePathThread, poissionStreamThread.servicesMap,
                clock);
        ProvisionExecutor provisionExecutor = new ProvisionExecutor(computePathThread, clock);
        Trigger trigger = new Trigger(computePathThread, reconfigExecutor, provisionExecutor);
        poissionStreamThread.start();
        computePathThread.start();
    }
}
