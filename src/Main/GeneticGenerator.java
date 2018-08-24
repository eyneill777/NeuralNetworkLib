package Main;
 
 import java.awt.Dimension;
import java.util.ArrayList;
 
 public class GeneticGenerator 
 {
	Island[] islands;
  	int numIslands;
  	NeuralNet bestNetwork;
  	double bestScore;
  	final int syncFrequency = 5;
 	boolean verbose = false;
 	int passingScore;
 	int maxNetworks, minNetworks;
 	boolean displaying = false;
 	NetworkDisplay networkDisplay;
 	DistanceMapDisplay distanceMapDisplay;
 	final int maxRadius = 100, minRadius = 20;
 	Dimension mapDimension;
  	
  	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork, int numIslands, boolean verbose, int passingScore, int maxNetworks, int minNetworks, Dimension mapDim)
  	{
  		this.mapDimension = mapDim;
  		this.maxNetworks = maxNetworks;
  		this.minNetworks = minNetworks;
  		this.verbose = verbose;
  		bestNetwork = startingNetwork;
 		bestScore = data.testNetwork(startingNetwork, false, "Genetic");
 		System.out.println(bestScore);
 		islands = new Island[numIslands];
 		this.numIslands = numIslands;
 		this.passingScore = passingScore;
 		
 		for(int i = 0;i<numIslands;i++)
 		{
 			islands[i] = new Island(startingNetwork, data.getCopy(), i, verbose, numNetworks, maxNetworks, minNetworks, minRadius, maxRadius, mapDimension);
 		}
 	}
  	
  	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork, int numIslands, boolean verbose, int passingScore, int maxNetworks, int minNetworks, Dimension mapDim, boolean displaying)
  	{
  		this.mapDimension = mapDim;
  		this.maxNetworks = maxNetworks;
  		this.minNetworks = minNetworks;
  		this.verbose = verbose;
  		bestNetwork = startingNetwork;
 		bestScore = data.testNetwork(startingNetwork, false, "Genetic");
 		System.out.println(bestScore);
 		islands = new Island[numIslands];
 		this.numIslands = numIslands;
 		this.passingScore = passingScore;
 		
 		for(int i = 0;i<numIslands;i++)
 		{
 			islands[i] = new Island(startingNetwork, data.getCopy(), i, verbose, numNetworks, maxNetworks, minNetworks, minRadius, maxRadius, mapDimension);
 		}
 		this.displaying = displaying;
 		if(displaying)
 		{
 			networkDisplay = new NetworkDisplay(bestNetwork);
 			distanceMapDisplay = new DistanceMapDisplay(islands[0]);
 		}
 	}
  	
  	public void trainNetwork()
  	{
  		int syncCount = 0;
 		while(bestScore > passingScore)
 		{
 			long t = System.currentTimeMillis();
 			if(syncCount >= syncFrequency)
 			{
 				mixIslands();
 				syncCount = 0;
 			}
 			
 			for(int i = 0;i<numIslands;i++)
 	 		{
 	 			ThreadWrapper w = new ThreadWrapper(islands[i], islands[i].data, i);
 	 			islands[i].wrapper = w;
 	 			w.start();
 	 		}
 			for(int i = 0;i<numIslands;i++)
 			{
	 			try {
					islands[i].wrapper.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
 			}
 			for(int i = 0;i<numIslands;i++)
 			{
 				if(bestScore > islands[i].bestScore)
 				{
 					bestScore = islands[i].bestScore;
 					bestNetwork = islands[i].bestNetwork.getCopy();
 					bestNetwork.saveNetwork("src/Data/autosave");
 				}
 			}
 			bestNetwork.saveNetwork("src/Data/autosave");
 			
 			syncCount++;
 			System.out.println("Total best score "+ bestScore + " Percent Correct "+bestNetwork.percentCorrect);
 			System.out.println();
 			if(displaying)
 			{
 				networkDisplay.repaint(bestNetwork);
 				distanceMapDisplay.repaint(islands[0]);
 			}
 			System.gc();
 		}
  	}
  	
  	private void mixIslands()
  	{
  		for(int i = 0;i<islands.length;i++)
  		{
  			for(int j = 0;j<islands.length;j++)
  			{
  				if(i != j)
  				{
  					try
  					{
  						islands[i].mixWith(islands[j], bestNetwork, bestScore);
  						//islands[i].weightRandomness = islands[i].baseWeightRandomness;
  	  					//islands[i].biasRandomness = islands[i].baseBiasRandomness;
  					}
  					catch(NullPointerException e)
  					{
  						
  					}
  				}
  			}
  		}
  	}
 }