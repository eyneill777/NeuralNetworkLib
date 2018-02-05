package Main;

import java.util.ArrayList;

public class Layer 
{
	ArrayList<Node> nodeList = new ArrayList<Node>();
	
	public Layer()
	{
		
	}
	
	public Layer(int numNodes)
	{
		for(int i=0;i<numNodes;i++)
		{
			nodeList.add(new Node());
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
}
