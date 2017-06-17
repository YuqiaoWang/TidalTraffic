package Service;

import Topology.Vertex;

/**
 * Created by yuqia_000 on 2017/6/15.
 */
public class Service {
    Vertex srcNode;     //源节点
    Vertex desNode;     //宿节点
    double bandwidth;   //带宽
    double wavelenth;   //占用的波长
    int serviceTime;    //请求服务时间
    boolean isComputed; //是否已算路

    public Service(Vertex srcNode, Vertex desNode, double bandwidth, double wavelenth, int serviceTime) {
        this.srcNode = srcNode;
        this.desNode = desNode;
        this.bandwidth = bandwidth;
        this.wavelenth = wavelenth;
        this.serviceTime = serviceTime;
        this.isComputed = false;
    }

    public boolean isPathComputed(){
        return isComputed;
    }
}
