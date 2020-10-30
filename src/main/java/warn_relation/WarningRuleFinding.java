package warn_relation;

import analysis.Analysis;
import analysis.WarningGroupData;

import java.util.ArrayList;
import java.util.HashMap;

public class WarningRuleFinding {
    private ArrayList<WarningGroupData> org_warn_data;

    private ArrayList<WarnChain> founded_chains;

    private HashMap<Integer, Integer> err_count_times;

    private int min_support;

    private double min_belief;

    public WarningRuleFinding(Analysis analysis) {
        org_warn_data = new ArrayList<WarningGroupData>();
        org_warn_data.addAll(analysis.related_warning_data);
        founded_chains = new ArrayList<WarnChain>();
        err_count_times = new HashMap<Integer, Integer>();
    }

    public void SetArgsForFinding(int min_support, double min_belief) {
        this.min_belief = min_belief;
        this.min_support = min_support;

    }

    public void FindRulesFromOriginData() {
        // Step 1: Build the List of split chains
        for (WarningGroupData data:
             org_warn_data) {
            WarnChain chain = new WarnChain(data);
            ArrayList<WarnChain> chains = chain.AnalyseChain(3);
            chain.UpdateCountTimes(err_count_times);
            founded_chains.addAll(chains);
        }

        // Step 2: According to the HashMap, Leave out all the errors less than min_support

        for (int err:
             err_count_times.keySet()) {
            if (err_count_times.get(err) > this.min_support) {

            }

        }
    }
}
