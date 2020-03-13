package coursework;

import java.lang.reflect.Field;
import java.util.Random;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters {
 

	private static int numHidden = 5;	
	private static int numGenes = calculateNumGenes();
	
	/**
	 * Normal Parameters
	 * 
	 */
	public static double minGene = -3; // specifies minimum and maximum weight values 
	public static double maxGene = +3;
	//The number of randomly selected individuals to use for the selection tournament 
	public static int tournamentSize = 5;
	// The number of crossover points to use for reproduction 
	public static int crossoverPoints = 2;
	
	public static int popSize = 10;
	public static int maxEvaluations = 20000;
	
	// Parameters for mutation 
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double mutateRate = 0.3; // mutation rate for mutation operator
	public static double mutateChange = 1.4; // delta change for mutation operator
	
	//Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);
	
	public static double wepMax = 1.0;
	public static double wepMin = 0.2;
	public static double expAccuracy = 6;
	public static double minTravel = 0;
	public static double maxTravel = 4;

	
	

	//set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = MultiVerseOptimisation.class;
	
	/**
	 * Do not change any methods that appear below here.
	 * 
	 */
	
	public static int getNumGenes() {					
		return numGenes;
	}

	
	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden + NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}
	
	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();		
	}

	public static String printParams() {
		String str = "";
		for(Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += name + " \t" + val + "\r\n";
			
		}
		return str;
	}
	
	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}
	
	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}
	
	public static void main(String[] args) {
		printParams();
	}
}
