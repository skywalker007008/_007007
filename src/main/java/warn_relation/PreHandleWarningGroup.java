package warn_relation;

import analysis.WarningGroupData;
import read.WarningFormatData;
import torpo.TorpoRoute;

import java.util.ArrayList;

public class PreHandleWarningGroup {

    private TorpoRoute torpo_route;

    private ArrayList<WarningFormatData> warn_data_list;

    public PreHandleWarningGroup(WarningGroupData data) {
        this.torpo_route = data.GetTorpoRoute();
        warn_data_list = new ArrayList<WarningFormatData>();
        warn_data_list.addAll(data.GetFormatDataList());
    }

    // STEP 1: MAKE THE DIRECTIONS
    // time

}
