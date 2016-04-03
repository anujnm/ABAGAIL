package func.test;

import dist.Distribution;
import dist.MultivariateGaussian;
import func.EMClusterer;
import shared.DataSet;
import shared.Instance;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import util.linalg.DenseVector;
import util.linalg.RectangularMatrix;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class EMClustererTest {
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/sick_replace_missing.arff");
        // read in the raw data
        DataSet set = dsr.read();
        DataSet set2 = dsr.read();
        EMClusterer em = new EMClusterer();
        em.estimate(set);
        System.out.println(em);

        PrintWriter writer = new PrintWriter("sick_EM_" + System.currentTimeMillis() + ".csv", "UTF-8");
        for (int i = 0; i < set.size(); i ++) {
            Instance instance = set.get(i);
            Distribution dist = em.distributionFor(instance);
            double[] probabilities = dist.getProbabilities();
            Double output = set2.get(i).getData().get(33);
            String label = "negative";
            if (output > 0.5) {
                label = "sick";
            }
            double value = probabilities[0];
            String cluster_attribute = new DecimalFormat("#.######").format(value);
            writer.println(instance.getData().toString() + "," + cluster_attribute + ',' + label);
        }
    }
}
