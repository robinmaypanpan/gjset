package gjset.client.gui.pages;

import gjset.client.ClientController;
import gjset.client.gui.MainFrame;
import gjset.gui.DialogPage;
import gjset.gui.framework.Button;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;

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
 * This page provides launching points for the other applications that we might run.
 */
@SuppressWarnings("serial")
public class LaunchPage extends DialogPage
{

	/**
	 * This creates a LaunchPage with a link back to the parent {@link MainFrame} that created
	 * it.
	 *
	 * @param controller - The parent frame of this object.
	 */
	public LaunchPage(ClientController controller)
	{
		super(controller);
		
		title.setText("Main Menu");
		
		createButtons();
	}

	/**
	 * Adds all of the buttons to the screen.
	 *
	 */
	private void createButtons()
	{	
		Rectangle usableArea = border.getInnerArea();
		
		addButtonAndLabel(new AbstractAction("Single Player")
		{
			public void actionPerformed(ActionEvent e)
			{
				// TODO: Fix this. Currently, our server doesn't work, so this won't work.
				System.out.println("The local game server doesn't currently work.");
				
				// First create the server.
//				ServerConsole console = new CommandLineConsole();
//				GameServer server = new GameServer(GameConstants.GAME_PORT, console);
//				new ServerController(server, console);
//				server.listenForClients();
//				
//				// Set up the communicator to talk to the server through.
//				ConcreteClientCommunicator client = new ConcreteClientCommunicator("127.0.0.1", GameConstants.GAME_PORT);
//				
//				// Request a game to be initiated with the username "Player"
//				GameInitiator initiator = new GameInitiator(client, "Score", new GameInitiationHandler()
//				{
//					public void onGameInitiated(ClientGUIController controller)
//					{
//						// First load the new page. 
//						PlayGamePage page = new PlayGamePage(controller, mainFrame);
//						mainFrame.loadPage(page);
//					}
//
//					public void onConnectionFailure(String failureReason)
//					{
//						MessagePage page = new MessagePage(mainFrame, "Could not start game");
//						page.setMessage(failureReason);
//						page.setDestination(new LaunchPage(mainFrame));
//						mainFrame.loadPage(page);
//						System.exit(-1);
//					}
//				});
//				
//				// Initiate the game.
//				initiator.initiateGame();
//
//				// Tell the initiator to start the game as soon as everything is ready.
//				initiator.indicateReadyToStart();
			}
		}, new Rectangle(usableArea.x, 70, usableArea.width, 40));
		
		addButtonAndLabel(new AbstractAction("Multiplayer")
		{
			public void actionPerformed(ActionEvent e)
			{
				controller.launchMultiplayerGame();
			}
		}, new Rectangle(usableArea.x, 120, usableArea.width, 40));
	}

	/**
	 * Added a button and an associated label.
	 *
	 * @param action
	 * @param string
	 */
	private void addButtonAndLabel(Action action, Rectangle frame)
	{
		// You may now proceed to add all of the buttons and labels.
		Button button = new Button(action, lnf.getDialogButtonStyle());
		
		button.setTextVisible(false);
		button.setSize(40, 22);
		button.setLocation(40, frame.y);
		add(button);
		
		JLabel label = new JLabel((String)action.getValue(Action.NAME));
		label.setFont(lnf.getDialogFont());

		label.setLocation(115, frame.y - 10);
		label.setSize(frame.width - label.getX(), 40);
		
		add(label);
	}
}
