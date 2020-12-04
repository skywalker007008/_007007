import analysis.Analysis;
import analysis.cluster.methods.k_means.Cluster;
import analysis.cluster.methods.k_means.FindClusters;
import read.WarningData;
import resource.TypeLib;
import torpo.TorpoData;

import java.util.ArrayList;

public class MainProcedure {
    public static void main(String args[]) {

        System.out.println("Torpo Information Loading...");
        TorpoData torpo_data = new TorpoData();
        torpo_data.ReadTorpoInfo("/home/skywalker/桌面/G410/_007007/resources/torpo_new.xls");
        System.out.println("Torpo Information Loaded Success!");
        // Tested pass, but unable to visualize

        System.out.println("Excel Data Loading...");
        WarningData training_data = new WarningData(1000);
        training_data.ReadExcelDataNew("/home/skywalker/桌面/G410/_007007/resources/test_part.xls", false);
        WarningData proof_data = new WarningData(1000);
        proof_data.ReadExcelDataNew("/home/skywalker/桌面/G410/_007007/resources/test_second2.xls", false);
        //excel_data.ReadExcelDataNew("C://Users//Administrator//IdeaProjects//_007007/test_new.xls");
        System.out.println("Excel Data Loaded Success!");
        // Test Almost Finished

        TypeLib lib = new TypeLib();

        System.out.println("Analysing...");
        Analysis analyse = new Analysis(training_data, torpo_data);
        analyse.SetLib(lib);
        analyse.AnalysisData();

        System.out.println("Finding Clusters...");
        Double[][] arg = analyse.ReadArgs("./args/args_for_model3.xls");
        FindClusters clusters = analyse.FindClustersByMethod("K-Means", arg);
        System.out.println("Finding Clusters Ending");

        Analysis proof_analyse = new Analysis(proof_data, torpo_data);
        proof_analyse.SetLib(lib);
        proof_analyse.AnalysisData();
        proof_analyse.MatchCluster(clusters);
        System.out.println("Analyse finished!");
/*
        System.out.println("Finding Chain Message...");
        WarningRuleFinding rule_find = new WarningRuleFinding(analyse);
        rule_find.SetArgsForFinding(5, 0.5);

        rule_find.FindRulesFromOriginData();
        System.out.println("Finding finished!");
*/


    }

}
