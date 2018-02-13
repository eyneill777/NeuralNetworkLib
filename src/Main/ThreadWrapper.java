package Main;

public class ThreadWrapper extends Thread
{
	Island island;
	TrainingData data;
	int threadIndex;
	
	public ThreadWrapper(Island island, TrainingData data, int threadIndex)
	{
		this.data = data;
		this.threadIndex = threadIndex;
		this.island = island;
	}
	
	public void run()
	{
		island.trainNetwork();
		System.out.println("Island "+threadIndex);
	}
}
