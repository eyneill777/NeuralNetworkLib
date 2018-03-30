package Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TrainingData 
{
	double[][] inputData, expectedOutputData;
	File[] inputFiles;
	boolean isPSD;
	int percentCorrectWeight;
	double error = 0;
	double percentCorrect = 0;
	int totalCases = 0;
	int correctCases = 0;
	
	public TrainingData(double[][] inputData, double[][] expectedOutputData, int percentCorrectWeight)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.inputData = inputData;
		this.expectedOutputData = expectedOutputData;
	}
	
	public TrainingData(Double[][] inputData, Double[][] expectedOutputData, int percentCorrectWeight) 
	{
		this.percentCorrectWeight = percentCorrectWeight;
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
	
	public TrainingData(File[] inputData, boolean isPSD, int percentCorrectWeight)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.isPSD = isPSD;
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

	public double testNetwork(NeuralNet network, boolean verbose)
	{
		error = 0;
		percentCorrect = 0;
		totalCases = 0;
		correctCases = 0;
		
		if(inputData != null)
		{
			for(int dataSet = 0;dataSet<inputData.length;dataSet++)
			{
				double individualError = 0;
				double[] input = inputData[dataSet];
				double[] eOutput = expectedOutputData[dataSet];
				for(int i = 0;i<input.length;i++)
				{
					network.layerList.get(0).nodeList.get(i).setValue(input[i]);
				}
				network.propigateNetwork();
				for(int i = 0;i<eOutput.length;i++)
				{
					double d = Math.abs(eOutput[i]-network.layerList.get(network.layerList.size()-1).nodeList.get(i).value);
					individualError+=d;
					error+=d;
				}
				if(individualError == 0)
					correctCases++;
				totalCases++;
			}
			percentCorrect = correctCases*1.0/totalCases;
		}
		else
		{
			if(!isPSD)
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
									calculateError(f,network,verbose,inputFile);
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
				percentCorrect = correctCases*1.0/totalCases;
				network.percentCorrect = percentCorrect;
				if(verbose)
				{
					System.out.println("Correct Cases: "+correctCases);
					System.out.println("Total Cases: "+totalCases);	
					System.out.println("Percent correct: "+percentCorrect);
				}
			}
			else
			{
				for(int f = 0;f<inputFiles.length;f++)
				{
					try 
					{
						Scanner scanner = new Scanner(inputFiles[f]);
						ArrayList<Double> inputData = new ArrayList<Double>();
						while(scanner.hasNextLine())
						{
							String s = scanner.nextLine();
							if(!s.equals(""))
							{
								String[] ds = s.split("\t");
								for(int i = 0;i<ds.length;i++)
								{
									inputData.add(Double.parseDouble(ds[i]));
								}
							}
							else
							{
								for(int i = 0;i<inputData.size();i++)
								{
									network.layerList.get(0).nodeList.get(i).setValue(inputData.get(i));
								}
								network.propigateNetwork();
								calculateError(f, network, verbose, inputFiles[f]);
								inputData.clear();
							}
						}
					} catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					}
				}
				percentCorrect = correctCases*1.0/totalCases;
				network.percentCorrect = percentCorrect;
				if(verbose)
				{
					System.out.println("Correct Cases: "+correctCases);
					System.out.println("Total Cases: "+totalCases);	
					System.out.println("Percent correct: "+percentCorrect);
				}
			}
		}
		return error+(percentCorrectWeight-percentCorrect*percentCorrectWeight);
	}
	
	private void calculateError(int file, NeuralNet network, boolean verbose, File inputFile)
	{
		double[] eOutput = expectedOutputData[file];
		double[] output = new double[eOutput.length];
		double individualError = 0;
		for(int l = 0;l<eOutput.length;l++)
		{
			double d = Math.abs(eOutput[l]-network.layerList.get(network.layerList.size()-1).nodeList.get(l).value);
			individualError+=d;
			output[l] = network.layerList.get(network.layerList.size()-1).nodeList.get(l).value;
		}
		error+=individualError;
		totalCases++;
		if(individualError == 0)
		{
			correctCases++;
		}
		if(verbose && individualError < 1)
		{
			System.out.println(inputFile.getName()+" : "+individualError);
			for(int b = 0;b<output.length;b++)
			{
				System.out.print(output[b]+" ");
			}
			System.out.print(" : ");
			for(int b = 0;b<output.length;b++)
			{
				System.out.print(eOutput[b]+" ");
			}
			System.out.println();
		}
	}
	
	public TrainingData getCopy()
	{
		TrainingData data;
		if(inputData != null)
			data = new TrainingData(inputData, expectedOutputData, percentCorrectWeight);
		else
			data = new TrainingData(inputFiles, isPSD, percentCorrectWeight);
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
