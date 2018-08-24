package Main;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

public class Layer 
{
	public ArrayList<Node> nodeList = new ArrayList<Node>();
	int numSuccessfulChanges = 0;
	
	public Layer()
	{
		
	}
	
	public Layer(int numNodes)
	{
		for(int i=0;i<numNodes;i++)
		{
			nodeList.add(new Node(this));
		}
	}
	
	public void RandomizeWeights()
	{
		for(Node n:nodeList)
		{
			n.randomizeWeights();
		}
	}
	
	public void connectLayer(Layer l)
	{
		for(Node n:nodeList)
			n.connectToLayer(l);
	}
	
	public void calculateNodeValues()
	{
		for(Node n:nodeList)
		{
			n.calculateValue();
		}
	}
	
	public void setAllWeights(double val)
	{
		for(Node n:nodeList)
		{
			n.setAllWeights(val);
		}
	}
	
	public void mixLayer(Layer layer2)
	{
		for(int i = 0;i<nodeList.size();i++)
		{
			if(i<layer2.nodeList.size())
			{
				nodeList.get(i).mixWithNode(layer2.nodeList.get(i));
			}
			else
			{
				System.out.println("TODO Error: Nodelist size differs between networks.  See Layer.mixLayer()");
			}
		}
	}
	
	public void setUnbiased(boolean unbiased)
	{
		for(Node n:nodeList)
		{
			n.unbiased = unbiased;
		}
	}
}