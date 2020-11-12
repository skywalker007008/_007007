package analysis.cluster;

/*
 * Distance according to different calculations
 */
public class Distance {

    public class DistanceType {

        public static final String DISTANCE_EURCID = "eurcid";

        public static final String DISTANCE_MANHATTAN = "manhattan";

        public static final String DISTANCE_MINKOWSKI = "minkowski";

        private String type;

        private int q_value;

        public DistanceType(String type, int q_value) {
            this.type = type;
            this.q_value = q_value;
        }

        public String GetType() {
            return type;
        }

        public int GetQValue() {
            return q_value;
        }
    }
    /*
     * default: a != b
     */
    public static final double GetDimensionDistanceByFormat(double a, double b, DistanceType type) {
        String str_type = type.GetType();
        if (str_type.equals(DistanceType.DISTANCE_EURCID)) {
            return (a - b) * (a - b);
        } else if (str_type.equals(DistanceType.DISTANCE_MINKOWSKI)) {
            if (a < b) {
                return Math.pow(b - a, type.GetQValue());
            } else {
                return Math.pow(a - b, type.GetQValue());
            }
        } else if (str_type.equals(DistanceType.DISTANCE_MANHATTAN)) {
            if (a < b) {
                return (b - a);
            } else {
                return (a - b);
            }
        } else {
            return 0;
            // SOME ERROR
        }
    }
}


