package Service;

import SimulationImpl.Tools;
import Topology.Vertex;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream extends Thread {
    public double lambda = Tools.DEFAULTLAMBDA ;
    public long programStartTime;
    public List<Service> listOfServices = new ArrayList<Service>();
    public BlockingQueue<Service> serviceBlockingQueue;
    public SimpleWeightedGraph graph;
    public HashMap<String, Vertex> vertexHashMap = new HashMap<String, Vertex>();

    public PoissionStream() {

    }
    public PoissionStream(BlockingQueue<Service> bq, SimpleWeightedGraph graph, long startTime) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
        this.programStartTime = startTime;

        Iterator<Vertex> iterator = this.graph.vertexSet().iterator();



        while (iterator.hasNext()) {
            Vertex currentVertex = iterator.next();
            vertexHashMap.put(currentVertex.nodeId, currentVertex);
        }
    }

    @Override
    public void run() {
        double x;

        for(int i = 0; i < Tools.DEFAULTSERVICENUMBER; i++) {
            x = poissionNumber(Tools.DEFAULTLAMBDA);
            int time = (int) x * Tools.TIMESCALE;
            Service service = generateService();
            if(service.srcNode.nodeId.equals(service.desNode.nodeId)) {
                i = i - 1;
                continue;
            }
            try{
                this.sleep(time);     //用线程休眠来模拟泊松流到达过程
                service.setServiceId(String.format("%4d", i).replace(" ", "0"));
                System.out.println("--------业务 " + service.serviceId + " 到来，距上次 " + time/Tools.TIMESCALE + " 秒--------");
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
    public double poissionNumber(double lambda) {
        double x = 0;
        double b = 1;
        double c = Math.exp(-lambda);
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
        //Vertex srcNode = new Vertex(srcNodeId);
        Vertex srcNode = vertexHashMap.get(srcNodeId);
        //Vertex desNode = new Vertex(desNodeId);
        Vertex desNode = vertexHashMap.get(desNodeId);
        int numberOfwavelength = rand.nextInt(Tools.DEFAULTMAXNUMBEROFWAVELENGTH) + 1;
        //double bandwidth = unitWavelenth * numberOfwavelength;
        //double wavelenth = 192 + Math.random();
        //int serviceTime = rand.nextInt(Tools.DEFAULTMAXSERVICETIME) + 1;
        //int serviceTime = (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME);
        Random randForServiceTime = new Random();
        int serviceTime;
        if(randForServiceTime.nextInt(50)>45) {
            serviceTime = (int) poissionNumber(1.6 * Tools.DEFAULTAVERAGESERVICETIME);
        }else {
            serviceTime = (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME);
        }
        Service randomService = new Service(srcNode, desNode, numberOfwavelength, serviceTime);
        return randomService;
    }

    /**节点不均匀随机分布(与运行时间有关)*/
    /*
    public String srcNuniformNode() {
        Random rand = new Random();
        int i;
        if(System.currentTimeMillis() - this.programStartTime < Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE) {
            i = rand.nextInt(16) + 1;
            switch (i) {
                case 16 :
                    i = 1;
                    break;
                case 15 :
                    i = 2;
                    break;
                case 14 :
                    i = 3;
                    break;
                case 13 :
                    i = 1;
                    break;
                case 12 :
                    i = 2;
                    break;
                case 11 :
                    i = 3;
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
            i = rand.nextInt(13) + 1;
            switch (i) {
                case 13 :
                    i = 4;
                    break;
                case 12 :
                    i = 5;
                    break;
                case 11 :
                    i = 4;
                    break;
                case 10 :
                    i = 5;
                    break;
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
    }*/
    public String srcNuniformNode() {
        Random rand = new Random();
        int i;
        if(System.currentTimeMillis() - this.programStartTime < Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE) {
            i = rand.nextInt(50) + 1;
            if(i > 14) {
                i = (i - 14) % 9 + 1;
            }
        }else {
            i = rand.nextInt(54) + 1;
            if(i > 14) {
                i = (i - 14) % 10 + 5;
            }
        }
        return Integer.toString(i);
    }
    /*
    public String desNuniformNode() {
        Random rand = new Random();
        int i ;
        if(System.currentTimeMillis() - this.programStartTime < (Tools.DEFAULTWORKINGTIME*Tools.TIMESCALE)) {
            i = rand.nextInt(16) + 1;
            switch (i) {
                case 16 :
                    i = 6;
                    break;
                case 15 :
                    i = 5;
                    break;
                case 14 :
                    i = 1;
                    break;
                case 13 :
                    i = 2;
                    break;
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
            i = rand.nextInt(15) + 1;
            switch (i) {
                case 15 :
                    i = 4;
                    break;
                case 14 :
                    i = 5;
                    break;
                case 13 :
                    i = 6;
                    break;
                case 12 :
                    i = 7;
                    break;
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
    }*/
    public String desNuniformNode() {
        Random rand = new Random();
        int i;
        if(System.currentTimeMillis() - this.programStartTime < Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE) {
            i = rand.nextInt(32) + 1;
            if(i > 14) {
                i = (i - 14) % 9 + 1;
            }
        }else {
            i = rand.nextInt(34) + 1;
            if(i > 14) {
                i = (i - 14) % 10 + 5;
            }
        }
        return Integer.toString(i);
    }

}

