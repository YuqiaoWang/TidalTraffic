package Service.Reconfiguration;

/**
 * Created by yuqia_000 on 2018/5/11.
 */

import Service.Service;

import java.util.Comparator;

/**
 * 为了给service集合排序，需要借助一个comparator
 */
public class ServiceComparator implements Comparator<Service>{
    @Override
    public int compare(Service s1, Service s2) {
        int num = (-1) * (s1.numberOfWavelenthes - s2.numberOfWavelenthes);
        int num2 = num == 0 ? (-1) * (s1.graphPath.getLength() - s2.graphPath.getLength()) : num;
        return num2;
    }
}
