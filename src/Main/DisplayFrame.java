package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class DisplayFrame extends JFrame
{
	BufferedImage image;
	
	public DisplayFrame(BufferedImage imageToDisplay)
	{
		this.image = imageToDisplay;
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(image, 10, 30, null);
	}
}
