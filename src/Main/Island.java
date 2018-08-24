package Main;

import java.awt.Dimension;
import java.awt.Point;
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
 	double highMutateChance = .3;
 	double mutateChance = highMutateChance;//Should be adjusted based on number of degrees of freedom and size of network pool
 	double miracleChance = .1;
 	private int layerNo;
 	private int nodeNo;
 	private Dimension mapDim;
 	private int maxRadius, minRadius, maxDistanceFromParent = 300;
 	private int turnsSinceImprovement = 0;
	
	public Island(NeuralNet startingNetwork, TrainingData data, int islandNo, boolean verbose, int numNetworks,int maxNetworks, int minNetworks, int minConnectionRadius, int maxConnectionRadius, Dimension mapDimension)
	{
		this.minRadius = minConnectionRadius;
		this.maxRadius = maxConnectionRadius;
		this.mapDim = mapDimension;
		startingNetwork.mapCoord = new Point(mapDim.width/2, mapDim.height/2);
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
 			if(i<numNetworks/10)
 				networkList[i].connectionRadius = maxRadius;
 			else
 			{
 				networkList[i].connectionRadius = Math.random()*(maxRadius-minRadius)+minRadius;
 			}
 		}
 		networkList[0].connectionRadius = 1000;
	}
	
	public double trainNetwork()
 	{
  		//Remove unfit candidates
  		ArrayList<NeuralNet> networks = removeFailures();
  		System.out.println("length "+networks.size());
  		//breed fit candidates
  		reproduceNetworks(networks);
 		//If there are too many networks kill randomly
 		cullPopulation();
  		//Test Networks
 		worstScore = 0;
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
			baseWeightRandomness/=2.0;
			weightRandomnessModifier/=2.0;
			maxWeightRandomness/=2.0;
			weightRandomness = baseWeightRandomness;
		}
		if(biasRandomness>maxBiasRandomness)
		{
			baseBiasRandomness/=2.0;
			biasRandomnessModifier/=2.0;
			maxBiasRandomness/=2.0;
			biasRandomness = baseBiasRandomness;
		}
		if(lastBest <= bestScore)
		{
			turnsSinceImprovement = 0;
			weightRandomness+=weightRandomnessModifier;
			biasRandomness+=biasRandomnessModifier;
		}
		else if(weightRandomness > baseWeightRandomness)
		{
			turnsSinceImprovement++;
			weightRandomness = baseWeightRandomness;
			biasRandomness = baseBiasRandomness;
		}
		System.out.println("randomness "+weightRandomness+" : "+maxWeightRandomness);
		mutateChance-=.01;
		if(turnsSinceImprovement>40 || mutateChance <=.01)
			mutateChance = highMutateChance;
		
		if(verbose)
 			System.out.println("Island "+ islandNo+" : "+bestScore);
	}
	
	private void reproduceNetworks(ArrayList<NeuralNet> networks)
 	{
		//Use these to change the same node for all networks in the pool
		//layerNo = (int) (Math.random()*(networks.get(0).layerList.size()-1))+1;
		//nodeNo = (int) (Math.random()*networks.get(0).layerList.get(layerNo).nodeList.size());
		
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
 			for(int j = 0;j<len;j++)
 			{
 				NeuralNet n2 = networks.get(j);
 				NeuralNet thisNet = networks.get(i);
 				//boolean bred = false;
 				if(i!=j && Math.pow(thisNet.mapCoord.x-n2.mapCoord.x, 2)+Math.pow(thisNet.mapCoord.y-n2.mapCoord.y, 2) < Math.pow(thisNet.connectionRadius, 2)+Math.pow(n2.connectionRadius, 2))
 				{
 					//bred = true;
		 			NeuralNet n = thisNet.breedWithNetwork(n2, maxDistanceFromParent, Math.random()*(maxRadius-minRadius)+minRadius);
		 			for(int l = 0; l<n.layerList.size();l++)
		 			{
		 				if(n.layerList.get(l).numSuccessfulChanges < n2.layerList.get(l).numSuccessfulChanges)
		 					n.layerList.get(l).numSuccessfulChanges = n2.layerList.get(l).numSuccessfulChanges;
		 			}
		 			//n.applyMomentum();
		 			randomizeNetworkWeights(n);
		 			networks.add(n);
 				}
 				//if(!bred)
 					//thisNet.mapCoord = new Point((int)(Math.random()*mapDim.width), (int)(Math.random()*mapDim.height));
 			}
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
 				if(networkList[i].mapCoord.x<0 || networkList[i].mapCoord.x > mapDim.width || networkList[i].mapCoord.y<0 || networkList[i].mapCoord.y>mapDim.height)
 					networkList[i].mapCoord = new Point((int)(Math.random()*mapDim.width), (int)(Math.random()*mapDim.height));
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
 			passingNetworks.add(bestNetwork);
 			networkList = new NeuralNet[passingNetworks.size()];
 			for(int i = 0;i<passingNetworks.size();i++)
 			{
 				networkList[i] = passingNetworks.get(i);
 			}
 			if(verbose)
 				System.out.println("Networks Culled");
 		}
 	}
	
	private void randomizeNetworkWeights(NeuralNet network)
 	{
		layerNo = (int) (Math.random()*(network.layerList.size()-1))+1;
		nodeNo = (int) (Math.random()*network.layerList.get(layerNo).nodeList.size());
 		//for(int l = 2;l<network.layerList.size();l++)
		int l = layerNo;
 		{
 			network.layerList.get(l).numSuccessfulChanges++;
 			//for(int n = 0;n<network.layerList.get(l).nodeList.size();n++)
 			int n = nodeNo;
 			{
 				{
	 				network.layerList.get(l).nodeList.get(n).bias+=Math.random()*biasRandomness-biasRandomness/2.0;
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