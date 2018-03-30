package Main;

import java.util.ArrayList;

public class Island
{
	NeuralNet[] networkList;
	TrainingData data;
	int islandNo;
	NeuralNet bestNetwork;
  	double bestScore;
 	public double weightRandomness = 10, biasRandomness = 10;
 	public double baseWeightRandomness = 10, baseBiasRandomness = 10, maxWeightRandomness = 20, maxBiasRandomness = 20;//must be greater than 0
 	//private double randomnessResetChance = .1;
 	private double biasRandomnessModifier = 1, weightRandomnessModifier = 1;
 	final int repetitionToModify = 30;
  	public double worstScore = 0;
 	boolean verbose;
 	final int maxNetworks, minNetworks;
 	public ThreadWrapper wrapper;
 	double mutateChance = .1;
 	double miracleChance = .3;
	
	public Island(NeuralNet startingNetwork, TrainingData data, int islandNo, boolean verbose, int numNetworks,int maxNetworks, int minNetworks)
	{
		this.maxNetworks = maxNetworks;
		this.minNetworks = minNetworks;
		this.verbose = verbose;
		this.data = data;
		this.islandNo = islandNo;
		
		bestNetwork = startingNetwork;
 		bestScore = data.testNetwork(startingNetwork, false);
 		networkList = new NeuralNet[numNetworks];
 		for(int i = 0;i<networkList.length;i++)
 		{
 			networkList[i] = startingNetwork.getCopy();
 			randomizeNetworkWeights(networkList[i]);
 		}
	}
	
	public double trainNetwork()
 	{
  		//Remove unfit candidates
  		ArrayList<NeuralNet> networks = removeFailures();
  		//breed fit candidates
  		reproduceNetworks(networks);
 		//If there are too many networks kill randomly
 		cullPopulation();
 		//printMomentums();
 		worstScore = 0;
  		//Test Networks
  		testNetworks(data);
  		return bestScore;
 	}
	
	private void testNetworks(TrainingData data)
	{
		double lastBest = bestScore;
		for(int i = 0;i<networkList.length;i++)
		{
			double score = data.testNetwork(networkList[i], false);
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
		if(lastBest <= bestScore)
		{
			weightRandomness+=weightRandomnessModifier;
			biasRandomness+=biasRandomnessModifier;
		}
		else if(weightRandomness > baseWeightRandomness)
		{
			weightRandomness = baseWeightRandomness;
			biasRandomness = baseBiasRandomness;
		}
		if(weightRandomness>maxWeightRandomness)
		{
			baseWeightRandomness/=2;
			weightRandomnessModifier/=2;
			maxWeightRandomness/=2;
			weightRandomness = baseWeightRandomness;
		}
		if(biasRandomness>maxBiasRandomness)
		{
			baseBiasRandomness/=2;
			biasRandomnessModifier/=2;
			maxWeightRandomness/=2;
			biasRandomness = baseBiasRandomness;
			
		}
		System.out.println("randomness "+weightRandomness);
		
		if(verbose)
 			System.out.println("Island "+ islandNo+" : "+bestScore);
	}
	
	private void reproduceNetworks(ArrayList<NeuralNet> networks)
 	{
 		int len = networks.size();
 		for(int i = 1;i<len;i++)
 		{
 			NeuralNet n = networks.get(i).breedWithNetwork(networks.get((int)(Math.random()*len)));
 			//n.applyMomentum();
 			randomizeNetworkWeights(n);
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
 		//Make the scoreRange be the best through average scores
 		double scoreRange = 0;
 		for(int i = 0;i<networkList.length;i++)
 		{
 			scoreRange+=networkList[i].score;
 		}
 		scoreRange/=networkList.length;
 		scoreRange-=bestScore;
 		
 		if(verbose)
 			System.out.println(bestScore+" "+scoreRange+" "+worstScore);
 		int remCount = 0;
  		ArrayList<NeuralNet> passingNetworks = new ArrayList<NeuralNet>();
  		for(int i = 0;i<networkList.length;i++)
  		{
 			if(networkList[i].score<bestScore+scoreRange || remCount >= networkList.length-minNetworks || Math.random()<miracleChance)
  			{
  				passingNetworks.add(networkList[i]);
  			}
 			else
 			{
 				remCount++;
 			}
 		}
  		Runtime.getRuntime().gc();
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
	
	private void randomizeNetworkWeights(NeuralNet network)
 	{
 		for(int l = 0;l<network.layerList.size();l++)
 		{
 			for(int n = 0;n<network.layerList.get(l).nodeList.size();n++)
 			{
 				if(Math.random()<mutateChance)
 				{
	 				network.layerList.get(l).nodeList.get(n).bias+=Math.random()*biasRandomness-biasRandomness/2;
	 				if(network.layerList.get(l).nodeList.get(n).bias > 15)
	 					network.layerList.get(l).nodeList.get(n).bias = 15;
	 				else if(network.layerList.get(l).nodeList.get(n).bias < -15)
	 					network.layerList.get(l).nodeList.get(n).bias = -15;
	 					
	 				for(int c = 0;c<network.layerList.get(l).nodeList.get(n).connectionList.size();c++)
	 				{
	 					network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight+=(Math.random()-.5)*weightRandomness;
	 					if(network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight > 15)
	 					{
	 						network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight = 15;
	 					}
	 					else if(network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight < -15)
	 					{
	 						network.layerList.get(l).nodeList.get(n).connectionList.get(c).weight = -15;
	 					}
	 				}
 				}
 			}
 		}
 	}
	
	public void printMomentums()
	{
		System.out.println("avg momentum");
		for(NeuralNet n:networkList)
		{
			System.out.println(n.getAvgMomentum()[1]+"\t"+n.getAvgMomentum()[0]);
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
