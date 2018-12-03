package DataProcess;

import java.io.IOException;

import org.python.util.PythonInterpreter;

/**
 * 调用python的matplotlib生成流量负载图用
 */
public class FigureGenerate {
    /**
     * 执行python脚本，生成图片
     */
    public static void generateFigure() throws IOException {
        String fileName = "src/main/python/pic_gen/tidal_pic_gen.py";
        // PythonInterpreter pyInterpreter = new PythonInterpreter();
        // pyInterpreter.execfile(fileName);
        // pyInterpreter.close();
        Process p = Runtime.getRuntime().exec("python " + fileName);
    }

}