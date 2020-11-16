package analysis.cluster;

import java.util.ArrayList;

public class Coefficient {
    public static double COEF_WARN;
    public static double COEF_BOARD;
    public static double COEF_PAIR;
    public static double COEF_LEVEL;

    public static void ReadCoef(ArrayList<Double> list) {
        COEF_WARN = list.get(0);
        COEF_BOARD = list.get(1);
        COEF_PAIR = list.get(2);
        COEF_LEVEL = list.get(3);
    }

    public static double GetCoef(int i) {
        switch (i) {
            case 0:
                return COEF_WARN;
            case 1:
                return COEF_BOARD;
            case 2:
                return COEF_PAIR;
            case 3:
                return COEF_LEVEL;

            default:
                return 0;
        }
    }

}
