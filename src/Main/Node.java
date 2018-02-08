package Main;

import java.util.ArrayList;

public class Node 
{
	double bias, value;
	ArrayList<Connection> connectionList = new ArrayList<Connection>();
	
	public Node()
	{
		bias = 0;
		value = 0;
	}
	
	public Node(double bias, double value)
	{
		this.bias = bias;
		this.value = value;
	}
	
	public void mixWithNode(Node node2)
	{
		for(int i = 0;i<connectionList.size();i++)
		{
			if(i<node2.connectionList.size())
			{
				connectionList.get(i).weight = (connectionList.get(i).weight+node2.connectionList.get(i).weight)/2;
			}
			else
			{
				System.out.println("TODO Error: ConnectionList size differs between networks.  See Node.mixWithNode()");
			}
		}
		bias = bias+node2.bias/2.0;
	}
	
	public void connectToLayer(Layer l)
	{
		for(int i = 0;i<l.nodeList.size();i++)
		{
			new Connection(l.nodeList.get(i), this, 0);
		}
	}
	
	public void calculateValue()
	{
		double sum = 0;
		for(int i = 0;i<connectionList.size();i++)
		{
			sum+=connectionList.get(i).node1.value*connectionList.get(i).weight;
		}
		sum+=bias;
		setValue(activationFunction(sum));
	}
	
	private double activationFunction(double x)
	{
		return 1.0/(1+Math.pow(Math.E, (-1*x)));
	}

	public double getBias() {
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
