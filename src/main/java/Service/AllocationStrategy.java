package Service;

import java.util.List;
import java.util.Iterator;
import Topology.SimpleEdge;

public class AllocationStrategy {
    /**
     * First-Fit 分配策略
     * 
     * @param service                业务
     * @param freeWavelengthesNumber 空闲波长
     * @param edgeList               业务路径
     * @return null
     */
    public static void firstFit(Service service, List<Integer> freeWavelengthesNumber, List<SimpleEdge> edgeList) {
        int n = service.numberOfWavelenthes;
        // System.out.printf("分配的波长资源：");
        for (int i = 0; i < n; i++) {
            int currentWavelenthNumber = freeWavelengthesNumber.get(i).intValue(); // 取出波长号
            service.wavelengthesNumber.add(Integer.valueOf(currentWavelenthNumber));// 将波长号放入service对象中
            Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
            while (edgeIterator.hasNext()) {
                SimpleEdge currentEdge = edgeIterator.next();
                currentEdge.wavelenthOccupation[currentWavelenthNumber] = true;
                currentEdge.serviceOnWavelength[currentWavelenthNumber] = service.serviceId; // 将每个波长跑的什么业务记录下来
                currentEdge.numberOfOccupatedWavelength += 1;
            }
            // System.out.print("[" + currentWavelenthNumber + "]");
        }
        // System.out.printf("\n");
        service.isAllocated = true;
    }
}