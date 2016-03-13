package opt.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import dist.*;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * Copied from ContinuousPeaksTest
 * @version 1.0
 */
public class FourPeaksTest {
    /** The n value */
    private static final int N = 20;
    /** The t value */
    private static final int T = N / 10;
    
    public static void main(String[] args) {
        try {
            double current = System.currentTimeMillis();
            PrintStream out = new PrintStream(new FileOutputStream("output_" + current + ".txt"));
            System.setOut(out);
        } catch (Exception e) {
            System.out.println("Could not set logger");
        }

        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
        long starttime = System.currentTimeMillis();
        //fit.train();
        //System.out.println("RHC: " + ef.value(rhc.getOptimal()));
        //System.out.println("Optimal point: " + rhc.getOptimal().toString());
        //System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
        fit = new FixedIterationTrainer(sa, 1000);
        starttime = System.currentTimeMillis();
        fit.train();
        System.out.println("SA: " + ef.value(sa.getOptimal()));
        System.out.println("Optimal point: " + sa.getOptimal().toString());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        fit = new FixedIterationTrainer(ga, 30000);
        starttime = System.currentTimeMillis();
        fit.train();
        System.out.println("GA: " + ef.value(ga.getOptimal()));
        System.out.println("Optimal point: " + ga.getOptimal().toString());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        MIMIC mimic = new MIMIC(200, 20, pop);
        fit = new FixedIterationTrainer(mimic, 3000);
        starttime = System.currentTimeMillis();
        fit.train();
        System.out.println("MIMIC: " + ef.value(mimic.getOptimal()));
        System.out.println("Optimal point: " + mimic.getOptimal().toString());
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
    }
}
