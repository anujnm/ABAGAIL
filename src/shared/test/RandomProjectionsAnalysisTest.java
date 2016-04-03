package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.RandomizedProjectionFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by ANM on 4/3/16.
 */
public class RandomProjectionsAnalysisTest {

    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {

        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/sick_replace_missing.arff");
        // read in the raw data
        DataSet set = dsr.read();
        DataSet set2 = dsr.read();
        RandomizedProjectionFilter filter = new RandomizedProjectionFilter(15, 34);
        filter.filter(set);
        PrintWriter writer = new PrintWriter("sick_RJ_" + System.currentTimeMillis() + ".csv", "UTF-8");
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
