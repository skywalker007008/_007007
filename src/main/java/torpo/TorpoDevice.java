package torpo;

import read.resource.Device;
import java.util.HashMap;
import java.util.HashSet;

public class TorpoDevice extends Device {

    public static final int TYPE_L40_1 = 1;
    public static final int TYPE_M40_1 = 2;
    public static final int TYPE_D40_1 = 3;
    public static final int TYPE_OLP_1 = 4;
    public static final int TYPE_OLP_2 = 5;
    public static final int TYPE_OLP_3 = 6;
    public static final int TYPE_COMMON = 0;

    // Device Type
    private int dev_type;
    // Map of next
    private HashMap<String, TorpoDevice> next_dev_map;
    // Map of next_route and above_route
    private HashMap<Integer, HashSet<String>> next_route_map;
    private HashMap<Integer, HashSet<String>> above_route_map;
    // Map of level
    private HashMap<Integer, Integer> level_map;
    // Whether the side_route
    private boolean is_side;
    // Flush Times
    private HashMap<Integer, Integer> flush_map;

    // Judge the dev type by label
    public static final int JudgeDeviceTypeByLabel(String label) {
        String[] dev_info = label.split("-");
        String board_name = dev_info[3];
        int port = Integer.parseInt(dev_info[4]);
        if (board_name.contains("L40") && port == 1) {
            return TorpoDevice.TYPE_L40_1;
        }
        if (board_name.contains("M40") && port == 1) {
            return TorpoDevice.TYPE_M40_1;
        }
        if (board_name.contains("D40") && port == 1) {
            return TorpoDevice.TYPE_D40_1;
        }

        return TorpoDevice.TYPE_COMMON;
    }

    public TorpoDevice() {
        super();
        this.dev_type = -1;
        this.is_side = false;
        this.next_dev_map = new HashMap<String, TorpoDevice>();
        this.level_map = new HashMap<Integer, Integer>();
        this.next_route_map = new HashMap<Integer, HashSet<String>>();
        this.above_route_map = new HashMap<Integer, HashSet<String>>();
        this.flush_map = new HashMap<Integer, Integer>();
        flush_map.put(0,0);
        flush_map.put(1,0);
        flush_map.put(2,0);
        flush_map.put(3,0);
    }

    public boolean ContainsRouteType(int route_type) {
        if (level_map.containsKey(route_type)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean SetAboveDev(TorpoDevice dev, int route_type) {
        String label = dev.GetLabel();
        if (above_route_map.containsKey(route_type)) {
            HashSet<String> strings = above_route_map.get(route_type);
            if (strings.contains(label)) {
                return false;
            } else {
                strings.add(label);
            }
        }
        else {
            HashSet<String> strings = new HashSet<String>();
            strings.add(label);
            above_route_map.put(route_type, strings);
        }
        if (!next_dev_map.containsKey(label)) {
            next_dev_map.put(label, dev);
        }
        int level = dev.GetLevelOfRouteType(route_type);
        this.level_map.put(route_type, level);
        return true;
    }

    public boolean SetBelowDev(TorpoDevice dev, int route_type) {
        String label = dev.GetLabel();
        if (next_route_map.containsKey(route_type)) {
            HashSet<String> strings = next_route_map.get(route_type);
            if (strings.contains(label)) {
                return false;
            } else {
                strings.add(label);
            }
        }
        else {
            HashSet<String> strings = new HashSet<String>();
            strings.add(label);
            next_route_map.put(route_type, strings);
        }
        if (!next_dev_map.containsKey(label)) {
            next_dev_map.put(label, dev);
        }
        return true;
    }
/*
    public boolean SetNextDev(TorpoDevice dev, int route_type) {
        String label = dev.GetLabel();
        if (next_dev_map.containsKey(label)) {
            // Something?
            HashSet<String> tmp_str_list = next_route_map.get(route_type);
            if (tmp_str_list.contains(dev)) {
                // Error here, replenish again
                return false;
            } else {
                if (next_route_map.containsKey(route_type)) {
                    HashSet<String> str_list = next_route_map.get(route_type);
                    str_list.add(label);
                } else {
                    HashSet<String> str_list = new HashSet<String>();
                    str_list.add(label);
                    next_route_map.put(route_type, str_list);
                }
                return true;
            }
        } else {
            next_dev_map.put(label, dev);
            if (next_route_map.containsKey(route_type)) {
                HashSet<String> str_list = next_route_map.get(route_type);
                str_list.add(label);
            } else {
                HashSet<String> str_list = new HashSet<String>();
                str_list.add(label);
                next_route_map.put(route_type, str_list);
            }
            return true;
        }
    }

 */

    public boolean SetLevelOfRouteType(int level, int route_type) {
        if (level_map.containsKey(route_type)) {
            // Something happens
            System.out.println("Reassign init_level");
            level_map.put(route_type, level);
            return true;
        }
        else {
            level_map.put(route_type, level);
            return true;
        }
    }

    public int GetLevelOfRouteType(int route_type) {
        if (level_map.containsKey(route_type)) {
            return level_map.get(route_type);
        } else {
            return -1;
        }

    }

    public void FlushData(int level, int route_type) {
        int now_level = this.level_map.get(route_type);
        if (level > now_level) {
            this.level_map.put(route_type, level);
            now_level = level;
        }

        int times = this.flush_map.get(route_type);
        times = times + 1;
        if (times < this.above_route_map.get(route_type).size()) {
            return;
        }
        for (String str:
            next_route_map.get(route_type)) {
            TorpoDevice dev = next_dev_map.get(str);
            if (dev != null) {
                dev.FlushData(now_level + 1, route_type);
            }
        }
    }

    public void setDev_type(int dev_type) {
        this.dev_type = dev_type;
    }

    public int getDev_type() {
        return dev_type;
    }

}
