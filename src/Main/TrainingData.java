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
	String dataType = "RawImage";
	int percentCorrectWeight;
	double error = 0;
	double percentCorrect = 0;
	int totalCases = 0;
	int correctCases = 0;
	boolean[] outputWeights;
	int numDataToTest = -1;
	int[][] files;
	int xDim, yDim, numData;
	boolean useHighestValue;
	
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
	
	public TrainingData(File[] inputData, double[][] outputData,  String dataType, int percentCorrectWeight, int xDim, int yDim, int numData)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.dataType = dataType;
		expectedOutputData = outputData;
		this.xDim = xDim;
		this.yDim = yDim;
		this.numData = numData;
		
		files = new int[inputData.length][];
		for(int f = 0;f<inputData.length;f++)
		{
			File inputFile = inputData[f];
			files[f] = new int[xDim*yDim*numData];
			try {
				FileInputStream stream = new FileInputStream(inputFile);
				int cnt = 0;
				int data;
				while((data= stream.read())!=-1)
				{
					files[f][cnt] = data;
					cnt++;
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
	}
	
	public TrainingData(File[] inputData, double[][] outputData,  String dataType, int percentCorrectWeight, int numDataToTest, int xDim, int yDim, int numData)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.dataType = dataType;
		expectedOutputData = outputData;
		this.numDataToTest = numDataToTest;
		this.xDim = xDim;
		this.yDim = yDim;
		this.numData = numData;
		
		files = new int[inputData.length][];
		for(int f = 0;f<inputData.length;f++)
		{
			File inputFile = inputData[f];
			files[f] = new int[xDim*yDim*numData];
			try {
				FileInputStream stream = new FileInputStream(inputFile);
				int cnt = 0;
				int data;
				while((data= stream.read())!=-1)
				{
					files[f][cnt] = data;
					cnt++;
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
	}
	
	public TrainingData(int[][] files, double[][] outputData,  String dataType, int percentCorrectWeight, int xDim, int yDim, int numData)
	{
		this.percentCorrectWeight = percentCorrectWeight;
		this.dataType = dataType;
		expectedOutputData = outputData;
		this.xDim = xDim;
		this.yDim = yDim;
		this.numData = numData;
		this.files = files;
	}

	public double testNetwork(NeuralNet network, boolean verbose, String trainerType)
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
					//System.out.println("grad"+grad+" "+eOutput[i]+" "+network.layerList.get(network.layerList.size()-1).nodeList.get(i).value);
					
					individualError+=d;
					error+=d;
				}
				
				if(trainerType.equals("Backpropigation"))
				{
					if(dataSet == 0)
					{
						network.setGradientAndPropigateBack(true, eOutput);
					}
					else
					{
						network.setGradientAndPropigateBack(false, eOutput);
					}
					network.updateBackpropWeights();
					network.setInputErrorSignalForAllNodes(0);
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
			outputWeights = new boolean[files.length];
			for(int i = 0;i<files.length;i++)
			{
				outputWeights[i] = false;
			}
			if(dataType.equals("RawImage") || dataType.equals("PSD"))
			{
				for(int f = 0;f<files.length;f++)
				{
					int xIndex = 0, yIndex = 0;
					int[][] pixels = new int[xDim][yDim];
						int cnt = 0;
						int data;
						while(cnt<numData && (cnt < numDataToTest || numDataToTest < 0))
						{
							int pixIndex = cnt*(xDim*yDim)+yIndex*xDim+xIndex;
							data = files[f][pixIndex];
							pixels[xIndex][yIndex] = data;
							if(xIndex < xDim-1)
								xIndex++;
							else
							{
								xIndex = 0;
								yIndex++;
							}
							if(yIndex >= yDim)
							{
								loadTrainingDataImage(xDim,yDim,pixels, network);
								network.propigateNetwork();
								calculateError(f,network,verbose);
								yIndex = 0;
								cnt++;
							}
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
		if(trainerType.equals("Genetic"))
			return (error+(percentCorrectWeight-percentCorrect*percentCorrectWeight))/getOutputModifier();
		else
			return error;
	}
	
	public double testNetwork(NeuralNet network, boolean verbose, String trainerType, double learnRate)
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
					//System.out.println("grad"+grad+" "+eOutput[i]+" "+network.layerList.get(network.layerList.size()-1).nodeList.get(i).value);
					
					individualError+=d;
					error+=d;
				}
				
				if(trainerType.equals("Backpropigation"))
				{
					if(dataSet == 0)
					{
						network.setGradientAndPropigateBack(true, eOutput);
					}
					else
					{
						network.setGradientAndPropigateBack(false, eOutput);
					}
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
			outputWeights = new boolean[files.length];
			for(int i = 0;i<files.length;i++)
			{
				outputWeights[i] = false;
			}
			if(dataType.equals("RawImage") || dataType.equals("PSD"))
			{
				for(int f = 0;f<files.length;f++)
				{
					int xIndex = 0, yIndex = 0;
					int[][] pixels = new int[xDim][yDim];
						int cnt = 0;
						int data;
						while(cnt<numData && (cnt < numDataToTest || numDataToTest < 0))
						{
							int pixIndex = cnt*(xDim*yDim)+yIndex*xDim+xIndex;
							data = files[f][pixIndex];
							pixels[xIndex][yIndex] = data;
							if(xIndex < xDim-1)
								xIndex++;
							else
							{
								xIndex = 0;
								yIndex++;
							}
							if(yIndex >= yDim)
							{
								loadTrainingDataImage(xDim,yDim,pixels, network);
								network.propigateNetwork();
								calculateError(f,network,verbose);
								yIndex = 0;
								cnt++;
							}
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
		if(trainerType.equals("Genetic"))
			return (error+(percentCorrectWeight-percentCorrect*percentCorrectWeight))/getOutputModifier();
		else
			return error;
	}
	
	private void calculateError(int file, NeuralNet network, boolean verbose)
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
			if(useHighestValue)
			{
				if(network.layerList.get(network.layerList.size()-1).nodeList.get(l).value < maxValue)
					output[l] = 0;
				else
					output[l] = 1;
			}
			else
			{
				output[l] = network.layerList.get(network.layerList.size()-1).nodeList.get(l).value;
			}
		}
		double correctness = 0;
		for(int l = 0;l<eOutput.length;l++)
		{
			double d;
			if(l==file)
				d = Math.abs(eOutput[l]-network.layerList.get(network.layerList.size()-1).nodeList.get(l).value);
			else
				d = Math.abs(eOutput[l]-output[l]);
			correctness+=Math.abs(eOutput[l]-output[l]);
			individualError+=d;
			output[l] = network.layerList.get(network.layerList.size()-1).nodeList.get(l).value;
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
			System.out.println("File"+file+" : "+individualError);
			for(int b = 0;b<output.length;b++)
			{
				System.out.print(network.layerList.get(network.layerList.size()-1).nodeList.get(b).value+" ");
				//System.out.print(output[b]+" ");
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
		try
		{
			int sum = 1;
			for(int i = 0;i<outputWeights.length;i++)
			{
				if(outputWeights[i])
					sum++;
			}
			return sum;
		}
		catch(NullPointerException e)
		{
			return 1;
		}
	}
	
	public TrainingData getCopy()
	{
		TrainingData data;
		if(inputData != null)
			data = new TrainingData(inputData, expectedOutputData, percentCorrectWeight);
		else
			data = new TrainingData(files, expectedOutputData, dataType, percentCorrectWeight, xDim, yDim, numData);
		data.numDataToTest = numDataToTest;
		return data;
	}
	
	private static void loadTrainingDataImage(int xDim, int yDim ,int[][] pixels, NeuralNet network)
	{
		Double[] inputData = new Double[xDim*yDim];
		//BufferedImage buffer = new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
		for(int x = 0;x<xDim;x++)
		{
			for(int y = 0;y<yDim;y++)
			{
				//if(pixels[x][y] > 0 && pixels[x][y]< 105)
					//pixels[x][y]+=150;
				inputData[x+y*yDim] = (double) (pixels[x][y]/255.0);
				//buffer.setRGB(x, y, new Color(pixels[x][y],pixels[x][y],pixels[x][y]).getRGB());
			}
		}
		//DisplayFrame f = new DisplayFrame(buffer);
		//f.setSize(50,50);
		//f.setVisible(true);
		for(int i = 0;i<inputData.length;i++)
		{
			network.layerList.get(0).nodeList.get(i).setValue(inputData[i]);
		}
	}
}