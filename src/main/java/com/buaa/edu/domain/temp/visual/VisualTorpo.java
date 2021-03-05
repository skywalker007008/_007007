package com.buaa.edu.domain.temp.visual;

import com.buaa.edu.domain.temp.torpo.*;
import jxl.write.Label;
import jxl.write.WritableSheet;

import java.util.HashMap;
import java.util.HashSet;

public class VisualTorpo {
    private WritableSheet output_sheet;

    private HashSet<TorpoDevice> tp_set;

    private HashMap<Integer, Integer> output_map;

    public VisualTorpo(WritableSheet sheet) {
        output_sheet = sheet;
        output_map = new HashMap<Integer, Integer>();
        tp_set = new HashSet<TorpoDevice>();
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

    public void AddDevInfo(TorpoDevice dev) {
        tp_set.add(dev);
    }

    public void PrintGlobalTorpo() {
        System.out.println("Torpo should be displayed here, but not finished.");
        Label label;
        try {
            label = new Label(0, 0, "Port-Id");
            output_sheet.addCell(label);
            label = new Label(1, 0, "Level of Positive-ODD");
            output_sheet.addCell(label);
            label = new Label(2, 0, "Level of Positive-EVEN");
            output_sheet.addCell(label);
            label = new Label(3, 0, "Level of Negative-ODD");
            output_sheet.addCell(label);
            label = new Label(4, 0, "Level of Negative-EVEN");
            output_sheet.addCell(label);
            int i = 1;
            for (TorpoDevice dev:
                 tp_set) {
                double level;
                label = new Label(0, i, dev.GetLabel());
                output_sheet.addCell(label);
                level = dev.GetTorpoLevelOfRoute(TorpoRoute.POSITIVE_ODD);
                label = new Label(1, i, String.valueOf(level));
                output_sheet.addCell(label);
                level = dev.GetTorpoLevelOfRoute(TorpoRoute.POSITIVE_EVEN);
                label = new Label(2, i, String.valueOf(level));
                output_sheet.addCell(label);
                level = dev.GetTorpoLevelOfRoute(TorpoRoute.NEGATIVE_ODD);
                label = new Label(3, i, String.valueOf(level));
                output_sheet.addCell(label);
                level = dev.GetTorpoLevelOfRoute(TorpoRoute.NEGATIVE_EVEN);
                label = new Label(4, i, String.valueOf(level));
                output_sheet.addCell(label);
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
