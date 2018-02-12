package Main;

public class ThreadWrapper extends Thread
{
	NeuralNet network;
	TrainingData data;
	int threadIndex;
	
	public ThreadWrapper(NeuralNet network, TrainingData data, int threadIndex)
	{
		this.network = network;
		this.data = data;
		this.threadIndex = threadIndex;
	}
	
	public void run()
	{
		double score = data.testNetwork(network);
		 network.score = score;
		//System.out.println("Thread Index "+threadIndex);
	}
}
