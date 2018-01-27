package DataProcess;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedReader;
import java.io.FileReader;

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

        for(int i = 0; i < 15; i++) {
            Row row = sheet.createRow(i);

        }



        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            double value = Double.valueOf(line);

        }
    }


}
