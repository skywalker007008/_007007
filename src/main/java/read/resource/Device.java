package read.resource;

// 0-一二三四五-4-X3L401-1(IN/OUT)，数字依次为：
// 0-子架，
// 4-板卡号，
// X3L401-板卡名称，
// 1(IN/OUT)-端口号

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device {

    // id
    public int line_id;

    public String line_name;

    // location
    public int subrack;

    //public String subrack_info;

    public int board_num;

    public String board_name;

    public int port_num;

    public String port_info;

    public String extra_info;

    // type
    public String device_type;

    // Only-Sign
    private String only_label;
    private String line_label;

    // pattern info

    // public static final String device_location_pattern ="(\\d+)[-]((\\S|[\u4e00-\u9fa5])+)[-](\\d+)[-]((\\S|[\u4e00-\u9fa5])+)[-](\\d+)([(](\\S|[\u4e00-\u9fa5])+[)])[-]((\\S|[\u4e00-\u9fa5])+)";

   public static final String device_location_pattern =
           "[\u4e00-\u9fa5]*(\\d+)[-]*((\\S|[ ]|[\u4e00-\u9fa5]))*[-](\\d+)[-]((\\S|[\u4e00-\u9fa5])+)[-](\\d+)[(]";


    public Device() {
        line_id = -1;
    }

    public Device(Device dev) {
        this.board_name = dev.board_name;
        this.only_label = dev.GetLabel();
        this.line_id = dev.line_id;
        this.line_name = dev.line_name;
        this.device_type = dev.device_type;
        this.subrack = dev.subrack;
        this.board_num = dev.board_num;
        this.extra_info = dev.extra_info;
        this.port_num = dev.port_num;
        this.port_info = dev.port_info;
    }

    // Get label of device
    public static final String GetDeviceLabel(String info_name, String info_dev) {
        Device dev = new Device();
        dev.ReadDeviceInfo(info_dev);
        dev.ReadDeviceLine(info_name);
        dev.CountOnlyLabel();
        return dev.GetLabel();
    }

    public void CountOnlyLabel() {

        if (line_id != -1) {
            this.only_label = String.valueOf(line_id) + "-" + String.valueOf(subrack) + "-" + String.valueOf(board_num) + "-" + board_name +
                    "-" + String.valueOf(port_num);
        }

        //this.only_label = this.line_label + this.only_label;
    }

    public boolean ReadDeviceInfo(String info) {

        Pattern pat = Pattern.compile(Device.device_location_pattern);
        Matcher match = pat.matcher(info);

        if (match.find()) {
            this.subrack = Integer.parseInt(match.group(1));
            //this.subrack_info = match.group(2);
            this.board_num = Integer.parseInt(match.group(4));
            this.board_name = match.group(5);
            this.port_num = Integer.parseInt(match.group(7));
            //this.port_info = match.group(8);
            //this.extra_info = match.group(10);
            this.extra_info = info.substring(0, match.end());

            return true;
        } else {
            return false;
        }


        //this.only_label = info;
        // return true;
    }

    public boolean ReadDeviceLine(String name) {
        this.line_label = name;
        String pat_name = "[H](\\d+)[-](([\u4e00-\u9fa5]|\\S)+)";
        Pattern pat = Pattern.compile(pat_name);
        Matcher match = pat.matcher(name);
        if (match.find()) {
            this.line_id = Integer.parseInt(match.group(1));
            this.line_name = match.group(2);
            if (this.line_id < 1000) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String GetLabel() {
        return this.only_label;
    }

    // TO BE IMPLEMENTED
    public boolean equals(Object o) {
        if (o instanceof Device) {
            if (this.only_label.equals(((Device) o).only_label)) {
                return true;
            }
        }
        return false;
    }
}
