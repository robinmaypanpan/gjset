package gjset.client.gui;

import gjset.gui.framework.ResourceManager;
import gjset.gui.framework.SimpleImagePanel;
import gjset.gui.framework.SimpleLookAndFeel;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;

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
 *	This displays the deck to players.
 */
@SuppressWarnings("serial")
public class DeckPanel extends JPanel
{
	private Image deckIcon;
	private JLabel label;
	private SimpleLookAndFeel lnf;
	private final int NUDGE = 5;
	
	public DeckPanel()
	{	
		lnf = SimpleLookAndFeel.getLookAndFeel();

		ResourceManager resourceManager = ResourceManager.getInstance();
		deckIcon = resourceManager.getImage("icon_deck.png");
		
		configurePanel();
		
		createDeckIconPanel();
		
		createDeckSizeLabel();
	}

	/**
	 * Constructs the deck size label.
	 *
	 */
	private void createDeckSizeLabel()
	{
		label = new JLabel("0", JLabel.RIGHT);
		
		label.setForeground(lnf.getDeckSizeColor());
		label.setFont(lnf.getDeckSizeFont());
		
		label.setSize(getWidth() - deckIcon.getWidth(this) - NUDGE, getHeight());
		label.setLocation(0, 0);
		add(label);
	}

	/**
	 * Constructs the deck icon panel
	 *
	 */
	private void createDeckIconPanel()
	{
		SimpleImagePanel panel = new SimpleImagePanel(deckIcon);
		panel.setLocation(getWidth() - deckIcon.getWidth(this), NUDGE);
		panel.setSize(deckIcon.getWidth(this), deckIcon.getHeight(this));
		add(panel);
	}

	/**
	 * Configure the basic panel settings.
	 *
	 */
	private void configurePanel()
	{
		setLayout(null);
		setOpaque(false);
		
		Rectangle playingFrame = MainFrame.PLAYING_FIELD_AREA;

		setSize(100, deckIcon.getHeight(this) + NUDGE);
		setLocation(playingFrame.width - getWidth() + playingFrame.x - NUDGE, playingFrame.height - getHeight() + 2 * NUDGE);
	}

	/**
	 * Called when the GUI wants to update the number of cards in the deck.
	 *
	 * @param cardsInDeck
	 */
	public void updateSize(int cardsInDeck)
	{
		label.setText("" + cardsInDeck);
		repaint();
	}

	
	
}
