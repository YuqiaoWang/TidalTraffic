package Service;

import Topology.Area;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.TimerTask;

/**
 * Created by yuqia_000 on 2017/12/6.
 */
public class LoadCountTask extends TimerTask{

    FileWriter area1LoadCountFileWriter;
    FileWriter area2LoadCountFileWriter;
    FileWriter area3LoadCountFileWriter;
    /*
    FileOutputStream area1LoadCountStream;
    FileOutputStream area2LoadCountStream;
    FileOutputStream area3LoadCountStream;
    */
    Area area1;
    Area area2;
    Area area3;

    public LoadCountTask(){

    }

    public LoadCountTask(Area area1, Area area2, Area area3) throws Exception{

        area1LoadCountFileWriter = new FileWriter("target/generated-sources/area1loadCount.txt", true);
        area2LoadCountFileWriter = new FileWriter("target/generated-sources/area2loadCount.txt", true);
        area3LoadCountFileWriter = new FileWriter("target/generated-sources/area3loadCount.txt", true);
        /*
        area1LoadCountStream = new FileOutputStream("target/generated-sources/area1loadCount.txt", true);
        area2LoadCountStream = new FileOutputStream("target/generated-sources/area2loadCount.txt", true);
        area3LoadCountStream = new FileOutputStream("target/generated-sources/area1loadCount.txt", true);
        */
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
    }

    public void run() {
        try{
            /*
            FileChannel area1Channel = area1LoadCountStream.getChannel();
            FileChannel area2Channel = area2LoadCountStream.getChannel();
            FileChannel area3Channel = area3LoadCountStream.getChannel();
            FileLock area1Lock = area1Channel.tryLock();
            */
            //FileLock area2Lock = area2Channel.tryLock();
            //FileLock area3Lock = area3Channel.tryLock();

            //String s1 = Double.toString(area1.load);
            //s1 = s1 + "\n";
            //String s2 = Double.toString(area2.load);
            //s2 = s2 + "\n";
            //String s3 = Double.toString(area3.load);
            //s3 = s3 + "\n";


            //area1LoadCountStream.write(s1.getBytes());
            //area2LoadCountStream.write(s2.getBytes());
            //area3LoadCountStream.write(s3.getBytes());

            //Thread.sleep(200);

            //area1Lock.release();
            //area2Lock.release();
            //area3Lock.release();
            area1LoadCountFileWriter.write(area1.load + "\n");
            area1LoadCountFileWriter.flush();
            area2LoadCountFileWriter.write(area2.load + "\n");
            area2LoadCountFileWriter.flush();
            area3LoadCountFileWriter.write(area3.load + "\n");
            area3LoadCountFileWriter.flush();



        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
