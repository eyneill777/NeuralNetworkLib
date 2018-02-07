package Main;

import java.util.ArrayList;

public class GeneticGenerator 
{
	NeuralNet[] networkList;
	NeuralNet bestNetwork;
	double bestScore;
	final double weightRandomness = 1;
	final int repetitionToModify = 10;
	final double scoreRange = .5;
	private double worstScore = 0;
	private final int numChildren = 1;
	
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
			ArrayList<NeuralNet> networks = removeFailures();
			reproduceNetworks(networks);
			
			for(int i = 0;i<networkList.length;i++)
			{
				randomizeNetworkWeightsAndBiases(networkList[i]);
			}
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
		
	}
	
	private ArrayList<NeuralNet> removeFailures()
	{
		ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
		for(int i = 0;i<networkList.length;i++)
		{
			if(networkList[i].score<bestScore+scoreRange)
			{
				passingNetworks.add(networkList[i]);
			}
		}
		return passingNetworks;
	}
	
	private void testNetworks(TrainingData data)
	{
		for(int i = 0;i<networkList.length;i++)
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
