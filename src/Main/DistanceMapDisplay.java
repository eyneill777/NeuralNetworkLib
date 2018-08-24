package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DistanceMapDisplay 
{
	DistanceDisplayPanel panel;
	
	public DistanceMapDisplay(Island island)
	{
		JFrame frame = new JFrame();
		panel = new DistanceDisplayPanel(island);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public void repaint(Island island)
	{
		panel.island = island;
		panel.repaint();
	}
	
	public static void displayIsland(Island island)
	{
		JFrame frame = new JFrame();
		DistanceDisplayPanel displayPanel = new DistanceDisplayPanel(island);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(displayPanel);
		frame.setVisible(true);
	}
}

class DistanceDisplayPanel extends JPanel
{
	Island island;
	
	public DistanceDisplayPanel(Island island)
	{
		super();
		this.island = island;
	}
	
	public void paint(Graphics gfx)
	{
		Dimension panelDim = this.getSize();
		
		Graphics2D g = (Graphics2D) gfx;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, panelDim.width, panelDim.height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(NeuralNet n:island.networkList)
		{
			g.setColor(new Color(0, 0, 255, 50));
			if(n.mapCoord.equals(island.bestNetwork.mapCoord))
				g.setColor(new Color(255, 0, 0));
			g.fillOval((int)(n.mapCoord.x-n.connectionRadius), (int)(n.mapCoord.y-n.connectionRadius), (int)n.connectionRadius*2, (int)n.connectionRadius*2);
		}
	}
}
