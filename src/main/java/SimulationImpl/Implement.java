package SimulationImpl;
import Service.Service;
import Service.PoissionStream;
import Topology.*;
/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class Implement {
    public static void main(String[] args) {
        //拓扑读取
        SimpleGraph simpleGraph = new SimpleGraph();
        simpleGraph.parseJsonToGraph();

        //业务发生
        PoissionStream poissionStream = new PoissionStream();
        poissionStream.start();

        //初步算路

    }
}
