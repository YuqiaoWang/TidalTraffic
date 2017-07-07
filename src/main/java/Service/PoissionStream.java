package Service;

import SimulationImpl.Tools;
import Topology.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream extends Thread {
    public double lambda = 2 ;
    public long programStartTime;
    public List<Service> listOfServices = new ArrayList<Service>();
    public BlockingQueue<Service> serviceBlockingQueue;

    public PoissionStream() {

    }
    public PoissionStream(BlockingQueue<Service> bq, long startTime) {
        this.serviceBlockingQueue = bq;
        this.programStartTime = startTime;
    }

    @Override
    public void run() {
        double x;

        for(int i = 0; i < Tools.DEFAULTSERVICENUMBER; i++) {
            x = poissionNumber();
            int time = (int) x * 1000;
            Service service = generateService();
            if(service.srcNode.nodeId.equals(service.desNode.nodeId)) {
                i = i - 1;
                continue;
            }
            try{
                this.sleep(time);     //用线程休眠来模拟泊松流到达过程
                service.setServiceId(String.format("%4d", i).replace(" ", "0"));
                System.out.println("--------业务 " + service.serviceId + " 到来，距上次 " + time/1000 + " 秒--------");
                System.out.println("srcNodeId: " + service.srcNode.nodeId);
                System.out.println("desNodeId: " + service.desNode.nodeId);
                System.out.printf("bandwidth: %.2f \n" , service.bandwidth);
                System.out.println("serviceTime: " + service.serviceTime);
                serviceBlockingQueue.put(service);
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
        double unitWavelenth = 6.25;
        Random rand = new Random();
        //String srcNodeId = Integer.toString(rand.nextInt(7) + 1);
        String srcNodeId = srcNuniformNode();
        //String desNodeId = Integer.toString(rand.nextInt(7) + 1);
        String desNodeId = desNuniformNode();
        Vertex srcNode = new Vertex(srcNodeId);
        Vertex desNode = new Vertex(desNodeId);
        int numberOfwavelength = rand.nextInt(8) + 1;
        //double bandwidth = unitWavelenth * numberOfwavelength;
        //double wavelenth = 192 + Math.random();
        int serviceTime = rand.nextInt(50) + 1;
        Service randomService = new Service(srcNode, desNode, numberOfwavelength, serviceTime);
        return randomService;
    }

    /**节点不均匀随机分布(与运行时间有关)*/
    public String srcNuniformNode() {
        Random rand = new Random();
        int i;
        if(System.currentTimeMillis() - this.programStartTime < Tools.DEFAULTWORKINGTIME) {
            i = rand.nextInt(10) + 1;
            switch (i) {
                case 10 :
                    i = 1;
                    break;
                case 9 :
                    i = 2;
                    break;
                case 8 :
                    i = 3;
                    break;
                default:
                    break;
            }
        }else {
            i = rand.nextInt(9) + 1;
            switch (i) {
                case 9 :
                    i = 4;
                    break;
                case 8 :
                    i = 5;
                    break;
                default:
                    break;
            }

        }

        return Integer.toString(i);
    }
    public String desNuniformNode() {
        Random rand = new Random();
        int i ;
        if(System.currentTimeMillis() - this.programStartTime < (Tools.DEFAULTWORKINGTIME*1000)) {
            i = rand.nextInt(12) + 1;
            switch (i) {
                case 12 :
                    i = 6;
                    break;
                case 11 :
                    i = 7;
                    break;
                case 10 :
                    i = 1;
                    break;
                case 9 :
                    i = 2;
                    break;
                case 8 :
                    i = 3;
                    break;
                default:
                    break;
            }
        }else {
            i = rand.nextInt(11) + 1;
            switch (i) {
                case 11 :
                    i = 4;
                    break;
                case 10 :
                    i = 5;
                    break;
                case 9 :
                    i = 6;
                    break;
                case 8 :
                    i = 7;
                    break;
                default:
                    break;
            }
        }

        return Integer.toString(i);
    }

}

