package Main;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.w3c.dom.NodeList;

public class NeuralNet 
{
	//double biasMomentumFriction = 1.1; @deprecated
	public ArrayList<Layer> layerList = new ArrayList<Layer>();
	double outputThreshold = .85;
	double score = -1;
	double percentCorrect = 0;
	public ArrayList<NeuralNet> linkedNetworks = new ArrayList<NeuralNet>();
	public Point mapCoord;
	public double connectionRadius;
	
	public NeuralNet()
	{
		
	}
	
	public NeuralNet(int numLayers)
	{
		for(int i = 0;i<numLayers;i++)
		{
			layerList.add(new Layer(this));
		}
		layerList.get(0).setUnbiased(true);
		//layerList.get(layerList.size()-1).setUnbiased(true);
	}
	
	public NeuralNet(int[] layerSizes)
	{
		for(int i = 0;i<layerSizes.length;i++)
		{
			layerList.add(new Layer(layerSizes[i], this));
			if(i>0)
			{
				layerList.get(i).connectLayer(layerList.get(i-1));
			}
		}
		layerList.get(0).setUnbiased(true);
		//layerList.get(layerList.size()-1).setUnbiased(true);
	}
	
	public NeuralNet(String filePath)
	{
		File file = new File(filePath);
		try 
		{
			Scanner scanner = new Scanner(file);
			layerList = new ArrayList<Layer>();
			int layerNo = 0;
			while(scanner.hasNextLine())
			{
				String s = scanner.nextLine();
				Layer l = new Layer(this);
				layerList.add(l);
				
				String[] nodes = s.split(",");
				for(int n = 0;n<nodes.length;n++)
				{
					parseNode(nodes[n], layerNo);
				}
				layerNo++;
			}
			scanner.close();
		} catch (FileNotFoundException e) 
		{
				e.printStackTrace();
		}
		layerList.get(0).setUnbiased(true);
		//layerList.get(layerList.size()-1).setUnbiased(true);
	}
	
	public void randomizeWeights()
	{
		for(Layer l:layerList)
		{
			l.RandomizeWeights();
		}
	}
	
	public void randomizeBiases()
	{
		for(Layer l:layerList)
		{
			l.RandomizeBiases();
		}
	}
	
	public void setAllWeights(double val)
	{
		for(Layer l:layerList)
		{
			l.setAllWeights(val);
		}
	}
	
	private void parseNode(String s, int layerNo)
	{
		String[] info = s.split(";");
		Node n = new Node(Double.parseDouble(info[0]), 0, layerList.get(layerNo));
		for(int i = 1;i<info.length;i++)
		{
			String[] connInfo = info[i].split(":");
			new Connection(layerList.get(layerNo-1).nodeList.get(Integer.parseInt(connInfo[1])), n, Double.parseDouble(connInfo[0]), Integer.parseInt(connInfo[1]), layerList.get(layerNo-1).nodeList.indexOf(n));
		}
		layerList.get(layerNo).nodeList.add(n);
	}
	
	public NeuralNet breedWithNetwork(NeuralNet network2, int maxDistanceFromParent, double newRadius)
	{
		NeuralNet mixedNetwork = getCopy();
		mixedNetwork.mapCoord.x+=(Math.random()-.5)*maxDistanceFromParent;
		mixedNetwork.mapCoord.y+=(Math.random()-.5)*maxDistanceFromParent;
		mixedNetwork.connectionRadius = newRadius;
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
		return mixedNetwork;
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
			network.layerList.add(new Layer(this));
			for(int n = 0;n<layerList.get(l).nodeList.size();n++)
			{
				network.layerList.get(l).nodeList.add(new Node(layerList.get(l).nodeList.get(n).bias, layerList.get(l).nodeList.get(n).value, network.layerList.get(l)));
				network.layerList.get(l).nodeList.get(network.layerList.get(l).nodeList.size()-1).unbiased = layerList.get(l).nodeList.get(n).unbiased;
				for(int c = 0;c<layerList.get(l).nodeList.get(n).connectionList.size();c++)
				{
					network.layerList.get(l).
					nodeList.get(n).
					connectionList.add
					(new Connection(
							network.layerList.get(l-1).nodeList.get(this.layerList.get(l).nodeList.get(n).connectionList.get(c).node1Index),
							layerList.get(l).nodeList.get(n)
							, layerList.get(l).nodeList.get(n).connectionList.get(c).getWeight(), true, this.layerList.get(l).nodeList.get(n).connectionList.get(c).node1Index, this.layerList.get(l).nodeList.get(n).connectionList.get(c).node2Index));
					//network.layerList.get(l).nodeList.get(n).connectionList.get(c).momentum = layerList.get(l).nodeList.get(n).connectionList.get(c).momentum;
				}
			}
		}
		network.outputThreshold = this.outputThreshold;
		network.percentCorrect = this.percentCorrect;
		network.score = this.score;
		network.connectionRadius = this.connectionRadius;
		network.mapCoord = new Point(this.mapCoord.x, this.mapCoord.y);
		return network;
	}
	
	public void saveNetwork(String filePath)
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
					writer.write(n.getBias()+"");
					for(Connection con:n.connectionList)
					{
						writer.write(";"+con.getSaveString());
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
	
	public void setGradientAndPropigateBack(boolean reset, double[] targetVal)
	{
		int cnt = 1;
		for(int l = layerList.size()-1;l>=0;l--)
		{
			Layer layer = layerList.get(l);
			for(int n = 0;n<layer.nodeList.size();n++)
			{
				Node node = layer.nodeList.get(n);
				if(cnt == 1)
					node.setGradient(reset, cnt ,targetVal[n]);
				else
					node.setGradient(reset, cnt , -1);
				//System.out.print(node.gradient+"\t");
			}
			//System.out.println();
			cnt++;
		}
		//System.out.println();
	}
	
	public void updateBackpropWeights()
	{
		for(Layer l:layerList)
		{
			for(Node n: l.nodeList)
			{
				if(!n.unbiased)
				{
					n.biasGrad+=n.gradient;
				}
				for(Connection c:n.connectionList)
				{
					
					c.setGradient(c.node1.getInputErrorSignal()*c.node2.getValue());
					c.setWeight(c.getWeight()+c.getGradient());
					System.out.println(c.getWeight());
					if(c.getWeight()>c.maxWeight)
						c.setWeight(c.maxWeight);
					else if(c.getWeight() < c.minWeight)
						c.setWeight(c.minWeight);
				}
			}
		}
	}
	
	public void setInputErrorSignalForAllNodes(double inputErrorSignal)
	{
		for(Layer l:layerList) 
		{
			for(Node n:l.nodeList)
			{
				n.setInputErrorSignal(inputErrorSignal);
			}
		}
	}
	
	public void updateBackpropBiases()
	{
		for(Layer l:layerList)
		{
			for(Node n: l.nodeList)
			{
				//n.bias+=n.biasGrad*.005;
				n.biasGrad = 0;
			}
		}
	}
}
