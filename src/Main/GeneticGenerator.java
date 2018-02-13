 package Main;
 
 import java.util.ArrayList;
 
 public class GeneticGenerator 
 {
	Island[] islands;
  	int numIslands;
  	NeuralNet bestNetwork;
  	double bestScore;
 	boolean verbose = false;
  	
  	public GeneticGenerator(TrainingData data, int numNetworks, NeuralNet startingNetwork, int numIslands)
  	{
 		bestScore = data.testNetwork(startingNetwork);
 		islands = new Island[numIslands];
 		
 		for(int i = 0;i<numIslands;i++)
 		{
 			islands[i] = new Island(startingNetwork, data.getCopy(), i, verbose, numNetworks);
 		}
 		
 		while(bestScore > 0)
 		{
 			for(int i = 0;i<numIslands;i++)
 	 		{
 	 			ThreadWrapper w = new ThreadWrapper(islands[i], islands[i].data, i);
 	 			w.start();
 	 			try {
					w.join();
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
 			System.out.println("Total best score "+ bestScore);
 			if(bestScore == 0)
 				System.out.println("test");
 		}
 	}
 }