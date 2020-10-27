package analysis;

import javafx.util.Pair;
import jxl.write.WritableSheet;
import read.WarningFormatData;
import read.resource.Device;
import read.resource.MyTime;
import torpo.TorpoDevice;
import visual.VisualTorpo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

    private HashMap<String, TorpoDevice> start_devs;

    /*
     * Visualization Related
     */

    private HashMap<TorpoDevice, Integer> painting_map; // For painted
    private WritableSheet sheet;
    private VisualTorpo visual_torpo;


    public WarningGroupData() {

    }

    public WarningGroupData(String line_name, ArrayList<WarningFormatData> warn_list, HashMap<String, TorpoDevice> tp_list) {
        warn_data_list = new ArrayList<WarningFormatData>(warn_list);
        torpo_map = new HashMap<String, TorpoDevice>(tp_list);

        this.warning_type = TYPE_RELATED_WARNING;
        this.warning_on_route = line_name;

    }

    public WarningGroupData(HashMap<String, Device> exist_map) {
        //cache_device_map = new HashMap<String, Device>();
        //cache_device_map.putAll(exist_map);
        this.warning_type = WarningGroupData.TYPE_RELATED_WARNING;
    }

    public WarningGroupData(WarningFormatData base_data) {

        this.warning_type = WarningGroupData.TYPE_FREQUENT_WARNING;
        this.base_warning_data = base_data;
    }

    public void SetWarningRoute(String str) {
        this.warning_on_route = str;
    }

    public String GetWarningRoute() {
        return this.warning_on_route;
    }

    public void SetSheetForTorpoVisualize(WritableSheet sheet) {
        this.sheet = sheet;
    }

    /*
        Make Group Node in order
     */
    public Pair<ArrayList<WarningFormatData>, HashMap<String, TorpoDevice>> MakeGroup() {
        warn_data_map = new HashMap<String, WarningFormatData>();
        cache_device_map = new HashMap<String, TorpoDevice>();
        start_devs = new HashMap<String, TorpoDevice>();

        this.min_level = -1;

        /*
         * Step 1: According to the direction, build its relationship
         * Because all the warnings are based on time order
         * So we can just go as its original order
         */

        for (WarningFormatData warn_data:
             this.warn_data_list) {
            String str = warn_data.device_data.GetLabel();
            // Add to map
            warn_data_map.put(str, warn_data);
            if (!torpo_map.containsKey(str)) {
                // Something error
            }
            TorpoDevice tp_dev = torpo_map.get(str);
            cache_device_map.put(str, torpo_map.get(str));
            int level = tp_dev.getDev_level();

            // IF more below, replace the map\
            if (this.min_level != -1) {
                if (level < this.min_level) {
                    this.min_level = level;
                    start_devs.clear();
                    start_devs.put(str, tp_dev);
                } else if (level == this.min_level) {
                    start_devs.put(str, tp_dev);
                } else {
                    // No other operation
                }
            } else {
                this.min_level = level;
                // start_devs.clear();
                start_devs.put(str, tp_dev);
            }

        }

        /*
         * Step 2: Build route downside
         * Just use for Print
         */

        this.painting_map = new HashMap<TorpoDevice, Integer>();
        this.BuildRelatedWarningsTorpo(false);

        /*
         * Step 3: Release out unlinked devs
         */

        ArrayList<WarningFormatData> unlink_warn_list = new ArrayList<WarningFormatData>();
        HashMap<String, TorpoDevice> unlink_torpo_dev = new HashMap<String, TorpoDevice>();
        for (WarningFormatData warn_data: warn_data_list
             ) {
            TorpoDevice tmp_dev = torpo_map.get(warn_data.device_data.GetLabel());
            if (!painting_map.containsKey(tmp_dev)) {
                unlink_warn_list.add(warn_data);
                unlink_torpo_dev.put(tmp_dev.GetLabel(), tmp_dev);
            }
        }

        return new Pair<ArrayList<WarningFormatData>, HashMap<String, TorpoDevice>>(unlink_warn_list, unlink_torpo_dev);

        //return null;
    }

    public void BuildRelatedWarningsTorpo(boolean is_output) {
        this.painting_map = new HashMap<TorpoDevice, Integer>();
        for (String str:
             this.start_devs.keySet()) {

            TorpoDevice dev = start_devs.get(str);
            this.PrintRelatedNodeWarnings(dev, is_output);
        }
    }

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

    public void PrintRelatedNodeWarnings(TorpoDevice dev, boolean is_output) {
        // IF PAINTED, release out
        if (painting_map.containsKey(dev)) {
            return;
        }
        painting_map.put(dev, 0);
        // Output its original data;
        WarningFormatData data = this.warn_data_map.get(dev.GetLabel());
        if (is_output) {


            if (visual_torpo == null) {
                visual_torpo = new VisualTorpo(sheet);
            }
            visual_torpo.PrintTorpo(dev.GetLabel() + " @@ " + data.err_signal.string_type, dev.getDev_level());
        }

        // Find its route till the end

        if ((dev.getDev_type() == TorpoDevice.TYPE_L40_1) ||
                (dev.getDev_type() == TorpoDevice.TYPE_D40_1)) {
            for (String str :
                 dev.GetBelowDevs().keySet()) {
                if (this.cache_device_map.containsKey(str)) {
                    TorpoDevice device = cache_device_map.get(str);
                    this.PrintRelatedNodeWarnings(device, is_output);
                }
            }
        } else {
            // Get to the end
            if (dev.getBelow_dev() == null) {
                return;
            }
            String str = dev.getBelow_dev().GetLabel();
            if (this.cache_device_map.containsKey(str)) {
                TorpoDevice device = cache_device_map.get(str);
                this.PrintRelatedNodeWarnings(device, is_output);
            }
        }

    }



}
