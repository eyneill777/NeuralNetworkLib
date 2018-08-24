package Main;

import java.util.ArrayList;

public class Node 
{
	double bias, value, gradient;
	ArrayList<Connection> connectionList = new ArrayList<Connection>();
	Layer layer;
	boolean unbiased = false;
	
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
	
	public void randomizeWeights()
	{
		for(Connection c:connectionList)
		{
			c.setWeight(Math.random()*(c.maxWeight-c.minWeight)+c.minWeight);
		}
	}
	
	public void randomizeBias()
	{
		bias = Math.random()*30-15;
	}
	
	public void mixWithNode(Node node2)
	{
		if(Math.random()<.5)
		{
			for(int i = 0;i<connectionList.size();i++)
			{
				if(i<node2.connectionList.size())
				{
					connectionList.get(i).setWeight(node2.connectionList.get(i).getWeight());
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
			c.setWeight(val);
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
			sum+=connectionList.get(i).node1.value*connectionList.get(i).getWeight();
		}
		if(!unbiased)
			sum+=bias;
		setValue(activationFunction(sum));
	}
	
	public void setGradientAndPropigateBack(double grad, boolean reset, double learnRate, int depth)
	{
		if(reset)
		{
			gradient = 0;
			for(Connection c:connectionList)
			{
				c.setGradient(0);
			}
		}
		
		double sum = 0;
		for(int i = 0;i<connectionList.size();i++)
		{
			sum+=connectionList.get(i).node1.value*connectionList.get(i).getWeight();
		}
		if(!unbiased)
			sum+=bias;
		double dg = (grad)*activationFunctionDerivative(sum);
		double db = (grad)*activationFunctionDerivative(sum);
		gradient+=db*learnRate;
		for(int i = 0;i<depth;i++)
			System.out.print("\t");
		System.out.println(gradient+" bgradient");
		for(Connection c:connectionList)
		{	
			c.setGradient(c.getGradient() + dg*(c.getWeight()*c.node1.value)*learnRate);
			for(int i = 0;i<depth;i++)
				System.out.print("\t");
			System.out.println("node1n value: "+c.node1.value);
			double desiredChange = grad*c.getWeight();
			for(int i = 0;i<depth;i++)
				System.out.print("\t");
			System.out.println(c.getGradient()+" wgradient");
			//System.out.println("\t"+c.weight+" weight");
			c.node1.setGradientAndPropigateBack(desiredChange, reset, learnRate, depth+1);
		}
	}
	
	//private double calcErrorSignal()
	{
		
		
		double e;
		//double dg = activationFunctionDerivative(sum);
		//double dk = 
		//return e;
	}
	
	private double activationFunctionDerivative(double x)
	{
		return (Math.pow(Math.E,-1*x)/(Math.pow((1+Math.pow(Math.E, -1*x)), 2)));
	}
	
	private double activationFunction(double x)
	{
		return (1.0/(1+Math.pow(Math.E, (-1*x))));
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