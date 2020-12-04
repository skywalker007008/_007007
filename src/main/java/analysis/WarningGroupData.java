package analysis;

import javafx.util.Pair;
import jxl.write.Label;
import jxl.write.WritableSheet;
import read.WarningFormatData;
import resource.MyTime;
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

    private static void PrintLabelSheet(WritableSheet sheet, int start_line) {
        try {
            sheet.addCell(new Label(0, start_line,"ROUTE_NAME"));
            start_line++;
            sheet.addCell(new Label(0, start_line, "PORT-ID"));
            sheet.addCell(new Label(1, start_line, "ERROR_TYPE"));
            sheet.addCell(new Label(2, start_line, "HAPPEN_TIME"));
            sheet.addCell(new Label(3, start_line, "HANDLE_TIME"));
            sheet.addCell(new Label(4, start_line, "LANE"));
            sheet.addCell(new Label(5, start_line, "PORT_LEVEL"));
            sheet.addCell(new Label(6, start_line, "FREQUENT_TIME"));
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

    public boolean ReUnitFormatDataByLevelOfRoute(int route) {
        ArrayList<WarningFormatData> tmp_list = new ArrayList<WarningFormatData>();
        ArrayList<Double> db_list = new ArrayList<Double>();
        for (WarningFormatData data: this.warn_data_list) {
            String label = data.device_data.GetLabel();
            double level_a = this.torpo_map.get(label).GetLevelOfRouteType(route, true);
            if (tmp_list.isEmpty()) {
                tmp_list.add(data);
                db_list.add(level_a);
                continue;
            }
            for (int i = 0; i < tmp_list.size(); i++) {
                double level_b = db_list.get(i);
                if (level_a <= level_b) {
                    tmp_list.add(i, data);
                    db_list.add(i, level_a);
                    break;
                } else if (i == tmp_list.size() - 1) {
                    tmp_list.add(data);
                    db_list.add(level_a);
                    break;
                }
            }
        }
        warn_data_list.clear();
        warn_data_list.addAll(tmp_list);
        return true;
    }

    public void SetSheetForTorpoVisualize(WritableSheet sheet) {
        this.sheet = sheet;
    }

    public int MakeGroup(WritableSheet sheet, int start_line) {
        PrintLabelSheet(sheet, start_line);

        this.ReUnitFormatDataByLevelOfRoute(0);
        int i = start_line + 2;
        try {
            sheet.addCell(new Label(1, start_line, this.warning_on_route));
            for (WarningFormatData data: warn_data_list
                 ) {
                String label = data.device_data.GetLabel();

                sheet.addCell(new Label(0, i, label));
                sheet.addCell(new Label(1, i, data.err_signal.string_type));
                sheet.addCell(new Label(2, i, data.happen_time.PrintOut()));
                sheet.addCell(new Label(3, i, data.handle_time.PrintOut()));
                sheet.addCell(new Label(4, i, "0"));
                sheet.addCell(new Label(5, i, String.valueOf((this.torpo_map.get(label)).GetLevelOfRouteType(0, true))));
                sheet.addCell(new Label(6, i, String.valueOf(data.combine_time)));
                //sheet.addCell(new Label(6, 0, "PORT-ID"));
                //sheet.addCell(new Label(7, 0, "PORT-ID"));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
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

    public ArrayList<Object> GetWarningLevel(String dev_id) {
        TorpoDevice tp_dev = torpo_map.get(dev_id);
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < 4; i++) {
            Object ob = tp_dev.GetLevelObjectOfRouteType(i);
            list.add(ob);
        }
        return list;
    }


    public void FlushOrderByLevelOnRoute(int direction) {
    }

    public int GetSize() {
        return this.warn_data_list.size();
    }

    public int PrintOutAlertPlace(WritableSheet sheet, int i, String handle_process) {
        if (!handle_process.equals("MUTLOS")) {
            return i;
        }
        int last_level = -1;
        String last_port_name = null;
        int this_level;
        String this_port_name;
        for (WarningFormatData data: this.warn_data_list) {
            if (data.err_signal.string_type.equals("MUT_LOS")) {
                this_port_name = data.device_data.GetLabel();
                TorpoDevice dev = this.torpo_map.get(this_port_name);
                Object o = dev.GetLevelObjectOfRouteType(0);
                if (o instanceof Integer) {
                    this_level = (Integer)o;
                } else if (o instanceof Pair){
                    Pair<Integer, Integer> pair = (Pair<Integer, Integer>) o;
                    int a = pair.getKey();
                    int b = pair.getValue();
                    this_level = a + b;
                } else {
                    return i;
                }
                if (last_level == -1 || last_port_name == null) {
                    last_level = this_level;
                    last_port_name = this_port_name;
                } else {
                    if (this_level - last_level == 1) {
                        try {
                            Label label = new Label(0, i, "Alert-location:");
                            sheet.addCell(label);
                            label = new Label(2, i, last_port_name);
                            sheet.addCell(label);
                            label = new Label(2, i+1, this_port_name);
                            sheet.addCell(label);
                            label = new Label(3, i, String.valueOf(last_level));
                            sheet.addCell(label);
                            label = new Label(3, i+1, String.valueOf(this_level));
                            sheet.addCell(label);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        last_level = this_level;
                        last_port_name = this_port_name;
                    }
                }
            }

        }
        return (i + 2);
    }
}
