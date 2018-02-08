package Main;

import java.util.ArrayList;

public class GeneticGenerator 
{
	NeuralNet[] networkList;
	NeuralNet bestNetwork;
	double bestScore;
	final double weightRandomness = 1;
	final int repetitionToModify = 80;
	double scoreRange = .5;
	private double worstScore = 0;
	boolean verbose = false;
	
	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork)
	{
		bestNetwork = startingNetwork;
		bestScore = data.testNetwork(startingNetwork);
		networkList = new NeuralNet[numNetworks];
		for(int i = 0;i<networkList.length;i++)
		{
			networkList[i] = startingNetwork.getCopy();
			randomizeNetworkWeightsAndBiases(networkList[i]);
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
			scoreRange = (worstScore-bestScore)/2/(networkList.length/1000.0);
			//Remove unfit candidates
			ArrayList<NeuralNet> networks = removeFailures();
			//breed fit candidates
			reproduceNetworks(networks);
			
			//update stats
			if(bestScore != scoreTracker)
			{
				cnt = 0;
				scoreTracker = bestScore;
			}
			else if(cnt >= repetitionToModify)
			{
				System.out.println("test");
			}
			cnt++;
		}
	}
	
	private void reproduceNetworks(ArrayList<NeuralNet> networks)
	{
		int len = networks.size();
		for(int i = 1;i<len;i++)
		{
			NeuralNet n = networks.get(i).breedWithNetwork(networks.get(i-1));
			randomizeNetworkWeightsAndBiases(n);
			networks.add(n);
		}
		networkList = new NeuralNet[networks.size()];
		for(int i = 0;i<networks.size();i++)
		{
			networkList[i] = networks.get(i).getCopy();
		}
		if(verbose)
			System.out.println("Size "+networkList.length);
	}
	
	private ArrayList<NeuralNet> removeFailures()
	{
		if(verbose)
			System.out.println(bestScore+" "+scoreRange+" "+worstScore);
		int remCount = 0;
		ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
		for(int i = 0;i<networkList.length;i++)
		{
			if(networkList[i] != null && networkList[i].score<bestScore+scoreRange)
			{
				passingNetworks.add(networkList[i]);
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
	
	private void testNetworks(TrainingData data)
	{
		for(int i = 0;i<networkList.length;i++)
		{
			if(networkList[i] == null)
				System.out.println("Null network "+i);
			{
				double score = data.testNetwork(networkList[i]);
				networkList[i].score = score;
				if(score < bestScore)
				{
					bestScore = score;
					bestNetwork = networkList[i].getCopy();
				}
				else if(score > worstScore)
				{
					worstScore = score;
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
