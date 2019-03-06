package Service;

import Topology.SimpleEdge;

import java.util.Iterator;
import java.util.TimerTask;

/**
 * Created by yuqia_000 on 2017/6/20.
 */

/**
 * 执行[业务离去]动作
 */
public class ServiceLeavingTask extends TimerTask {
    Service service; // 要终止的业务
    // Timer timer; // 执行这个任务的timer

    /*
     * public ServiceLeavingTask(Service service, Timer timer) { this.service =
     * service; this.timer = timer; }
     */

    public ServiceLeavingTask(Service service) {
        this.service = service;
    }

    public void run() {
        // System.out.println("--------service No." + service.serviceId + "left, the
        // resource released--------");
        for (int i = 0; i < service.wavelengthesNumber.size(); i++) { // 对于该业务的每个波长
            int currentWavelenthNumber = service.wavelengthesNumber.get(i); // 拿到波长号
            try {
                Iterator<SimpleEdge> edgeIterator = service.getGraphPath().getEdgeList().iterator();
                while (edgeIterator.hasNext()) { // 对于该业务占用路径的每一条link
                    SimpleEdge currentEdge = edgeIterator.next();
                    currentEdge.wavelenthOccupation[currentWavelenthNumber] = false;// 取消资源占用
                    currentEdge.numberOfOccupatedWavelength -= 1; // 该link[已被占用的波长数]-1
                    currentEdge.serviceOnWavelength[currentWavelenthNumber] = null; // 把该link的[该波长号是哪个业务占用的]这个信息清除
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        service.wavelengthesNumber.clear();
        service.isOutOfTime = true;
        // this.timer.cancel();
    }

}
