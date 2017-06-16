package Service;

import Topology.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream {
    public double lambda ;


    public static void main(String[] args) {
        GenerationThread serviceGeneratingThread = new GenerationThread();
        serviceGeneratingThread.start();
    }
    public void getService() {

    }

    public void generateService() {

    }


}

class GenerationThread extends Thread {
    public double lambda = 4;

    @Override
    public void run() {
        double x;
        List<Service> listOfServices = new ArrayList<Service>();

        for(int i = 0; i < 10; i++) {
            x = poissionNumber();
            int time = (int) x * 1000;
            try{
                this.sleep(time);     //用线程休眠来模拟泊松流到达过程
                System.out.println("业务到来，距上次 " + time/1000 + " 秒");
                Service service = generateService();
                System.out.println("srcNodeId: " + service.srcNode.nodeId);
                System.out.println("desNodeId: " + service.desNode.nodeId);
                System.out.printf("bandwidth: %.2f \n" , service.bandwidth);
                System.out.println("serviceTime: " + service.serviceTime);
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
        String srcNodeId = Integer.toString(rand.nextInt(10));
        String desNodeId = Integer.toString(rand.nextInt(10));
        Vertex srcNode = new Vertex(srcNodeId);
        Vertex desNode = new Vertex(desNodeId);
        double bandwidth = Math.random()*10;
        double wavelenth = 192 + Math.random();
        int serviceTime = rand.nextInt(100);
        Service randomService = new Service(srcNode, desNode, bandwidth, wavelenth, serviceTime);

        return randomService;

    }
}
