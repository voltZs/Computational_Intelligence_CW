package coursework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	
	static final int ONE_POINT_CROSSOVER = 1;
	static final int TWO_POINT_CROSSOVER = 2;
	static final int RANDOM_CROSSOVER = -1;
	static final int UNIFORM_CROSSOVER = -2;
	
	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = select(Parameters.tournamentSize); 
			Individual parent2 = select(Parameters.tournamentSize);

			// Generate a child by crossover. - third parameter is number of crossover points
			ArrayList<Individual> children = reproduce(parent1, parent2, Parameters.crossoverPoints);		
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	public void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	public Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	public ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 */
	protected Individual select(int tournamentSize) {		
		ArrayList<Individual> contestants = new ArrayList<>();
		while (contestants.size() < tournamentSize){
			Individual individual = null;
			do { 
				individual = population.get(Parameters.random.nextInt(Parameters.popSize));
			} while (contestants.contains(individual));
			contestants.add(individual);
		}
		
		double bestFitness = Double.MAX_VALUE;
		Individual bestContestant = contestants.get(0);
		for (Individual contestant : contestants){
			if(contestant.fitness < bestFitness){
				bestFitness = contestant.fitness;
				bestContestant = contestant;
			}
		}
		
		return bestContestant.copy();
	}
	
	
	/**
	 * Crossover / Reproduction
	 * 
	 * the crossoverAmount is the number of generated crossover points. at every crossover point the dominant and submissive parent swap for a child. 
	 * Two opposite children are produced
	 */
	protected ArrayList<Individual> reproduce(Individual parentA, Individual parentB, Integer crossoverAmount) {
		return nPointCrossover(parentA,parentB, crossoverAmount);
	}
	
	private ArrayList<Individual> nPointCrossover(Individual parentA, Individual parentB, Integer crossoverAmount) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parentA.copy());
		children.add(parentB.copy());
		
		Individual[] parents = {parentA, parentB};
		int chromosomeLength = parentA.chromosome.length;
		ArrayList<Integer> crossoverPoints = new ArrayList<>();
		Random rand = new Random();
		
		if(crossoverAmount == RANDOM_CROSSOVER){
			crossoverAmount = rand.nextInt(chromosomeLength/3);
		}
		if (crossoverAmount == UNIFORM_CROSSOVER){
			crossoverAmount = rand.nextInt(chromosomeLength-1);
		}
		
		while (crossoverPoints.size() < crossoverAmount){
			Integer newPoint = 0;
			do {
				newPoint = rand.nextInt(chromosomeLength-1)+1; //+1 to avoid getting a zero and -1 as a result (to avoid an out of bounds exception)
			} while (crossoverPoints.contains(newPoint));
			crossoverPoints.add(newPoint);
		}
		Collections.sort(crossoverPoints);
		

		int dominant = 0;
		int submissive = 1;
		
		for(int i=0; i<chromosomeLength; i++){
			if(crossoverPoints.contains(i)){
				int temp = dominant;
				dominant = submissive;
				submissive = temp;
			}
			children.get(0).chromosome[i] = parents[dominant].chromosome[i];
			children.get(1).chromosome[i] = parents[submissive].chromosome[i];
		}
		
		return children;
	} 
	
//	private void printChromosome(double[] chromosome){
//		for(int i=0; i< chromosome.length; i++){
//			System.out.print(chromosome[i] + " ");
//		}
//		System.out.println();
//	}
	
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	protected void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * 
	 */
	protected void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			
			//make sure the individual getting replaced is worse than the offspring? - fitness is better when it's lower
			if(population.get(idx).fitness > individual.fitness){
			
				population.set(idx, individual);
			}
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
