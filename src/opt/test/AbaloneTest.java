package opt.test;

import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying abalone as having either fewer 
 * or more than 15 rings. 
 *
 * @author Hannah Lau
 * @version 1.0
 */
public class AbaloneTest {

    private static String trainingFile = "src/opt/test/sick_train_replaced_missing_values.txt";
    private static String testingFile = "src/opt/test/sick_test_replaced_missing_values.txt";

    private static Integer numberTrainingInstances = 2641;
    private static Integer numberTestingInstances = 1131;

    private static Instance[] trainingInstances = initializeInstances(trainingFile, numberTrainingInstances);
    private static Instance[] testingInstances = initializeInstances(testingFile, numberTestingInstances);

    private static int inputLayer = 33, hiddenLayer = 5, outputLayer = 1, trainingIterations = 1;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(trainingInstances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];
    //private static ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem();

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA", "MIMIC"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
        try {
            double current = System.currentTimeMillis();
            PrintStream out = new PrintStream(new FileOutputStream("output_" + current + ".txt"));
            System.setOut(out);
        } catch (Exception e) {
            System.out.println("Could not set logger");
        }

        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);
        //oa[3] = new MIMIC(100, 10, nnop[3]);

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, testingTimeOTD, correct = 0, incorrect = 0, correctOTD = 0, incorrectOTD = 0;
            ArrayList<Double> errorList = train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            for (int j = 0; j < errorList.size(); j++) {
                System.out.println(df.format(errorList.get(j)));
            }

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for(int j = 0; j < trainingInstances.length; j++) {
                networks[i].setInputValues(trainingInstances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(trainingInstances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);

            double predictedOTD, actualOTD;
            start = System.nanoTime();
            for(int j = 0; j < testingInstances.length; j++) {
                networks[i].setInputValues(testingInstances[j].getData());
                networks[i].run();

                predictedOTD = Double.parseDouble(testingInstances[j].getLabel().toString());
                actualOTD = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predictedOTD - actualOTD) < 0.5 ? correctOTD++ : incorrectOTD++;

            }
            end = System.nanoTime();
            testingTimeOTD = end - start;
            testingTimeOTD /= Math.pow(10,9);

            results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                        + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n" +
                        "Correctly classfied " + correctOTD + " test instances. " +
                        "\nIncorrect classified " + incorrectOTD + " test instances. \nPercent correctly classified test data: "
                        + df.format(correctOTD/(correctOTD + incorrectOTD) *100) + "%\nTesting time on test data: " + df.format(testingTimeOTD) + " seconds \n";
        }

        System.out.println(results);
    }

    private static ArrayList<Double> train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");

        ArrayList<Double> errorList = new ArrayList<Double>();

        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < trainingInstances.length; j++) {
                network.setInputValues(trainingInstances[j].getData());
                network.run();

                Instance output = trainingInstances[j].getLabel();
                Instance example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }

            errorList.add(error);
        }
        return errorList;
    }

    private static Instance[] initializeInstances(String fileName, Integer numberInstances) {

        double[][][] attributes = new double[numberInstances][][];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[33]; // 7 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 33; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications are either 0 or 1
            instances[i].setLabel(new Instance(attributes[i][1][0] < 0.5 ? 0 : 1));
        }

        return instances;
    }
}
