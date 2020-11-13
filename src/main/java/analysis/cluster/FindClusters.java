package analysis.cluster;

import analysis.WarningGroupData;
import analysis.cluster.methods.K_Means;
import resource.TypeLib;

import java.util.ArrayList;

/*
 * main procedure to find each cluster
 */
public class FindClusters {

    private ArrayList<Cluster> cluster_list;

    private ArrayList<GroupNode> nodes;

    public FindClusters() {
        cluster_list = new ArrayList<Cluster>();
        nodes = new ArrayList<GroupNode>();
    }

    public void AddNewGroupData(WarningGroupData data) {
        GroupNode node = new GroupNode();
        node.ReadFromWarningGroupData(data);
        nodes.add(node);
    }

    public void FindClustersByMethod(String method, int iter_time, int value) {
        // Now K-means
        if (method.equals("K-means")) {
            K_Means k_means = new K_Means();
            k_means.AddNodes(nodes);
            k_means.SetClusterNum(value);
            k_means.SetIterTime(iter_time);
            k_means.Clusters();
            ArrayList<Cluster> list = k_means.GetClusters();
            cluster_list.clear();
            cluster_list.addAll(list);
        }
    }
}
