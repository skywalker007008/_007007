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

    private TypeLib lib;

    public FindClusters(TypeLib lib) {
        this.lib = lib;
    }

    public void AddNewGroupData(WarningGroupData data) {
        GroupNode node = new GroupNode();
        node.ReadFromWarningGroupData(data);
    }

    public void FindClustersByMeans(String method, int iter_time, int value) {
        // Now K-means
        if (method.equals("K-means")) {
            K_Means k_means = new K_Means();
            k_means.AddNodes(nodes);
            k_means.SetClusterNum(value);
            k_means.SetIterTime(iter_time);
            k_means.SetReferenceLib(lib);
            ArrayList<Cluster> list = k_means.Clusters();
            cluster_list.clear();
            cluster_list.addAll(list);
        }
    }
}
