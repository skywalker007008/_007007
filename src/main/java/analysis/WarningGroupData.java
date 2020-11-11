package analysis;

import javafx.util.Pair;
import jxl.write.Label;
import jxl.write.WritableSheet;
import read.WarningFormatData;
import read.resource.MyTime;
import torpo.TorpoDevice;
import visual.VisualTorpo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WarningGroupData {

    // Constant
    public static final int TYPE_FREQUENT_WARNING = 0;
    public static final int TYPE_RELATED_WARNING = 2;

    // Type of this Warning group

    private int warning_type;

    // OMS route
    private String warning_on_route;

    // For Frequent Warnings
    // The native warning data
    private WarningFormatData base_warning_data;

    // For Group Warnings
    // Set for related Devices
    private HashMap<String, TorpoDevice> cache_device_map;

    private ArrayList<WarningFormatData> warn_data_list;

    private HashMap<String, TorpoDevice> torpo_map;

    private HashMap<String, WarningFormatData> warn_data_map;

    private MyTime start_time;

    private int min_level;

    private String id_label;

    private HashMap<String, TorpoDevice> start_devs;

    /*
     * Visualization Related
     */

    private HashMap<TorpoDevice, Integer> painting_map; // For painted
    private WritableSheet sheet;
    private VisualTorpo visual_torpo;

    private static void PrintLabelSheet(WritableSheet sheet) {
        try {
            sheet.addCell(new Label(0,0,"ROUTE_NAME"));
            sheet.addCell(new Label(0, 1, "PORT-ID"));
            sheet.addCell(new Label(1, 1, "ERROR_TYPE"));
            sheet.addCell(new Label(2, 1, "HAPPEN_TIME"));
            sheet.addCell(new Label(3, 1, "HANDLE_TIME"));
            sheet.addCell(new Label(4, 1, "LANE"));
            sheet.addCell(new Label(5, 1, "PORT_LEVEL"));
            sheet.addCell(new Label(6, 1, "FREQUENT_TIME"));
            //sheet.addCell(new Label(6, 0, "PORT-ID"));
            //sheet.addCell(new Label(7, 0, "PORT-ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String CountIdLabel() {
        String str = new String();
        for (WarningFormatData data:
             this.warn_data_list) {
            str = str + String.valueOf(data.order) + "-";
        }
        return str;
    }

    public ArrayList<WarningFormatData> GetFormatDataList() {
        return this.warn_data_list;
    }

    public WarningGroupData() {

    }

    public WarningGroupData(String line_name, ArrayList<WarningFormatData> warn_list, HashMap<String, TorpoDevice> tp_list) {
        warn_data_list = new ArrayList<WarningFormatData>(warn_list);
        torpo_map = new HashMap<String, TorpoDevice>(tp_list);

        this.warning_type = TYPE_RELATED_WARNING;
        this.warning_on_route = line_name;

    }

    public WarningGroupData(WarningFormatData base_data) {

        this.warning_type = WarningGroupData.TYPE_FREQUENT_WARNING;
        this.base_warning_data = base_data;
    }

    public void SetSheetForTorpoVisualize(WritableSheet sheet) {
        this.sheet = sheet;
    }

    public void MakeGroup(WritableSheet sheet) {

    }

    /*
        Make Group Node in order
     */
    public Pair<ArrayList<WarningFormatData>, HashMap<String, TorpoDevice>> MakeGroup() {
        return null;
    }


    /*
     * PrintOut related to frequent warnings
     */
    public void PrintOutFrequentMsgCsv(BufferedWriter stream) {
        String out_txt = base_warning_data.device_data.GetLabel() + "," +
                base_warning_data.err_signal.string_type + "," +
                base_warning_data.happen_time.PrintOut() + "," +
                base_warning_data.handle_time.PrintOut() + "," +
                String.valueOf(base_warning_data.combine_time);

        try {
            if (warning_type == WarningGroupData.TYPE_FREQUENT_WARNING) {
                stream.write(out_txt);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



}
