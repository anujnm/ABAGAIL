package opt.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import opt.ga.NQueensFitnessFunction;
import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.SwapNeighbor;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.SwapMutation;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * @author kmanda1
 * @version 1.0
 */
public class NQueensTest {
    /** The n value */
    private static final int N = 9;

    /** The maximum fitness value */
    private static final int MAX_FITNESS = N * (N - 1) / 2;

    public static double getError(double currentFitness) {
        return MAX_FITNESS - currentFitness;
    }

    public static void main(String[] args) {
        try {
            double current = System.currentTimeMillis();
            PrintStream out = new PrintStream(new FileOutputStream("output_" + current + ".txt"));
            System.setOut(out);
        } catch (Exception e) {
            System.out.println("Could not set logger");
        }

        int[] ranges = new int[N];
        Random random = new Random(N);
        for (int i = 0; i < N; i++) {
        	ranges[i] = random.nextInt();
        }
        NQueensFitnessFunction ef = new NQueensFitnessFunction();
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 100);
        fit.train();
        long starttime = System.currentTimeMillis();
//        System.out.println("RHC: " + ef.value(rhc.getOptimal()));
//        System.out.println("RHC: Board Position: ");
//        System.out.println(ef.boardPositions());
//        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        System.out.println("============================");
        
        SimulatedAnnealing sa = new SimulatedAnnealing(1E1, .1, hcp);
        fit = new FixedIterationTrainer(sa, 500);
        starttime = System.currentTimeMillis();
        fit.train();
        double val = ef.value(sa.getOptimal());
        System.out.println("SA: " + val);
        System.out.println("SA: Board Position: ");
        System.out.println(ef.boardPositions());
        System.out.println("Time: "+ (System.currentTimeMillis() - starttime));
        System.out.println("Error: " + getError(val));
        System.out.println("============================");
        

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 0, 10, gap);
        fit = new FixedIterationTrainer(ga, 30000);
        starttime = System.currentTimeMillis();
        fit.train();
        val = ef.value(ga.getOptimal());
        System.out.println("GA: " + val);
        System.out.println("GA: Board Position: ");
        System.out.println(ef.boardPositions());
        System.out.println("Error: " + getError(val));
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
        
        System.out.println("============================");
        

        MIMIC mimic = new MIMIC(200, 10, pop);
        fit = new FixedIterationTrainer(mimic, 50000);
        starttime = System.currentTimeMillis();
        fit.train();
        val = ef.value(mimic.getOptimal());
        System.out.println("MIMIC: " + val);
        System.out.println("MIMIC: Board Position: ");
        System.out.println(ef.boardPositions());
        System.out.println("Error: " + getError(val));
        System.out.println("Time : "+ (System.currentTimeMillis() - starttime));
    }
}
