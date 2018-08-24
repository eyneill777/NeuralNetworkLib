package Main;

public class BackPropigationTrainer 
{
	public NeuralNet network;
	private TrainingData data;
	int repeatsToStop = 30;
	double error;
	NetworkDisplay display;
	
	public BackPropigationTrainer(NeuralNet network, TrainingData data, NetworkDisplay display)
	{
		this.network = network;
		this.data = data;
		this.display = display;
	}
	
	public void trainNetwork()
	{
		int repeatCount = 0;
		double score = 99999999;
		while(repeatCount<repeatsToStop && score > 0)
		{
			try 
			{
				display.repaint(network);
			}
			catch(NullPointerException e){}
			double error = data.testNetwork(network, false, "Backpropigation");
			if(error > score)
			{
				//data.setLearnRate(data.getLearnRate()*(data.getLearnRate()));
			}
			else
			{
				network.updateWeights();
			}
			score = error;
			System.out.println("error "+error);
			System.out.println();
			long t = System.currentTimeMillis();
			while(System.currentTimeMillis()<t+600)
			{}
		}
	}
}
