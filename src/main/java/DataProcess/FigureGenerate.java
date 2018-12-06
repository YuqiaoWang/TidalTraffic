package DataProcess;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;

import org.python.util.PythonInterpreter;

import SimulationImpl.Tools;

/**
 * 调用python的matplotlib生成流量负载图用
 */
public class FigureGenerate {
    /**
     * 执行python脚本，生成图片,同时完成数据写入excel
     */
    public static void generateFigure() throws IOException {
        String fileName = "src/main/python/pic_gen/tidal_pic_gen.py";
        // PythonInterpreter pyInterpreter = new PythonInterpreter();
        // pyInterpreter.execfile(fileName);
        // pyInterpreter.close();
        Process p = Runtime.getRuntime().exec("python " + fileName);
    }

    /**
     * 将本次仿真的参数输出成json文件
     * 
     * @throws IOException
     */
    public static void generateJson(int blockTimes, int tidalBlockTimes, double hop) throws IOException {
        String fileName = "data/simulation-config/config.json";
        File parentPath = new File(fileName).getParentFile();
        if (!parentPath.exists()) {
            parentPath.mkdirs();
        }
        // json对象构造
        JsonObject configJson = new JsonObject();
        JsonObject serviceObject = new JsonObject();
        JsonObject statisticObject = new JsonObject();
        serviceObject.addProperty("lambda", Tools.DEFAULTLAMBDA);
        serviceObject.addProperty("service_time", Tools.DEFAULTAVERAGESERVICETIME);
        serviceObject.addProperty("threshold", Tools.DEFAULTTHRESHOLD);
        statisticObject.addProperty("count_times", Tools.COUNT_TIMES);
        statisticObject.addProperty("block_times", blockTimes);
        statisticObject.addProperty("tidalBlock_times", tidalBlockTimes);
        statisticObject.addProperty("average_hop", hop);
        configJson.addProperty("data_type", "training_data");
        configJson.add("service", serviceObject);
        configJson.add("statistic", statisticObject);
        String fileContent = configJson.toString(); // json文件内容
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(fileContent);
        fileWriter.close();

    }

}