package SimulationImpl;

/**
 * Created by yuqia_000 on 2017/7/7.
 */
public class Tools {
    //用于设置生成业务个数
    public static int DEFAULTSERVICENUMBER = 200;

    //用于设定每个area的门限
    public static double DEFAULTTHRESHOLD = 0.7;

    //用于设定每条边的波长数
    public static int DEFAULTNUMBEROFWAVELENTHES = 40;

    //生成业务时，不同时间段产生的业务源宿点概率不同，用来区分时间
    public static int DEFAULTWORKINGTIME = 15;

    //业务最大持续时间
    public static int DEFAULTMAXSERVICETIME = 10;
}
