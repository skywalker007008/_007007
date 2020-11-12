package analysis.cluster;

import java.util.ArrayList;

/*
 * The cluster that we find
 */
public class Cluster {

    private ArrayList<GroupNode> nodes;

    private int cluster_id;

    private Cluster(int id) {
        this.cluster_id = id;
    }
}
