package analysis;

import read.resource.Device;

import java.util.HashMap;

public class WarningDevice extends Device {

    private HashMap<String, WarningDevice> above_dev_map;
    private HashMap<String, WarningDevice> below_dev_map;

    private boolean is_above_end;

    private boolean is_below_end;

    public WarningDevice(Device dev) {
        super(dev);
        is_above_end = false;
        is_below_end = false;
        above_dev_map = new HashMap<String, WarningDevice>();
        below_dev_map = new HashMap<String, WarningDevice>();
    }

    public boolean AddAboveDev(WarningDevice dev) {
        above_dev_map.put(dev.GetLabel(), dev);
        return true;
    }

    public boolean AddBelowDev(WarningDevice dev) {
        below_dev_map.put(dev.GetLabel(), dev);
        return true;
    }

}
