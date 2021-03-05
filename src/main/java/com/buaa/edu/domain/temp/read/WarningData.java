package com.buaa.edu.domain.temp.read;

import com.buaa.edu.domain.temp.resource.*;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.util.ArrayList;


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

    public void ReadExcelDataNew(File target_file, boolean is_new, boolean is_new2) {
        File file = target_file;

        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            if (!is_new) {
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
                        dev.device_type = sheet.getCell(5, i).getContents();
                        boolean judge = dev.ReadDeviceLine(sheet.getCell(4, i).getContents());
                        judge = judge && dev.ReadDeviceInfo(sheet.getCell(6, i).getContents());
                        data.device_data = dev;
                        dev.CountOnlyLabel();
                        if (judge) {
                            data.device_data.CountOnlyLabel();
                            InsertData(data);
                        }
                        String impt_value = sheet.getCell(1, i).getContents();
                        int err_value = Integer.parseInt(sheet.getCell(2, i).getContents());
                        String string_type = sheet.getCell(3, i).getContents();
                        ErrorSignalType err_signal = new ErrorSignalType(string_type, err_value, impt_value);
                        data.err_signal = err_signal;

                        data.happen_time = new MyTime(sheet.getCell(8, i).getContents());
                        data.handle_time = new MyTime(sheet.getCell(9, i).getContents());
                        data.confirm_time = new MyTime(sheet.getCell(10, i).getContents());

                    }
                }
            } else {
                for (int index = 0; index < sheet_size; index++) {
                    // 每个页签创建一个Sheet对象
                    Sheet sheet = wb.getSheet(index);

                    int columns = sheet.getColumns();
                    int rows = sheet.getRows();
                    // sheet.getRows()返回该页的总行数
                    for (int i = 1; i < sheet.getRows(); i++) {
                        // sheet.getColumns()返回该页的总列数
                        WarningFormatData data = new WarningFormatData();

                        data.order = Integer.parseInt(sheet.getCell(24, i).getContents());
                        Device dev = new Device();
                        dev.device_type = sheet.getCell(0, i).getContents();
                        boolean judge = dev.ReadDeviceLine(sheet.getCell(2, i).getContents());
                        judge = judge && dev.ReadDeviceInfo(sheet.getCell(13, i).getContents());
                        data.device_data = dev;
                        dev.CountOnlyLabel();
                        if (judge) {
                            data.device_data.CountOnlyLabel();
                            InsertData(data);
                        } else {
                            continue;
                        }
                        String impt_value = sheet.getCell(6, i).getContents();
                        int err_value = Integer.parseInt(sheet.getCell(18, i).getContents());
                        String string_type = sheet.getCell(4, i).getContents();
                        ErrorSignalType err_signal = new ErrorSignalType(string_type, err_value, impt_value);
                        data.err_signal = err_signal;

                        data.happen_time = new MyTime(sheet.getCell(8, i).getContents(), is_new2);
                        data.handle_time = new MyTime(sheet.getCell(10, i).getContents(), is_new2);
                        data.confirm_time = new MyTime(sheet.getCell(9, i).getContents(), is_new2);

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
