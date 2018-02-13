package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Test 
{
	private static long[] runtimes = new long[100];
	private static final int numTests = 100;
	private static final long maxRunTimeForHistogram = 400;
	
	public static void main(String[] args)
	{
		NeuralNet network = new NeuralNet(new int[]{2,2,1});
		
		double[][] inputData = new double[4][2];
		inputData[0] = new double[] {0,0};
		inputData[1] = new double[] {0,1};
		inputData[2] = new double[] {1,0};
		inputData[3] = new double[] {1,1};
		
		double[][] expectedOutputData = new double[4][1];
		expectedOutputData[0] = new double[] {0};
		expectedOutputData[1] = new double[] {1};
		expectedOutputData[2] = new double[] {1};
		expectedOutputData[3] = new double[] {0};
		
		TrainingData trainingData = new TrainingData(inputData, expectedOutputData);
		
		for(int i = 0;i<100;i++)
		{
			runtimes[i] = 0;
		}
		
		for(int i = 0;i<numTests;i++)
		{
			long t = System.currentTimeMillis();
			GeneticGenerator generator = new GeneticGenerator(trainingData, 1000, network, 1);
			t = System.currentTimeMillis()-t;
			if(t<maxRunTimeForHistogram)
			{
				runtimes[(int) (t*100/maxRunTimeForHistogram)] +=1;
			}
			else
				runtimes[runtimes.length-1]++;
		}
		
		
		System.out.println("Run Times:");
		for(int i = 0;i<100;i++)
		{
			System.out.println(runtimes[i]);
		}
		
		
		/**
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[0]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[1]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[2]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[3]);
		**/
		
		
	}
}