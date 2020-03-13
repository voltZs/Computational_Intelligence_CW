package coursework;

import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

public class MultiVerseOptimisation extends NeuralNetwork{
	
	ArrayList<Universe> multiverse = null;
	
	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		multiverse = bigBang();

		//Record a copy of the best Individual in the population
		best = getBest();
//		System.out.println("Best From Initialisation " + bestUniverse);
		
		while (evaluations < Parameters.maxEvaluations) {
			runLightYear(evaluations);
//			evaluateMultiverse(multiverse);
			best = getBest();
			outputStats();
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}
	
	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateMultiverse(ArrayList<Universe> multiverse) {
		for (Universe universe : multiverse) {
			universe.fitness = Fitness.evaluate(universe, this);
		}
	}
	
	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Universe getBest() {
		return multiverse.get(getBestIndex());
	}
	
	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getBestIndex() {
		Universe best = null;
		int idx = -1;
		for (int i = 0; i < multiverse.size(); i++) {
			Universe universe = multiverse.get(i);
			if (best == null) {
				best = universe;
				idx = i;
			} else if (universe.fitness < best.fitness) {
				best = universe;
				idx = i; 
			}
		}
		return idx;
	}	
	
	private Universe getWorst() {
		return multiverse.get(getWorstIndex());
	}
	
	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Universe worst = null;
		int idx = -1;
		for (int i = 0; i < multiverse.size(); i++) {
			Universe universe = multiverse.get(i);
			if (worst == null) {
				worst = universe;
				idx = i;
			} else if (universe.fitness > worst.fitness) {
				worst = universe;
				idx = i; 
			}
		}
		return idx;
	}	
	
	/**
	 * Initialise the multiverse
	 */
	private ArrayList<Universe> bigBang() {
		multiverse = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Universe universe = new Universe();
			multiverse.add(universe);
		}
		evaluateMultiverse(multiverse);
		return multiverse;
	}
	
	
	private void runLightYear(int year){
		Random rand = new Random();
		ArrayList<Universe> multiverseCopy = new ArrayList<>();
		//create a copy of the universe
		for(Universe universe : multiverse){
			multiverseCopy.add((Universe) universe.copy());
		}
		
		for(int i = 0; i< multiverseCopy.size(); i++){
			Universe currentUniverse = multiverseCopy.get(i);
			double inflation = calculateInflation(currentUniverse.fitness);
			
			for(int j = 0; j<currentUniverse.chromosome.length; j++){
				double wep = wormholeExistenceProbability(year);
				double tdr = travelDistanceRate(year);
				double r1 = rand.nextDouble();
				double r2 = rand.nextDouble();
				double r3 = rand.nextDouble();
				double r4 = rand.nextDouble();
				
				if(r1 < inflation){
					//index of the donor white hole universe -> rouletteSelector will make it most likely one of the good fitness universes
					int whiteHoleIndex = rouletteSelection();
					currentUniverse.chromosome[j] = multiverseCopy.get(whiteHoleIndex).chromosome[j];
				}
				if(r2 < wep){
					if(r3 < 0.5){
						currentUniverse.chromosome[j] = getBest().chromosome[j] + tdr * ((Parameters.maxTravel - Parameters.minTravel) * r4 + Parameters.minTravel);
					} else {
						currentUniverse.chromosome[j] = getBest().chromosome[j] - tdr * ((Parameters.maxTravel - Parameters.minTravel) * r4 + Parameters.minTravel);
					}
				}
			}
			//if the newly created universe is more fit, replace! ADDITION
			currentUniverse.fitness = Fitness.evaluate(currentUniverse, this);
			if(currentUniverse.fitness < multiverse.get(i).fitness){
				multiverse.set(i, currentUniverse);
			}
		}
	}
	
	private double wormholeExistenceProbability(int iteration){
		return Parameters.wepMin + (iteration * ((Parameters.wepMax - Parameters.wepMin) / Parameters.maxEvaluations));
	}
	
	private double travelDistanceRate(int iteration){
		return 1 - ((Math.pow(iteration, 1/Parameters.expAccuracy)) / (Math.pow(Parameters.maxEvaluations, 1/Parameters.expAccuracy)));
	}
	
	private int rouletteSelection(){
		//return index of a universe to be the white hole - lower inflation, higher chance
		ArrayList<RouletteTuple> probabilities = new ArrayList<>();
		
		double min = getBest().fitness;
		double max = getWorst().fitness;
		float totalReverseInflations = 0;
		for(Universe universe : multiverse){
			totalReverseInflations += 1 - (universe.fitness - min)/(max-min);
		}
		float testSum = 0;
		for(Universe universe : multiverse){
			float reverseInflation = (float) (1 - (universe.fitness - min)/(max-min));
			probabilities.add(new RouletteTuple(universe, reverseInflation/totalReverseInflations));
			testSum += (reverseInflation/totalReverseInflations);
		}	
		probabilities.sort(new Comparator<RouletteTuple>(){
			public int compare(RouletteTuple o1, RouletteTuple o2) {
				return Float.compare(o1.probability, o2.probability);
			}
		});
		
		Random rand = new Random();
		double r = rand.nextDouble();
		for(RouletteTuple tuple : probabilities){
			if(r < tuple.probability){
				return multiverse.indexOf(tuple.universe);
			}
		}
		return 0;
	}
	
	private double calculateInflation(double fitness){
		//calculates the inflation rate of the universe 
		//normalised value of the fitness 
		// -> the lower (better) the fitness, the lower the inflation 
		// -> the higher (worse) the fitness, the higher the inflation
		double min = getBest().fitness;
		double max = getWorst().fitness;
		return (fitness - min)/(max-min);
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

	private class Universe extends Individual{
		
	}
	
	private class RouletteTuple{
		float probability;
		Universe universe;
		
		public RouletteTuple(Universe universe, float probability){
			this.universe = universe;
			this.probability = probability;
		}
	}

}
