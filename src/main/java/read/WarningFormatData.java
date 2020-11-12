package read;

import resource.Device;
import resource.ErrorSignalType;
import resource.MyTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarningFormatData {
    public int order;

    public MyTime happen_time;

    public MyTime handle_time;

    public MyTime confirm_time;

    public ErrorSignalType err_signal;

    public Device device_data;

    public String extra_data;

    public long duration;

    public long combine_time;

    // To be implemented: frequent Warnings

    public WarningFormatData() {
        combine_time = 1;
    }

    public boolean SetDuration(String duration) {
        String pattern_tme = "(\\d+)([\u4e00-\u9fa5]+)(\\d+)([\u4e00-\u9fa5]+)(\\d+)([\u4e00-\u9fa5]+)";

        Pattern pat = Pattern.compile(pattern_tme);
        Matcher match = pat.matcher(duration);
        if (match.find()) {
            int hour = Integer.parseInt(match.group(1));
            int min = Integer.parseInt(match.group(3));
            int sec = Integer.parseInt(match.group(5));
            this.duration = hour * 1440 + min * 60 + sec;
            return true;
        }


        return false;
    }

}
