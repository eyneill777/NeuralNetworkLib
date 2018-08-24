package Main;
public class TestBackProp 
{
	public static void main(String[] args)
	{
		NeuralNet network = new NeuralNet(new int[]{2,2,1});
		for(Layer l:network.layerList)
		{
			for(Node n:l.nodeList)
			{
				//int i = 1;
				for(Connection c:n.connectionList)
				{
					c.setWeight(Math.random()*-1);
					//i*=-1;
				}
			}
		}
		double[][] inputData = new double[4][2];
		inputData[0] = new double[] {0,0};
		inputData[1] = new double[] {0,1};
		inputData[2] = new double[] {1,0};
		inputData[3] = new double[] {1,1};
		
		double[][] expectedOutputData = new double[4][1];
		expectedOutputData[0] = new double[] {0};
		expectedOutputData[1] = new double[] {1};
		expectedOutputData[2] = new double[] {1};
		expectedOutputData[3] = new double[] {1};
		
		TrainingData trainingData = new TrainingData(inputData, expectedOutputData, 0);
		trainingData.setLearnRate(.3);
		NetworkDisplay display = new NetworkDisplay(network);
		
		BackPropigationTrainer trainer = new BackPropigationTrainer(network, trainingData, display);
		trainer.trainNetwork();
	}
}
