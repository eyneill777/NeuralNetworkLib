package Main;

public class Connection 
{
	Node node1, node2;
	double weight;
	int node1Index, node2Index;
	//double momentum = 0;
	
	public Connection(Node n1, Node n2, double weight, int node1Index, int node2Index)
	{
		node1 = n1;
		node2 = n2;
		this.weight = weight;
		this.node1Index = node1Index;
		this.node2Index = node2Index;
		n2.connectionList.add(this);
	}
	
	public Connection(Node n1, Node n2, double weight, boolean copying,  int node1Index, int node2Index)
	{
		node1 = n1;
		node2 = n2;
		this.node1Index = node1Index;
		this.node2Index = node2Index;
		this.weight = weight;
	}
	
	public String getSaveString()//return the string form of this connection for saving
	{
		String s = ""; 
		s+=weight;
		s+=":";
		s+=node1Index;
		return s;
	}
}