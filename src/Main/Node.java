package Main;

import java.util.ArrayList;

public class Node 
{
	double bias, value;
	ArrayList<Connection> connectionList = new ArrayList<Connection>();
	Layer layer;
	boolean unbiased = false;
	//double biasMomentum = 0;
	
	public Node(Layer layer)
	{
		bias = 0;
		value = 0;
		this.layer = layer;
	}
	
	public Node(double bias, double value, Layer layer)
	{
		this.bias = bias;
		this.value = value;
		this.layer = layer;
	}
	
	public void mixWithNode(Node node2)
	{
		if(Math.random()<.5)
		{
			for(int i = 0;i<connectionList.size();i++)
			{
				if(i<node2.connectionList.size())
				{
					connectionList.get(i).weight = node2.connectionList.get(i).weight;
				}
				else
				{
					System.out.println("TODO Error: ConnectionList size differs between networks.  See Node.mixWithNode()");
				}
			}
			bias = node2.bias;
		}
	}
	
	public void setAllWeights(double val)
	{
		for(Connection c:connectionList)
		{
			c.weight = val;
		}
	}
	
	public void connectToLayer(Layer l)
	{
		for(int i = 0;i<l.nodeList.size();i++)
		{
			new Connection(l.nodeList.get(i), this, 0, i, layer.nodeList.indexOf(this));
		}
	}
	
	public void calculateValue()
	{
		double sum = 0;
		for(int i = 0;i<connectionList.size();i++)
		{
			sum+=connectionList.get(i).node1.value*connectionList.get(i).weight;
		}
		if(!unbiased)
			sum+=bias;
		setValue(activationFunction(sum));
	}
	
	private double activationFunction(double x)
	{
		return (1.0/(1+Math.pow(Math.E, (-1*x)))-.5)*2;
	}

	public double getBias() {
		if(unbiased)
			return 0;
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}