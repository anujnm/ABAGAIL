package shared.test;

import shared.DataSet;
import shared.DistanceMeasure;
import shared.EuclideanDistance;
import shared.Instance;
import shared.filt.LinearDiscriminantAnalysis;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import util.linalg.DenseVector;

import java.io.File;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class LinearDiscriminantAnalysisTest {
    
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/krvskp_binary_train.arff");
        // read in the raw data
        DataSet set = dsr.read();
        DataSet set2 = dsr.read();
        System.out.println("Before LDA");
        System.out.println(set);
        LinearDiscriminantAnalysis filter = new LinearDiscriminantAnalysis(set);
        filter.filter(set);
        System.out.println(filter.getProjection());
        System.out.println("After LDA");
        System.out.println(set);
        filter.reverse(set);
        System.out.println("After reconstructing");
        System.out.println(set);

        DistanceMeasure dm = new EuclideanDistance();
        double total = 0.0;
        for(int i = 0; i < set.size(); i++) {
            Instance original = set2.get(i);
            Instance reconstructed = set.get(i);
            total += dm.value(original, reconstructed);
        }

        System.out.println("Reconstruction error: " + total);
        
    }

}
