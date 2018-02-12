package Main;

import java.util.ArrayList;

public class GeneticGenerator 
{
	ThreadWrapper[] threadList;
	NeuralNet bestNetwork;
	double bestScore;
	final double weightRandomness = 10;
	final int repetitionToModify = 30;
	double scoreRange = .5;
	private double worstScore = 0;
	boolean verbose = false;
	final int maxNetworks = 50000, minNetworks = 10;
	
	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork)
	{
		bestNetwork = startingNetwork;
		bestScore = data.testNetwork(startingNetwork);
		threadList = new ThreadWrapper[numNetworks];
		for(int i = 0;i<threadList.length;i++)
		{
			threadList[i] = new ThreadWrapper(startingNetwork.getCopy(), data, i);
			randomizeNetworkWeightsAndBiases(threadList[i].network);
		}
		long t = System.currentTimeMillis();
		trainNetwork(data);
		t=System.currentTimeMillis()-t;
		System.out.println("time: "+t);
	}
	
	private void trainNetwork(TrainingData data)
	{
		worstScore = 0;
		double scoreTracker = 10000000;
		int cnt = 0;
		while (bestScore > 0)
		{
			
			//Test Networks
			testNetworks(data);
			//Adjust minimum fitness for survival
			double sizeModifier = threadList.length/1000.0;
			scoreRange = (worstScore-bestScore)/2/(sizeModifier);
			//Remove unfit candidates
			ArrayList<NeuralNet> networks = removeFailures();
			//breed fit candidates
			reproduceNetworks(networks, data);
			//If there are too many networks kill randomly
			cullPopulation(data);
			
			//update stats
			if(bestScore != scoreTracker)
			{
				cnt = 0;
				scoreTracker = bestScore;
			}
			else if(cnt >= repetitionToModify)
			{
				System.out.println("test");
				cnt = 0;
			}
			cnt++;
		}
	}
	
	private void reproduceNetworks(ArrayList<NeuralNet> networks, TrainingData data)
	{
		int len = networks.size();
		for(int i = 1;i<len;i++)
		{
			NeuralNet n = networks.get(i).breedWithNetwork(networks.get(i-1));
			randomizeNetworkWeightsAndBiases(n);
			networks.add(n);
		}
		threadList = new ThreadWrapper[networks.size()];
		for(int i = 0;i<networks.size();i++)
		{
			threadList[i] = new ThreadWrapper(networks.get(i).getCopy(), data, i);
		}
		if(verbose)
			System.out.println("Size "+threadList.length);
	}
	
	private ArrayList<NeuralNet> removeFailures()
	{
		if(verbose)
			System.out.println(bestScore+" "+scoreRange+" "+worstScore);
		int remCount = 0;
		ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
		for(int i = 0;i<threadList.length;i++)
		{
			if(threadList[i].network.score<bestScore+scoreRange || remCount >= threadList.length-minNetworks)
			{
				passingNetworks.add(threadList[i].network);
			}
			else
			{
				remCount++;
			}
		}
		if(verbose)
			System.out.println(remCount+" networks removed");
		return passingNetworks;
	}
	
	private void cullPopulation(TrainingData data)
	{
		if(threadList.length > maxNetworks)
		{
			ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
			for(int i = 0;i<maxNetworks/2;i++)
			{
				int n = (int) (Math.random()*threadList.length);
				passingNetworks.add(threadList[n].network);
			}
			threadList = new ThreadWrapper[passingNetworks.size()];
			for(int i = 0;i<passingNetworks.size();i++)
			{
				threadList[i] = new ThreadWrapper(passingNetworks.get(i).getCopy(), data, i);
			}
			if(verbose)
				System.out.println("Networks Culled");
		}
	}
	
	private void testNetworks(TrainingData data)
	{
		long t = System.currentTimeMillis();
		for(int i = 0;i<threadList.length;i++)
		{
			threadList[i].start();
			try {
				threadList[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		t = System.currentTimeMillis()-t;
		System.out.println("t "+t);
		
		for(int i = 0;i<threadList.length;i++)
		{
			if(threadList[i].network == null)
				System.out.println("Null network "+i);
			else
			{
				if(threadList[i].network.score < bestScore)
				{
					bestScore = threadList[i].network.score;
					bestNetwork = threadList[i].network.getCopy();
				}
				else if(threadList[i].network.score > worstScore)
				{
					worstScore = threadList[i].network.score;
				}
			}
		}
		
		
		if(!verbose)
			System.out.println(bestScore);
	}
	
	private void randomizeNetworkWeightsAndBiases(NeuralNet network)
	{
		for(int l = 0;l<network.layerList.size();l++)
		{
			for(int n = 0;n<network.layerList.get(l).nodeList.size();n++)
			{
				network.layerList.get(l).nodeList.get(n).bias+=Math.random()-.5;
				for(int c = 0;c<network.layerList.get(l).nodeList.get(n).connectionList.size();c++)
				{
					network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight+=(Math.random()-.5)*weightRandomness;
					if(network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight > 15)
						network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight = 15;
					else if(network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight < -15)
						network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight = -15;
				}
			}
		}
	}
	
	
}
