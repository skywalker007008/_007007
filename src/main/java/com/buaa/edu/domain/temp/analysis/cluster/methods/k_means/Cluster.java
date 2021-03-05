package com.buaa.edu.domain.temp.analysis.cluster.methods.k_means;

import jxl.write.WritableSheet;

import java.util.ArrayList;

/*
 * The cluster that we find
 */
public class Cluster {

    private ArrayList<GroupNode> nodes;

    private Coordinate coordinate;

    private double e_value;

    private Long cluster_id;

    public Cluster(Long id) {
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

    public Long getCluster_id() {
        return cluster_id;
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

    public Long GetClusterId() {
        return this.cluster_id;
    }

    public void PrintResult(WritableSheet sheet, boolean with_result) {
        int i = 0;
        for (GroupNode node: nodes) {
            int start_line = node.PrintResult(sheet, i, with_result);
            i = start_line + 2;
        }
    }

    public ArrayList<GroupNode> getNodes() {
        return nodes;
    }

    public void setId(Long id) {
        this.cluster_id = id;
    }
}
