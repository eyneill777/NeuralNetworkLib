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
 	public double baseWeightRandomness = 5, baseBiasRandomness = 1.25, maxWeightRandomness = 10, maxBiasRandomness = 2.5;//must be greater than 0
 	//private double randomnessResetChance = .1;
 	private double biasRandomnessModifier = .1, weightRandomnessModifier = .1;
 	//final int repetitionToModify = 30;
  	public double worstScore = 0;
 	boolean verbose;
 	final int maxNetworks, minNetworks;
 	public ThreadWrapper wrapper;
 	double mutateChance = .1;//Should be adjusted based on number of degrees of freedom and size of network pool
 	double miracleChance = .1;
 	private int layerNo;
 	private int nodeNo;
	
	public Island(NeuralNet startingNetwork, TrainingData data, int islandNo, boolean verbose, int numNetworks,int maxNetworks, int minNetworks)
	{
		weightRandomness = baseWeightRandomness;
		biasRandomness = baseBiasRandomness;
		this.maxNetworks = maxNetworks;
		this.minNetworks = minNetworks;
		this.verbose = verbose;
		this.data = data;
		this.islandNo = islandNo;
		this.layerNo = startingNetwork.layerList.size()-1;
		this.nodeNo = startingNetwork.layerList.get(layerNo).nodeList.size()-1;
		
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
		//layerNo = (int) (Math.random()*(networks.get(0).layerList.size()-1))+1;
		//nodeNo = (int) (Math.random()*networks.get(0).layerList.get(layerNo).nodeList.size());
		//if(layerNo == 2)
			//nodeNo = (int) (Math.random()*20)+0;
		//if(layerNo == 3)
			//nodeNo = (int) (Math.random()*2)+0;
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
 			layerNo = (int) (Math.random()*(networks.get(0).layerList.size()-1))+1;
 			nodeNo = (int) (Math.random()*networks.get(0).layerList.get(layerNo).nodeList.size());
 			//if(layerNo == 2)
			//nodeNo = (int) (Math.random()*100)+200;
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
