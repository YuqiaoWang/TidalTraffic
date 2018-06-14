package Service;

import Topology.SimpleEdge;
import Topology.Vertex;
import org.jgrapht.GraphPath;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by yuqia_000 on 2017/6/20.
 */
public class ServiceLeavingTask extends TimerTask {
    Service service;
    public ServiceLeavingTask(Service service) {
        this.service = service;
    }

    public void run() {
        System.out.println("--------业务" + service.serviceId + "离去，资源被释放--------");

        for(int i = 0; i < service.wavelengthesNumber.size(); i++) {
            int currentWavelenthNumber = service.wavelengthesNumber.get(i);
            Iterator<SimpleEdge> edgeIterator = service.graphPath.getEdgeList().iterator();
            while (edgeIterator.hasNext()) {
                SimpleEdge currentEdge = edgeIterator.next();
                currentEdge.wavelenthOccupation[currentWavelenthNumber] = false;
                currentEdge.numberOfOccupatedWavelength-=1;
                currentEdge.serviceOnWavelength[currentWavelenthNumber] = null; //把"该link的该波长号是哪个业务占用的"这个信息清除
            }

        }
        service.wavelengthesNumber.clear();
        service.isOutOfTime = true;
    }


}
