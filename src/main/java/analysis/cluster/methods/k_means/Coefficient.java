package analysis.cluster.methods.k_means;

import java.util.ArrayList;

public class Coefficient {
    public static double COEF_WARN;
    public static double COEF_BOARD;
    public static double COEF_PAIR;
    public static double COEF_LEVEL;

    public static void ReadCoef(Double[] list) {
        COEF_WARN = list[0];
        COEF_BOARD = list[1];
        COEF_PAIR = list[2];
        COEF_LEVEL = list[3];
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
