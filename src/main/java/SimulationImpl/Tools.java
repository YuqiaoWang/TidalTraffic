package SimulationImpl;

/**
 * Created by yuqia on 2017/7/1.
 */

import TrafficDescription.NowIntervalTraffic;
import TrafficDescription.NowIntervalTrafficData;
import TrafficDescription.PredictedIntervalTraffic;
import TrafficDescription.PredictedIntervalTrafficData;

/**no - process - branch*/
public class Tools {
    /**拓扑相关*/
    //用于设定每个area的负载门限
    public static double DEFAULTTHRESHOLD = 0.75;
    //用于设定每条边的波长数
    public static int DEFAULTNUMBEROFWAVELENTHES = 60;

    /**业务发生相关*/
    //用于设置生成业务个数
    public static int DEFAULTSERVICENUMBER = 1000;
    //平均业务到达率
    public static double DEFAULTLAMBDA = 2;
    //生成业务时，不同时间段产生的业务源宿点概率不同，用来区分时间
    public static int DEFAULTWORKINGTIME = 1500;
    //业务平均持续时间
    public static int DEFAULTAVERAGESERVICETIME = 200;
    //单个业务占用最大带宽数
    public static int DEFAULTMAXNUMBEROFWAVELENGTH = 4;
    //为节约运算时间，调整下方参数可
    public static int TIMESCALE = 25;
    //初始平滑增大业务达到率的时间
    public static int PLAINTIME = 200;

    /**关于参数的确定*/

    // 平均业务持续时间=200不变
    // 平均业务到达率 = {2, 1.6, 1.33, 1.14, 1}
    // 业务数量=1000不变
    // 迁移起始时间 = {1200,1000,800,700,600}

    /**重构相关参数*/

    /**thrift建立连接所需参数*/
    public static int PORT = 9095;
    //public static String IP_LOCALHOST = "localhost";
    public static String IP_LOCALHOST = "10.108.68.219";

    /**thrift相关数据格式转换*/
    public static NowIntervalTrafficData inputDataFormatTrans(NowIntervalTraffic inputData) {
        NowIntervalTrafficData outputData =
                new NowIntervalTrafficData(inputData.areaId, inputData.timeOfHour, inputData.nowIntervalTraffic);
        return  outputData;
    }

    public static PredictedIntervalTraffic outputDataFormatTrans(PredictedIntervalTrafficData inputData) {
        PredictedIntervalTraffic outputData =
                new PredictedIntervalTraffic(inputData.migration, inputData.predictedIntervalTraffic);
        return outputData;
    }
}
