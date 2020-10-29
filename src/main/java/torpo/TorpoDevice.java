package torpo;

import read.resource.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TorpoDevice extends Device {
    // This Information Used as Device only for Torpo Information
    // Constant of DeviceType
    /*
    - L_40: 1-many & many-1
    - M_40: many-1
    - D_40: 1-many
    - OLP-1: main route
    - OLP-2: side route
    - OLP-3: SPLIT_END
    - COMMON ONES
   */

    public static final int TYPE_L40_1 = 1;
    public static final int TYPE_M40_1 = 2;
    public static final int TYPE_D40_1 = 3;
    public static final int TYPE_OLP_1 = 4;
    public static final int TYPE_OLP_2 = 5;
    public static final int TYPE_OLP_3 = 6;
    public static final int TYPE_COMMON = 0;

    // Device Type
    private int dev_type;

    // For all devs
    // dev_level
    private int dev_level;
    // above and below dev
    private TorpoDevice above_dev;
    private TorpoDevice below_dev;

    // For L40 devs
    // Whether 1-many(true) or many-1(false)
    private boolean is_many_in;

    // For L40,M40,D40 devs
    // Above and Below devs set
    private HashMap<String, TorpoDevice> above_dev_map;
    private HashMap<String, TorpoDevice> below_dev_map;

    // For OLP related devs
    // Whether main(true) or side(false) route
    private boolean is_main_route;

    // For OLP-3 devs
    // Whether side_in(true) or side_out(false)
    private boolean is_side_in;
    // Side Torpo dev
    private TorpoDevice side_dev;
    private boolean is_flush;

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
        /*
        if (board_name.contains("OLP")) {
            if (port == 1) {
                return TorpoDevice.TYPE_OLP_1;
            }
            if (port == 2) {
                return TorpoDevice.TYPE_OLP_2;
            }
            if (port == 3) {
                return TorpoDevice.TYPE_OLP_3;
            }
        }
        */
        return TorpoDevice.TYPE_COMMON;
    }

    // Default Build
    public TorpoDevice(int dev_type) {
        super();
        this.dev_type = dev_type;
        this.is_main_route = true;
        this.is_many_in = false;
        this.above_dev = null;
        this.above_dev_map = new HashMap<String, TorpoDevice>();
        this.below_dev_map = new HashMap<String, TorpoDevice>();
        this.below_dev = null;
        this.side_dev = null;
        this.dev_level = -1;
        this.is_side_in = false;
    }

    public TorpoDevice() {
        super();
        this.dev_type = -1;
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

    public void FlushData(String str) {
        // Step0: According to conditions to decide whether flush

        if (this.GetLabel().equals("1016-0-15-12D40-22")) {
            int i = 0 + 1;
        }

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

    public boolean setAbove_dev(TorpoDevice above_dev) {
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

    public boolean setBelow_dev(TorpoDevice below_dev) {
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
                    below_dev.setIs_main_route(false);
                    return true;
                }
            }
        }
        this.below_dev = below_dev;
        return true;
    }

    public void setDev_level(int dev_level) {
        this.dev_level = dev_level;
    }

    public void setIs_main_route(boolean is_main_route) {
        this.is_main_route = is_main_route;
    }

    public void setDev_type(int dev_type) {
        this.dev_type = dev_type;
    }

    public void setIs_many_in(boolean is_many_in) {
        this.is_many_in = is_many_in;
    }

    public void setIs_side_in(boolean is_side_in) {
        this.is_side_in = is_side_in;
    }

    public boolean setSide_dev(TorpoDevice side_dev) {
        if (this.side_dev != null) {
            // Exception: SIDE_DEV REDEFINE
        }
        this.side_dev = side_dev;

        if (this.is_side_in) {
            if (this.dev_level <= side_dev.dev_level) {
                this.dev_level = side_dev.dev_level + 1;
            }
        }

        return true;
    }

    public int getDev_level() {
        return dev_level;
    }

    public int getDev_type() {
        return dev_type;
    }

    public boolean addL40Above_dev(TorpoDevice device) {
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
    
    public boolean addL40Below_dev(TorpoDevice device) {
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

    public boolean addM40Above_dev(TorpoDevice device) {
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

    public boolean addD40Below_dev(TorpoDevice device) {
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

    public boolean isIs_main_route() {
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

}
