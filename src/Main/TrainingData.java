package Main;

public class TrainingData 
{
	double[][] inputData, expectedOutputData;
	
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

	public double testNetwork(NeuralNet network)
	{
		double error = 0;
		
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
		
		return error;
	}
	
	public TrainingData getCopy()
	{
		TrainingData data = new TrainingData(inputData, expectedOutputData);
		return data;
	}
}
