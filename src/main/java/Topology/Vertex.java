package Topology;

/**
 * Created by yuqia_000 on 2017/6/15.
 */
public class Vertex {
    public String areaId;
    public String nodeId;
    public int portNumber;
    public String[] portId;

    public Vertex(String nodeId) {
        this(nodeId,3);//默认3个端口
    }

    public Vertex(String nodeId, int portNumber) {
        this.nodeId = nodeId;
        this.portNumber =portNumber;
        this.portId = new String[portNumber];
        for(int i = 0; i < portNumber; i++) {
            portId[i] = Integer.toString(i);
        }
    }

    public String getAreaId() {
        return this.areaId;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public boolean equals(Object obj) {
        Vertex x = (Vertex) obj;
        if(x.hashCode() == this.hashCode()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(this.nodeId);
    }
}
