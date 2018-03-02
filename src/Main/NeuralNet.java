package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class NeuralNet 
{
	ArrayList<Layer> layerList = new ArrayList<Layer>();
	double outputThreshold = .9;
	double score = -1;
	
	public NeuralNet()
	{
		
	}
	
	public NeuralNet(int numLayers)
	{
		for(int i = 0;i<numLayers;i++)
		{
			layerList.add(new Layer());
		}
	}
	
	public NeuralNet(int[] layerSizes)
	{
		for(int i = 0;i<layerSizes.length;i++)
		{
			layerList.add(new Layer(layerSizes[i]));
			if(i>0)
			{
				layerList.get(i).connectLayer(layerList.get(i-1));
			}
		}
	}
	
	public NeuralNet(String filePath)
	{
		File file = new File(filePath);
		try 
		{
			Scanner scanner = new Scanner(file);
			layerList = new ArrayList<Layer>();
			while(scanner.hasNextLine())
			{
				String s = scanner.nextLine();
				Layer l = new Layer();
				layerList.add(l);
				
				String[] separateConnectionsAndBias = s.split(";");
			}
			scanner.close();
		} catch (FileNotFoundException e) 
		{
				e.printStackTrace();
		}
	}
	
	public NeuralNet breedWithNetwork(NeuralNet network2)
	{
		NeuralNet mixedNetwork = getCopy();
		for(int i = 0;i<layerList.size();i++)
		{
			Layer layer1 = mixedNetwork.layerList.get(i);
			if(i<network2.layerList.size())
			{
				layer1.mixLayer(network2.layerList.get(i));
			}
			else
			{
				System.out.println("TODO Error: too many layers.  See NeuralNet.breedWithNetwork");
			}
		}
		return network2;
	}
	
	public void propigateNetwork()
	{
		for(int i = 1;i<layerList.size();i++)
		{
			layerList.get(i).calculateNodeValues();
		}
		for(int i = 0;i<layerList.get(layerList.size()-1).nodeList.size();i++)
		{
			if(layerList.get(layerList.size()-1).nodeList.get(i).value > outputThreshold)
				layerList.get(layerList.size()-1).nodeList.get(i).value = 1;
			else if(layerList.get(layerList.size()-1).nodeList.get(i).value < 1-outputThreshold)
				layerList.get(layerList.size()-1).nodeList.get(i).value = 0;
		}
	}
	
	public NeuralNet getCopy()
	{
		NeuralNet network = new NeuralNet();
		for(int l = 0;l<layerList.size();l++)
		{
			network.layerList.add(new Layer());
			for(int n = 0;n<layerList.get(l).nodeList.size();n++)
			{
				network.layerList.get(l).nodeList.add(new Node(layerList.get(l).nodeList.get(n).bias, layerList.get(l).nodeList.get(n).value));
				for(int c = 0;c<layerList.get(l).nodeList.get(n).connectionList.size();c++)
				{
					network.layerList.get(l).nodeList.get(n).connectionList.add(new Connection(network.layerList.get(l-1).nodeList.get(c), layerList.get(l).nodeList.get(n), layerList.get(l).nodeList.get(n).connectionList.get(c).weight, true));
				}
			}
		}
		return network;
	}
	
	public void saveNetwork(String filePath)//TODO
	{
		File file = new File(filePath);
		try 
		{
			int a = 0;
			FileWriter writer = new FileWriter(file);
			for(Layer l:layerList)
			{
				for(Node n:l.nodeList)
				{
					writer.write(n.bias+"");
					for(Connection con:n.connectionList)
					{
						writer.write(";"+con.getSaveString(layerList.get(a-1)));
					}
					writer.write(",");
				}
				a++;
				writer.write("\n");
				writer.flush();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
