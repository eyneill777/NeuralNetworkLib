package Main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

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
	boolean[] outputWeights;
	int numDataToTest = -1;
	
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
	
	public TrainingData(File[] inputData, double[][] outputData,  boolean isPSD, int percentCorrectWeight)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.isPSD = isPSD;
		this.inputFiles = inputData;
		expectedOutputData = outputData;
	}
	
	public TrainingData(File[] inputData, double[][] outputData,  boolean isPSD, int percentCorrectWeight, int numData)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.isPSD = isPSD;
		this.inputFiles = inputData;
		expectedOutputData = outputData;
		this.numDataToTest = numData;
	}

	public double testNetwork(NeuralNet network, boolean verbose)
	{
		error = 0;
		percentCorrect = 0;
		totalCases = 0;
		correctCases = 0;
		outputWeights = new boolean[inputFiles.length];
		for(int i = 0;i<inputFiles.length;i++)
		{
			outputWeights[i] = false;
		}
		
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
			network.percentCorrect = percentCorrect;
		}
		else
		{
			if(!isPSD)
			{
				for(int f = 0;f<inputFiles.length;f++)
				{
					File inputFile = inputFiles[f];
					try {
						FileInputStream stream = new FileInputStream(inputFile);
						int xIndex = 0, yIndex = 0;
						int[][] pixels = new int[28][28];
						int cnt = 0;
						int data;
						while((data=stream.read())!=-1 && (cnt < numDataToTest || numDataToTest < 0))
						{
							pixels[xIndex][yIndex] = data;
							if(xIndex < 27)
								xIndex++;
							else
							{
								xIndex = 0;
								yIndex++;
							}
							if(yIndex >= 28)
							{
								loadTrainingDataImage(pixels, network);
								network.propigateNetwork();
								calculateError(f,network,verbose,inputFile);
								yIndex = 0;
								cnt++;
							}
						}
						stream.close();
					} catch (Exception e) {
						if(e instanceof IOException)
							System.out.println("IO Exceiption on " + inputFile.getName());
						else if(e instanceof FileNotFoundException)
							System.out.println("File Read Failed on File " + inputFile.getName());
						e.printStackTrace();
					}
				}
				percentCorrect = correctCases*1.0/totalCases;
				network.percentCorrect = percentCorrect;
				if(verbose)
				{
					System.out.println("Output Modifier: "+getOutputModifier());
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
						int cnt = 0;
						while(scanner.hasNextLine() && (cnt < numDataToTest || numDataToTest < 0))
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
								cnt++;
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
					System.out.println("Output Modifier: "+getOutputModifier());
					System.out.println("Correct Cases: "+correctCases);
					System.out.println("Total Cases: "+totalCases);	
					System.out.println("Percent correct: "+percentCorrect);
				}
			}
		}
		return (error+(percentCorrectWeight-percentCorrect*percentCorrectWeight))/getOutputModifier();
	}
	
	private void calculateError(int file, NeuralNet network, boolean verbose, File inputFile)
	{
		double[] eOutput = expectedOutputData[file];
		double[] output = new double[eOutput.length];
		double individualError = 0;
		double maxValue = 0;
		for(int l = 0;l<eOutput.length;l++)
		{
			if(network.layerList.get(network.layerList.size()-1).nodeList.get(l).value > maxValue)
				maxValue = network.layerList.get(network.layerList.size()-1).nodeList.get(l).value;
		}
		for(int l = 0;l<eOutput.length;l++)
		{
			if(network.layerList.get(network.layerList.size()-1).nodeList.get(l).value < maxValue)
				output[l] = 0;
			else
				output[l] = 1;
		}
		double correctness = 0;
		for(int l = 0;l<eOutput.length;l++)
		{
			double d = Math.abs(eOutput[l]-network.layerList.get(network.layerList.size()-1).nodeList.get(l).value);
			//double d = Math.abs(eOutput[l]-output[l]);
			correctness+=Math.abs(eOutput[l]-output[l]);
			individualError+=d;
			//output[l] = network.layerList.get(network.layerList.size()-1).nodeList.get(l).value;
		}
		error+=individualError;
		totalCases++;
		if(correctness == 0)
		{
			correctCases++;
		}
		if(individualError<1 && outputWeights[file] == false)
			outputWeights[file] = true;
		if(verbose && correctness == 0)
		{
			System.out.println(inputFile.getName()+" : "+individualError);
			for(int b = 0;b<output.length;b++)
			{
				System.out.print(network.layerList.get(network.layerList.size()-1).nodeList.get(b).value+" ");
			}
			System.out.print(" : ");
			for(int b = 0;b<output.length;b++)
			{
				System.out.print(eOutput[b]+" ");
			}
			System.out.println();
		}
	}
	
	private int getOutputModifier()
	{
		int sum = 1;
		for(int i = 0;i<outputWeights.length;i++)
		{
			if(outputWeights[i])
				sum++;
		}
		return sum;
	}
	
	public TrainingData getCopy()
	{
		TrainingData data;
		if(inputData != null)
			data = new TrainingData(inputData, expectedOutputData, percentCorrectWeight);
		else
			data = new TrainingData(inputFiles, expectedOutputData, isPSD, percentCorrectWeight);
		data.numDataToTest = numDataToTest;
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