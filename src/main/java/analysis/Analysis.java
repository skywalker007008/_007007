package analysis;

import javafx.util.Pair;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import read.WarningData;
import read.WarningFormatData;
import read.resource.Device;
import read.resource.ErrorSignalType;
import read.resource.MyTime;
import torpo.TorpoData;
import torpo.TorpoDevice;
import torpo.TorpoRoute;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Analysis {
    // Volume: Analysed WarningGroupData
    public WarningData origin_data;
    public TorpoData torpo_data;

    // Constant
    // Frequency Warning Time Limit
    public static final int FREQUENT_WARNINGS_TIME_LIMIT = 1800;
    // Related Warning Time Limit
    public static final int RELATED_WARNINGS_TIME_LIMIT = 450;

    // Cache of Frequent Warnings:map
    // TODOLIST: Modify Device into its only -label
    private HashMap<Pair<String, Integer>, WarningFormatData> cache_frequent_warning_data;
    // Data expected to handle of group warnings
    public ArrayList<WarningFormatData> group_warning_data;
    // Data handled of frequent warnings
    public ArrayList<WarningGroupData> frequent_warning_data;

    // Data Struture of Related warnings
    // Map of the Related WarningGroupData
    public ArrayList<WarningGroupData> related_warning_data;
    // Map of the cached WarningGroupData
    private HashMap<String, ArrayList<WarningFormatData>> cache_related_warning_data;
    private HashMap<String, HashMap<String, TorpoDevice>> cache_related_device_torpo;

    public Analysis(){


    }

    public Analysis(WarningData excel_data, TorpoData torpo_data) {
        cache_frequent_warning_data = new HashMap<Pair<String, Integer>, WarningFormatData>();
        group_warning_data = new ArrayList<WarningFormatData>();
        frequent_warning_data = new ArrayList<WarningGroupData>();
        related_warning_data = new ArrayList<WarningGroupData>();
        cache_related_warning_data = new HashMap<String, ArrayList<WarningFormatData>>();
        cache_related_device_torpo = new HashMap<String, HashMap<String, TorpoDevice>>();
        this.origin_data = excel_data;
        this.torpo_data = torpo_data;
    }


    public void AnalysisData() {
        // Step 1: Combine Frequent Warnings
        CombineFrequentWarnings();
        // Step 2: Find Relevant Warnings
        GroupRelevantWarnings();
        // Step 3: Add uncached warnings together
        RejoinCachedWarnings();
        // Step 4: (Optional)PrintStack of the warnings relationship
        AnalysisResultPrintOut();
    }

    public void CombineFrequentWarnings() {
        int max_volume = this.origin_data.data_list.size();
        try {
            // Step 1: Iterator each warning data
            for (int i = 0; i < max_volume; i++) {
                WarningFormatData warn_data = origin_data.data_list.get(i);
                Device dev = warn_data.device_data;
                ErrorSignalType type = warn_data.err_signal;

                Pair<String, Integer> tmp_pair = new Pair<String, Integer>(dev.GetLabel(), type.value_type);
                if (cache_frequent_warning_data.containsKey(tmp_pair)) {
                    // Already Exist such warnings, Judge whether able to combine
                    WarningFormatData old_data = cache_frequent_warning_data.get(tmp_pair);
                    boolean is_renew = TryToCombineFrequentWarnings(old_data, warn_data);
                    if (is_renew) {
                        // Renew, so delete this warning
                       //
                        // i--;
                    } else if (old_data.combine_time > 1){
                        // Not renew, make this old cache into the list, and add the new one.
                        cache_frequent_warning_data.remove(tmp_pair);
                        WarningGroupData group_data =
                                new WarningGroupData(old_data);
                        frequent_warning_data.add(group_data);
                        cache_frequent_warning_data.put(tmp_pair, warn_data);
                        group_warning_data.add(warn_data);
                        // Whether leave out the frequent warnings
                        // group_warning_data.remove(old_data);
                    } else {
                        cache_frequent_warning_data.put(tmp_pair, warn_data);
                        group_warning_data.add(warn_data);
                    }
                } else {
                    // Completely new
                    cache_frequent_warning_data.put(tmp_pair, warn_data);
                    group_warning_data.add(warn_data);
                }

                // System.out.println("Finish" + i);
            }
            // PrintOut
            BufferedWriter text = new BufferedWriter(new FileWriter(new File("result.csv")));
            text.newLine();
            text.write("Device-id, Errortype, Start-time, End-Time, Repeat Times");
            for (WarningGroupData data : frequent_warning_data
            ) {
                text.newLine();
                data.PrintOutFrequentMsgCsv(text);
            }
            text.flush();
            text.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Finish, clear the cache
        cache_frequent_warning_data.clear();
    }

    public void GroupRelevantWarnings() {
        // Step 1: Find warning data from left warnings
        for (WarningFormatData warn_data:
             group_warning_data) {
            // Step 2: First Group line with time limit exceeded
            MyTime now_happen_time = warn_data.happen_time;
            HashSet<String> set = new HashSet<String>();
            set.addAll(cache_related_warning_data.keySet());
            for (String str:
                 set) {
                ArrayList<WarningFormatData> list = cache_related_warning_data.get(str);
                if (list.size() >= 1) {
                    MyTime first_time = list.get(0).happen_time;
                    if (now_happen_time.sub(first_time) > Analysis.RELATED_WARNINGS_TIME_LIMIT) {
                        AddGroupWarningsToList(str);
                    }
                }
            }
            // Step 3: Find routes dev in
            boolean is_find = false;
            for (String route_name:
                 this.torpo_data.route_map.keySet()) {
                TorpoRoute route = torpo_data.GetRouteByName(route_name);
                String lab = warn_data.device_data.GetLabel();
                if (route.IsRouteContainsDevice(warn_data.device_data.GetLabel())) {
                    is_find = true;
                    TorpoDevice torpo_dev = route.GetTorpoDeviceByLabel(warn_data.device_data.GetLabel());
                    // Step 4: Find whether this line has existed
                    if (this.cache_related_warning_data.containsKey(route_name)) {
                        cache_related_warning_data.get(route_name).add(warn_data);
                        cache_related_device_torpo.get(route_name).put(lab, torpo_dev);
                    }
                    else {
                        ArrayList<WarningFormatData> ar_list = new ArrayList<WarningFormatData>();
                        HashMap<String, TorpoDevice> tp_list = new HashMap<String, TorpoDevice>();
                        ar_list.add(warn_data);
                        tp_list.put(lab, torpo_dev);
                        cache_related_warning_data.put(route_name, ar_list);
                        cache_related_device_torpo.put(route_name, tp_list);
                    }
                }
                // else : no handling
            }
            if (!is_find) {
                System.out.println("Unfound PortId: " + warn_data.device_data.GetLabel());
            }
        }
        // Step 4: Add left into
        HashSet<String> set = new HashSet<String>(cache_related_warning_data.keySet());
        for (String str:
             set) {
            AddGroupWarningsToList(str);
        }
    }

    public void RejoinCachedWarnings() {

    }

    public void AnalysisResultPrintOut() {
        File analyse_file = new File("Analysis_NEW450.xls");
        try {
            analyse_file.createNewFile();
            WritableWorkbook workbook = Workbook.createWorkbook(analyse_file);
            int i = 0;
            for (WarningGroupData data:
                 this.related_warning_data) {
                WritableSheet sheet = workbook.createSheet("ErrorInfo-" + String.valueOf(i), i);
                i = i + 1;
                data.SetSheetForTorpoVisualize(sheet);
                //data.BuildRelatedWarningsTorpo(true);
                data.MakeGroup(sheet);
                System.out.println("Finish Analysis" + i);
            }
            if (related_warning_data.size() > 0) {
                workbook.write();
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void AddGroupWarningsToList(String line_name) {
        ArrayList<WarningFormatData> warn_list = cache_related_warning_data.get(line_name);
        HashMap<String, TorpoDevice> tp_list = cache_related_device_torpo.get(line_name);
        WarningGroupData group_data = new WarningGroupData(line_name, warn_list, tp_list);

        related_warning_data.add(group_data);
        group_data.MakeGroup();
        cache_related_warning_data.remove(line_name);
        cache_related_device_torpo.remove(line_name);

    }

    public boolean TryToCombineFrequentWarnings(WarningFormatData old_data, WarningFormatData new_data) {
        long timeval = new_data.happen_time.sub(old_data.handle_time);
        if (timeval < Analysis.FREQUENT_WARNINGS_TIME_LIMIT) {
            old_data.handle_time = new_data.handle_time;
            old_data.combine_time++;
            return true;

        } else {
            return false;
        }
    }


}

