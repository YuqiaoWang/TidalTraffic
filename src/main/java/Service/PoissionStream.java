package Service;

import SimulationImpl.Tools;
import Topology.Vertex;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream extends Thread {
    public double lambda = Tools.DEFAULTLAMBDA; // 业务到达率
    public Map<String, Service> servicesMap = new HashMap<>(); // 存储service的map
    // public List<Service> listOfServices = new ArrayList<Service>();
    public BlockingQueue<Service> serviceBlockingQueue; // 阻塞队列（与算路分配模块对接）
    public long programStartTime; // 程序启动时间

    public PoissionStream() {

    }

    public PoissionStream(BlockingQueue<Service> bq, long startTime) {
        this.serviceBlockingQueue = bq;
        this.programStartTime = startTime;
        this.setName("poission_generate_thread");
    }

    @Override
    public void run() {
        double x;
        for (int i = 0; i < Tools.DEFAULTSERVICENUMBER; i++) {
            // 程序初始化时的平滑增长到达率
            double realLambda = (System.currentTimeMillis() - programStartTime < Tools.PLAINTIME * Tools.TIMESCALE)
                    ? -(2 * Tools.DEFAULTLAMBDA / (Tools.PLAINTIME * Tools.TIMESCALE))
                            * (System.currentTimeMillis() - programStartTime - Tools.PLAINTIME * Tools.TIMESCALE)
                            + Tools.DEFAULTLAMBDA
                    : Tools.DEFAULTLAMBDA;
            x = poissionNumber(realLambda);
            int time = (int) x * Tools.TIMESCALE;
            Service service = generateService();
            if (service.srcNode.nodeId.equals(service.desNode.nodeId)) {
                i = i - 1;
                continue;
            }
            try {
                this.sleep(time); // 用线程休眠来模拟泊松流到达过程
                service.setServiceId(String.format("%4d", i).replace(" ", "0"));
                System.out.println("--------service No. " + service.serviceId + " coming, " + time / 1000
                        + " seconds later than last time--------");
                /*
                 * System.out.println("srcNodeId: " + service.srcNode.nodeId);
                 * System.out.println("desNodeId: " + service.desNode.nodeId);
                 * System.out.printf("bandwidth: %.2f \n" , service.bandwidth);
                 * System.out.println("serviceTime: " + service.serviceTime);
                 */
                serviceBlockingQueue.put(service);
                // listOfServices.add(service);
                servicesMap.put(service.serviceId, service);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /** 产生满足泊松分布的随机数 */
    public double poissionNumber(double lambda) {
        double x = 0;
        double b = 1;
        double c = Math.exp(-lambda);
        double u;
        do {
            u = Math.random();
            b *= u;
            if (b >= c) {
                x++;
            }
        } while (b >= c);

        return x;
    }

    /** 随机产生业务 */
    public Service generateService() {
        double unitWavelenth = 6.25;
        Random rand = new Random();
        // String srcNodeId = Integer.toString(rand.nextInt(7) + 1);
        String srcNodeId = srcNuniformNode();
        // String desNodeId = Integer.toString(rand.nextInt(7) + 1);
        String desNodeId = desNuniformNode();
        Vertex srcNode = new Vertex(srcNodeId);
        Vertex desNode = new Vertex(desNodeId);
        int numberOfwavelength = rand.nextInt(Tools.DEFAULTMAXNUMBEROFWAVELENGTH) + 1;
        // double bandwidth = unitWavelenth * numberOfwavelength;
        // double wavelenth = 192 + Math.random();
        // int serviceTime = rand.nextInt(Tools.DEFAULTMAXSERVICETIME) + 1;
        Random randForServiceTime = new Random();
        int serviceTime;
        // 通过随机来实现业务持续时间是"长"还是"普通"
        if (randForServiceTime.nextInt(50) > 45) {
            serviceTime = (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME)
                    + (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME)
                    + (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME);

        } else {
            serviceTime = (int) poissionNumber(Tools.DEFAULTAVERAGESERVICETIME);
        }
        Service randomService = new Service(srcNode, desNode, numberOfwavelength, serviceTime);
        return randomService;
    }

    /** 节点不均匀随机分布 */
    public String srcNuniformNode() {
        Random rand = new Random();
        int i;
        long nowTime = System.currentTimeMillis();
        if (nowTime - this.programStartTime < Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE) {
            i = rand.nextInt(50) + 1;
            if (i > 14) {
                i = (i - 14) % 9 + 1;
            }
        } else if (nowTime - this.programStartTime > Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE
                && nowTime - this.programStartTime < (Tools.DEFAULTWORKINGTIME + Tools.DEFAULTAVERAGESERVICETIME)
                        * Tools.TIMESCALE) {
            i = rand.nextInt(34) + 1;
            if (i > 14) {
                i = (i - 14) % 10 + 5;
            }
        } else if (nowTime - this.programStartTime > (Tools.DEFAULTWORKINGTIME + Tools.DEFAULTAVERAGESERVICETIME)
                * Tools.TIMESCALE
                && nowTime - this.programStartTime < (Tools.DEFAULTWORKINGTIME + 2 * Tools.DEFAULTAVERAGESERVICETIME)
                        * Tools.TIMESCALE) {
            i = rand.nextInt(44) + 1;
            if (i > 14) {
                i = (i - 14) % 10 + 5;
            }
        } else {
            i = rand.nextInt(54) + 1;
            if (i > 14) {
                i = (i - 14) % 10 + 5;
            }
        }
        return Integer.toString(i);
    }

    public String desNuniformNode() {
        Random rand = new Random();
        int i;
        if (System.currentTimeMillis() - this.programStartTime < Tools.DEFAULTWORKINGTIME * Tools.TIMESCALE) {
            i = rand.nextInt(32) + 1;
            if (i > 14) {
                i = (i - 14) % 9 + 1;
            }
        } else {
            i = rand.nextInt(34) + 1;
            if (i > 14) {
                i = (i - 14) % 10 + 5;
            }
        }
        return Integer.toString(i);
    }
}
