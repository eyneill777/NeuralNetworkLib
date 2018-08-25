package Main;

import java.util.ArrayList;

public class Node 
{
	double bias, value, gradient;
	ArrayList<Connection> connectionList = new ArrayList<Connection>();
	Layer layer;
	boolean unbiased = false;
	int maxBias = 15, minBias = -15;
	double inputErrorSignal = 0;
	
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
		bias = Math.random()*(maxBias-minBias)+minBias;
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
	
	private double getOutputNodeErrorSignal(double targetValue)
	{
		inputErrorSignal = (targetValue-getValue())*activationFunctionDerivative(getValue())*(1-getValue());
		System.out.println(targetValue+"\t"+getValue()+"\t"+activationFunctionDerivative(getValue())+"\t");
		return inputErrorSignal;
	}
	
	private double getHiddenLayerErrorSignal()
	{
		double d = getValue()*(1-getValue());
		return d * inputErrorSignal;
		
	}
	
	public void setGradientAndPropigateBack(boolean reset, int depth, double targetVal)
	{	
		if(depth <= 1)
		{
			inputErrorSignal = getOutputNodeErrorSignal(targetVal);
		}
		else
		{
			inputErrorSignal = getHiddenLayerErrorSignal();
		}
		for(Connection c:connectionList)
		{	
			c.node1.inputErrorSignal+=inputErrorSignal*c.getWeight();
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