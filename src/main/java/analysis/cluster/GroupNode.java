package analysis.cluster;

import analysis.WarningGroupData;
import javafx.util.Pair;
import read.WarningFormatData;
import resource.TypeLib;
import torpo.TorpoRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/*
 * Add GroupWarnings into a group node
 */
public class GroupNode {

    private WarningGroupData group_data;

    private Coordinate graph_coordinate;

    private Cluster in_cluster;

    private ArrayList<Integer> level_list;

    public GroupNode() {
        graph_coordinate = new Coordinate();
        level_list = new ArrayList<Integer>();
    }

    public void ReadFromWarningGroupData(WarningGroupData group_data) {
        this.group_data = group_data;
        group_data.FlushOrderByLevelOnRoute(TorpoRoute.POSITIVE_ODD);
        HashMap<Integer, Double> warn_ratio;
        HashMap<Integer, Double> board_ratio;
        HashMap<Integer, Double> pair_ratio;
        warn_ratio = new HashMap<Integer, Double>();
        board_ratio = new HashMap<Integer, Double>();
        pair_ratio = new HashMap<Integer, Double>();
        // To be Implemented
        ArrayList<WarningFormatData> warn_list = group_data.GetFormatDataList();
        for (WarningFormatData data: warn_list) {
            String board_name = data.device_data.board_name;
            String err = data.err_signal.string_type;
            String dev_id = data.device_data.GetLabel();
            Object ob = group_data.GetWarningLevel(dev_id).get(0);
            int level;
            if (ob instanceof Integer) {
                level = (Integer) ob;
            } else {
                Pair<Integer, Integer> pair = (Pair<Integer, Integer>) ob;
                int a = pair.getKey();
                int b = pair.getValue();
                level = a + b;
            }
            level_list.add(level);
            String board_err_name = board_name + "-" + err;
            int board_type = TypeLib.GetBoardTypeInt(board_name);
            int warn_type = TypeLib.GetWarnTypeInt(err);
            int pair_type = TypeLib.GetPairTypeInt(board_err_name);

            if (pair_ratio.containsKey(pair_type)) {
                double a = pair_ratio.get(pair_type);
                pair_ratio.put(pair_type, a + 1);
            } else {
                pair_ratio.put(pair_type, 1.0);
            }

            if (board_ratio.containsKey(board_type)) {
                double a = board_ratio.get(board_type);
                board_ratio.put(board_type, a + 1);
            } else {
                board_ratio.put(board_type, 1.0);
            }

            if (warn_ratio.containsKey(warn_type)) {
                double a = warn_ratio.get(warn_type);
                warn_ratio.put(warn_type, a + 1);
            } else {
                warn_ratio.put(warn_type, 1.0);
            }
        }

        // Analyse avg value
        int total_num = level_list.size();
        Set<Integer> set = board_ratio.keySet();
        for (int i: set) {
            double ratio = board_ratio.get(i) / total_num;
            board_ratio.put(i, ratio);
        }

        set = warn_ratio.keySet();
        for (int i: set) {
            double ratio = warn_ratio.get(i) / total_num;
            warn_ratio.put(i, ratio);
        }

        set = pair_ratio.keySet();
        for (int i: set) {
            double ratio = pair_ratio.get(i) / total_num;
            pair_ratio.put(i, ratio);
        }

        double gap_levels = 0.0;
        int pre_level = level_list.get(0);
        for (int i = 1; i < total_num; i++) {
            int now_level = level_list.get(i);
            if (now_level < pre_level) {
                gap_levels += 1;
            }
            else {
                gap_levels += Math.pow(now_level - pre_level, 2);
            }
        }

        gap_levels /= total_num;

        graph_coordinate.AddWarnRatio(warn_ratio);
        graph_coordinate.AddBoardRatio(board_ratio);
        graph_coordinate.AddPairRatio(pair_ratio);

        graph_coordinate.SetGapLevel(gap_levels);

        warn_ratio.clear();
        board_ratio.clear();
        pair_ratio.clear();
    }

    public Coordinate GetCoordinate() {
        return this.graph_coordinate;
    }

    public void SetCluster(Cluster cluster) {
        this.in_cluster = cluster;
    }

    public double CountDistanceToCluster(Cluster tmp_cluster, Distance.DistanceType dis_type) {
        return Coordinate.GetDistance(tmp_cluster.GetCoordinate(), this.graph_coordinate, dis_type);
    }
}
