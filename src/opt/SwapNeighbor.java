package opt;

import dist.Distribution;

import shared.Instance;

/**
 * A swap one neighbor function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class SwapNeighbor implements NeighborFunction {
    
    /**
     * @see opt.ga.MutationFunction#mutate(opt.OptimizationData)
     */
    public Instance neighbor(Instance d) {
        Instance cod = (Instance) d.copy();
        //System.out.println("Instance is " + cod.toString());
        int i = Distribution.random.nextInt(cod.getData().size());
        int j = Distribution.random.nextInt(cod.getData().size());
        //System.out.println("i and j are: " + i + ", " + j);
        double temp = cod.getContinuous(i);
        cod.getData().set(i, cod.getContinuous(j));
        cod.getData().set(j, temp);
        //System.out.println("Returned Instance is " + cod.toString());
        return cod;
    }
}