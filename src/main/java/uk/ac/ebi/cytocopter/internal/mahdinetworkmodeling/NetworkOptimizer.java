package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JOptionPane;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.EdgeException;

public class NetworkOptimizer extends FitnessFunction
{
	public final double sizeFac;
	public final double naFac;

	public final int popSize;

	public final double maxTime;
	public final int maxGen;

	public final double relTol;

	private final CNONetwork network;
	private int timePoint;

	SortedMap<Double, ArrayList<Integer>> allResults;

	SortedMap<Double, ArrayList<Integer>> desiredResults;
	ArrayList<Double> desiredResultsWeights;

	public SortedMap<Double, ArrayList<Integer>> getAllResults()
	{
		return allResults;
	}

	public SortedMap<Double, ArrayList<Integer>> getDesiredResults()
	{
		return desiredResults;
	}

	public ArrayList<Double> getDesiredResultsWeights()
	{
		return desiredResultsWeights;
	}

	public NetworkOptimizer(CNONetwork network, int timePoint, double sizeFac, double naFac, int popSize,
			double maxTime, int maxGen, double relTol)
	{

		this.network = network;
		this.timePoint = timePoint;

		this.sizeFac = sizeFac;
		this.network.setEdgePenaltyParameter(sizeFac);

		this.naFac = naFac;
		this.network.setNANodePenaltyParameter(naFac);
		this.popSize = popSize;

		this.maxTime = maxTime;
		this.maxGen = maxGen;
		this.relTol = relTol;

		this.allResults = new TreeMap<Double, ArrayList<Integer>>();

	}

	@Override
	protected double evaluate(IChromosome chromosome)
	{
		double result = Double.MIN_VALUE;
		ArrayList<Integer> desired_edges = new ArrayList<Integer>();

		for (int i = 0; i < network.numberOfEdges(); i++)
		{
			desired_edges.add((Integer) chromosome.getGene(i).getAllele());

		}

		try
		{
			network.restoreEdges();
			network.removeEdges(desired_edges);

		}
		catch (EdgeException e1)
		{
			e1.printStackTrace();
		}

		try
		{

			return network.getWorstCaseFitnessFunction() - network.getFitnessNumber(timePoint);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Integer> run() throws Exception
	{
		if (allResults.isEmpty())
		{
			this.runs();
			double bestFit = allResults.firstKey();
			return allResults.get(bestFit);
		}
		else
		{
			double bestFit = allResults.firstKey();
			return allResults.get(bestFit);

		}

	}
        
        

	// looking for best set of results
	public SortedMap<Double, ArrayList<Integer>> runs() throws Exception
	{
		Configuration.reset();
		Configuration conf = new DefaultConfiguration();

		FitnessFunction myFunc = this;

		conf.setFitnessFunction(myFunc);

		Gene[] requiredGenes = new Gene[network.numberOfEdges()];

		for (int i = 0; i < network.numberOfEdges(); i++)
		{
			requiredGenes[i] = new IntegerGene(conf, 0, 1);
		}

		Chromosome sampleChromosome = new Chromosome(conf, requiredGenes);
		conf.setSampleChromosome(sampleChromosome);

		conf.setPopulationSize(popSize);

		Genotype population = Genotype.randomInitialGenotype(conf);

		long startTime = System.currentTimeMillis();
		boolean timeFlag = false;

		for (int i = 0; i < maxGen && timeFlag == false; i++)
		{
			population.evolve();
			List<IChromosome> oneIterationResults = population.getFittestChromosomes(popSize);
			saveGeneticResults(oneIterationResults);

			this.evaluate(population.getFittestChromosome());
			long endTime = System.currentTimeMillis();
			if ((endTime - startTime) > maxTime * 1000)
			{
				timeFlag = true;
			}
		}
		IChromosome bestSolutionSoFar = population.getFittestChromosome();

		ArrayList<Integer> result3 = new ArrayList<Integer>();

		for (int i = 0; i < network.numberOfEdges(); i++)
		{
			result3.add((Integer) bestSolutionSoFar.getGene(i).getAllele());

		}

		desiredResults = new TreeMap<Double, ArrayList<Integer>>();
		Double bestScore = allResults.firstKey();
		Double bestScoreRelTol = bestScore * (1 + relTol);

		for (Map.Entry<Double, ArrayList<Integer>> entry : allResults.entrySet())
		{
			if (entry.getKey() < bestScoreRelTol)
			{
				desiredResults.put(entry.getKey(), entry.getValue());
			}
		}

		computeWeights();
		return desiredResults;
	}

	private ArrayList<Double> computeWeights()
	{

		int desiredResultsNum = desiredResults.size();
		int length = desiredResults.get(desiredResults.firstKey()).size();
		desiredResultsWeights = new ArrayList<Double>();
		for (int i = 0; i < length; i++)
		{
			desiredResultsWeights.add(0d);

		}

		for (ArrayList<Integer> oneDesiredSolution : desiredResults.values())
		{
			for (int i = 0; i < length; i++)
			{
				desiredResultsWeights.set(i, desiredResultsWeights.get(i) + oneDesiredSolution.get(i));
			}
		}

		for (int i = 0; i < length; i++)
		{
			desiredResultsWeights.set(i, desiredResultsWeights.get(i) / desiredResultsNum);
		}

		return desiredResultsWeights;
	}

	public ArrayList<Double> getAdaptedDesiredResultsWeights()
	{
		ArrayList<Double> adaptedDesiredResultsWeights = new ArrayList<Double>();

		for (int i = 0; i < desiredResultsWeights.size(); i++)
		{
			Edge edge = network.getAvailableEdge(i);
			if (edge.getSources().size() == 1)
			{
				adaptedDesiredResultsWeights.add(desiredResultsWeights.get(i));
			}
			else
			{
				// all the Source nodes to the And node
				// plus one for the AND node to the target node
				for (int j = 0; j < edge.getSources().size() + 1; j++)
				{
					adaptedDesiredResultsWeights.add(desiredResultsWeights.get(i));
				}
			}

		}

		return adaptedDesiredResultsWeights;
	}

	public ArrayList<String> getAdaptedEdgeNames()
	{
		return network.getAdaptedEdgeNames();
	}

	private void saveGeneticResults(List<IChromosome> oneIterationResults)
	{

		for (IChromosome oneChromosome : oneIterationResults)
		{
			if (isContainedChromoneinAllResults(oneChromosome) == false)
			{
				allResults.put(Math.abs(oneChromosome.getFitnessValue() - network.getWorstCaseFitnessFunction()),
						chromosome2intArrayList(oneChromosome));
			}
		}

	}

	private boolean isContainedChromoneinAllResults(IChromosome chromosome)
	{
		ArrayList<Integer> candidateChromosome = chromosome2intArrayList(chromosome);

		if (allResults.containsValue(candidateChromosome) == true)
			return true;
		else
			return false;

	}

	private ArrayList<Integer> chromosome2intArrayList(IChromosome chromosome)
	{
		ArrayList<Integer> candidateChromosome = new ArrayList<Integer>();

		for (int i = 0; i < chromosome.size(); i++)
		{
			candidateChromosome.add((Integer) (chromosome.getGene(i).getAllele()));
		}
		return candidateChromosome;
	}

}
