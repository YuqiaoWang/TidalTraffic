package Service;

import Topology.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream extends Thread {
    public double lambda = 4;
    public List<Service> listOfServices = new ArrayList<Service>();

    @Override
    public void run() {
        double x;

        for(int i = 0; i < 10; i++) {
            x = poissionNumber();
            int time = (int) x * 1000;
            Service service = generateService();
            if(service.srcNode.nodeId.equals(service.desNode.nodeId)) {
                i = i - 1;
                continue;
            }
            try{
                this.sleep(time);     //用线程休眠来模拟泊松流到达过程
                System.out.println("业务到来，距上次 " + time/1000 + " 秒");
                System.out.println("srcNodeId: " + service.srcNode.nodeId);
                System.out.println("desNodeId: " + service.desNode.nodeId);
                System.out.printf("bandwidth: %.2f \n" , service.bandwidth);
                System.out.println("serviceTime: " + service.serviceTime);
                service.setServiceId(String.format("%4d", i).replace(" ", "0"));
                listOfServices.add(service);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**产生满足泊松分布的随机数*/
    public double poissionNumber() {
        double x = 0;
        double b = 1;
        double c = Math.exp(-this.lambda);
        double u;

        do {
            u = Math.random();
            b *= u;
            if(b >= c) {
                x++;
            }
        }while (b >= c);

        return x;
    }

    /**随机产生业务*/
    public Service generateService() {
        Random rand = new Random();
        String srcNodeId = Integer.toString(rand.nextInt(7) + 1);
        String desNodeId = Integer.toString(rand.nextInt(7) + 1);
        Vertex srcNode = new Vertex(srcNodeId);
        Vertex desNode = new Vertex(desNodeId);
        double bandwidth = Math.random()*10;
        double wavelenth = 192 + Math.random();
        int serviceTime = rand.nextInt(100);
        Service randomService = new Service(srcNode, desNode, bandwidth, wavelenth, serviceTime);
        return randomService;
    }
}

