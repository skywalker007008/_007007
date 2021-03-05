package com.buaa.edu.domain.temp.analysis.cluster.methods.k_means;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
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
        long t = 0;
        for (GroupNode node: init_nodes) {
            Cluster cluster = new Cluster(t);
            t++;
            cluster.SetCoordinate(node.GetCoordinate());
            cluster_list.add(cluster);
            //node.SetCluster(cluster);
        }

        // Step 2: According to the iter_times to make clusters
        for (int i = 0; i < iter_time; i++) {
            KMeansIterate();
            if (i > 0 && (e_value_history.get(i).equals(e_value_history.get(i - 1)))) {
                break;
            }
        }

        //this.PrintResult();

    }

    private void PrintArgResult(WritableSheet sheet) {
        try {
            sheet.addCell(new Label(0, 0, "COEF_FOR_WARNS"));
            sheet.addCell(new Label(0, 1, "COEF_FOR_TYPES"));
            sheet.addCell(new Label(0, 2, "COEF_FOR_PAIRS"));
            sheet.addCell(new Label(0, 3, "COEF_FOR_LEVELS"));
            sheet.addCell(new Label(1, 0, String.valueOf(Coordinate.GetCoef(0))));
            sheet.addCell(new Label(1, 1, String.valueOf(Coordinate.GetCoef(1))));
            sheet.addCell(new Label(1, 2, String.valueOf(Coordinate.GetCoef(2))));
            sheet.addCell(new Label(1, 3, String.valueOf(Coordinate.GetCoef(3))));
            // Add model-2
            sheet.addCell(new Label(0, 4, "COEF_FOR_MANY_TYPE"));
            sheet.addCell(new Label(1, 4, String.valueOf(Coordinate.GetCoef(4))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PrintArgs(String path) {
        File arg_file = new File(path + "args.xls");
        try {
            if (arg_file.createNewFile()) {
                WritableWorkbook book = Workbook.createWorkbook(arg_file);
                WritableSheet sheet = book.createSheet("args", 0);
                PrintArgResult(sheet);
                book.write();
                book.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PrintResult(String path) {
        File tmp_file = new File(path);
        if (!tmp_file.exists()) {
            tmp_file.mkdir();
            this.PrintArgs(path);
        }
        File k_means_file = new File(path +
                "k_means(cluster_" + this.cluster_num + ")_result.xls");
        try {
            k_means_file.createNewFile();
            WritableWorkbook workbook = Workbook.createWorkbook(k_means_file);
            int i = 0;
            for (Cluster cluster : cluster_list) {
                WritableSheet sheet = workbook.createSheet("cluster-" + String.valueOf(i), i);
                i++;
                cluster.PrintResult(sheet, false);
            }
            WritableSheet sheet = workbook.createSheet("value_history", cluster_num);
            i = 0;
            for (double e_value: e_value_history) {
                sheet.addCell(new Label(0, i, String.valueOf(e_value)));
                i++;
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PrintIterate() {
        for (GroupNode node: nodes) {
            System.out.printf("%d ", node.GetInCluster().GetClusterId());
        }
        System.out.println();

        System.out.println("E_Value: " + e_value_history.get(e_value_history.size() - 1));
    }

    private void KMeansIterate() {
        // Step 1: Clear previous ones
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
            cluster.UpdateEValue(dis_type);
            e_value += cluster.GetEValue();
        }
        e_value_history.add(e_value);

        // Step 4: Print Out

        this.PrintIterate();


        for (Cluster cluster: cluster_list) {
            cluster.UpdateCoordinate();
        }

    }

    private ArrayList<GroupNode> ChooseInitialNodes() {
        ArrayList<GroupNode> node_list = new ArrayList<GroupNode>();
        int size = nodes.size();
        int iter = size / (cluster_num + 1);
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

    public ArrayList<Double> GetEValues() {
        return this.e_value_history;
    }

/*
    public void PrintEValue(String path) {
        File file = new File(path);
        try {
            boolean answer = file.createNewFile();
            if (answer) {
                WritableWorkbook book = Workbook.createWorkbook(file);
                WritableSheet sheet = book.createSheet("e_value", 0);
                sheet.addCell(new Label(0, 0, "CLUSTER_NUM"));
                sheet.addCell(new Label(1, 0, "E_VALUE_HISTORY"));
                sheet.addCell(new Label(0, 1, String.valueOf(this.cluster_num)));
                for (int i = 0; i < e_value_history.size(); i++) {
                    sheet.addCell(new Label(i + 1, 1, String.valueOf(e_value_history.get(i))));
                }
                book.write();
                book.close();
            } else {
                //WritableWorkbook book = Workbook.getWorkbook(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */
}
