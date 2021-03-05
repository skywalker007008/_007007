package com.buaa.edu.domain.temp.warn_relation;


import com.buaa.edu.domain.temp.analysis.*;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class WarningRuleFinding {
    private ArrayList<WarningGroupData> org_warn_data;

    private ArrayList<WarnChain> founded_chains;

    private HashMap<Integer, Integer> err_count_times;

    private HashMap<String, Integer> relate_count_times;

    private int min_support;

    private double min_belief;

    public WarningRuleFinding(Analysis analysis) {
        org_warn_data = new ArrayList<WarningGroupData>();
        org_warn_data.addAll(analysis.related_warning_data);
        founded_chains = new ArrayList<WarnChain>();
        err_count_times = new HashMap<Integer, Integer>();
        relate_count_times = new HashMap<String, Integer>();
    }

    public void SetArgsForFinding(int min_support, double min_belief) {
        this.min_belief = min_belief;
        this.min_support = min_support;

    }

    public void UpdateCountTimesOfChain(WarnChain chain) {
        String str = chain.GetTypeString();
        if (relate_count_times.containsKey(str)) {
            int i = relate_count_times.get(str);
            relate_count_times.put(str, i + 1);
        } else {
            relate_count_times.put(str, 1);
        }


    }

    public void FindRulesFromOriginData() {
        // Step 1: Build the List of split chains
        for (WarningGroupData data:
             org_warn_data) {
            WarnChain chain = new WarnChain(data.GetFormatDataList());
            ArrayList<WarnChain> chains = chain.AnalyseChain(3);
            chain.UpdateCountTimes(err_count_times);
            founded_chains.addAll(chains);
            for (WarnChain cn:
                 chains) {
               this.UpdateCountTimesOfChain(cn);
            }
        }

        System.out.println(founded_chains.size());

        // Step 2: Calculate the belief of the founded chains
        this.PrintRelatedRulesByBelief();
        // Step 3
        this.PrintRelatedRulesOnlyBySupport();
    }

    public void PrintRelatedRulesByBelief() {

        File file = new File("chain_belief_count-3.xls");
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("beilef", 0);
            Label label = null;
            label = new Label(0, 0, "TYPE");
            sheet.addCell(label);
            label = new Label(1, 0, "RATIO_FRONT");
            sheet.addCell(label);
            label = new Label(2, 0, "RATIO_BACK");
            sheet.addCell(label);
            label = new Label(3, 0, "TIMES");
            sheet.addCell(label);
            label = new Label(4, 0, "TIMES_FRONT");
            sheet.addCell(label);
            label = new Label(5, 0, "TIMES_BACK");
            sheet.addCell(label);
            int i = 1;
            for (String str :
                    relate_count_times.keySet()) {
                String[] sub_list = str.split("-");
                int size = sub_list.length;
                if (size <= 1) {
                    continue;
                }
                String str_back = sub_list[size - 1] + "-";
                String str_front = new String();
                for (int t = 0; t < size - 1; t++) {
                    str_front = str_front + sub_list[t] + "-";
                }
                int count_time_front = relate_count_times.get(str_front);
                int count_time_back = relate_count_times.get(str_back);
                int count_time = relate_count_times.get(str);
                double ratio_front = (double)count_time / (double)count_time_front;
                double ratio_back = (double)count_time / (double)count_time_back;
                if (ratio_back >= this.min_belief || ratio_front >= this.min_belief) {
                    label = new Label(0, i, str);
                    sheet.addCell(label);
                    label = new Label(1, i, Double.toString(ratio_front));
                    sheet.addCell(label);
                    label = new Label(2, i, Double.toString(ratio_back));
                    sheet.addCell(label);
                    label = new Label(3, i, String.valueOf(count_time));
                    sheet.addCell(label);
                    label = new Label(4, i, String.valueOf(count_time_front));
                    sheet.addCell(label);
                    label = new Label(5, i, String.valueOf(count_time_back));
                    sheet.addCell(label);
                    i++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PrintRelatedRulesOnlyBySupport() {
        File file= new File("chain_count-3.xls");
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("chain", 0);
            Label label = new Label(0, 0, "TYPE_RELATION");
            sheet.addCell(label);
            label = new Label(1, 0, "COUNT_TIMES");
            sheet.addCell(label);
            int i = 1;
            for (String err :
                    relate_count_times.keySet()) {
                if (relate_count_times.get(err) > this.min_support) {
                    label = new Label(0, i, err);
                    sheet.addCell(label);
                    label = new Label(1, i, String.valueOf(relate_count_times.get(err)));
                    sheet.addCell(label);
                    i++;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (book != null) {
            try {
                book.write();
                book.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
