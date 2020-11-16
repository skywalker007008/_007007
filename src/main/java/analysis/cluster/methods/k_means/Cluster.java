package analysis.cluster.methods.k_means;

import jxl.write.WritableSheet;

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
        this.e_value = 0;
    }

    public boolean UpdateCoordinate() {
        // To be implemented


        if (nodes.isEmpty()) {
            return false;
        } else {
            this.coordinate.ClearCoordinate();
        }
        for (GroupNode node: this.nodes) {
            Coordinate cord = node.GetCoordinate();
            this.coordinate.AddCoordinate(cord);
        }
        this.coordinate.DivideConstant(nodes.size());
        return true;
    }

    public void UpdateEValue(Distance.DistanceType type) {
        for (GroupNode node: this.nodes) {
            Coordinate cord = node.GetCoordinate();
            double distance = Coordinate.GetDistance(this.coordinate, cord, type);
            e_value += (distance * distance);
        }
    }

    public double GetEValue() {
        return this.e_value;
    }

    public int GetClusterId() {
        return this.cluster_id;
    }

    public void PrintResult(WritableSheet sheet) {
        int i = 0;
        for (GroupNode node: nodes) {
            int start_line = node.PrintResult(sheet, i);
            i = start_line + 2;
        }
    }
}
