package Main;
public class TestBackProp 
{
	public static void main(String[] args)
	{
		NeuralNet network = new NeuralNet(new int[]{2,2,1});
		network.randomizeWeights();
		network.randomizeBiases();
		
		double[][] inputData = new double[4][2];
		inputData[0] = new double[] {0.1,0.1};
		inputData[1] = new double[] {0.1,.9};
		inputData[2] = new double[] {.9,0.1};
		inputData[3] = new double[] {.9,.9};
		
		double[][] expectedOutputData = new double[4][1];
		expectedOutputData[0] = new double[] {0.1};
		expectedOutputData[1] = new double[] {.1};
		expectedOutputData[2] = new double[] {.1};
		expectedOutputData[3] = new double[] {.9};
		
		TrainingData trainingData = new TrainingData(inputData, expectedOutputData, 0);
		//NetworkDisplay display = new NetworkDisplay(network);
		
		//BackPropigationTrainer trainer = new BackPropigationTrainer(network, trainingData, display);
		BackPropigationTrainer trainer = new BackPropigationTrainer(network, trainingData, null);
		trainer.setLearnRate(.1);
		trainer.trainNetwork();
		network.saveNetwork("src/Data/network1");
	}
}
