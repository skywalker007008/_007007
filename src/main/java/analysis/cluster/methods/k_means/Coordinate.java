package analysis.cluster.methods.k_means;

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

    public Coordinate(Coordinate coordinate) {
        warn_type_ratio =
                new HashMap<Integer, Double>(
                        coordinate.GetWarnTypeRatioMap());
        pair_type_ratio =
                new HashMap<Integer, Double>(
                        coordinate.GetPairTypeRatioMap());
        board_type_ratio =
                new HashMap<Integer, Double>(
                        coordinate.GetBoardTypeRatioMap());
        level_gap = coordinate.GetLevelGap();

    }

    public Double GetLevelGap() {
        return this.level_gap;
    }

    public HashMap<Integer, Double> GetWarnTypeRatioMap() {
        return this.warn_type_ratio;
    }

    public HashMap<Integer, Double> GetPairTypeRatioMap() {
        return this.pair_type_ratio;
    }

    public HashMap<Integer, Double> GetBoardTypeRatioMap() {
        return this.board_type_ratio;
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

    public static final double GetDistance(Coordinate cod_a, Coordinate cod_b, Distance.DistanceType dis_type) {

        ArrayList<Set<Integer>> list_a = cod_a.GetTypeSet();
        ArrayList<Set<Integer>> list_b = cod_b.GetTypeSet();
        double distance = 0.0;

        for (int i = 0; i < list_a.size(); i++) {
            double tmp_distance = 0.0;
            Set<Integer> set_a = list_a.get(i);
            Set<Integer> tmp_set_b = list_b.get(i);
            HashSet<Integer> set_b = new HashSet<Integer>(tmp_set_b);
            for (int type: set_a) {
                double value_a = cod_a.GetTypeRatio(i, type);
                double value_b = cod_b.GetTypeRatio(i, type);
                tmp_distance += Distance.GetDimensionDistanceByFormat(value_a, value_b, dis_type);
                if (value_b != 0.0) {
                    set_b.remove(type);
                }
            }
            for (int type: set_b) {
                double value_b = cod_b.GetTypeRatio(i, type);
                tmp_distance += Distance.GetDimensionDistanceByFormat(0, value_b, dis_type);
            }
            distance += (tmp_distance * GetCoef(i));
        }

        double level_a = cod_a.GetLevelGap();
        double level_b = cod_b.GetLevelGap();
        double value = Distance.
                GetDimensionDistanceByFormat(level_a, level_b, dis_type);
        distance += value * Coefficient.COEF_LEVEL;

        return distance;
    }

    public double GetTypeRatio(int i, int type) {
        switch (i) {
            case 0:
                return this.GetWarnTypeRatio(type);
            case 1:
                return this.GetBoardTypeRatio(type);
            case 2:
                return this.GetPairTypeRatio(type);
            default:
                return -1;
        }
    }

    public void AddWarnRatio(HashMap<Integer, Double> warn_ratio) {
        this.warn_type_ratio.putAll(warn_ratio);
    }

    public void AddBoardRatio(HashMap<Integer, Double> board_ratio) {
        this.board_type_ratio.putAll(board_ratio);
    }

    public void AddPairRatio(HashMap<Integer, Double> pair_ratio) {
        this.pair_type_ratio.putAll(pair_ratio);
    }

    public void AddPairRatio(int type, double ratio) {
        this.pair_type_ratio.put(type, ratio);
    }

    public void AddBoardRatio(int type, double ratio) {
        this.board_type_ratio.put(type, ratio);
    }

    public void AddWarnRatio(int type, double ratio) {
        this.warn_type_ratio.put(type, ratio);
    }

    public void SetGapLevel(double gap_levels) {
        level_gap = gap_levels;
    }

    public static final double GetCoef(int i) {
        /*
        switch (i) {
            case 0:
                return Coefficient.COEF_FOR_WARNS;
            case 1:
                return Coefficient.COEF_FOR_BOARDS;
            case 2:
                return Coefficient.COEF_FOR_PAIRS;
            case 3:
                return Coefficient.COEF_FOR_LEVELS;

            default:
                return 0;
        }
        */

        return Coefficient.GetCoef(i);
    }

    public void ClearCoordinate() {
        this.warn_type_ratio.clear();
        this.pair_type_ratio.clear();
        this.board_type_ratio.clear();
        this.level_gap = 0.0;
    }

    public void AddCoordinate(Coordinate cord) {
        HashMap<Integer, Double> cord_warn_ratio = cord.GetWarnTypeRatioMap();
        HashMap<Integer, Double> cord_board_ratio = cord.GetBoardTypeRatioMap();
        HashMap<Integer, Double> cord_pair_ratio = cord.GetPairTypeRatioMap();

        for (int type: cord_warn_ratio.keySet()) {
            double cord_value = cord_warn_ratio.get(type);
            if (this.warn_type_ratio.containsKey(type)) {
                double prev_value = warn_type_ratio.get(type);
                warn_type_ratio.put(type, prev_value + cord_value);
            } else {
                warn_type_ratio.put(type, cord_value);
            }
        }

        for (int type: cord_board_ratio.keySet()) {
            double cord_value = cord_board_ratio.get(type);
            if (this.board_type_ratio.containsKey(type)) {
                double prev_value = board_type_ratio.get(type);
                board_type_ratio.put(type, prev_value + cord_value);
            } else {
                board_type_ratio.put(type, cord_value);
            }
        }

        for (int type: cord_pair_ratio.keySet()) {
            double cord_value = cord_pair_ratio.get(type);
            if (this.pair_type_ratio.containsKey(type)) {
                double prev_value = pair_type_ratio.get(type);
                pair_type_ratio.put(type, prev_value + cord_value);
            } else {
                pair_type_ratio.put(type, cord_value);
            }
        }
        this.level_gap += cord.GetLevelGap();
    }

    public void DivideConstant(int num) {

        for (int type: warn_type_ratio.keySet()) {
            double value = warn_type_ratio.get(type);
            warn_type_ratio.put(type, value / num);
        }

        for (int type: pair_type_ratio.keySet()) {
            double value = pair_type_ratio.get(type);
            pair_type_ratio.put(type, value / num);
        }

        for (int type: board_type_ratio.keySet()) {
            double value = board_type_ratio.get(type);
            board_type_ratio.put(type, value / num);
        }

        this.level_gap /= num;

    }
}
