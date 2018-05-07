package TrafficDescription;

import SimulationImpl.Tools;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


/**
 * Created by yuqia_000 on 2018/5/7.
 */
public class TransClientTest {
    protected TrafficDataService.Client client;
    protected TTransport transport;

    /**
     * create TTransport
     * @return
     */
    public TTransport createTTransport() {
        //TTransport transport = new TSocket(Utils.IP_LOCAL_HOST, Utils.PORT);
        TTransport transport = new TSocket(Tools.IP_LOCALHOST, Tools.PORT);
        return transport;
    }

    /**
     * open TTransport
     * @param transport
     * @throws TTransportException
     */
    public void openTTransport(TTransport transport) throws TTransportException {
        if(transport == null) {
            return ;
        }
        transport.open();
    }

    /**
     * close TTransport
     * @param transport
     */
    public void closeTTransport(TTransport transport) {
        if(transport == null) {
            return;
        }
        transport.close();
    }

    /**
     * create client
     * @param transport
     * @return
     */
    public TrafficDataService.Client createClient(TTransport transport) {
        if(transport == null) {
            return null;
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        if(protocol == null) {
            return null;
        }
        TrafficDataService.Client client = new TrafficDataService.Client(protocol);
        return client;
    }

    public static void main(String[] args) {
        try{
            TransClientTest testClient = new TransClientTest();
            testClient.transport = testClient.createTTransport();
            testClient.openTTransport(testClient.transport);
            testClient.client = testClient.createClient(testClient.transport);

            //service calling
            if(testClient.client.equals(null)) {
                //log.info("创建客户端失败...");
                return ;

            }else{
                System.out.println("创建客户端成功");
            }

        } catch (Exception e) {
            e.printStackTrace();



        }
    }
}
