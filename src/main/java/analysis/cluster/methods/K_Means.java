package analysis.cluster.methods;

import analysis.cluster.Cluster;
import analysis.cluster.Distance;
import analysis.cluster.GroupNode;

import java.util.ArrayList;

/*
 * Using K-means method to make clusters
 */
public class K_Means {

    private ArrayList<GroupNode> nodes;

    private ArrayList<Cluster> cluster_list;

    private int cluster_num;

    private int iter_time;

    private ArrayList<Double> e_value_history;

    private Distance.DistanceType dis_type;

    public K_Means() {
        nodes = new ArrayList<GroupNode>();
        cluster_list = new ArrayList<Cluster>();
        e_value_history = new ArrayList<Double>();
        Distance dis = new Distance();
        dis_type = dis.new DistanceType(Distance.DistanceType.DISTANCE_EURCID, 0);
    }


    public void AddNodes(ArrayList<GroupNode> nodes) {
        this.nodes.addAll(nodes);
    }

    public void SetClusterNum(int value) {
        this.cluster_num = value;
    }

    public void SetIterTime(int iter_time) {
        this.iter_time = iter_time;
    }

    public ArrayList<Cluster> GetClusters() {
        return cluster_list;
    }

    public void Clusters() {
        // Step 1: Init several clusters at the beginnings
        ArrayList<GroupNode> init_nodes = ChooseInitialNodes();
        int i = 0;
        for (GroupNode node: init_nodes) {
            Cluster cluster = new Cluster(i);
            i++;
            cluster.SetCoordinate(node.GetCoordinate());
            //node.SetCluster(cluster);
        }

        // Step 2: According to the iter_times to make clusters
        for (i = 0; i < iter_time; i++) {
            KMeansIterate();
        }

    }

    private void KMeansIterate() {
        // Step 1: Clear all nodes in cluster
        for (Cluster cluster: cluster_list) {
            cluster.ClearNodes();
        }
        // Step 2: Add new relationships between nodes and clusters
        for (GroupNode node: nodes) {
            double distance = -1;
            Cluster cluster = null;
            for (Cluster tmp_cluster: cluster_list) {
                double tmp_distance = node.CountDistanceToCluster(tmp_cluster, dis_type);
                if (distance == -1 || tmp_distance < distance) {
                    distance = tmp_distance;
                    cluster = tmp_cluster;
                }
            }
            boolean error = cluster.AddNodes(node);
            if (!error) {
                // Error here
                System.out.println("Repeat Add node");
            }
            node.SetCluster(cluster);
        }

        // Step 3: Update Clusters results
        double e_value = 0;
        for (Cluster cluster: cluster_list) {
            cluster.UpdateCoordinate();
            e_value += cluster.GetEValue();
        }
        e_value_history.add(e_value);
    }

    private ArrayList<GroupNode> ChooseInitialNodes() {
        ArrayList<GroupNode> node_list = new ArrayList<GroupNode>();
        int size = nodes.size();
        int iter = size / (cluster_num - 1);
        if (cluster_num == 1) {
            GroupNode node = nodes.get(0);
            node_list.add(node);
            return node_list;
        }
        for (int i = 0; i < cluster_num; i++) {
            GroupNode node = nodes.get(i * iter);
            node_list.add(node);
        }
        return node_list;
    }


}
