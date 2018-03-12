 package Main;
 
 import java.util.ArrayList;
 
 public class GeneticGenerator 
 {
	Island[] islands;
  	int numIslands;
  	NeuralNet bestNetwork;
  	double bestScore;
  	final int syncFrequency = 1;
 	boolean verbose = false;
 	int passingScore;
 	int maxNetworks, minNetworks;
  	
  	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork, int numIslands, boolean verbose, int passingScore, int maxNetworks, int minNetworks)
  	{
  		this.maxNetworks = maxNetworks;
  		this.minNetworks = minNetworks;
  		this.verbose = verbose;
 		bestScore = data.testNetwork(startingNetwork, false);
 		islands = new Island[numIslands];
 		this.numIslands = numIslands;
 		this.passingScore = passingScore;
 		
 		for(int i = 0;i<numIslands;i++)
 		{
 			islands[i] = new Island(startingNetwork, data.getCopy(), i, verbose, numNetworks, maxNetworks, minNetworks);
 		}
 	}
  	
  	public void trainNetwork()
  	{
  		int syncCount = 0;
 		while(bestScore > passingScore)
 		{
 			long t = System.currentTimeMillis();
 			if(syncCount >= syncFrequency)
 				mixIslands();
 			
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
 			
 			syncCount++;
 			System.out.println("Total best score "+ bestScore + " Percent Correct "+bestNetwork.percentCorrect);
 			System.out.println();
 		}
  	}
  	
  	private void mixIslands()
  	{
  		for(int i = 0;i<islands.length;i++)
  		{
  			for(int j = 0;j<islands.length;j++)
  			{
  				if(i != j)
  					islands[i].mixWith(islands[j], bestNetwork, bestScore);
  			}
  		}
  	}
 }