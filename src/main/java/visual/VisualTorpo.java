package visual;

import jxl.Sheet;
import jxl.write.Label;
import jxl.write.WritableSheet;

import java.io.OutputStream;
import java.util.HashMap;

public class VisualTorpo {
    private WritableSheet output_sheet;

    private HashMap<Integer, Integer> output_map;

    public VisualTorpo(WritableSheet sheet) {
        output_sheet = sheet;
        output_map = new HashMap<Integer, Integer>();
    }

    public void PrintTorpo(String str, int line) {
        Label label = null;
        int column;
        if (output_map.containsKey(line)) {
            column = output_map.get(line);
        } else {
            column = 0;
        }
        label = new Label(column, line, str);
        try {
            output_sheet.addCell(label);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(str);
        }
        output_map.put(line, column+1);
    }


}
