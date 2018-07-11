package DataProcess;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.functions.Value;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/1/10.
 */

/**
 * 本类用做跑完程序后，做整理数据的后处理入口
 */
public class PreProcess {
    public static HSSFWorkbook workbookForEdge;
    public static HSSFWorkbook workbookForArea;
    public static final int NUMBER_EVERY_COL = 15;

    public PreProcess() {
        this.workbookForEdge = new HSSFWorkbook();
        this.workbookForArea = new HSSFWorkbook();
    }

    public static void main(String[] args) throws Exception {
        PreProcess preProcess = new PreProcess();
        String[] edgeFileNameArray = {"edge12", "edge13", "edge18", "edge23", "edge24", "edge35", "edge46", "edge56",
                        "edge59", "edge67", "edge78", "edge79", "edge410", "edge514", "edge812", "edge912" ,"edge1011",
                        "edge1013", "edge1112", "edge1114", "edge1213", "edge1314"};
        //将边负载写入excel
        //TODO：2018-06-14现在边预测的数据要求改了！！暂时不要用这个方法
        for(String fileName : edgeFileNameArray) {
            preProcess.writeEdgeLoadIntoExcel(fileName, preProcess.workbookForEdge);
        }

        //将域负载写入excel
        String[] areaFileNameArray = {"area1loadCount", "area2loadCount", "area3loadCount"};
        for(String filename : areaFileNameArray) {
            preProcess.writeAreaLoadIntoExcel(filename, preProcess.workbookForArea);
        }

        //将数据写入excel文件
        String pathForEdge = "target/generated-sources/edgeload/各边流量统计_old.xls";
        String pathForArea = "target/generated-sources/各域流量统计.xls";
        OutputStream streamForEdge = new FileOutputStream(pathForEdge);
        OutputStream streamForArea = new FileOutputStream(pathForArea);
        preProcess.workbookForEdge.write(streamForEdge);
        preProcess.workbookForArea.write(streamForArea);
        preProcess.workbookForEdge.close();
        preProcess.workbookForArea.close();
    }

    //TODO：2018-06-14现在边预测的数据要求改了！！暂时不要用这个方法
    @Deprecated
    public void writeEdgeLoadIntoExcel(String filename, HSSFWorkbook workbook) throws IOException {
        HSSFSheet sheet = workbook.createSheet("边" + filename +"预处理");
        FileReader fileReader = new FileReader("target/generated-sources/edgeload/"+ filename +".txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        /**
         * 写入时间数据（excel第1行）
         */
        Row timeRow = sheet.createRow(0);
        for(int i = 0; i < 24; i++) {
            timeRow.createCell(i).setCellValue(i/24.0);
        }

        /**
         * 创建存放数据的行
         */
        Row[] rows = new Row[47];
        for(int i = 1; i <= 46; i++) {
            rows[i] = sheet.createRow(i);
        }

        List<Double> dataList = new ArrayList<Double>();
        String data = bufferedReader.readLine();
        //先把数据读入list中
        while(data != null) {
            dataList.add(Double.valueOf(data));
            data = bufferedReader.readLine();
        }
        int startIndex = 0;
        int columnIndex = 0;
        int count = 0;
        /**
         * 写入excel
         */
        while(columnIndex <= 23) {
            for(int i = 1; i <= 30; i++) {
                if(columnIndex!=23) {
                    rows[i].createCell(columnIndex).setCellValue(dataList.get(startIndex + i-1));
                }else{
                    if(i <= 15) {
                        rows[i].createCell(columnIndex).setCellValue(dataList.get(startIndex + i-1));
                    }else {
                        rows[i].createCell(columnIndex).setCellValue(dataList.get(i-16));
                    }
                }
                count++;
            }
            columnIndex++;
            startIndex += 15;
        }

        //写入预测数据（excel第33行-47行）
        fileReader = new FileReader("target/generated-sources/edgeload/" + filename + ".txt");
        bufferedReader = new BufferedReader(fileReader);
        columnIndex = 0;
        while(columnIndex <= 23) {
            for(int i = 32; i <= 46; i++) {
                rows[i].createCell((columnIndex+22) % 24).setCellValue(Double.valueOf(bufferedReader.readLine()));
            }
            columnIndex++;
        }
    }

    public void writeAreaLoadIntoExcel(String filename, HSSFWorkbook workbook) throws IOException {
        HSSFSheet sheet = workbook.createSheet("域" + filename + "预处理");
        FileReader fileReader = new FileReader("target/generated-sources/" + filename +".txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        /**
         * 写入时间数据（excel第1行）
         */
        Row timeRow = sheet.createRow(0);
        for(int i = 0; i < 24; i++) {
            timeRow.createCell(i).setCellValue(i/24.0);
        }

        /**
         * 创建存放数据的行
         */
        Row[] rows = new Row[47];
        for(int i = 1; i <= 46; i++) {
            rows[i] = sheet.createRow(i);
        }
        List<Double> dataList = new ArrayList<Double>();
        String data = bufferedReader.readLine();
        //先把数据读入list中
        while(data != null) {
            dataList.add(Double.valueOf(data));
            data = bufferedReader.readLine();
        }
        int startIndex = 0;
        int columnIndex = 0;
        int count = 0;
        /**
         * 写入excel
         */
        while(columnIndex <= 23) {
            for(int i = 1; i <= 30; i++) {
                if(columnIndex!=23) {
                    rows[i].createCell(columnIndex).setCellValue(dataList.get(startIndex + i-1));
                }else{
                    if(i <= 15) {
                        rows[i].createCell(columnIndex).setCellValue(dataList.get(startIndex + i-1));
                    }else {
                        rows[i].createCell(columnIndex).setCellValue(dataList.get(i-16));
                    }
                }

                count++;
            }
            columnIndex++;
            startIndex += 15;
        }

        //写入预测数据（excel第33行-47行）
        fileReader = new FileReader("target/generated-sources/" + filename + ".txt");
        bufferedReader = new BufferedReader(fileReader);
        columnIndex = 0;
        while(columnIndex <= 23) {
            for(int i = 32; i <= 46; i++) {
                rows[i].createCell((columnIndex+22) % 24).setCellValue(Double.valueOf(bufferedReader.readLine()));
            }
            columnIndex++;
        }
    }
}

