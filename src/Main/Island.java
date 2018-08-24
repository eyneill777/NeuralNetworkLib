package Main;

import java.util.ArrayList;

public class Island
{
	NeuralNet[] networkList;
	TrainingData data;
	int islandNo;
	NeuralNet bestNetwork;
  	double bestScore;
 	public double weightRandomness, biasRandomness;
 	private final double startingMaxWeightRandomness = 10, startingMaxBiasRandomness = 2.5, startingBaseWeightRandomness = 5, startingBaseBiasRandomness = 1.25, startingBiasRandomnessModifier = .1, startingWeightRandomnessModifier = .1, minWeightRandomness = .00001, minBiasRandomness = .00001;
 	public double baseWeightRandomness, baseBiasRandomness, maxWeightRandomness, maxBiasRandomness, biasRandomnessModifier, weightRandomnessModifier;
  	public double worstScore = 0;
 	boolean verbose;
 	final int maxNetworks, minNetworks;
 	public ThreadWrapper wrapper;
 	double mutateChance = .01;//Should be adjusted based on number of degrees of freedom and size of network pool
 	double miracleChance = .1;
 	private int layerNo;
 	private int nodeNo;
	
	public Island(NeuralNet startingNetwork, TrainingData data, int islandNo, boolean verbose, int numNetworks,int maxNetworks, int minNetworks)
	{
		maxWeightRandomness = startingMaxWeightRandomness;
		maxBiasRandomness = startingMaxBiasRandomness;
		baseBiasRandomness = startingBaseBiasRandomness;
		baseWeightRandomness = startingBaseWeightRandomness;
		weightRandomness = baseWeightRandomness;
		biasRandomness = baseBiasRandomness;
		biasRandomnessModifier = startingBiasRandomnessModifier;
		weightRandomnessModifier = startingWeightRandomnessModifier;
		this.maxNetworks = maxNetworks;
		this.minNetworks = minNetworks;
		this.verbose = verbose;
		this.data = data;
		this.islandNo = islandNo;
		this.layerNo = startingNetwork.layerList.size()-1;
		this.nodeNo = startingNetwork.layerList.get(layerNo).nodeList.size()-1;
		
		bestNetwork = startingNetwork;
 		bestScore = data.testNetwork(startingNetwork, false, "Genetic");
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
			double score = data.testNetwork(networkList[i], false, "Genetic");
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
		if(weightRandomness<minWeightRandomness)
		{
			baseWeightRandomness = startingBaseWeightRandomness;
			maxWeightRandomness = startingMaxWeightRandomness;
			weightRandomnessModifier = startingWeightRandomnessModifier;
			weightRandomness = baseWeightRandomness;
		}
		if(biasRandomness<minBiasRandomness)
		{
			baseBiasRandomness = startingBaseBiasRandomness;
			maxBiasRandomness = startingMaxBiasRandomness;
			biasRandomnessModifier = startingBiasRandomnessModifier;
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
			maxBiasRandomness/=2;
			biasRandomness = baseBiasRandomness;
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
		System.out.println("randomness "+weightRandomness+" : "+maxWeightRandomness);
		
		if(verbose)
 			System.out.println("Island "+ islandNo+" : "+bestScore);
	}
	
	private void reproduceNetworks(ArrayList<NeuralNet> networks)
 	{
		layerNo = (int) (Math.random()*(networks.get(0).layerList.size()-1))+1;
		nodeNo = (int) (Math.random()*networks.get(0).layerList.get(layerNo).nodeList.size());
		//if(layerNo == 2)
			//nodeNo = (int) (Math.random()*20)+0;
		//if(layerNo == 3)
			//nodeNo = (int) (Math.random()*2)+0;
		//Code for iterating through all nodes
		/**
		nodeNo--;
		if(nodeNo<0)
		{
			layerNo--;
			if(layerNo<0)
				layerNo = networkList[0].layerList.size()-1;
			nodeNo = networkList[0].layerList.get(layerNo).nodeList.size()-1;
		}
		
		System.out.println("LayerNo "+layerNo+" NodeNo "+nodeNo);
		**/
 		int len = networks.size();
 		for(int i = 1;i<len;i++)
 		{
 			//layerNo = (int) (Math.random()*(networks.get(0).layerList.size()-1))+1;
 			//nodeNo = (int) (Math.random()*networks.get(0).layerList.get(layerNo).nodeList.size());
 			//if(layerNo == 2)
			//nodeNo = (int) (Math.random()*20)+80;
 			NeuralNet n2 = networks.get((int)(Math.random()*len));
 			NeuralNet n = networks.get(i).breedWithNetwork(n2);
 			for(int l = 0; l<n.layerList.size();l++)
 			{
 				if(n.layerList.get(l).numSuccessfulChanges < n2.layerList.get(l).numSuccessfulChanges)
 					n.layerList.get(l).numSuccessfulChanges = n2.layerList.get(l).numSuccessfulChanges;
 			}
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
 			for(int i = 0;i<maxNetworks/3;i++)
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
 		//for(int l = 2;l<network.layerList.size();l++)
		int l = layerNo;
 		{
 			network.layerList.get(l).numSuccessfulChanges++;
 			//for(int n = 0;n<network.layerList.get(l).nodeList.size();n++)
 			int n = nodeNo;
 			{
 				{
	 				network.layerList.get(l).nodeList.get(n).bias+=Math.random()*biasRandomness-biasRandomness/2;
	 				if(network.layerList.get(l).nodeList.get(n).bias > 15)
	 					network.layerList.get(l).nodeList.get(n).bias = 15;
	 				else if(network.layerList.get(l).nodeList.get(n).bias < -15)
	 					network.layerList.get(l).nodeList.get(n).bias = -15;
	 					
	 				for(int c = 0;c<network.layerList.get(l).nodeList.get(n).connectionList.size();c++)
	 				{
	 					if(Math.random()<mutateChance)
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