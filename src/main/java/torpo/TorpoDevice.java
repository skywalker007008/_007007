package torpo;

import read.resource.Device;
import java.util.HashMap;

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
    // Map of level
    private HashMap<Integer, Integer> level_map;
    // Whether the side_route
    private boolean is_side;

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
        this.is_main_route = true;
        this.is_many_in = false;
        this.above_dev = null;
        this.above_dev_map = new HashMap<String, TorpoDevice>();
        this.below_dev_map = new HashMap<String, TorpoDevice>();
        this.below_dev = null;
        this.side_dev = null;
        this.dev_level = -1;
        this.is_side_in = false;
        this.is_flush = false;
    }

    public boolean ContainsRouteType(int route_type) {
        if (level_map.containsKey(route_type)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean SetNextDev(TorpoDevice dev, int route_type) {
        if (next_dev_map.containsKey(dev.GetLabel())) {
            // Something?
            return true;
        } else {
            next_dev_map.put(dev.GetLabel(), route_type);
            return true;
        }
    }

    public boolean SetLevelOfRouteType(int level, int route_type) {
        if (level_map.containsKey(route_type)) {
            // Something happens
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

    public void FlushData(String str) {
        // Step0: According to conditions to decide whether flush

        if (is_flush) {
            return;
        }

        if (this.is_side_in) {
            if (str.equals(above_dev.GetLabel())) {
                return;
            }
        }

        // Step1: update level and main/side
        if (this.above_dev == null && this.above_dev_map.size() == 0) {
            this.dev_level = 0;
            this.is_main_route = true;
        } else if (this.above_dev != null) {
            this.dev_level = above_dev.getDev_level() + 1;
            if (this.side_dev != null) {
                this.is_main_route = true;
                if ((this.above_dev.getDev_level() < this.side_dev.getDev_level()) && this.is_side_in) {
                    this.dev_level = this.side_dev.getDev_level() + 1;
                }
            } else {
                this.is_main_route = this.above_dev.is_main_route;
            }
        } else {
            // temp-remain-same
            for (String temp_str:
                 this.above_dev_map.keySet()) {
                TorpoDevice tp_dev = above_dev_map.get(temp_str);
                if (this.dev_level < tp_dev.getDev_level() + 1) {
                    this.dev_level = tp_dev.getDev_level() + 1;
                }
            }
            this.is_main_route = true;
        }

        this.is_flush = true;

        // Step 2: Flush Other data

        if (this.below_dev == null && this.below_dev_map.size() == 0) {
            return;
        }

        if (this.below_dev != null) {
            this.below_dev.FlushData(this.GetLabel());
            if (this.side_dev != null && !is_side_in) {
                this.side_dev.FlushData(GetLabel());
            }
            return;
        }
        
        if (this.below_dev_map.size() > 0) {
            for (String str_below:
                 below_dev_map.keySet()) {
                TorpoDevice dev = below_dev_map.get(str_below);
                dev.FlushData(this.GetLabel());
            }
        }

    }

    public boolean setAbove_dev(TorpoDevice above_dev, int route_type) {
        if (this.above_dev != null) {
            if (this.above_dev.GetLabel().equals(above_dev.GetLabel())) {
                return false;
            } else {
                if (this.side_dev != null) {
                    if (this.side_dev.GetLabel().equals(above_dev.GetLabel())) {
                        return false;
                    } else {
                        System.out.println("BOTH IN AND OUT");
                        return false;
                    }
                } else {
                    this.side_dev = above_dev;
                    this.is_side_in = true;
                    this.is_main_route = true;
                    return true;
                }
            }
        }
        this.above_dev = above_dev;
        this.dev_level = above_dev.getDev_level() + 1;
        return true;
    }

    public boolean setBelow_dev(TorpoDevice below_dev, int route_type) {
        if (this.below_dev != null) {
            if (this.below_dev.GetLabel().equals(below_dev.GetLabel())) {
                return false;
            } else {
                if (this.side_dev != null) {
                    if (this.side_dev.GetLabel().equals(below_dev.GetLabel())) {
                        return false;
                    } else {
                        if (below_dev.GetLabel().equals("1084-0-56-Y2C210-3")) {
                            int i = 0;
                        }
                        System.out.println(this.below_dev.GetLabel() + "-" + below_dev.GetLabel());
                        System.out.println("BOTH IN AND OUT");
                        return false;
                    }
                } else {
                    this.side_dev = below_dev;
                    this.is_side_in = false;
                    this.is_main_route = true;
                    below_dev.setIs_main_route(false, route_type);
                    return true;
                }
            }
        }
        this.below_dev = below_dev;
        return true;
    }

    public void setDev_level(int dev_level, int route_type) {
        this.dev_level = dev_level;
    }

    public void setIs_main_route(boolean is_main_route, int route_type) {
        this.is_main_route = is_main_route;
    }

    public void setDev_type(int dev_type) {
        this.dev_type = dev_type;
    }

    public int getDev_level(int route_type) {
        return dev_level;
    }

    public int getDev_type() {
        return dev_type;
    }

    public boolean addL40Above_dev(TorpoDevice device, int route_type) {
        if (above_dev_map.containsKey(device.GetLabel())) {
            // Exception: Re-addInfo
        }
        else {
            above_dev_map.put(device.GetLabel(), device);
            if (above_dev_map.size() >= 2) {
                is_many_in = true;
            }
            if (this.dev_level > 0 && this.dev_level != device.getDev_level() + 1) {
                // Exception: Level error
                this.dev_level = device.getDev_level() + 1;
            } else {
                this.dev_level = device.getDev_level() + 1;
            }

        }

        return true;
    }
    
    public boolean addL40Below_dev(TorpoDevice device, int route_type) {
        if (below_dev_map.containsKey(device.GetLabel())) {
            // Exception: Re-addInfo
        }
        else {
            below_dev_map.put(device.GetLabel(), device);
            if (below_dev_map.size() >= 2) {
                if (is_many_in) {
                    // Exception: both in and out
                } else {
                    is_many_in = false;
                }
            }
        }

        return true;
    }

    public boolean addM40Above_dev(TorpoDevice device, int route_type) {
        if (above_dev_map.containsKey(device.GetLabel())) {
            // Exception: Re-addInfo
        }
        else {
            above_dev_map.put(device.GetLabel(), device);
            if (this.dev_level > 0 && this.dev_level != device.getDev_level() + 1) {
                // Exception: Level error
                this.dev_level = device.getDev_level() + 1;
            } else {
                this.dev_level = device.getDev_level() + 1;
            }

        }

        return true;
    }

    public boolean addD40Below_dev(TorpoDevice device, int route_type) {
        if (below_dev_map.containsKey(device.GetLabel())) {
            // Exception: Re-addInfo
        }
        else {
            below_dev_map.put(device.GetLabel(), device);
        }

        return true;
    }

    public TorpoDevice getAbove_dev() {
        return above_dev;
    }

    public TorpoDevice getBelow_dev() {
        return below_dev;
    }

    public TorpoDevice getSide_dev() {
        return side_dev;
    }

    public boolean isIs_main_route(int route_type) {
        return is_main_route;
    }

    public boolean isIs_many_in() {
        return is_many_in;
    }

    public boolean isIs_side_in() {
        return is_side_in;
    }


    public HashMap<String, TorpoDevice> GetBelowDevs() {
        if ((dev_type == TorpoDevice.TYPE_L40_1) ||
                (dev_type == TorpoDevice.TYPE_D40_1)) {
            return this.below_dev_map;
        } else {
            return null;
        }
    }

    private boolean LinkAndEraseDevs(TorpoDevice in_dev, TorpoDevice out_dev, int route_type) {
        if (in_dev == null || out_dev == null) {
            return false;
        }
        // Type 1: D40-COMMONS

        // Kind 1: Common-D40
        if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_D40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev, route_type);
            boolean is_success2 = out_dev.setAbove_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: D40-COMMONS
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_D40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addD40Below_dev(out_dev, route_type);
            boolean is_success2 = out_dev.setAbove_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true, route_type);

            // Success here
        }
        // END D40

        // Type 2: COMMONS-M40

        // Kind 1: COMMONS-M40
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_M40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev, route_type);
            boolean is_success2 = out_dev.addM40Above_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: M40-COMMON

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_M40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev, route_type);
            boolean is_success2 = out_dev.setAbove_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true, route_type);
            // Success here
        }

        // END M40

        // Type 3: L40

        // Kind 1: COMMON(S)-L40

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_L40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev, route_type);
            boolean is_success2 = out_dev.addL40Above_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: L40-COMMON(S)

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_L40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addL40Below_dev(out_dev, route_type);
            boolean is_success2 = out_dev.setAbove_dev(in_dev, route_type);

            if (!is_success1 || !is_success2) {
                // Something Wrong
            }

        } else {

            boolean is_success1 = in_dev.setBelow_dev(out_dev, route_type);
            boolean is_success2 = out_dev.setAbove_dev(in_dev, route_type);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(in_dev.isIs_main_route(route_type), route_type);
        }

        return true;
    }

}
