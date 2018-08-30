package Main;

public class BackPropigationTrainer 
{
	public NeuralNet network;
	private TrainingData data;
	int repeatsToStop = 30;
	double error;
	private double learnRate = .01;
	public double getLearnRate() {
		return learnRate;
	}

	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}

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
			network.updateBackpropBiases();
			//network.updateBackpropWeights(learnRate);
			score = error;
			System.out.println("error "+error);
			System.out.println();
			long t = System.currentTimeMillis();
			while(System.currentTimeMillis()<t+100)
			{}
		}
	}
}
