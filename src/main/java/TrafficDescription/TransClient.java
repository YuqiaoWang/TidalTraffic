package TrafficDescription;

import SimulationImpl.Tools;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by yuqia_000 on 2018/5/2.
 */

/**
 * 与python server端建立通信的client端
 */
public class TransClient {
    public static int port = Tools.PORT;
    public static String ip = Tools.IP_LOCALHOST;
    public TrafficDataService.Client client;
    public TTransport transport;

    /**
     * 创建TTransport
     *
     */
    public TTransport createTTransport() {
        if(transport == null) {
            transport = new TSocket(ip, port);
        }
        return transport;
    }

    /**
     * 开启 TTransport
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
     * 关闭 TTransport
     * @param transport
     */
    public static void closeTTransport(TTransport transport) {
        if(transport.equals(null)) {
            return ;
        }
        transport.close();
    }

    /**
     * 创建客户端
     * @param transport
     * @return
     */
    public static TrafficDataService.Client createClient(TTransport transport) throws Exception {
        if(transport.equals(null)) {
            //throw new Exception("创建client时, transport为null");
            return null;
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        if(protocol.equals(null)) {
            return null;
        }
        TrafficDataService.Client newClient = new TrafficDataService.Client(protocol);
        return  newClient;

    }

    public boolean isClientStarted() {
        if(client != null) {
            return true;
        }else {
            return false;
        }
    }



}
