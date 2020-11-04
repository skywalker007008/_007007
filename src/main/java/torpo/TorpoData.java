package torpo;

import javafx.util.Pair;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class TorpoData {
    public HashMap<String, TorpoRoute> route_map;

    public TorpoData() {
        route_map = new HashMap<String, TorpoRoute>();
    }

    public void ReadTorpoInfo(String filename) {
        File in_file = new File(filename);
        //File out_file = new File("Torpo_route.xls");
        File out_file = new File("Torpo_ROUTE_NEW.xls");
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(in_file.getAbsolutePath());
            out_file.createNewFile();
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);

            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            /*
            for (int index = 0; index < sheet_size - 1; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                TorpoRoute route = new TorpoRoute();
                route.ReadTorpoRoute(sheet);

                route_map.put(sheet.getName(), route);

                WritableSheet sheet_out = wb_out.createSheet(sheet.getName(), 0);
                route.PrintOutRouteMsg(sheet_out);

            }
            */
            for (int i = 0; i < sheet_size - 1; i++) {
                Sheet sheet = wb.getSheet(i);
                String[] route_name = TorpoRoute.GetRouteNameBySheet(sheet);
                TorpoRoute route;
                if (route_map.containsKey(route_name[0])) {
                    route = route_map.get(route_name[0]);
                    route.ReadTorpoRoute(sheet, true);
                } else if (route_map.containsKey(route_name[1])) {
                    route = route_map.get(route_name[1]);
                    route.ReadTorpoRoute(sheet, false);
                } else {
                    route = new TorpoRoute();
                    route.ReadTorpoRoute(sheet, true);
                    route_map.put(route_name[0], route);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Output

        try {
            WritableWorkbook wb_out = Workbook.createWorkbook(out_file);
            for (String str:
                 route_map.keySet()) {
                TorpoRoute route = route_map.get(str);
                WritableSheet sheet = wb_out.createSheet(route.GetRouteLabel(), 0);
                route.PrintOutRouteMsg(sheet);
            }
            wb_out.write();
            wb_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public TorpoRoute GetRouteByName(String name) {
        if (this.route_map.containsKey(name)) {
            return route_map.get(name);
        } else {
            return null;
        }
    }
}
