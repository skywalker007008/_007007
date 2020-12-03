package analysis.cluster.methods.k_means;

import analysis.WarningGroupData;
import resource.TypeLib;

import java.util.ArrayList;

/*
 * main procedure to find each cluster
 */
public class FindClusters {

    private ArrayList<Cluster> cluster_list;

    private ArrayList<GroupNode> nodes;

    private TypeLib lib;

    private String path;

    public FindClusters(TypeLib lib) {
        cluster_list = new ArrayList<Cluster>();
        nodes = new ArrayList<GroupNode>();
        this.lib = lib;
    }

    public ArrayList<Coordinate> GetAllClusterCoordinate() {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (Cluster cluster: cluster_list) {
            coordinates.add(cluster.GetCoordinate());
        }
        return coordinates;
    }

    public void AddNewGroupData(WarningGroupData data, int id) {
        GroupNode node = new GroupNode(id);
        node.ReadFromWarningGroupData(data, lib);
        nodes.add(node);
    }

    public Object FindClustersByMethod(String method, int iter_time, int value) {
        // Now K-means
        if (method.equals("K-Means")) {
            System.out.println("K-means start. Value: " + value);
            K_Means k_means = new K_Means();
            k_means.AddNodes(nodes);
            k_means.SetClusterNum(value);
            k_means.SetIterTime(iter_time);
            k_means.Clusters();
            k_means.PrintResult(path);
            ArrayList<Cluster> list = k_means.GetClusters();
            cluster_list.clear();
            cluster_list.addAll(list);
            System.out.println("K-means end. Value: " + value);
            return k_means.GetEValues();
        } else {
            return null;
        }
    }

    public void PrintResult() {

    }

    public void SetPath(String s) {
        path = s;
    }
}
