package gjset.client.gui;

import gjset.client.ClientGUIController;
import gjset.data.Card;
import gjset.gui.SymbolImageFactory;
import gjset.gui.framework.ResourceManager;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of Combo Cards.
 *  
 *  Combo Cards is Copyright 2008-2010 Artless Entertainment
 *  
 *  Set� is a registered trademark of Set Enterprises. 
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
 * This class stores the necessary code to graphically represent a given Card object.
 */
@SuppressWarnings("serial")
public class CardPanel extends JComponent
{
	// Store the different images that represent the card itself.
	private Image cardImage;
	private Image[] cardHalo;
	private Image cardBack;
	
	// Store the symbol image factory so that we can always request the latest symbol image based on our carddata.
	private SymbolImageFactory symbolFactory;
	
	// Store the controller so that we can send GUI events to it.
	private ClientGUIController controller;
	
	// Store the state of the card.
	private boolean highlighted;
	private boolean faceUp;
	
	// Indicates the type of card highlight to use (normal red border, red X, or green border)
	private int	highlightType;
	
	// Store the current card data on display.
	private Card cardData;
	
	public static final int NORMAL = 0;
	public static final int CORRECT_SET = 1;
	public static final int INCORRECT_SET = 2;
	
	/**
	 * 
	 * Create this CardPanel with the indicated data and controller.
	 *
	 * @param controller
	 * @param cardData
	 */
	public CardPanel(ClientGUIController controller, Card cardData)
	{
		super();
		
		this.cardData = cardData;
		this.controller = controller;
		
		highlighted = false;
		faceUp = true;
		highlightType = NORMAL;
		
		cardHalo = new Image[3];
		
		symbolFactory = SymbolImageFactory.getInstance();
		
		obtainResources();
		configurePanel();
		addActionListener();
	}
	
	/**
	 * 
	 * Resets this cards information to the indicated card data.
	 *
	 * @param cardData
	 */
	public void setCardData(Card cardData)
	{
		this.cardData = cardData;
		repaint();
	}
	
	/**
	 * 
	 * Can be used to turn this card face up or face down.
	 *
	 * @param value
	 */
	public void setFaceUp(boolean value)
	{
		this.faceUp = value;
		repaint();
	}
	
	/**
	 * 
	 * This function is used to set whether this card should be highlighted or not.
	 *
	 * @param value
	 */
	public void setHighlighted(boolean value, int highlightType)
	{
		this.highlighted = value;
		this.highlightType = highlightType;
		repaint();
	}


	/**
	 * 
	 * Paint this component.
	 *
	 * @param g
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		// Draw the card background and abort early if face down.
		if(!faceUp || cardData == null)
		{
			g.drawImage(cardBack, 0, 0, this);
			return;
		}
		
		// Draw the card background.
		g.drawImage(cardImage, 0, 0, this);
		
		// Draw the symbols, centered on the image.
		Image symbolImage = symbolFactory.getImage(cardData);
		g.drawImage(symbolImage, cardImage.getWidth(this) / 2 - symbolImage.getWidth(this) / 2,
				cardImage.getHeight(this) / 2 - symbolImage.getHeight(this) / 2, this);
		
		// Draw the halo if highlighted.
		if(highlighted)
		{
			g.drawImage(cardHalo[highlightType], 0, 0, this);
		}
	}

	/**
	 * Adds all of the action listeners to our class.
	 *
	 */
	private void addActionListener()
	{
		MouseInputAdapter ma = new MouseInputAdapter()
		{
			public void mouseReleased(MouseEvent me)
			{
				// Verify that the mouse was released inside this card.
				if(faceUp && contains(me.getPoint()))
				{
					// Tell the controller we selected this card.
					controller.selectCard(cardData);
				}
			}
		};
		
		addMouseListener(ma);
	}

	/**
	 * Configure this JPanel with the basic settings.
	 *
	 */
	private void configurePanel()
	{
		setSize(cardImage.getWidth(this), cardImage.getHeight(this));
		setOpaque(false);
	}

	/**
	 * Get all of the resources we need for building cards
	 *
	 */
	private void obtainResources()
	{
		ResourceManager resourceManager = ResourceManager.getInstance();
		cardImage = resourceManager.getImage("card.png");
		
		cardHalo[NORMAL] = resourceManager.getImage("card_halo.png");
		cardHalo[CORRECT_SET] = resourceManager.getImage("card_combo_true.png");
		cardHalo[INCORRECT_SET] = resourceManager.getImage("card_combo_false.png");

		cardBack = resourceManager.getImage("card_back.png");
	}
}
