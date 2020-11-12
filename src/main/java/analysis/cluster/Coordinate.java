package analysis.cluster;

import java.util.*;

/*
 * The coordinate of the certain Cluster
 */
public class Coordinate {

    private HashMap<Integer, Double> warn_type_ratio;

    private HashMap<Integer, Double> board_type_ratio;

    private HashMap<Integer, Double> pair_type_ratio;

    private Double level_gap;

    public Coordinate() {
        warn_type_ratio = new HashMap<Integer, Double>();
        board_type_ratio = new HashMap<Integer, Double>();
        pair_type_ratio = new HashMap<Integer, Double>();
        level_gap = 0.0;
    }

    public boolean SetNewWarnTypeRatio(int type, double value) {
        if (!warn_type_ratio.containsKey(type)) {
            return false;
        } else {
            warn_type_ratio.put(type, value);
            return true;
        }
    }

    public boolean SetNewBoardTypeRatio(int type, double value) {
        if (!board_type_ratio.containsKey(type)) {
            return false;
        } else {
            board_type_ratio.put(type, value);
            return true;
        }
    }

    public boolean SetNewPairTypeRatio(int type, double value) {
        if (!pair_type_ratio.containsKey(type)) {
            return false;
        } else {
            pair_type_ratio.put(type, value);
            return true;
        }
    }

    public double GetWarnTypeRatio(int type) {
        if (!warn_type_ratio.containsKey(type)) {
            return 0;
        } else {
            return warn_type_ratio.get(type);
        }
    }

    public double GetBoardTypeRatio(int type) {
        if (!board_type_ratio.containsKey(type)) {
            return 0;
        } else {
            return board_type_ratio.get(type);
        }
    }

    public double GetPairTypeRatio(int type) {
        if (!pair_type_ratio.containsKey(type)) {
            return 0;
        } else {
            return pair_type_ratio.get(type);
        }
    }

    public ArrayList<Set<Integer>> GetTypeSet() {
        ArrayList<Set<Integer>> list = new ArrayList<Set<Integer>>();
        list.add(warn_type_ratio.keySet());
        list.add(board_type_ratio.keySet());
        list.add(pair_type_ratio.keySet());
        return list;
    }

    public static final double GetDistance(Coordinate cod_a, Coordinate cod_b) {
        ArrayList<Set<Integer>> list_a = cod_a.GetTypeSet();
        ArrayList<Set<Integer>> list_b = cod_b.GetTypeSet();

        return 0;
    }


}
