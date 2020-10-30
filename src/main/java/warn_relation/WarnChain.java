package warn_relation;

import analysis.WarningGroupData;
import read.WarningFormatData;
import read.resource.ErrorSignalType;

import java.util.ArrayList;
import java.util.HashMap;

public class WarnChain {
    private ArrayList<WarningFormatData> format_chain;

    private ArrayList<ErrorSignalType> error_chain;

    private ArrayList<String> port_chain;

    private String chain_type_label;

    private int max_chain_length;

    /*
     * Initialize the big chain from the format data
     */

    public WarnChain(ArrayList<WarningFormatData> format_data) {
        format_chain.addAll(format_data);
        for (WarningFormatData tmp_data:
                format_chain) {
            ErrorSignalType type = tmp_data.err_signal;
            String str = tmp_data.device_data.GetLabel();
            error_chain.add(type);
            port_chain.add(str);
        }
        this.CountTypeString();
    }

    public WarnChain() {

    }

    /*
     * Update the times of certain warning_type, only related to big chains
     */
    public void UpdateCountTimes(HashMap<Integer, Integer> err_count_times) {
        for (ErrorSignalType type:
             error_chain) {
            int err_type = type.value_type;
            if (!err_count_times.containsKey(err_type)) {
                err_count_times.put(err_type, 1);
            } else {
                int times = err_count_times.get(err_type);
                err_count_times.put(err_type, times + 1);
            }
        }
    }

    /*
     * Group Big Chains into different parts
     */
    public ArrayList<WarnChain> AnalyseChain(int max_length) {
        // Step 1: Place where to start
        for (int i = 0; i < this.error_chain.size(); i++) {
            ArrayList<WarningFormatData> tmp_format_data = new ArrayList<WarningFormatData>();
            ArrayList<ErrorSignalType> tmp_err_data = new ArrayList<ErrorSignalType>();
            ArrayList<String> tmp_err
            for (int j = 0; j < max_length; j++) {

            }
        }
    }

    public int GetSizeOfChain() {
        return this.error_chain.size();
    }

    public ErrorSignalType GetErrorTypeByIndex(int index) {
        return this.error_chain.get(index);
    }

    public boolean TypeEquals(WarnChain chain) {
        return this.chain_type_label.equals(chain.GetTypeString());
    }

    public String CountTypeString() {
        String str = "label-";
        for (ErrorSignalType err_type:
             error_chain) {
            str = str + err_type.string_type + "-";
        }

        return str;
    }

    public String GetTypeString() {
        return this.chain_type_label;
    }

    /*
     * Whether this chain is the subchain of target chain
     */
    public boolean IsSubChain(WarnChain chain) {
        return chain.GetTypeString().contains(this.chain_type_label);
    }

}
