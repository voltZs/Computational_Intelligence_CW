package coursework;

import java.util.ArrayList;

import model.Individual;

public class QueenBeeEA extends ExampleEvolutionaryAlgorithm {
	
	@Override
	public void run() {		
		population = initialise();
		best = getBest();
		System.out.println("Best From Initialisation " + best);


		while (evaluations < Parameters.maxEvaluations) {
			Individual queen = select(Parameters.tournamentSize); 
			Individual bee = select(Parameters.tournamentSize);

			// Generate a child by crossover. - third parameter is number of crossover points
			ArrayList<Individual> children = reproduce(queen, bee, Parameters.crossoverPoints);		
			
			mutateBees(children);
			
			evaluateIndividuals(children);			

			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}
	
	protected void mutateBees(ArrayList<Individual> bees){
		double mutateRatio = Parameters.mutateRatio;
		for(int i = 0; i<bees.size(); i++){
			if(i<mutateRatio*bees.size()){
				Parameters.mutateRate = Parameters.mutateRateSmall;
				mutateBee(bees.get(i));
			} else {
				Parameters.mutateRate = Parameters.mutateRateLarge;
				mutateBee(bees.get(i));
			}
		}
	}
	
	protected void mutateBee(Individual bee){
		for (int i = 0; i < bee.chromosome.length; i++) {
			if (Parameters.random.nextDouble() < Parameters.mutateRate) {
				if (Parameters.random.nextBoolean()) {
					bee.chromosome[i] += (Parameters.mutateChange);
				} else {
					bee.chromosome[i] -= (Parameters.mutateChange);
				}
			}
		}
	}
}
