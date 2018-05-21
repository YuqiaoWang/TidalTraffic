package Service.Reconfiguration;

import Service.ComputePath;
import SimulationImpl.Tools;
import Topology.Area;
import Topology.SimpleEdge;
import TrafficDescription.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/4/27.
 */

/**
 * 重构触发器相关，目前没想好这个与资源分配在代码上怎么结合
 * Q1写一些静态方法？
 */
public class Trigger{

    public List<Double> nowIntervalTraffic;

    public TransClient transClient;    //用于thrift与神经网络沟通的client端
    public HashMap<String, Area> areaHashMap;
    public ReconfigExecutor reconfigExecutor;
    public ReconfigStatistic reconfigStatistic;


    public Trigger() {

    }

    public Trigger(ComputePath computePathThread, ReconfigExecutor reconfigExecutor) {
        computePathThread.regist(this);
        transClient = new TransClient();
        areaHashMap = computePathThread.areaHashMap;
        this.reconfigExecutor = reconfigExecutor;
        this.reconfigStatistic = computePathThread.reconfigStatistic;
    }

    /**
     * TODO：（此方法入参还未确定），此方法被算路线程被动调用，定时传入，刷新流量
     */
    public void flushTraffic(List<NowIntervalTraffic> nowIntervalTrafficList) {
        for(NowIntervalTraffic nowTrafficForEachArea : nowIntervalTrafficList) {
            PredictedIntervalTraffic predictedTrafficForEachArea =
                    getPredictedTraffic(nowTrafficForEachArea);
            if(isReconfigurationNeeded(predictedTrafficForEachArea)) {      //判断当前该area是否需要重构
                //TODO:执行重构
                System.out.println("^^^^ area" + nowTrafficForEachArea.areaId + " 需要重构^^^^");
                this.reconfigStatistic.reconfigTimes++;     //统计重构次数+1
                //TODO:对不同的域，执行重构的时间不同
                /**20180517测试server传过来的值是否正常加上的注释*/
                //Area currentArea = areaHashMap.get(nowTrafficForEachArea.areaId);
                //reconfigExecutor.doReconfig(currentArea, predictedTrafficForEachArea);
            }else {                                                        //若当前该area不需要重构
                System.out.println("^^^^area" + nowTrafficForEachArea.areaId +"不需要重构^^^^");
            }
        }
    }

    /**
     * 将1h的流量信息传入机器学习引擎进行判断
     * @param nowIntervalTraffic
     * @return
     */
    public PredictedIntervalTraffic getPredictedTraffic(NowIntervalTraffic nowIntervalTraffic) {
        PredictedIntervalTraffic predictedIntervalTraffic = new PredictedIntervalTraffic();
        try{
            //TODO:将数据通过thrift传给tensorflow；运算并得返回潮汐标识与预测流量
            //包括client连接server
            if(!transClient.isClientStarted()) {
                transStart(this.transClient);
            }
            //TODO:格式转换 将NowIntervalTraffic转换成client要发送的数据结构
            PredictedAreaTrafficData wrappedOutput =
                    transClient.client.getPredictedData(Tools.inputDataFormatTrans(nowIntervalTraffic));
            //TODO:格式转换 server返回的数据结构转PredictedIntervalTraffic
            predictedIntervalTraffic = Tools.outputDataFormatTrans(wrappedOutput);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return  predictedIntervalTraffic;
    }

    /**
     * 判断是否应进行重构
     * @param predictedIntervalTraffic
     * @return
     */
    public boolean isReconfigurationNeeded(PredictedIntervalTraffic predictedIntervalTraffic) {
        return predictedIntervalTraffic.migration;
    }

    /**
     * 启动thrift的客户端
     * @param transClient
     */
    public void transStart(TransClient transClient) {
        try{
            transClient.transport = transClient.createTTransport();
            transClient.openTTransport(transClient.transport);
            transClient.client = transClient.createClient(transClient.transport);

            //service calling
            if(transClient.client.equals(null)) {
                System.out.println("创建thrift客户端失败..");
                return ;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void run() {
        //1.要先拿到流量信息，而且是被动拿到的(监听机制)

        //2.包装成nowIntervalTraffic

        //3.传入tensorflow判断

    }*/
}
