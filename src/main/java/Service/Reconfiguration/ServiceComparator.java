package Service.Reconfiguration;

/**
 * Created by yuqia_000 on 2018/5/11.
 */

import Service.Service;

import java.util.Comparator;

/**
 * 为了给service集合排序，需要借助一个comparator
 */
public class ServiceComparator implements Comparator<Service> {
    @Override
    public int compare(Service s1, Service s2) {
        int num = (-1) * (s1.numberOfWavelenthes - s2.numberOfWavelenthes);
        int lengthDiff = 0;
        try {
            lengthDiff = s1.getGraphPath().getLength() - s2.getGraphPath().getLength();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        int num2 = num == 0 ? (-1) * lengthDiff : num;
        return num2;
    }
}
