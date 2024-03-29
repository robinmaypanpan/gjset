package gjset.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gjset.GameConstants;
import gjset.client.ClientGUIController;
import gjset.client.ClientGUIModel;
import gjset.client.ConcreteClientCommunicator;
import gjset.client.ConcreteClientGUIController;
import gjset.client.GameInitiationHandler;
import gjset.client.GameInitiator;
import gjset.data.CardTableData;
import gjset.server.GameServer;
import gjset.server.ServerController;
import gjset.server.game.GameModel;

import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
 * Exercise the server's controller to verify that its behavior is correct.
 */
public class TestServerController
{
	private MockMessageHandler clientHandler;
	
	private ConcreteClientCommunicator client;
	private ConcreteClientGUIController clientController;
	
	private GameServer server;
	private ServerController serverController;

	private GameInitiator initiator;
	
	/**
	 * Sets up the socket server that will be used to communicate with the client communicator.
	 */
	@Before
	public void setUp()
	{
		// Create the server!
		server = new GameServer(GameConstants.GAME_PORT);
		
		serverController = new ServerController(server);
		
		// Start listening.
		server.listenForClients();
		
		// Create our basic client.
		client = new ConcreteClientCommunicator("127.0.0.1", GameConstants.GAME_PORT);
		
		// In order to fully test the server controller, we'll need the entire application created.
		clientHandler = new MockMessageHandler();
		client.addMessageHandler(clientHandler);
		
		initiator = new GameInitiator(client, "Player", new GameInitiationHandler()
		{
			public void onGameInitiated(ClientGUIController controller)
			{
				clientController = (ConcreteClientGUIController) controller;
			}

			public void onConnectionFailure(String failureReason)
			{
				org.junit.Assert.fail("Connection failed");
			}
			
		});
		
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Destroy the server.
	 */
	@After
	public void tearDown()
	{
		if(server != null && client != null)
		{
			server.destroy();
			client.destroy();
			if(clientController != null) clientController.destroy();
			serverController.onHide();
			
			server = null;
			client = null;
			clientHandler = null;
			serverController = null;
			clientController = null;
			
			try
			{
				Thread.sleep(200);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Verify that the client controller receives a game update upon connection.
	 */
	@Test
	public void testGameUpdateUponConnection()
	{
		// We should already be connected, so let's see if that update went through.
		Element lastMessage = clientHandler.getLastMessage();
		
		Element gameupdateElement = lastMessage.element("gameupdate");
		
		assertNotNull(gameupdateElement);
	}
	
	/**
	 * Verify that starting a new game works correctly on the server.
	 */
	@Test
	public void testStartNewGame()
	{
		initiator.indicateReadyToStart();
		
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		GameModel serverModel = serverController.getModel();
		CardTableData serverCardTable = serverModel.getCardTable();
		
		// Verify things on the server.
		assertEquals(69, serverModel.getDeck().getRemainingCards());
		assertEquals(12, serverCardTable.getNumCards());
		assertEquals(3, serverCardTable.getRows());
		assertEquals(4, serverCardTable.getCols());
	
		ClientGUIModel clientModel = clientController.getModel();
		CardTableData clientCardTable = clientModel.getCardTable();
		
		// Verify things on the client.
		assertEquals(69, clientModel.getCardsInDeck());
		assertEquals(12, clientCardTable.getNumCards());
		assertEquals(3, clientCardTable.getRows());
		assertEquals(4, clientCardTable.getCols());
		
		assertTrue(clientModel.canCallSet());
		assertTrue(clientModel.canDrawCards());
		assertTrue(clientModel.canSelectCards());
	}
	
	/**
	 * Verify that we can draw cards from a new game.
	 */
	@Test
	public void testDrawingCards()
	{
		initiator.indicateReadyToStart();
		
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Send the command from the client.
		clientController.drawMoreCards();
		
		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Verify the changes on the server.
		GameModel serverModel = serverController.getModel();
		CardTableData serverCardTable = serverModel.getCardTable();
		assertEquals(66, serverModel.getDeck().getRemainingCards());
		assertEquals(15, serverCardTable.getNumCards());
		assertEquals(3, serverCardTable.getRows());
		assertEquals(5, serverCardTable.getCols());
		
		// Verify the changes on the client.
		ClientGUIModel clientModel = clientController.getModel();
		CardTableData clientCardTable = clientModel.getCardTable();
		assertEquals(66, clientModel.getCardsInDeck());
		assertEquals(15, clientCardTable.getNumCards());
		assertEquals(3, clientCardTable.getRows());
		assertEquals(5, clientCardTable.getCols());
	}
	
	/**
	 * Verify that calling set actually works correctly.
	 */
	@Test
	public void testCallSet()
	{
		initiator.indicateReadyToStart();
		
		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Send the command from the client.
		clientController.callSet();
		
		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Verify the changes on the server.
		GameModel serverModel = serverController.getModel();
		
		assertEquals(GameConstants.GAME_STATE_SET_CALLED, serverModel.getGameState());
		assertEquals(clientController.getModel().getLocalPlayer().getId(), serverModel.getSetCaller().getId());
		
		// Verify the changes on the client.
		ClientGUIModel clientModel = clientController.getModel();
		
		assertEquals(GameConstants.GAME_STATE_SET_CALLED, clientModel.getGameState());
		
		assertFalse(clientModel.canCallSet());
		assertFalse(clientModel.canDrawCards());
		assertTrue(clientModel.canSelectCards());
		
	}
	
	/**
	 * Verify that card selection works correctly.
	 */
	@Test
	public void testCardSelection()
	{
		// Start the game.
		initiator.indicateReadyToStart();
		
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Get the data from the client.
		ClientGUIModel clientModel = clientController.getModel();
		CardTableData clientCardTable = clientModel.getCardTable();
		
		// Send the command from the client.
		clientController.selectCard(clientCardTable.getCardAt(0,0));

		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Start by verifying that set has been called.
		assertEquals(GameConstants.GAME_STATE_SET_CALLED, clientModel.getGameState());
		assertTrue(clientModel.canSelectCards());
		assertFalse(clientModel.canDrawCards());
		assertFalse(clientModel.canCallSet());
		
		clientCardTable = clientModel.getCardTable();
		
		// Now verify that the card is highlighted.
		assertTrue(clientCardTable.isHighlighted(clientCardTable.getCardAt(0,0)));
		
		// We'll continue by selecting another card.
		clientController.selectCard(clientCardTable.getCardAt(0,1));

		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Start by verifying that set is still called.
		assertEquals(GameConstants.GAME_STATE_SET_CALLED, clientModel.getGameState());
		assertTrue(clientModel.canSelectCards());
		assertFalse(clientModel.canDrawCards());
		assertFalse(clientModel.canCallSet());
		
		clientCardTable = clientModel.getCardTable();
		
		// Now verify that the card is highlighted.
		assertTrue(clientCardTable.isHighlighted(clientCardTable.getCardAt(0,1)));
		
		// Then deselect the original.
		clientController.selectCard(clientCardTable.getCardAt(0,0));

		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		// Start by verifying that set is still called.
		assertEquals(GameConstants.GAME_STATE_SET_CALLED, clientModel.getGameState());
		assertTrue(clientModel.canSelectCards());
		assertFalse(clientModel.canDrawCards());
		assertFalse(clientModel.canCallSet());
		
		clientCardTable = clientModel.getCardTable();
		
		// Now verify that the card is highlighted.
		assertFalse(clientCardTable.isHighlighted(clientCardTable.getCardAt(0,0)));
	}
}
