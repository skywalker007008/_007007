import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;

public class TestPortRoute {
    public static void main(String[] args) {
        File file = new File("Torpo_TYPE_NEW_LINUX-03.xls");
        File write_file = new File("PortSet.xls");
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            WritableWorkbook workbook1 = Workbook.createWorkbook(write_file);
            WritableSheet writableSheet = workbook1.createSheet("set", 0);
//            Sheet sheet = workbook.getSheet(0);
//            if (sheet == null) {
//                throw new NullPointerException();
//            }
            Label label;
            int row = 0;

            Sheet[] sheets = workbook.getSheets();

            for (Sheet sheet: sheets) {

                String name = sheet.getName();

                int length = sheet.getRows();
                for (int i = 1; i < length; i++) {
                    label = new Label(0, row, sheet.getCell(0, i).getContents());
                    writableSheet.addCell(label);
                    label = new Label(1, row, name);
                    writableSheet.addCell(label);
                    row++;
                }
            }

            workbook.close();
            workbook1.write();
            workbook1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
