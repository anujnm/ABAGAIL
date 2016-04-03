package func.test;

import dist.DiscreteDistribution;
import dist.Distribution;
import dist.MultivariateGaussian;
import func.KMeansClusterer;
import shared.DataSet;
import shared.DistanceMeasure;
import shared.EuclideanDistance;
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
public class KMeansClustererTest {

    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {

        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/krvskp_binary_train.arff");
        // read in the raw data
        DataSet set = dsr.read();
        DataSet set2 = dsr.read();

        KMeansClusterer km = new KMeansClusterer(21);
        km.estimate(set);
        System.out.println(km);
        PrintWriter writer = new PrintWriter("krvskp_KMeans_" + System.currentTimeMillis() + ".csv", "UTF-8");
        for (int i = 0; i < set.size(); i ++) {

            Instance instance = set.get(i);
            Distribution dist = km.distributionFor(instance);
            double[] probabilities = dist.getProbabilities();

            Double output = set2.get(i).getData().get(40);
            String label = "won";
            if (output > 0.5) {
                label = "nowin";
            }
            double value = probabilities[0];
            String cluster_attribute = new DecimalFormat("#.######").format(value);
            writer.println(instance.getData().toString() + "," + cluster_attribute + ',' + label);

        }
        writer.close();
        //System.out.println("Total score = " + total);
        //writer.close();
    }
}
