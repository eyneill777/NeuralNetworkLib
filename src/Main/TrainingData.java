package Main;

public class TrainingData 
{
	double[][] inputData, expectedOutputData;
	
	public TrainingData(double[][] inputData, double[][] expectedOutputData)
	{
		this.inputData = inputData;
		this.expectedOutputData = expectedOutputData;
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
}
