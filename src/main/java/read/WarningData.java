package read;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import read.resource.Device;
import read.resource.ErrorSignalType;
import read.resource.MyTime;

public class WarningData {
    public ArrayList<WarningFormatData> data_list;

    public int list_volume;
    public WarningData(int list_volume) {
        this.list_volume = list_volume;
        data_list = new ArrayList<WarningFormatData>();
    }

    public WarningData() {
        data_list = new ArrayList<WarningFormatData>();
        this.list_volume = 0;
    }

    public boolean InsertData(WarningFormatData data) {
        data_list.add(data);
        list_volume = list_volume + 1;
        //System.out.printf("Insert %d\n", data.order);
        return true;
    }

    public void ReadExcelDataNew(String filename) {
        File file = new File(filename);

        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);

                int columns = sheet.getColumns();
                int rows = sheet.getRows();
                // sheet.getRows()返回该页的总行数
                for (int i = 1; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
                    WarningFormatData data = new WarningFormatData();

                    data.order = Integer.parseInt(sheet.getCell(12, i).getContents());
                    Device dev = new Device();
                    dev.device_type = sheet.getCell(5,i).getContents();
                    boolean judge = dev.ReadDeviceLine(sheet.getCell(4, i).getContents(  ));
                    judge = judge && dev.ReadDeviceInfo(sheet.getCell(6, i).getContents());
                    data.device_data = dev;
                    dev.CountOnlyLabel();
                    if (judge) {
                        data.device_data.CountOnlyLabel();
                        InsertData(data);
                    }
                    String impt_value = sheet.getCell(1,i).getContents();
                    int err_value = Integer.parseInt(sheet.getCell(2,i).getContents());
                    String string_type = sheet.getCell(3,i).getContents();
                    ErrorSignalType err_signal = new ErrorSignalType(string_type, err_value, impt_value);
                    data.err_signal = err_signal;

                    data.happen_time = new MyTime(sheet.getCell(8,i).getContents());
                    data.handle_time = new MyTime(sheet.getCell(9,i).getContents());
                    data.confirm_time = new MyTime(sheet.getCell(10,i).getContents());

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadExcelData(String filename) {
        File file = new File(filename);

        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);

                int columns = sheet.getColumns();
                // sheet.getRows()返回该页的总行数
                for (int i = 1; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
                    WarningFormatData data = new WarningFormatData();
                    boolean judge = false;
                    for (int j = 1; j < columns; j++) {
                        switch (j){
                            case 1:{
                                String impt_value = sheet.getCell(1,i).getContents();
                                int err_value = Integer.parseInt(sheet.getCell(2,i).getContents());
                                String string_type = sheet.getCell(3,i).getContents();
                                ErrorSignalType err_signal = new ErrorSignalType(string_type, err_value, impt_value);
                                data.err_signal = err_signal;
                                j = 3;
                                break;
                            }
                            case 4:{
                                Device device = new Device();
                                String device_name = sheet.getCell(4,i).getContents();
                                String device_type = sheet.getCell(5,i).getContents();
                                judge = device.ReadDeviceLine(device_name);
                                device.device_type = device_type;
                                judge = judge && device.ReadDeviceInfo(sheet.getCell(6,i).getContents());
                                data.device_data = device;
                                j = 6;

                                break;
                            }
                            case 7: {
                                String extra = sheet.getCell(7,i).getContents();
                                data.extra_data = extra;
                                break;
                            }

                            case 8: {
                                data.happen_time = new MyTime(sheet.getCell(8,i).getContents());
                                data.handle_time = new MyTime(sheet.getCell(9,i).getContents());
                                data.confirm_time = new MyTime(sheet.getCell(10,i).getContents());
                                j = 10;
                                break;
                            }

                            case 12: {
                                data.order = Integer.parseInt(sheet.getCell(12,i).getContents());
                                break;
                            }

                            case 14: {
                                data.SetDuration(sheet.getCell(14,i).getContents());

                                break;
                            }

                            default:
                                continue;
                        }
                    }
                    if (judge) {
                        data.device_data.CountOnlyLabel();
                        InsertData(data);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
