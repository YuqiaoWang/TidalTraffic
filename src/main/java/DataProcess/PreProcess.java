package DataProcess;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.functions.Value;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuqia_000 on 2018/1/10.
 */
public class PreProcess {
    public static HSSFWorkbook workbook;
    public static final int NUMBER_EVERY_COL = 15;

    public static void main(String[] args) throws Exception {
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("边数据预处理");

        FileReader fileReader = new FileReader("target/generated-sources/edgeload/edge35.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        /**
         * 写入时间数据（excel第1行）
         */
        Row timeRow = sheet.createRow(0);
        for(int i = 0; i < 24; i++) {
            timeRow.createCell(i).setCellValue(i/24.0);
        }


        Row[] rows = new Row[47];
        for(int i = 1; i <= 46; i++) {
            rows[i] = sheet.createRow(i);
        }
        /*
        for (Row row: rows) {
            int cellIndex = 0;
            while(cellIndex <= 23) {
                row.createCell(cellIndex).setCellValue();
                cellIndex++;
            }

        }*/
        List<Double> dataList = new ArrayList<Double>();
        String data = bufferedReader.readLine();
        while(data != null) {
            dataList.add(Double.valueOf(data));
            data = bufferedReader.readLine();
        }
        System.out.println(dataList.size());
        int startIndex = 0;
        int columnIndex = 0;
        int count = 0;
        try{
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
        }catch (Exception e) {
            System.out.println(count+"已读");
        }







        /**
         * 写入预测数据（excel第33行-47行）
         */
        fileReader = new FileReader("target/generated-sources/edgeload/edge35.txt");
        bufferedReader = new BufferedReader(fileReader);
        columnIndex = 0;
        while(columnIndex <= 23) {
            for(int i = 32; i <= 46; i++) {
                rows[i].createCell((columnIndex+22) % 24).setCellValue(Double.valueOf(bufferedReader.readLine()));
            }
            columnIndex++;
        }

        /**
         * 将数据写入excel文件
         */
        String path = "target/generated-sources/edgeload/edge35load.xls";
        OutputStream stream = new FileOutputStream(path);
        workbook.write(stream);
        workbook.close();




        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            double value = Double.valueOf(line);

        }
    }


}


/*
finally {
            try{
                FileReader fileReader = new FileReader("target/generated-sources/area1loadCount.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                HSSFWorkbook workbook = new HSSFWorkbook();
                workbook.createSheet();
                int count = 0;
                String line;
                //
                while((line = bufferedReader.readLine()) != null) {
                    int chamberValue = Integer.valueOf(line);
                    int remainder = count % 31;

                    if()
                }


            }catch (Exception fe) {
                fe.printStackTrace();
            }

        }
 */
