package Service;

import Topology.Vertex;

/**
 * Created by yuqia_000 on 2017/6/15.
 */
public class Service {
    String serviceId;
    Vertex srcNode;     //源节点
    Vertex desNode;     //宿节点
    double unitbandwidth = 6.25;
    double bandwidth;   //带宽
    double wavelenth;   //占用的波长
    int numberOfWavelenthes;
    int serviceTime;    //请求服务时间
    boolean isComputed; //是否已算路
    boolean isAllocated;


    public Service(Vertex srcNode, Vertex desNode, int numberOfWavelenthes, int serviceTime) {
        this.srcNode = srcNode;
        this.desNode = desNode;
        this.bandwidth = unitbandwidth * numberOfWavelenthes;
        this.numberOfWavelenthes = numberOfWavelenthes;
        this.serviceTime = serviceTime;
        this.isComputed = false;
    }

    public boolean isPathComputed(){
        return isComputed;
    }

    public boolean isResourceAllocated() {
        return isAllocated;
    }

    public void setServiceId(String s) {
        serviceId = s;
    }

    @Override
    public int hashCode() {
        int x = srcNode.hashCode();
        int y = desNode.hashCode();
        return (x * y + x + y) * (int)bandwidth;
    }

    @Override
    public boolean equals(Object obj) {
        Service x = (Service)obj;
        if(x.hashCode() == this.hashCode()) {
            return true;
        }else {
            return false;
        }
    }
}
