package TrafficDescription;


import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by yuqia_000 on 2018/5/3.
 */
public class TransServer {
    private static int port = 9095;
    private static Handler handler;
    private static TrafficDataService.Processor processor;

    public static void start(TrafficDataService.Processor processor) {
        try{
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("starting the simple server...");
            server.serve();
        }catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        handler = new Handler();
        processor = new TrafficDataService.Processor(handler);
        start(processor);
    }
}
