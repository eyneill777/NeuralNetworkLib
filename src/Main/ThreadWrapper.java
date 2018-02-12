package Main;

public class ThreadWrapper extends Thread
{
	NeuralNet network;
	TrainingData data;
	
	public ThreadWrapper(NeuralNet network, TrainingData data)
	{
		this.network = network;
		this.data = data;
	}
	
	public void run()
	{
		double score = data.testNetwork(network);
		network.score = score;
	}
}
