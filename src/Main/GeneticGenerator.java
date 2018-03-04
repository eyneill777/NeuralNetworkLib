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
  	
  	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork, int numIslands, boolean verbose)
  	{
  		this.verbose = verbose;
 		bestScore = data.testNetwork(startingNetwork);
 		islands = new Island[numIslands];
 		this.numIslands = numIslands;
 		
 		for(int i = 0;i<numIslands;i++)
 		{
 			islands[i] = new Island(startingNetwork, data.getCopy(), i, verbose, numNetworks);
 		}
 	}
  	
  	public void trainNetwork()
  	{
  		int syncCount = 0;
 		while(bestScore > 0)
 		{
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
 				}
 			}
 			
 			syncCount++;
 			System.out.println("Total best score "+ bestScore);
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