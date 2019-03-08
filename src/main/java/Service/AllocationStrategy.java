package Service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import Topology.SimpleEdge;
import java.util.Random;

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

    /**
     * Random-Fit 分配策略
     * 
     * @param service                业务
     * @param freeWavelengthesNumber 空闲波长
     * @param edgeList               业务路径
     * @return null
     */
    public static void randomFit(Service service, List<Integer> freeWavelengthesNumber, List<SimpleEdge> edgeList) {
        int n = service.numberOfWavelenthes; // 记业务需要占用的波长数
        // int numberOfFreeWavelengthes = freeWavelengthesNumber.size(); // 空闲波长数
        Set<Integer> chosenWavelengthesNumber = new TreeSet<>();
        Random randForWavelength = new Random();
        for (int i = 0; i < n; i++) {
            int randInt = randForWavelength.nextInt(n);
            if (!chosenWavelengthesNumber.contains(randInt)) { // 如果该随机空闲波长未被随机抽中过
                int currentWavelengthNumber = freeWavelengthesNumber.get(randInt).intValue(); // 取出波长号
                service.wavelengthesNumber.add(Integer.valueOf(currentWavelengthNumber)); // 将波长号放入service对象中
                Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    currentEdge.wavelenthOccupation[currentWavelengthNumber] = true;
                    currentEdge.serviceOnWavelength[currentWavelengthNumber] = service.serviceId; // 将每个波长跑的什么业务记录下来
                    currentEdge.numberOfOccupatedWavelength += 1;
                }
                chosenWavelengthesNumber.add(randInt); // 将该随机数放入已抽中集合中
            } else {
                i--; // 重来一次
            }

        }
        service.isAllocated = true;
    }
}