package SimulationImpl;

/**
 * Created by yuqia on 2017/7/1.
 */

import TrafficDescription.*;
import TrafficDescription.AreaTraffic.NowIntervalTraffic;
import TrafficDescription.AreaTraffic.PredictedAreaTrafficData;
import TrafficDescription.EdgeTraffic.NowIntervalEdgeTraffic;
import TrafficDescription.EdgeTraffic.PredictedEdgeTraffic;
import TrafficDescription.NowAreaTrafficData;

/** no - process - branch */
public class Tools {
    /** 拓扑相关 */

    public static final double DEFAULTTHRESHOLD = 0.6; // 用于设定每个area的负载门限, 门限值候选{0.75，0.6，0.5}

    public static final int DEFAULTNUMBEROFWAVELENTHES = 60; // 用于设定每条边的波长数

    /** 业务发生相关 */
    public static final int DEFAULTSERVICENUMBER = 1000; // 用于设置生成业务个数

    public static final double DEFAULTLAMBDA = 2; // 平均业务到达率

    public static final int DEFAULTWORKINGTIME = 1600; // 生成业务时，不同时间段产生的业务源宿点概率不同，用来区分时间, 之前设定为1500

    public static final int DEFAULTAVERAGESERVICETIME = 300; // 业务平均持续时间

    public static final int DEFAULTMAXNUMBEROFWAVELENGTH = 1; // 单个业务占用最大带宽数

    public static final int TIMESCALE = 25; // 为节约运算时间，调整下方参数可

    public static final int PLAINTIME = 250; // 初始平滑增大业务达到率的时间

    /** 业务离去相关 */
    public static final int CORE_POOL_SIZE = 500; // 计时器线程池大小

    /** 流量统计相关 */
    public static final int COUNT_DELAY = 20; // 定时统计的时延

    public static final int COUNT_PERIOD = 8; // 定时统计的周期

    public static final int COUNT_TIMES = 360; // 数据统计次数

    /** 关于参数的确定 */

    // 平均业务持续时间=200不变
    // 平均业务到达率 = {2, 1.6, 1.33, 1.14, 1}
    // 业务数量=1000不变
    // 迁移起始时间 = {1200,1000,800,700,600}

    /** 重构相关参数 */

    /** thrift建立连接所需参数 */
    public static final int PORT = 9095;
    public static String IP_LOCALHOST = "localhost";
    // public static String IP_LOCALHOST = "10.108.68.219";

    /** 全局负载均衡指标 */
    public static double INIT_LOAD_BALANCE_TARGET = 1.0; // TODO:全局负载均衡指标 初值，没定好
    public static double THREASHOLD_LOAD_BALANCE_TARGET = 0.2; // TODO:全局负载均衡指标 限值

    /** thrift相关数据格式转换 */
    /** 面向域 */
    public static NowAreaTrafficData inputDataFormatTrans(NowIntervalTraffic inputData) throws Exception {
        // check格式
        int sizeOfTrafficData = inputData.getNowIntervalTraffic().size();
        // if(sizeOfTrafficData != 31) {
        // throw new Exception("发送area数据格式错误！");
        // }
        NowAreaTrafficData outputData = new NowAreaTrafficData(inputData.areaId, inputData.timeOfHour,
                inputData.nowIntervalTraffic);
        return outputData;
    }

    public static PredictedIntervalTraffic outputDataFormatTrans(PredictedAreaTrafficData inputData) {
        PredictedIntervalTraffic outputData = new PredictedIntervalTraffic(inputData.migration,
                inputData.predictedAreaTraffic);
        return outputData;
    }

    /** 面向边 */
    public static NowEdgeTrafficData inputEdgeDataFormatTrans(NowIntervalEdgeTraffic inputdata) throws Exception {
        // check格式
        // int sizeOfTrafficData = inputdata.getNowIntervalTraffic().size();
        // if(sizeOfTrafficData != 45) {
        // throw new Exception("发送link数据格式错误！");
        // }
        NowEdgeTrafficData outputData = new NowEdgeTrafficData(inputdata.nodeList, inputdata.timeOfHour,
                inputdata.nowIntervalTraffic);
        return outputData;
    }

    public static PredictedEdgeTraffic outputDataFormatTrans(PredictedEdgeTrafficData inputData) {
        PredictedEdgeTraffic outputData = new PredictedEdgeTraffic(inputData.predictedEdgeTraffic);
        return outputData;
    }

}
