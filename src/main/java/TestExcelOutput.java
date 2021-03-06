import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;

public class TestExcelOutput {
    public static void main(String[] args) {
        File file = new File("test_excel.xls");

        double a = Math.tan(45);
        double b = Math.PI;

        try {
            file.createNewFile();
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            Label label = null;
            label = new Label(1,0,"1");
            sheet.addCell(label);
            label = new Label(2,0,"2");
            sheet.addCell(label);
            WritableSheet sheet2 = workbook.createSheet("sheet1", 1);
            label = null;
            label = new Label(1,0,"1");
            sheet2.addCell(label);
            label = new Label(2,0,"2");
            sheet2.addCell(label);

            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
