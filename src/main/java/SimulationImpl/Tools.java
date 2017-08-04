package SimulationImpl;

/**
 * Created by yuqia_000 on 2017/7/7.
 */
/** Tidal - dev - branch*/
public class Tools {
    /**拓扑相关*/
    //用于设定每个area的负载门限
    public static double DEFAULTTHRESHOLD = 0.6;
    //用于设定每条边的波长数
    public static int DEFAULTNUMBEROFWAVELENTHES = 60;

    /**业务发生相关*/
    //用于设置生成业务个数
    public static int DEFAULTSERVICENUMBER = 1200;
    //平均业务到达率
    public static double DEFAULTLAMBDA = 1.538;
    //生成业务时，不同时间段产生的业务源宿点概率不同，用来区分时间
    public static int DEFAULTWORKINGTIME = 1080;
    //业务平均持续时间
    public static int DEFAULTAVERAGESERVICETIME = 200;
    //单个业务占用最大带宽数
    public static int DEFAULTMAXNUMBEROFWAVELENGTH = 4;
    //为节约运算时间，调整下方参数可
    public static int TIMESCALE = 20;
    //预知迁移过程会造成负载变化百分比
    public static double LOADCHANGEPERCENT = 0.4;

    /**关于参数的确定*/
    // 平均业务持续时间=200不变
    // 平均业务到达率 = {2, 1.6, 1.33, 1.14, 1} {,1.538,1.66,1.81,2}
    // 业务数量=1000不变
    // 迁移起始时间 = {1200,1000,800,700,600} {940, ,1120,1200}
    // 单个业务所占波长数=4(测试改为2)
}
