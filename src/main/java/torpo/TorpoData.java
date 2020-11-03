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
            WritableWorkbook wb_out = Workbook.createWorkbook(out_file);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                TorpoRoute route = new TorpoRoute();
                route.ReadTorpoRoute(sheet);

                route_map.put(sheet.getName(), route);

                WritableSheet sheet_out = wb_out.createSheet(sheet.getName(), 0);
                route.PrintOutRouteMsg(sheet_out);

            }
            if (sheet_size > 0) {
                wb_out.write();

            }
            wb_out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
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

    public Pair<String, Integer> FindDevRouteAndLevel(String label) {
        for (String key: this.route_map.keySet()
             ) {
           TorpoRoute torpo_route = route_map.get(key);
           int level = torpo_route.GetDevLevel(label);
           if (level >= 0) {
               Pair<String, Integer> pair = new Pair<String, Integer>(key, level);
               return pair;
           }
        }
        Pair<String, Integer> pair = new Pair<String, Integer>(null, -1);
        return pair;
    }
}
