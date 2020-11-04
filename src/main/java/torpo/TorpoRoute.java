package torpo;
import jxl.Sheet;
import jxl.write.WritableSheet;
import visual.VisualTorpo;

import java.util.HashMap;

// One Route of the Torpo
public class TorpoRoute {
    // Directions of Torpo
    public static final int POSITIVE_ODD = 0;
    public static final int POSITIVE_EVEN = 1;
    public static final int NEGATIVE_ODD = 2;
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

    public TorpoRoute() {
        device_map = new HashMap<String, TorpoDevice>();
        level_map = new HashMap<Integer, Integer>();
        this.stat = 0;
        entry_device_map = new HashMap<Integer, HashMap<String, TorpoDevice>>();
        exit_device_map = new HashMap<Integer, HashMap<String, TorpoDevice>>();
        cache_device_map = new HashMap<String, TorpoDevice>();
    }

    public static final String[] GetRouteNameBySheet(Sheet sheet) {
        String[] str = sheet.getName().split("-");
        String[] temp_str = str[0].split("H");
        String start_str = temp_str[0];
        temp_str = str[1].split("H");
        String end_str = temp_str[0];
        str = new String[2];
        str[0] = start_str + "-" + end_str;
        str[1] = end_str + "-" + start_str;
        return str;
    }

    public static final boolean IsDirectionExisted(int stat, int direction) {
        return (((stat >> direction) | 1) == 1);
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
                    in_dev.setDev_level(0, route_type);
                } else {

                    in_dev = new TorpoDevice();
                    int dev_type = TorpoDevice.JudgeDeviceTypeByLabel(in_label);
                    in_dev.setDev_type(dev_type);
                    in_dev.setDev_level(0, route_type);
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

            if (this.level_map.get(route_type) < in_dev.getDev_level(route_type)) {
                int level = in_dev.getDev_level(route_type);
                this.level_map.put(route_type, level);
            }

        }
        this.entry_device_map.put(route_type, temp_entry_device_map);
        return true;
    }

    private boolean LinkAndEraseDevs(TorpoDevice in_dev, TorpoDevice out_dev, int route_type) {
        boolean is_success1 = in_dev.SetNextDev(out_dev, route_type);
        boolean is_success2 = out_dev.SetNextDev(in_dev, route_type);

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
        painting_map = new HashMap<String, TorpoDevice>();

        UpdateTorpoData();

        for (String str:
                entry_device_map.keySet()) {
            TorpoDevice dev = entry_device_map.get(str);

            this.PrintTorpoDevMsg(str);
        }
    }

    public void UpdateTorpoData() {
        for (int route_type:
             entry_device_map.keySet()) {

        }
        for (String str:
                entry_device_map.keySet()) {
            TorpoDevice dev = entry_device_map.get(str);

            dev.FlushData(null);
        }
    }

    private void PrintTorpoDevMsg(String str) {
        int route_type;
        if (painting_map.containsKey(str)) {
            return;
        }
        TorpoDevice dev = device_map.get(str);
        painting_map.put(str, device_map.get(str));

        visual.PrintTorpo(str, dev.getDev_level(route_type));

        if ((dev.getDev_type() == TorpoDevice.TYPE_L40_1) ||
                (dev.getDev_type() == TorpoDevice.TYPE_D40_1)) {
            for (String str2 :
                    dev.GetBelowDevs().keySet()) {
                this.PrintTorpoDevMsg(str2);
            }
        }
        else {
            // Get to the end
            if (dev.getBelow_dev() == null) {
                return;
            }
            String str2 = dev.getBelow_dev().GetLabel();
            this.PrintTorpoDevMsg(str2);
            if (!dev.isIs_side_in() && dev.getSide_dev() != null) {
                String str3 = dev.getSide_dev().GetLabel();
                this.PrintTorpoDevMsg(str3);
            }
        }
    }
}
