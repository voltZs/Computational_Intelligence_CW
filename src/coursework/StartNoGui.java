package coursework;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Example of how to to run the {@link ExampleEvolutionaryAlgorithm} without the need for the GUI
 * This allows you to conduct multiple runs programmatically 
 * The code runs faster when not required to update a user interface
 *
 */
public class StartNoGui {

	public static void main(String[] args) {
		/**
		 * Train the Neural Network using our Evolutionary Algorithm 
		 * 
		 */

		//number of hidden nodes in the neural network
		Parameters.setHidden(5);
		
		ArrayList<Double> resultsTest = new ArrayList<>();
		ArrayList<Double> resultsTraining = new ArrayList<>();
		int repetitions = 20;
		
		double bestFitnessTest = Double.MAX_VALUE;
		double bestFitnessTraining = Double.MAX_VALUE;
		
		String resultStr = "";
		resultStr+= "RESULTS: TRAININGT | TEST \r\n";
		for(int i=0; i<repetitions; i++){
			Parameters.setDataSet(DataSet.Training);
			NeuralNetwork nn = new MultiVerseOptimisation();		
			nn.run();
			double fitnessTraining = Fitness.evaluate(nn);
			resultsTraining.add(fitnessTraining);
			
			Parameters.setDataSet(DataSet.Test);
			double fitnessTest = Fitness.evaluate(nn);
			resultsTest.add(fitnessTest);
			
			System.out.println( "Round "+ i + ": Fitness on " + Parameters.getDataSet() + " " + fitnessTest);
			
			
			resultStr += fitnessTraining + ", " + fitnessTest + "\r\n";
			
			if(fitnessTraining < bestFitnessTraining) bestFitnessTraining = fitnessTraining;
			if(fitnessTest < bestFitnessTest) bestFitnessTest = fitnessTest;
		}
		resultStr += "\r\n";
		double avgTraining = getAverage(resultsTraining);
		double avgTest = getAverage(resultsTest);
		resultStr += "Mean: " + avgTraining + ", " + avgTest + "\r\n";
		resultStr += "SD: " +  getSD(resultsTraining, avgTraining) + ", " + getSD(resultsTest, avgTest) + "\r\n";
		resultStr += "Median: " +  getMedian(resultsTraining) + ", " +  getMedian(resultsTest) + "\r\n";
		resultStr += "Best: " + bestFitnessTraining+ ", "+ bestFitnessTest + "\r\n";
		writeResultsToFile("results.txt", resultStr, true);

		
//		/* Print out the best weights found
//		 * (these will have been saved to disk in the project default directory) 
//		 */
//		System.out.println(nn.best);
//		
//		/**
//		 * We now need to test the trained network on the unseen test Set
//		 */
//		Parameters.setDataSet(DataSet.Test);
//		double fitness = Fitness.evaluate(nn);
//		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness);
		
		

		/**
		 * Or We can reload the NN from the file generated during training and test it on a data set 
		 * We can supply a filename or null to open a file dialog 
		 * Note that files must be in the project root and must be named *-n.txt
		 * where "n" is the number of hidden nodes
		 * ie  1518461386696-5.txt was saved at timestamp 1518461386696 and has 5 hidden nodes
		 * Files are saved automatically at the end of training
		 *  
		 *  Uncomment the following code and replace the name of the saved file to test a previously trained network 
		 */
		
//		NeuralNetwork nn2 = NeuralNetwork.loadNeuralNetwork("1234567890123-5.txt");
//		Parameters.setDataSet(DataSet.Random);
//		double fitness2 = Fitness.evaluate(nn2);
//		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness2);

	}
	
	private static double getAverage(ArrayList<Double> values){
		double sum = 0;
		for(int i=0; i<values.size(); i++){
			sum += values.get(i);
		}
		return sum/values.size();
	}
	
	private static double getMedian(ArrayList<Double> values){
		ArrayList<Double> sorted = values;
		Collections.sort(sorted);
		if(values.size()%2 == 0){
			int midR = values.size()/2;
			System.out.println(midR);
			System.out.println(values.get(midR));
			int midL = midR-1;
			System.out.println(midL);
			System.out.println(values.get(midL));
			return (values.get(midL)+values.get(midR))/2;
		} else {
			int mid = (values.size()/2);
			return values.get(mid);
		}
	}
	
	private static double getSD(ArrayList<Double> values, double mean){
		double sum = 0;
		for(int i=0; i<values.size(); i++){
			sum+= Math.pow(values.get(i)-mean, 2);
		}
		return Math.sqrt(sum/values.size());
	}
	
	private static void writeResultsToFile(String fileName, String string, boolean append) {
		try {
			FileWriter fw = new FileWriter(fileName, append);
			fw.write(string);
			fw.close();
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.print("\r\n" + e.getMessage() + "\r\n");			
			System.exit(-1);
		}
	}
}
