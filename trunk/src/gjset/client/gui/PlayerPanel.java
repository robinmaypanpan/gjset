package gjset.client.gui;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gjSet.
 *  
 *  gjSet is Copyright 2008, 2009 Joyce Murton and Andrea Kilpatrick
 *  
 *  The Set Game, card design, and basic game mechanics of the Set Game are
 *  registered trademarks of Set Enterprises. 
 *  
 *  This project is in no way affiliated with Set Enterprises, 
 *  but the authors of gjSet are very grateful for
 *  them creating such an excellent card game.
 *  
 *  gjSet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  gjSet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with gjSet.  If not, see <http://www.gnu.org/licenses/>.
 */

import gjset.client.EngineLinkInterface;
import gjset.data.Player;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Displays information, controls, and data to the player that's on this computer.
 * This should be the primary location for all interface elements.
 */
public class PlayerPanel extends JPanel
{
	//Store the offscreen image and graphics context.
	private Image				offScreenImage;
	private Graphics2D			offScreenGraphics;

	//Store a link to the card table component.
	private CardTableComponent	table;
	
	//Store a lnk to the player whose information will be represented here.
	private Player				player				= new Player(1);

	//Store the font for the name of the player.
	private Font				playerFont;
	
	//Store the colors used for the panel background.
	private static final Color	panelColor			= new Color(0, 51, 0);
	
	//Store the color used for the player name.
	private static final Color	playernameColor		= new Color(255, 192, 0);
	
	/**
	 * 
	 * Construct a PlayerPanel with a link to the indicated {@link CardTableComponent} and {@link GameEngine}.
	 *
	 * @param table The graphical representation of the card table
	 * @param engine The link to the game engine.
	 */
	public PlayerPanel(CardTableComponent table, final EngineLinkInterface engine)
	{
		super();

		this.table = table;

		// Set the size of the card table.
		setPreferredSize(new Dimension(720, 200));
		setMinimumSize(new Dimension(720, 200));
		setMaximumSize(new Dimension(720, 200));

		// Handle resizing the player panel.
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				JComponent component = (JComponent) e.getSource();

				int width = component.getWidth();
				int height = component.getHeight();

				offScreenImage = component.createImage(width, height);
				offScreenGraphics = (Graphics2D) offScreenImage.getGraphics();
				drawPanel(player);
			}
		});

		setLayout(new FlowLayout());

		//Create the "No more sets" button.
		JButton nosetButton = new JButton(new AbstractAction("No more sets.")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				engine.callNoMoreSets();
			}
		});

		// Set the button's size.
		nosetButton.setPreferredSize(new Dimension(180, 30));
		nosetButton.setMaximumSize(new Dimension(180, 30));
		nosetButton.setMinimumSize(new Dimension(180, 30));

		//Create the "Call Set" button.
		JButton callsetButton = new JButton(new AbstractAction("Call Set!")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				//Do nothing at this time.
			}
		});

		// Set the button's size.
		callsetButton.setPreferredSize(new Dimension(180, 30));
		callsetButton.setMaximumSize(new Dimension(180, 30));
		callsetButton.setMinimumSize(new Dimension(180, 30));

		// Add the buttons to the screen.
		add(callsetButton);
		add(nosetButton);
	}

	/**
	 * 
	 * Draw the player panel to the screen.  Whenever this is called, we are also simultaneously updating the player information
	 * being drawn to the screen.
	 *
	 * @param player The updated player information.
	 */
	public void drawPanel(Player player)
	{
		// Draw the background
		offScreenGraphics.setColor(table.getBackground());
		offScreenGraphics.fillRect(0, 0, getWidth(), getHeight());

		// Draw the bottom half of the panel.
		offScreenGraphics.setColor(panelColor);
		offScreenGraphics.fillRoundRect(15, getHeight() - 80, getWidth() - 30, 50, 15, 15);

		// Draw the bottom of the border of the panel.
		offScreenGraphics.setColor(Color.black);
		offScreenGraphics.setStroke(new BasicStroke(3.0f));
		offScreenGraphics.drawRoundRect(15, 0, getWidth() - 30, getHeight() - 30, 15, 15);

		// Draw the top half of the panel.
		offScreenGraphics.setColor(panelColor);
		offScreenGraphics.fillRect(15, 0, getWidth() - 30, getHeight() - 60);

		// Draw the border of the bottom of the panel.
		offScreenGraphics.setColor(Color.black);
		offScreenGraphics.setStroke(new BasicStroke(3.0f));
		offScreenGraphics.drawLine(15, 0, 15, getHeight() - 60);
		offScreenGraphics.drawLine(getWidth() - 15, 0, getWidth() - 15, getHeight() - 60);

		// Display the player score info.
		this.player = player;
		int fontsize = 24;
		playerFont = new Font("American Uncial", Font.PLAIN, fontsize);
		offScreenGraphics.setColor(playernameColor);
		offScreenGraphics.setFont(playerFont);
		offScreenGraphics.drawString(player.getName(), 30, fontsize);
		offScreenGraphics.setColor(Color.white);
		offScreenGraphics.drawString(String.valueOf(player.getScore()), 60, (fontsize *2));		
		
		// Flush the image.
		offScreenImage.flush();

		// Repaint the screen.
		repaint();
	}

	/**
	 * 
	 * Paint the off screen graphics buffer to the screen.
	 *
	 * @param g The Graphics context for this component.
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(offScreenImage, 0, 0, getWidth(), getHeight(), this);
	}
}