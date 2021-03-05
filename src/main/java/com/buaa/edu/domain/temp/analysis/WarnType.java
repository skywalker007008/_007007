package com.buaa.edu.domain.temp.analysis;

import java.util.HashMap;

public class WarnType {
    private int warn_type;

    public HashMap<Integer, Integer> related_times_map;

    public int show_times;

    public WarnType(int warn_type) {
        this.warn_type = warn_type;
        related_times_map = new HashMap<Integer, Integer>();
        show_times = 1;
    }

    public int GetShowTimes() {
        return show_times;
    }

    public HashMap<Integer, Integer> GetRelatedMap() {
        return related_times_map;
    }


}
