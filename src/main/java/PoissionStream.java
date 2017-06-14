/**
 * Created by yuqia on 2017/6/14.
 */
public class PoissionStream {
    public double lambda ;


    public static void main(String[] args) {
        GenerationThread serviceGeneratingThread = new GenerationThread();
    }
    public void getService() {

    }

    public void generateService() {

    }


}

class GenerationThread extends Thread {
    public double lambda;

    @Override
    public void run() {
        //写个方法，产生满足泊松分布的随机数
    }
}
