package torpo;
import jxl.Sheet;
import jxl.write.WritableSheet;
import visual.VisualTorpo;

import java.util.HashMap;

// One Route of the Torpo
public class TorpoRoute {
    // Set of the device
    private HashMap<String, TorpoDevice> device_map;
    // Levels of the map
    private int level;
    // Name of route
    private String route_name;
    // End of Route
    private HashMap<String, TorpoDevice> entry_device_map;
    private HashMap<String, TorpoDevice> exit_device_map;
    // Unused(cached map)

    private HashMap<String, TorpoDevice> cache_device_map;

    private VisualTorpo visual;
    private HashMap<String, TorpoDevice> painting_map;

    public TorpoRoute() {
        device_map = new HashMap<String, TorpoDevice>();
        level = 0;
        entry_device_map = new HashMap<String, TorpoDevice>();
        exit_device_map = new HashMap<String, TorpoDevice>();
        cache_device_map = new HashMap<String, TorpoDevice>();
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
        for (String str:
                entry_device_map.keySet()) {
            TorpoDevice dev = entry_device_map.get(str);

            dev.FlushData(null);
        }
    }

    private void PrintTorpoDevMsg(String str) {
        if (painting_map.containsKey(str)) {
            return;
        }
        TorpoDevice dev = device_map.get(str);
        painting_map.put(str, device_map.get(str));

        visual.PrintTorpo(str, dev.getDev_level());

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

    public boolean ReadTorpoRoute(Sheet sheet) {
        this.route_name = sheet.getName();
        int rows = sheet.getRows();

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

                in_dev = new TorpoDevice();
                int dev_type = TorpoDevice.JudgeDeviceTypeByLabel(in_label);
                in_dev.setDev_type(dev_type);
                in_dev.setDev_level(0);
                in_dev.ReadDeviceLine(in_device_line_info);
                in_dev.ReadDeviceInfo(in_device_info);
                in_dev.CountOnlyLabel();
                // Add to Entry and cache
                cache_device_map.put(in_label, in_dev);
                entry_device_map.put(in_label, in_dev);
                device_map.put(in_label, in_dev);
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
                // No exists, create new one.
                out_dev = new TorpoDevice();
                out_dev.ReadDeviceLine(out_device_line_info);
                out_dev.ReadDeviceInfo(out_device_info);
                out_dev.CountOnlyLabel();
                int out_type = TorpoDevice.JudgeDeviceTypeByLabel(out_label);
                out_dev.setDev_type(out_type);
                // Add to cache, dev map
                cache_device_map.put(out_label, out_dev);
                device_map.put(out_label, out_dev);
            }

            // According to the kind, deal with it.

            LinkAndEraseDevs(in_dev, out_dev, false);

            if (this.level < in_dev.getDev_level()) {
                this.level = in_dev.getDev_level();
            }

        }
        return true;
    }

    private boolean LinkAndEraseDevs(TorpoDevice in_dev, TorpoDevice out_dev, boolean is_use) {
        if (in_dev == null || out_dev == null) {
            return false;
        }
        // Type 1: D40-COMMONS

        // Kind 1: Common-D40
        if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_D40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: D40-COMMONS
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_D40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addD40Below_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true);

            // Success here
        }
        // END D40

        // Type 2: COMMONS-M40

        // Kind 1: COMMONS-M40
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_M40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.addM40Above_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: M40-COMMON

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_M40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true);
            // Success here
        }

        // END M40

        // Type 3: L40

        // Kind 1: COMMON(S)-L40

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_L40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.addL40Above_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: L40-COMMON(S)

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_L40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addL40Below_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);

            if (!is_success1 || !is_success2) {
                // Something Wrong
            }

        } else {

            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(in_dev.isIs_main_route());
        }

        return true;
    }
/*
    private boolean LinkAndEraseDevs(TorpoDevice in_dev, TorpoDevice out_dev) {
        // Type1: easiest: COMMON-COMMON
        if (in_dev == null || out_dev == null) {
            int i = 5 + 1;
        }
        if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(in_dev.isIs_main_route());
            // Success here
        }
        // Type2: mid-easiest: OLP_RELATED
        // Kind1: common-OLP3
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_OLP_3)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_side_in(false);

            // Success here
        }
        // Kind 2: OLP3-OLP1
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_OLP_3) && (out_dev.getDev_type() == TorpoDevice.TYPE_OLP_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            // Success here
        }
        // Kind 3: OLP3-OLP2
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_OLP_3) && (out_dev.getDev_type() == TorpoDevice.TYPE_OLP_2)) {
            boolean is_success1 = in_dev.setSide_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(false);
            // Success here
        }

        // Kind 4: OLPx-COMMON
        else if (
                ((in_dev.getDev_type() == TorpoDevice.TYPE_OLP_1) ||
                (in_dev.getDev_type() == TorpoDevice.TYPE_OLP_2)) &&
                (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(in_dev.isIs_main_route());

        }

        // Kind 5: COMMON-OLPx
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) &&
                ((out_dev.getDev_type() == TorpoDevice.TYPE_OLP_1) ||
                (out_dev.getDev_type() == TorpoDevice.TYPE_OLP_2))) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(in_dev.isIs_main_route());

        }
        // Kind 6: OLP1,OLP2--OLP3

        else if (
                ((in_dev.getDev_type() == TorpoDevice.TYPE_OLP_1) ||
                        (in_dev.getDev_type() == TorpoDevice.TYPE_OLP_2)) &&
                        (out_dev.getDev_type() == TorpoDevice.TYPE_OLP_3)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);

            boolean is_success2;
            if (in_dev.getDev_type() == TorpoDevice.TYPE_OLP_1) {
                is_success2 = out_dev.setAbove_dev(in_dev);
                in_dev.setIs_side_in(true);
            } else {
                is_success2 = out_dev.setSide_dev(in_dev);
            }
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true);


        }

        // Kind 7: OLP3-COMMON
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_OLP_3) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(false);

        }

        // Finish OLP_RELATED

        // Type 3: D40-COMMONS

        // Kind 1: Common-D40
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_D40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }

        }

        // Kind 2: D40-COMMONS
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_D40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addD40Below_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true);

            // Success here
        }

        // END D40

        // Type 4: COMMONS-M40

        // Kind 1: COMMONS-M40
        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_M40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.addM40Above_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }

        }

        // Kind 2: M40-COMMON

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_M40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
            out_dev.setIs_main_route(true);

            // Success here
        }

        // END M40

        // Type 5: L40

        // Kind 1: COMMON(S)-L40

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_COMMON) && (out_dev.getDev_type() == TorpoDevice.TYPE_L40_1)) {
            boolean is_success1 = in_dev.setBelow_dev(out_dev);
            boolean is_success2 = out_dev.addL40Above_dev(in_dev);
            if (!is_success1 || !is_success2) {
                // Something Wrong
            }
        }

        // Kind 2: L40-COMMON(S)

        else if ((in_dev.getDev_type() == TorpoDevice.TYPE_L40_1) && (out_dev.getDev_type() == TorpoDevice.TYPE_COMMON)) {
            boolean is_success1 = in_dev.addL40Below_dev(out_dev);
            boolean is_success2 = out_dev.setAbove_dev(in_dev);

            if (!is_success1 || !is_success2) {
                // Something Wrong
            }

        } else {
            // Exception: Not known pair
        }

        return true;
    }
    */

    public int GetDevLevel(String label) {
        if (device_map.containsKey(label)) {
            TorpoDevice dev = device_map.get(label);
            return dev.getDev_level();
        } else {
            return -1;
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

    /*
    private boolean ReadTorpoRoute(Sheet sheet) {
        this.route_name = sheet.getName();
        int rows = sheet.getRows();

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
            // Find in_label in cache

            if (cache_device_map.containsKey(in_label)) {
                // Find, means the device has the above exit.
                TorpoDevice found_in_device = cache_device_map.get(in_label);
                if (cache_device_map.containsKey(out_label)) {
                    // Find out cache, means it special device
                    TorpoDevice found_out_device = cache_device_map.get(out_label);
                    if (found_out_device.IsSpecial()) {
                        boolean is_success = ((TorpoSpecialDevice)found_out_device).SetAboveDevice(found_in_device);
                    } else {

                        // Exception here
                    }

                    if (found_in_device.IsSpecial()) {
                        // Exception here
                    } else {
                        found_in_device.SetBelowDevice(found_out_device);
                        // Erase from cache
                        cache_device_map.remove(in_label);
                    }
                } else {
                    // not Find, mean it common device, or the first special device

                    boolean is_special = TorpoDevice.IsSpecialDeviceLabel(out_label);
                    if (is_special) {
                        TorpoSpecialDevice out_dev = new TorpoSpecialDevice();
                        out_dev.ReadDeviceInfo(out_device_info);
                        out_dev.ReadDeviceName(out_device_line_info);
                        out_dev.SetAboveDevice(found_in_device);
                        out_dev.SetTorpoLevel(found_in_device.GetTorpoLevel() + 1);
                        cache_device_map.put(out_label, out_dev);
                        this.level = found_in_device.GetTorpoLevel();
                        // In must not special
                        if (found_in_device.IsSpecial()) {
                            // Exception Here
                        } else {
                            found_in_device.SetBelowDevice(out_dev);
                            // Erase from cache
                            cache_device_map.remove(in_label);
                        }
                    } else {
                        TorpoDevice out_dev = new TorpoDevice();
                        out_dev.ReadDeviceInfo(out_device_info);
                        out_dev.ReadDeviceName(out_device_line_info);
                        out_dev.SetAboveDevice(found_in_device);
                        out_dev.SetTorpoLevel(found_in_device.GetTorpoLevel() + 1);
                        cache_device_map.put(out_label, out_dev);
                        this.level = found_in_device.GetTorpoLevel();

                        // whether in_device special
                        if (found_in_device.IsSpecial()) {
                            // if special, not erase
                            boolean is_success = ((TorpoSpecialDevice)found_in_device).SetBelowDevice(out_dev);
                        }
                        else {
                            // erase
                            boolean is_success = found_in_device.SetBelowDevice(out_dev);
                            cache_device_map.remove(in_label);
                        }
                    }
                }
            }

            else {
                if (this.level != 0) {
                    // Exception: Not Found Device should Be Here
                } else {
                    // First level

                    // Whether the inlabel is special
                    if (TorpoDevice.IsSpecialDeviceLabel(in_label)) {
                        // if it is, create new one and add to cache
                        TorpoSpecialDevice in_dev = new TorpoSpecialDevice();
                        in_dev.ReadDeviceName(in_device_line_info);
                        in_dev.ReadDeviceInfo(in_device_info);
                        in_dev.SetTorpoLevel(this.level);
                        // Out must not special
                        TorpoDevice out_dev = new TorpoDevice();
                        out_dev.ReadDeviceInfo(out_device_info);
                        out_dev.ReadDeviceName(out_device_line_info);
                        out_dev.SetTorpoLevel(this.level + 1);
                        // Add link
                        in_dev.SetBelowDevice(out_dev);
                        out_dev.SetAboveDevice(in_dev);
                        // Add cache
                        cache_device_map.put(in_label, in_dev);
                        cache_device_map.put(out_label, out_dev);
                    } else {
                        // if not, create new one but not in cache
                    }
                }
            }

        }
        return true;
    }
    */
}
