package coursework;

import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;


public class BigBangBigCrunch extends NeuralNetwork {

	@Override
	public void run() {
		//these are constant for the algorithms ... the size of the whole search space 
		Parameters.minGene = -20;
		Parameters.maxGene = 20;
		population = new ArrayList<>();
		
		Individual center = new Individual();
		for(int i=0; i< center.chromosome.length; i++){
			center.chromosome[i] = 0;
		}
		bigBang(center);
		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);
		
		while (evaluations < Parameters.maxEvaluations) {
//			center = bigCrunch();
			bigBang(best);
			best = getBest();
			outputStats();
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	/**
	 * Generate candidates around center of gravity
	 */
	private void bigBang(Individual center) {
		System.out.println(center.chromosome[5] + " 6ths gene");
		population = new ArrayList<>(); //we want to delete the old solutions
		Random rand = new Random();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual newCandidate = new Individual();
			for(int j=0; j<center.chromosome.length; j++){
				double r = (rand.nextDouble()*2)-1;
				if(evaluations ==0){
					newCandidate.chromosome[j] = 0;
				} else {
//					newCandidate.chromosome[j] = center.chromosome[j] + ((Parameters.maxGene*r)/(evaluations)); //adding +1 to iterations (evaluations) because we start with 0 but this is 'first'
				float increment = (float) (1 - (Math.pow(evaluations, 1/8) / Math.pow(Parameters.maxEvaluations, 1/8))*Parameters.maxGene/2);
				newCandidate.chromosome[j] = center.chromosome[j] + increment*r;
				}
			}
			newCandidate.fitness = Fitness.evaluate(newCandidate, this);
			population.add(newCandidate);
		}
	}
	
	private Individual bigCrunch() {
		Individual newCenter = new Individual();
		//for each chromosome j
		for(int j =0; j<newCenter.chromosome.length; j++){
			float numeratorSum = 0;
			float denominatorSum = 0;
			//for each candidate
			for(int i=0; i<population.size(); i++){
				Individual current = population.get(i);
				numeratorSum += (1/current.fitness) * current.chromosome[j];
				denominatorSum += (1/current.fitness);
			}
			newCenter.chromosome[j] = (numeratorSum / denominatorSum);
		}
		
		return newCenter;
	}

	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
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
