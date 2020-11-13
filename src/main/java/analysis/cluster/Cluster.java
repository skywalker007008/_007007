package analysis.cluster;

import java.util.ArrayList;

/*
 * The cluster that we find
 */
public class Cluster {

    private ArrayList<GroupNode> nodes;

    private Coordinate coordinate;

    private double e_value;

    private int cluster_id;

    public Cluster(int id) {
        this.cluster_id = id;
        nodes = new ArrayList<GroupNode>();
    }

    public boolean AddNodes(GroupNode node) {
        if (nodes.contains(node)) {
            return false;
        } else {
            nodes.add(node);
            return true;
        }
    }

    public void SetCoordinate(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate);
    }


    public Coordinate GetCoordinate() {
        return this.coordinate;
    }

    public void ClearNodes() {
        nodes.clear();
    }

    public void UpdateCoordinate() {
        // To be implemented
    }

    public double GetEValue() {
        return this.e_value;
    }
}
