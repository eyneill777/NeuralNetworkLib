package Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrainingData 
{
	double[][] inputData, expectedOutputData;
	File[] inputFiles;
	
	public TrainingData(double[][] inputData, double[][] expectedOutputData)
	{
		this.inputData = inputData;
		this.expectedOutputData = expectedOutputData;
	}
	
	public TrainingData(Double[][] inputData, Double[][] expectedOutputData) 
	{
		this.inputData = new double[inputData.length][inputData[0].length];
		this.expectedOutputData = new double[expectedOutputData.length][expectedOutputData[0].length];
		for(int i = 0;i<inputData.length;i++)
		{
			for(int j = 0;j<inputData[i].length;j++)
			{
				this.inputData[i][j] = inputData[i][j];
			}
			for(int j = 0;j<expectedOutputData[i].length;j++)
			{
				this.expectedOutputData[i][j] = expectedOutputData[i][j];
			}
		}
	}
	
	public TrainingData(File[] inputData)
	{
		this.inputFiles = inputData;
		expectedOutputData = new double[inputData.length][inputData.length];
		for(int i = 0;i<inputData.length;i++)
		{
			expectedOutputData[i] = new double[inputData.length];
			for(int j = 0;j<inputData.length;j++)
			{
				if(j == i)
					expectedOutputData[i][j] = 1;
				else
					expectedOutputData[i][j] = 0;
			}
		}
	}

	public double testNetwork(NeuralNet network)
	{
		double error = 0;
		
		if(inputData != null)
		{
			for(int dataSet = 0;dataSet<inputData.length;dataSet++)
			{
				double[] input = inputData[dataSet];
				double[] eOutput = expectedOutputData[dataSet];
				for(int i = 0;i<input.length;i++)
				{
					network.layerList.get(0).nodeList.get(i).setValue(input[i]);
				}
				network.propigateNetwork();
				for(int i = 0;i<eOutput.length;i++)
				{
					error+=Math.abs(eOutput[i]-network.layerList.get(network.layerList.size()-1).nodeList.get(i).value);
				}
			}
		}
		else
		{
			for(int f = 0;f<inputFiles.length;f++)
			{
				File inputFile = inputFiles[f];
				try {
					int maxDataValue = 0;
					Scanner in = new Scanner(new FileInputStream(inputFile));
					in = new Scanner(new FileInputStream(inputFile)); //resets the scanner to top of file
					int xIndex = 0, yIndex = 0;
					int[][] pixels = new int[28][28];
					while(in.hasNextLine())
					{
						String s = in.nextLine();
						for(int i = 0;i<s.length();i++)
						{
							if(xIndex < 27)
								xIndex++;
							else
							{
								xIndex = 0;
								yIndex++;
							}
							if(maxDataValue<(int)s.charAt(i))
								maxDataValue = (int)s.charAt(i);
							pixels[xIndex][yIndex] = (int)s.charAt(i);
							if(yIndex >= 27)
							{
								loadTrainingDataImage(pixels, network);
								network.propigateNetwork();
								double[] eOutput = expectedOutputData[f];
								for(int l = 0;l<eOutput.length;l++)
								{
									error+=Math.abs(eOutput[l]-network.layerList.get(network.layerList.size()-1).nodeList.get(l).value);
								}
								yIndex = 0;
							}
						}
					}
					in.close();
				} catch (FileNotFoundException e) {
					System.out.println("File Read Failed on File " + inputFile.getName());
					e.printStackTrace();
				}
			}
		}
		
		return error;
	}
	
	public TrainingData getCopy()
	{
		TrainingData data;
		if(inputData != null)
			data = new TrainingData(inputData, expectedOutputData);
		else
			data = new TrainingData(inputFiles);
		return data;
	}
	
	private static void loadTrainingDataImage(int[][] pixels, NeuralNet network)
	{
		
		Double[] inputData = new Double[28*28];
		for(int x = 0;x<28;x++)
		{
			for(int y = 0;y<28;y++)
			{
				if(pixels[x][y] != 0)
					pixels[x][y] = 255;
				inputData[x+y*28] = (double) (pixels[x][y]/255);
			}
		}
		for(int i = 0;i<inputData.length;i++)
		{
			network.layerList.get(0).nodeList.get(i).setValue(inputData[i]);
		}
	}
}
