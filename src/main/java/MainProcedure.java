import analysis.Analysis;
import read.WarningData;
import torpo.TorpoData;

import warn_relation.WarningRuleFinding;
public class MainProcedure {
    public static void main(String args[]) {

        System.out.println("Excel Data Loading...");
        WarningData excel_data = new WarningData(1000);
        excel_data.ReadExcelDataNew("C://Users//Administrator//IdeaProjects//_007007/test_part.xls");
        //excel_data.ReadExcelDataNew("C://Users//Administrator//IdeaProjects//_007007/test_new.xls");
        System.out.println("Excel Data Loaded Success!");
        // Test Almost Finished

        System.out.println("Torpo Information Loading...");
        TorpoData torpo_data = new TorpoData();
        torpo_data.ReadTorpoInfo("C://Users//Administrator//IdeaProjects//_007007/torpo.xls");
        System.out.println("Torpo Information Loaded Success!");
        // Tested pass, but unable to visualize

        System.out.println("Analysing...");
        Analysis analyse = new Analysis(excel_data, torpo_data);
        analyse.AnalysisData();
        System.out.println("Analyse finished!");

        System.out.println("Finding Chain Message...");
        WarningRuleFinding rule_find = new WarningRuleFinding(analyse);
        rule_find.SetArgsForFinding(5, 0.5);

        rule_find.FindRulesFromOriginData();
        System.out.println("Finding finished!");

    }

}
