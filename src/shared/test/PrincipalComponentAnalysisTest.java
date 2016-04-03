package shared.test;

import shared.DataSet;
import shared.DistanceMeasure;
import shared.EuclideanDistance;
import shared.Instance;
import shared.filt.PrincipalComponentAnalysis;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import util.linalg.Matrix;

import java.io.File;
import java.io.PrintWriter;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class PrincipalComponentAnalysisTest {
    
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/sick_replace_missing.arff");
        // read in the raw data
        DataSet set = dsr.read();
        DataSet set2 = dsr.read();
        System.out.println("Before PCA");
        System.out.println(set);
        PrincipalComponentAnalysis filter = new PrincipalComponentAnalysis(set);
        System.out.println(filter.getEigenValues());
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);
        System.out.println("After PCA");
        System.out.println(set);

        PrintWriter writer = new PrintWriter("sick_PCA_" + System.currentTimeMillis() + ".csv", "UTF-8");
        for (int i = 0; i < set.size(); i ++) {
            Instance instance = set.get(i);
            Double output = set2.get(i).getData().get(33);
            String str_out = "negative";
            if (output > 0.5) {
                str_out = "sick";
            }
            writer.println(instance.getData().toString() + ',' + str_out);
        }
        writer.close();
    }

}
