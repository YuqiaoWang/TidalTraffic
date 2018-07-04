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

/**
 * 执行[业务离去]动作
 */
public class ServiceLeavingTask extends TimerTask {
    Service service;
    public ServiceLeavingTask(Service service) {
        this.service = service;
    }

    public void run() {
        System.out.println("--------业务" + service.serviceId + "离去，资源被释放--------");
        for(int i = 0; i < service.wavelengthesNumber.size(); i++) {            //对于该业务的每个波长
            int currentWavelenthNumber = service.wavelengthesNumber.get(i);     //拿到波长号
            Iterator<SimpleEdge> edgeIterator = service.graphPath.getEdgeList().iterator();
            while (edgeIterator.hasNext()) {                                    //对于该业务占用路径的每一条link
                SimpleEdge currentEdge = edgeIterator.next();
                currentEdge.wavelenthOccupation[currentWavelenthNumber] = false;//取消资源占用
                currentEdge.numberOfOccupatedWavelength-=1;                     //该link[已被占用的波长数]-1
                currentEdge.serviceOnWavelength[currentWavelenthNumber] = null; //把该link的[该波长号是哪个业务占用的]这个信息清除
            }
        }
        service.wavelengthesNumber.clear();
        service.isOutOfTime = true;
    }


}
