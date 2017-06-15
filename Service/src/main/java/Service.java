/**
 * Created by yuqia_000 on 2017/6/15.
 */
import
public class Service {
    Vertex srcNode;
    Vertex desNode;
    double bandwidth;
    double wavelenth;
    int serviceTime;

    public Service(Vertex srcNode, Vertex desNode, double bandwidth, double wavelenth, int serviceTime) {
        this.srcNode = srcNode;
        this.desNode = desNode;
        this.bandwidth = bandwidth;
        this.wavelenth = wavelenth;
        this.serviceTime = serviceTime;
    }
}
