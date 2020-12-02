package analysis;

import analysis.cluster.methods.k_means.Coefficient;
import analysis.cluster.methods.k_means.FindClusters;
import javafx.util.Pair;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import read.WarningData;
import read.WarningFormatData;
import resource.Device;
import resource.ErrorSignalType;
import resource.MyTime;
import resource.TypeLib;
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

    private TypeLib lib;

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

    public void SetLib(TypeLib lib) {
        this.lib = lib;
    }


    public void AnalysisData() {
        // Step 1: Combine Frequent Warnings
        CombineFrequentWarnings();
        // Step 2: Find Relevant Warnings
        GroupRelevantWarnings();

        // Step 4: (Optional)PrintStack of the warnings relationship
        AnalysisResultPrintOut();
        // Step 3: Use Cluster Methods to find some clusters

        System.out.println("Finding Clusters...");
        Double[][] args = ReadArgs("./args/args_for_model3.xls");
        FindClustersByMethod("K-Means", args);
        System.out.println("Finding Clusters Ending");

    }

    private Double[][] ReadArgs(String path) {
        File file = new File(path);
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows() - 1;
            int columns = sheet.getColumns();
            Double[][] args = new Double[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    String content = sheet.getCell(j, i + 1).getContents();
                    if (content.equals("")) {
                        return args;
                    }
                    args[i][j] = Double.parseDouble(content);
                }
            }
            return args;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void FindClustersByMethod(String method, Double[][] arg_list) {
        FindClusters find_cluster = new FindClusters(lib);
        int i = 0;
        for (WarningGroupData data:
             this.related_warning_data) {
            find_cluster.AddNewGroupData(data, i);
            i++;
        }
        int b_size = arg_list.length;


        for (i = 0; i < b_size; i++) {
            Coefficient.ReadCoef(arg_list[i]);
            find_cluster.SetPath("./result/k_means/model_3/arg_type" + String.valueOf(i) + "/");
            ArrayList<Object> result = new ArrayList<Object>();
            for (int cluster_num = 3; cluster_num < 20; cluster_num++) {

                Object e_list =
                        find_cluster.FindClustersByMethod(method, 10, cluster_num);
                result.add(cluster_num);
                result.add(e_list);
            }
            if (method.equals("K-Means")) {
                File e_file = new File("./result/k_means/model_3/arg_type" + String.valueOf(i) + "/e_value.xls");
                try {
                    e_file.createNewFile();
                    WritableWorkbook book = Workbook.createWorkbook(e_file);
                    WritableSheet sheet = book.createSheet("e_value", 0);
                    for (int t = 0; t < result.size() / 2; t++) {
                        sheet.addCell(new Label(0, t, String.valueOf((Integer)result.get(2 * t))));
                        ArrayList<Double> double_list = (ArrayList<Double>) result.get(2 * t + 1);
                        for (int o = 0; o < double_list.size(); o++) {
                            sheet.addCell(new Label(o + 1, t, String.valueOf(double_list.get(o))));
                        }
                    }
                    book.write();
                    book.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void CombineFrequentWarnings() {
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

    private void GroupRelevantWarnings() {
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

    private void AnalysisResultPrintOut() {
        File analyse_file = new File("Analysis_300Warnings.xls");
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
                data.MakeGroup(sheet, 0);
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

    private void AddGroupWarningsToList(String line_name) {
        ArrayList<WarningFormatData> warn_list = cache_related_warning_data.get(line_name);
        HashMap<String, TorpoDevice> tp_list = cache_related_device_torpo.get(line_name);
        WarningGroupData group_data = new WarningGroupData(line_name, warn_list, tp_list);

        related_warning_data.add(group_data);
        group_data.MakeGroup();
        cache_related_warning_data.remove(line_name);
        cache_related_device_torpo.remove(line_name);

    }

    private boolean TryToCombineFrequentWarnings(WarningFormatData old_data, WarningFormatData new_data) {
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

