package Main;

public class Test 
{
	public static void main(String[] args)
	{
		NeuralNet network = new NeuralNet(new int[]{2,3,1});
		/**
		for(Layer l:network.layerList)
		{
			for(Node n:l.nodeList)
			{
				for(Connection c:n.connectionList)
				{
					c.weight = 1;
				}
			}
		}
		network.layerList.get(1).nodeList.get(1).connectionList.get(0).weight = -1;
		network.layerList.get(1).nodeList.get(1).connectionList.get(1).weight = -1;
		network.layerList.get(1).nodeList.get(0).bias = .5;
		network.layerList.get(1).nodeList.get(1).bias = -1.5;
		network.layerList.get(2).nodeList.get(0).bias = 1.5;
		**/
		
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
		GeneticGenerator generator = new GeneticGenerator(trainingData, 1000, network);
		
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[0]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[1]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[2]);
		NetworkDisplay.displayNetwork(generator.bestNetwork.getCopy(), inputData[3]);
		
		///NetworkDisplay.displayNetwork(network, inputData[0]);
		///NetworkDisplay.displayNetwork(network, inputData[1]);
		//NetworkDisplay.displayNetwork(network, inputData[2]);
		//NetworkDisplay.displayNetwork(network, inputData[3]);
		
		
	}
}