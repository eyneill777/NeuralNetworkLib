package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class NetworkDisplay 
{
	DisplayPanel panel;
	
	public NetworkDisplay(NeuralNet network)
	{
		JFrame frame = new JFrame();
		panel = new DisplayPanel(network);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public void repaint(NeuralNet network)
	{
		panel.network = network;
		panel.repaint();
	}
	
	public static void displayNetwork(NeuralNet network)
	{
		JFrame frame = new JFrame();
		DisplayPanel displayPanel = new DisplayPanel(network);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(displayPanel);
		frame.setVisible(true);
	}
	
	public static void displayNetwork(NeuralNet network, double[] inputData)
	{
		for(int i = 0;i<inputData.length;i++)
		{
			network.layerList.get(0).nodeList.get(i).setValue(inputData[i]);
		}
		network.propigateNetwork();
		JFrame frame = new JFrame();
		DisplayPanel displayPanel = new DisplayPanel(network);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(displayPanel);
		frame.setVisible(true);
	}
}

class DisplayPanel extends JPanel
{
	NeuralNet network;
	
	public DisplayPanel(NeuralNet network)
	{
		super();
		this.network = network;
	}
	
	public void paint(Graphics gfx)
	{
		Dimension panelDim = this.getSize();
		
		Graphics2D g = (Graphics2D) gfx;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panelDim.width, panelDim.height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int layerSeparation = (panelDim.width-200)/network.layerList.size();
		
		double maxWeight = 0;
		for(int x = 1;x<network.layerList.size();x++)
		{
			for(int y = 0;y<network.layerList.get(x).nodeList.size();y++)
			{
				Node n = network.layerList.get(x).nodeList.get(y);
				for(int i = 0;i<n.connectionList.size();i++)
				{
					if(Math.abs(n.connectionList.get(i).weight) > maxWeight)
						maxWeight = Math.abs(n.connectionList.get(i).weight);
				}
			}
		}
		
		for(int x = 0;x<network.layerList.size();x++)
		{
			if(x>0)
			{
				for(int y = 0;y<network.layerList.get(x).nodeList.size();y++)
				{
					Node n = network.layerList.get(x).nodeList.get(y);
					for(int i = 0;i<n.connectionList.size();i++)
					{
						double w = n.connectionList.get(i).weight;
						if(w > 0)
							g.setColor(new Color(0, (int)(30+w/maxWeight*225), 0));
						else
							g.setColor(new Color((int)(30+Math.abs(w)/maxWeight*225), 0, 0));
						if(w==0)
							g.setColor(new Color((int)(30+Math.abs(w)/maxWeight*225), 0, 0, 0));
						int x1 = 100+(x-1)*layerSeparation+layerSeparation/2;
						int x2 = 100+(x)*layerSeparation+layerSeparation/2;
						int prevLayerNodeSeparation = (panelDim.height-100)/network.layerList.get(x-1).nodeList.size();
						int nodeSeparation = (panelDim.height-100)/network.layerList.get(x).nodeList.size();
						int y1 = 50+n.connectionList.get(i).node1Index*prevLayerNodeSeparation+prevLayerNodeSeparation/2;
						int y2 = 50+y*nodeSeparation+nodeSeparation/2;
						g.drawLine(x1, y1, x2, y2);
					}
				}
			}
		}
		
		for(int x = 0;x<network.layerList.size();x++)
		{
			int dx = 100+x*layerSeparation+layerSeparation/2;
			
			int nodeSeparation = (panelDim.height-100)/network.layerList.get(x).nodeList.size();
			for(int y = 0;y<network.layerList.get(x).nodeList.size();y++)
			{
				Node n = network.layerList.get(x).nodeList.get(y);
				int dy = 50+y*nodeSeparation+nodeSeparation/2;
				g.setColor(Color.blue);
				g.fillOval(dx-25, dy-25, 50, 50);
				g.setColor(Color.white);
				String s = ((int)(n.value*100))/100.0+"";
				g.drawString(s, dx-10, dy+5);
			}
		}
	}
}
