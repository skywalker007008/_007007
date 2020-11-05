package torpo;
import jxl.Sheet;
import jxl.write.WritableSheet;
import visual.VisualTorpo;

import java.util.HashMap;

// One Route of the Torpo
public class TorpoRoute {
    // Directions of Torpo
    public static final int POSITIVE_ODD = 0;
    public static final int POSITIVE_EVEN = 2;
    public static final int NEGATIVE_ODD = 1;
    public static final int NEGATIVE_EVEN = 3;

    // Which level has been logged in
    private int stat;
    // Set of the device
    private HashMap<String, TorpoDevice> device_map;
    // Levels of the map
    private HashMap<Integer, Integer> level_map;
    // Name of route
    private String route_name;
    // End of Route
    private HashMap<Integer, HashMap<String, TorpoDevice>> entry_device_map;
    private HashMap<Integer, HashMap<String, TorpoDevice>> exit_device_map;
    // Unused(cached map)
    private HashMap<String, TorpoDevice> cache_device_map;
    // Used for Visualize
    private VisualTorpo visual;
    private HashMap<Integer, HashMap<String, TorpoDevice>> painting_map;

    public TorpoRoute() {
        device_map = new HashMap<String, TorpoDevice>();
        level_map = new HashMap<Integer, Integer>();
        this.stat = 0;
        entry_device_map = new HashMap<Integer, HashMap<String, TorpoDevice>>();
        exit_device_map = new HashMap<Integer, HashMap<String, TorpoDevice>>();
        cache_device_map = new HashMap<String, TorpoDevice>();
        painting_map = new HashMap<Integer, HashMap<String, TorpoDevice>>();
    }

    public static final String[] GetRouteNameBySheet(Sheet sheet) {
        String[] str = sheet.getName().split("-");
        String[] temp_str = str[2].split("H");
        String start_str = temp_str[1];
        temp_str = str[4].split("H");
        String end_str = temp_str[1];
        str = new String[2];
        str[0] = start_str + "-" + end_str;
        str[1] = end_str + "-" + start_str;
        return str;
    }

    public static final boolean IsDirectionExisted(int stat, int direction) {
        return (((stat >> direction) & 1) == 1);
    }

    public String GetRouteLabel() {
        return this.route_name;
    }

    public boolean ReadTorpoRoute(Sheet sheet, boolean direction) {
        if (stat == 0) {
            String[] name_str = TorpoRoute.GetRouteNameBySheet(sheet);

            this.route_name = name_str[0];
        }
        int rows = sheet.getRows();

        // Judge the direction
        int route_type;
        if (!direction) {
            if (!TorpoRoute.IsDirectionExisted(this.stat, TorpoRoute.NEGATIVE_ODD)) {
                route_type = TorpoRoute.NEGATIVE_ODD;
            } else if (!TorpoRoute.IsDirectionExisted(this.stat, TorpoRoute.NEGATIVE_EVEN)) {
                route_type = TorpoRoute.NEGATIVE_EVEN;
            } else {
                // Error Here
                System.out.println(route_name + "ReInsert Route Type: NEGATIVE");
                return false;
            }
        } else {
            if (!TorpoRoute.IsDirectionExisted(this.stat, TorpoRoute.POSITIVE_ODD)) {
                route_type = TorpoRoute.POSITIVE_ODD;
            } else if (!TorpoRoute.IsDirectionExisted(this.stat, TorpoRoute.POSITIVE_EVEN)) {
                route_type = TorpoRoute.POSITIVE_EVEN;
            } else {
                // Error Here
                System.out.println(route_name + "ReInsert Route Type: POSITIVE");
                return false;
            }
        }

        // Init temp_hashmap

        HashMap<String, TorpoDevice> temp_entry_device_map = new HashMap<String, TorpoDevice>();

        for (int i = 0; i < rows; i++) {
            // Only used for query
            // in_device
            String in_device_line_info = sheet.getCell(0, i).getContents();
            String in_device_info = sheet.getCell(1, i).getContents();
            String in_label = TorpoDevice.GetDeviceLabel(in_device_line_info, in_device_info);

            // out device
            String out_device_line_info = sheet.getCell(2, i).getContents();
            String out_device_info = sheet.getCell(3, i).getContents();
            String out_label = TorpoDevice.GetDeviceLabel(out_device_line_info, out_device_info);

            // Find the in label whether in the map
            TorpoDevice in_dev;
            if (!cache_device_map.containsKey(in_label)) {
                // Not Exists, first entry or something wrong
                if (device_map.containsKey(in_label)) {
                    in_dev = device_map.get(in_label);
                    in_dev.SetLevelOfRouteType(0, route_type);
                } else {

                    in_dev = new TorpoDevice();
                    int dev_type = TorpoDevice.JudgeDeviceTypeByLabel(in_label);
                    in_dev.setDev_type(dev_type);
                    in_dev.SetLevelOfRouteType(0, route_type);
                    in_dev.ReadDeviceLine(in_device_line_info);
                    in_dev.ReadDeviceInfo(in_device_info);
                    in_dev.CountOnlyLabel();
                    device_map.put(in_label, in_dev);
                }
                // Add to Entry and cache
                cache_device_map.put(in_label, in_dev);
                temp_entry_device_map.put(in_label, in_dev);

            } else {
                // Find it, get it out
                in_dev = cache_device_map.get(in_label);
            }

            // Error: in_dev = null
            if (in_dev == null) {
                // Exception: Never find entry
                continue;
            }
            // Find the out_dev whether in the map
            TorpoDevice out_dev;
            if (cache_device_map.containsKey(out_label)) {
                // Exists, Get it out
                out_dev = cache_device_map.get(out_label);
            } else {
                if (device_map.containsKey(out_label)) {
                    out_dev = device_map.get(out_label);
                } else {
                    // No exists, create new one.
                    out_dev = new TorpoDevice();
                    out_dev.ReadDeviceLine(out_device_line_info);
                    out_dev.ReadDeviceInfo(out_device_info);
                    out_dev.CountOnlyLabel();
                    int out_type = TorpoDevice.JudgeDeviceTypeByLabel(out_label);
                    out_dev.setDev_type(out_type);
                    device_map.put(out_label, out_dev);
                }

                // Add to cache, dev map
                cache_device_map.put(out_label, out_dev);
            }

            // According to the kind, deal with it.

            LinkAndEraseDevs(in_dev, out_dev, route_type);

        }
        cache_device_map.clear();
        this.entry_device_map.put(route_type, temp_entry_device_map);
        this.stat = this.stat | (1 << route_type);
        return true;
    }

    private boolean LinkAndEraseDevs(TorpoDevice in_dev, TorpoDevice out_dev, int route_type) {
        boolean is_success1 = in_dev.SetBelowDev(out_dev, route_type);
        boolean is_success2 = out_dev.SetAboveDev(in_dev, route_type);
        //boolean is_success2 = out_dev.SetNextDev(in_dev, route_type);

        if (is_success1 && is_success2) {
            return true;
        } else {
            // Error?
            return false;
        }
    }

    public boolean IsRouteContainsDevice(String dev_name) {
        if (this.device_map.containsKey(dev_name)) {
            return true;
        } else {
            return false;
        }
    }

    public TorpoDevice GetTorpoDeviceByLabel(String dev_name) {
        if (this.device_map.containsKey(dev_name)) {
            return device_map.get(dev_name);
        } else {
            return null;
        }
    }

    public void PrintOutRouteMsg(WritableSheet sheet) {
        visual = new VisualTorpo(sheet);

        for (int i = 0; i < 4; i++) {
            if (!TorpoRoute.IsDirectionExisted(this.stat, i)) {
                continue;
            }
            UpdateTorpoData(i);
        }

        for (String str:
                device_map.keySet()) {
            TorpoDevice tp_dev = device_map.get(str);
            visual.AddDevInfo(tp_dev);
        }
        visual.PrintGlobalTorpo();
        System.out.println("Finish " + this.route_name);
    }

    private void UpdateTorpoData(int route_type) {
        HashMap<String, TorpoDevice> tmp_entry_map = entry_device_map.get(route_type);
        for (String str:
                tmp_entry_map.keySet()) {
            if (tmp_entry_map.get(str) == null) {
                continue;
            }
            TorpoDevice dev = tmp_entry_map.get(str);
            //dev.SetLevelOfRouteType(0, route_type);
            dev.FlushData(0, route_type);

        }
    }

    private void PrintTorpoDevMsg(String str, int route_type) {

    }

    public static final int GetRouteOtherSide(int route_type) {
        return (route_type + 2) % 4;
    }
}
