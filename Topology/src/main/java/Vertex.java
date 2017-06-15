/**
 * Created by yuqia_000 on 2017/6/15.
 */
public class Vertex {
    String nodeId;
    int portNumber;
    String[] portId;

    public Vertex(String nodeId) {
        this(nodeId,1);
    }

    public Vertex(String nodeId, int portNumber) {
        this.nodeId = nodeId;
        this.portNumber =portNumber;
        this.portId = new String[portNumber];
    }
}
