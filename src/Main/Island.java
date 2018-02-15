package Main;

import java.util.ArrayList;

public class Island
{
	NeuralNet[] networkList;
	TrainingData data;
	int islandNo;
	NeuralNet bestNetwork;
  	double bestScore;
 	final double weightRandomness = 10;
 	final int repetitionToModify = 30;
  	double scoreRange = .5;
  	public double worstScore = 0;
 	boolean verbose;
 	final int maxNetworks = 50000, minNetworks = 10;
 	public ThreadWrapper wrapper;
	
	public Island(NeuralNet startingNetwork, TrainingData data, int islandNo, boolean verbose, int numNetworks)
	{
		this.verbose = verbose;
		this.data = data;
		this.islandNo = islandNo;
		
		bestNetwork = startingNetwork;
 		bestScore = data.testNetwork(startingNetwork);
 		networkList = new NeuralNet[numNetworks];
 		for(int i = 0;i<networkList.length;i++)
 		{
 			networkList[i] = startingNetwork.getCopy();
 			randomizeNetworkWeightsAndBiases(networkList[i]);
 		}
	}
	
	public double trainNetwork()
 	{
  		//Adjust minimum fitness for survival
 		double sizeModifier = networkList.length/1000.0;
  		scoreRange = (worstScore-bestScore)/2/(sizeModifier);
  		//Remove unfit candidates
  		ArrayList<NeuralNet> networks = removeFailures();
  		//breed fit candidates
  		reproduceNetworks(networks);
 		//If there are too many networks kill randomly
 		cullPopulation();
 		worstScore = 0;
  		//Test Networks
  		testNetworks(data);
  		return bestScore;
 	}
	
	private void testNetworks(TrainingData data)
	{
		for(int i = 0;i<networkList.length;i++)
		{
			double score = data.testNetwork(networkList[i]);
			networkList[i].score = score;
			
			if(networkList[i] == null)
 				System.out.println("Null network "+i);
			
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
		
		if(verbose)
 			System.out.println("Island "+ islandNo+" : "+bestScore);
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
 			if(networkList[i].score<bestScore+scoreRange || remCount >= networkList.length-minNetworks)
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
  	
 	private void cullPopulation()
 	{
 		if(networkList.length > maxNetworks)
 		{
 			ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
 			for(int i = 0;i<maxNetworks/2;i++)
 			{
 				int n = (int) (Math.random()*networkList.length);
				passingNetworks.add(networkList[n]);
 			}
 			networkList = new NeuralNet[passingNetworks.size()];
 			for(int i = 0;i<passingNetworks.size();i++)
 			{
 				networkList[i] = passingNetworks.get(i).getCopy();
 			}
 			if(verbose)
 				System.out.println("Networks Culled");
 		}
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
	
	public void mixWith(Island island, NeuralNet topNetwork, double topScore)
	{
		int mixCount = networkList.length;
		for(int i = 0;i<mixCount;i++)
		{
			int j = (int) (Math.random()*(networkList.length-1));
			int k = (int) (Math.random()*(island.networkList.length-1));
			if(networkList[j].score>island.networkList[k].score)
				networkList[j] = island.networkList[k];
		}
		networkList[0] = topNetwork.getCopy();
		bestNetwork = networkList[0];
		bestScore = topScore;
	}
}
