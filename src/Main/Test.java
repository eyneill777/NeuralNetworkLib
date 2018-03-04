package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Test 
{
	private static int histResolution = 300;
	private static long[] runtimes = new long[histResolution];
	private static final int numTests = 1;
	private static final long maxRunTimeForHistogram = 400;
	private static long totTime = 0;
	
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
		
		for(int i = 0;i<histResolution;i++)
		{
			runtimes[i] = 0;
		}
		
		for(int i = 0;i<numTests;i++)
		{
			long t = System.currentTimeMillis();
			GeneticGenerator generator = new GeneticGenerator(trainingData, 100, network, 1, true, 0, 10000, 10);
			generator.trainNetwork();
			t = System.currentTimeMillis()-t;
			System.out.println("time " +t);
			totTime+=t;
			if(t<maxRunTimeForHistogram)
			{
				runtimes[(int) (t*histResolution/maxRunTimeForHistogram)] +=1;
			}
			else
				runtimes[runtimes.length-1]++;
		}
		
		long avg = totTime/numTests;
		System.out.println("average runtime "+avg);
		System.out.println("Run Times:");
		for(int i = 0;i<histResolution;i++)
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