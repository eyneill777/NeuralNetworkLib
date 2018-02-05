package Main;

public class Connection 
{
	Node node1, node2;
	double weight;
	
	public Connection(Node n1, Node n2, double weight)
	{
		node1 = n1;
		node2 = n2;
		this.weight = weight;
		n2.connectionList.add(this);
	}
	
	public Connection(Node n1, Node n2, double weight, boolean copying)
	{
		node1 = n1;
		node2 = n2;
		this.weight = weight;
	}
}
