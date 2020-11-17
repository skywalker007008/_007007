package analysis.cluster.methods.k_means;

import java.util.ArrayList;

public class Coefficient {

    public static double COEF_WARN;
    public static double COEF_BOARD;
    public static double COEF_PAIR;
    public static double COEF_LEVEL;
    // Add for model_2
    public static double COEF_MANY_TYPE;

    public static void ReadCoef(Double[] list) {
        COEF_WARN = list[0];
        COEF_BOARD = list[1];
        COEF_PAIR = list[2];
        COEF_LEVEL = list[3];
        // Add for model_2
        COEF_MANY_TYPE = list[4];
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
            // Add for model_2
            case 4:
                return COEF_MANY_TYPE;

            default:
                return 0;
        }
    }

}
