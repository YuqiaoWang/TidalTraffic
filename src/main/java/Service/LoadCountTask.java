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
        FileWriter fw1 = new FileWriter("target/generated-sources/area1loadCount.txt");
        fw1.write("");
        fw1.close();
        FileWriter fw2 = new FileWriter("target/generated-sources/area2loadCount.txt");
        fw2.write("");
        fw2.close();
        FileWriter fw3 = new FileWriter("target/generated-sources/area3loadCount.txt");
        fw3.write("");
        fw3.close();

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
            area1.flushLoad();
            area2.flushLoad();
            area3.flushLoad();
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
