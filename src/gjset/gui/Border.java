package gjset.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of Combo Cards!
 *  
 *  Combo Cards is Copyright 2008-2010 Artless Entertainment
 *  
 *  The Set Game, card design, and other elements of Set of the Set Game are
 *  registered trademarks of Set Enterprises. 
 *  
 *  This project is in no way affiliated with Set Enterprises, 
 *  but the authors of Combo Cards are very grateful for
 *  them creating such an excellent card game.
 *  
 *  Combo Cards is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  Combo Cards is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with Combo Cards.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 */
public class Border extends JComponent
{
	private Image cornerUL;
	private Image cornerUR;
	private Image cornerLL;
	private Image cornerLR;
	private Image left;
	private Image right;
	private Image top;
	private Image bottom;
	
	public Border(String style, boolean useTitle)
	{
		ResourceManager resourceManager = ResourceManager.getInstance();
		
		if(useTitle)
		{
			cornerUL = resourceManager.getImage(style + "_title_l.png");
			cornerUR = resourceManager.getImage(style + "_title_r.png");
			top 	= resourceManager.getImage(style + "_title_m.png");
		}
		else
		{	
			cornerUL = resourceManager.getImage(style + "_ul.png");
			cornerUR = resourceManager.getImage(style + "_ur.png");
			top 	= resourceManager.getImage(style + "_um.png");
		}
		
		cornerLL = resourceManager.getImage(style + "_ll.png");
		cornerLR = resourceManager.getImage(style + "_lr.png");
		
		left 	= resourceManager.getImage(style + "_ml.png");
		right 	= resourceManager.getImage(style + "_mr.png");
		bottom 	= resourceManager.getImage(style + "_lm.png");
	}

	public void paintComponent(Graphics g)
	{
		int width = getWidth();
		int height = getHeight();
		
		int topCornerWidth = cornerUL.getWidth(this);
		int topCornerHeight = cornerUL.getHeight(this);
		
		int bottomCornerWidth = cornerLL.getWidth(this);
		int bottomCornerHeight = cornerLL.getHeight(this);
		
		//Draw the sides
		Rectangle area = new Rectangle( topCornerWidth, 0, width - topCornerWidth - bottomCornerWidth, top.getHeight(this));
		texturePaintHorizontal(g, top, area);
		
		area = new Rectangle( bottomCornerWidth, height - bottom.getHeight(this), 
				width - topCornerWidth - bottomCornerWidth, bottom.getHeight(this));
		texturePaintHorizontal(g, bottom, area);
		
		area = new Rectangle( 0, topCornerHeight, left.getWidth(this), height - topCornerHeight - bottomCornerHeight);
		texturePaintVertical(g, left, area);
		
		area = new Rectangle( width - right.getWidth(this), topCornerHeight, 
				right.getWidth(this), height - topCornerHeight - bottomCornerHeight);
		texturePaintVertical(g, right, area);
		
		//Draw the corners
		g.drawImage(cornerUL, 0, 0, this);
		g.drawImage(cornerUR, width - topCornerWidth, 0, this);
		g.drawImage(cornerLL, 0, height - bottomCornerHeight, this);
		g.drawImage(cornerLR, width - bottomCornerWidth, height - bottomCornerHeight, this);
	}

	/**
	 * Paint a single texture horizontally across the screen.
	 *
	 * @param g
	 * @param image
	 * @param areaToPaint
	 */
	protected void texturePaintHorizontal(Graphics g, Image image, Rectangle areaToPaint)
	{
		int imageWidth = image.getWidth(this);
		int imageHeight = image.getHeight(this);
		
		int cols = areaToPaint.width / imageWidth;
		
		for (int x = 0; x < cols; x++) 
		{
			g.drawImage(image, x * imageWidth + areaToPaint.x, areaToPaint.y, this);
		}
		
		//Draw the last partial image in.
		int remainingWidth = areaToPaint.width - cols * imageWidth;
		
		BufferedImage temp = new BufferedImage(remainingWidth, imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2 = temp.createGraphics();
		g2.drawImage(image, 0, 0, this);
		temp.flush();
		
		g.drawImage(temp, areaToPaint.width + areaToPaint.x - remainingWidth, areaToPaint.y, this);
	}

	/**
	 * Paint a single texture verticall up and down the screen.
	 *
	 * @param g
	 * @param image
	 * @param areaToPaint
	 */
	protected void texturePaintVertical(Graphics g, Image image, Rectangle areaToPaint)
	{
		int imageWidth = image.getWidth(this);
		int imageHeight = image.getHeight(this);
		
		int rows = areaToPaint.height / imageHeight;
		
		for (int y = 0; y < rows; y++) 
		{
			g.drawImage(image, areaToPaint.x, y * imageHeight + areaToPaint.y, this);
		}
		
		//Draw the last partial image in.
		int remainingHeight = areaToPaint.height - rows * imageHeight;
		
		BufferedImage temp = new BufferedImage(imageWidth, remainingHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2 = temp.createGraphics();
		g2.drawImage(image, 0, 0, this);
		temp.flush();
		
		g.drawImage(temp, areaToPaint.x, areaToPaint.height + areaToPaint.y - remainingHeight, this);
	}

	/**
	 * TODO: Describe method
	 *
	 * @return
	 */
	public int getBorderWidth()
	{
		return bottom.getHeight(this);
	}
}