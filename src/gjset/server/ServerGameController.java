package gjset.server;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of Combo Cards.
 *  
 *  Combo Cards is Copyright 2008-2010 Artless Entertainment
 *  
 *  The Set Game, card design, and basic game mechanics of the Set Game are
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

import gjset.GameConstants;
import gjset.data.Card;
import gjset.data.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

/**
 * This class handles all operations for the server.  Whenever something changes, all players are updated.
 */
public class ServerGameController implements ServerMessageHandler, Observer
{	
	private GameModel model;
	private DocumentFactory documentFactory;
	private GameServer server;
	
	/**
	 * Create a ServerGameController
	 * 
	 * This constructor instantiates and initializes all of the game model data.
	 * @param gamePort 
	 *
	 */
	public ServerGameController(GameServer server)
	{
		this.server = server;
		server.addMessageHandler(this);
		
		model = new GameModel();
		model.addObserver(this);
		
		documentFactory = DocumentFactory.getInstance();
	}

	/**
	 * Start a new game and update the players with the relevant information.
	 *
	 */
	public void startNewGame()
	{
		model.startNewGame();
		
		updateAllPlayers();
	}

	/**
	 * Handle a message incoming from the server.
	 */
	public void handleMessage(PlayerClientHandler client, Element message)
	{
		// Start by getting the player id.
		int playerId = client.getPlayerId();
		
		// Now see if this was a command.
		Element responseElement = null;
		Element commandElement = message.element("command");
		if(commandElement != null)
		{
			String commandType = commandElement.attributeValue("type", "");
			if(commandType.equals("drawcards"))
			{
				responseElement = drawCards();
			}
			else if(commandType.equals("callset"))
			{
				responseElement = callSet(playerId);
			}
			else if(commandType.equals("selectcard"))
			{
				Element cardElement = commandElement.element("card");
				Card card = new Card(cardElement);
				responseElement = toggleSelection(playerId, card);
			}
			else if(commandType.equals("joinasplayer"))
			{
				// This is an initialization message.
				String username = commandElement.element("username").getText();
				
				responseElement = bindClient(client, username);
			}
			else if(commandType.equals("startgame"))
			{
				// For now, we'll just automatically start the game.
				startNewGame();
			}
		}
		
		// See if we need to send a response to the player.
		if(responseElement != null)
		{
			// Add the original command so that the client can cross reference it, if necessary.
			responseElement.add(commandElement.createCopy());
			
			// Send the result back to the player that sent it in.
			client.sendMessage(responseElement);
			
			// Check to see if we need to update all players with new data.
			String result = responseElement.attributeValue("result", "false");
			if(result.equals("success"))
			{
				updateAllPlayers();
			}
		}
	}

	/**
	 * Send a game update to the new client.
	 *
	 * @param client
	 * @see gjset.server.ServerMessageHandler#handleNewClient(gjset.server.PlayerClientHandler)
	 */
	public Element bindClient(PlayerClientHandler client, String username)
	{
		// Verify that we can do this.
		Player player = model.getExistingPlayer(username);
		
		if(player == null)
		{
			if(model.getPlayers().size() < GameConstants.MAX_PLAYERS)
			{
				// Create a new player in the model.
				player = model.addNewPlayer(username);
				client.setPlayer(player);

				// Return the player information.
				Element commandResponse = getCommandResponse(true, null);

				Element newPlayerElement = documentFactory.createElement("newplayer");
				newPlayerElement.add(player.getXMLRepresentation());
				
				commandResponse.add(newPlayerElement);
				
				return commandResponse;
			}
			else
			{
				return getCommandResponse(false, "You've already reached the max players for this server.");
			}
			
		}
		else
		{
			// Return the player information.
			Element commandResponse = getCommandResponse(true, null);

			Element newPlayerElement = documentFactory.createElement("newplayer");
			newPlayerElement.add(player.getXMLRepresentation());
			
			commandResponse.add(newPlayerElement);
			
			return commandResponse;
		}
	}

	/**
	 * After a change event from the model, update all of the players.
	 *
	 * @param arg0
	 * @param arg1
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable modelObservable, Object updateType)
	{
		Boolean updateWasSolicited = (Boolean)updateType;
		
		// Only update for unsolicited updates.  Solicited updates can fend for themselves.
		if(updateType != null && !updateWasSolicited.booleanValue())
		{
			updateAllPlayers();
		}
	}

	/**
	 * Destroy this object.
	 *
	 */
	public void destroy()
	{
		model = null;
		server = null;
		documentFactory = null;
	}

	/**
	 * Return the game model used by this controller. (Currently just used for test purposes.)
	 *
	 * @return
	 */
	public GameModel getModel()
	{
		return model;
	}

	/**
	 * Request more cards be drawn from the deck.
	 *
	 * @return An XML tag containing the results of the command
	 */
	private Element drawCards()
	{
		boolean success = false;
		String failureReason = null;
		
		int gameState = model.getGameState();
		
		// If the game is not currently idle, do nothing.
		if (gameState == GameConstants.GAME_STATE_IDLE)
		{
			Deck deck = model.getDeck();
			CardTable cardTable = model.getCardTable();
			
			if (deck.getRemainingCards() < 3)
			{
				// If we're out of cards, do nothing.
				failureReason = "No cards remaining";
			}
			else if (cardTable.getNumCards() < CardTable.CARD_LIMIT)
			{
				// We *CAN* draw the cards! YAY!
				model.drawCards();
				
				success = true;
			}
			else
			{
				// There are plenty of cards on the table.  No need to draw more.
				failureReason = "Sufficient cards already on table";
			}
		}
		else
		{
			// The game is not in the correct state to draw cards.
			failureReason = "Game is not idle.";
		}
		
		return getCommandResponse(success, failureReason);
	}
	
	/**
	 * Request that this player calls set.
	 *
	 * @return
	 */
	private Element callSet(int playerId)
	{
		// Verify that we can actually call set.
		if(model.getGameState() == GameConstants.GAME_STATE_IDLE)
		{
			model.callSet(playerId);
			return getCommandResponse(true, null);
		}
		else
		{
			return getCommandResponse(false, "Set has already been called");
		}
	}

	/**
	 * Request card selection for the indicated player.
	 *
	 * @param playerId
	 * @param card
	 * @return
	 */
	private Element toggleSelection(int playerId, Card card)
	{
		// First toggle selection on the card.
		int gameState = model.getGameState();
		Element commandResponse = null;
		
		if(gameState == GameConstants.GAME_STATE_IDLE)
		{
			// Call set.
			model.callSet(playerId);
			
			// Select the card.
			model.toggleCardSelection(card);
			
			commandResponse = getCommandResponse(true, null);
		}
		else if(gameState == GameConstants.GAME_STATE_SET_CALLED
				&& playerId == model.getSetCaller().getId())
		{
			// Allow the next card to be selected.
			model.toggleCardSelection(card);
			
			commandResponse = getCommandResponse(true, null);
		}
		else
		{
			// Abort early if we can't select a card.
			commandResponse = getCommandResponse(false, "You can't select cards");
		}
		
		// Now see if we've got 3 cards selected.
		List<Card> selectedCards = model.getCardTable().getSelectedCards();
		if(selectedCards.size() == 3)
		{
			// We do have 3 cards!  Check for a set!
			Element messageAddendum = documentFactory.createElement("setresult");
			
			// Check if this is a set.
			boolean setSelected = model.resolveSet();
			if(setSelected)
			{
				// Append a message about the result of the set.
				messageAddendum.addAttribute("isset", "true");
			}
			else
			{	
				// Append a message about the result of the set.
				messageAddendum.addAttribute("isset", "false");
			}
			
			commandResponse.add(messageAddendum);
		}
		
		
		return commandResponse;
	}

	/**
	 * Return your basic success response.
	 *
	 * @param success  True if successful, false if not.
	 * @param reason Adds a reason that can be added to the command response.
	 * 
	 * @return
	 */
	private Element getCommandResponse(boolean success, String reason)
	{
		String failureReason = "";
		
		// Create our response
		String resultText = "failed";
		if(success)
		{
			resultText = "success";
		}
		
		Element responseElement = documentFactory.createElement("commandresponse");
		responseElement.addAttribute("result", resultText);
		
		if(reason != null)
		{
			Element reasonElement = documentFactory.createElement("reason");
			reasonElement.setText(failureReason);
			responseElement.add(reasonElement);
		}
		
		return responseElement;
	}

	/**
	 * send game updates to all of the players.
	 *
	 */
	private void updateAllPlayers()
	{
		// Start by building the update XML.
		Element gameUpdate = buildGameUpdate();
		
		List<PlayerClientHandler> clients = server.getClients();
		Iterator<PlayerClientHandler> iterator = clients.iterator();

		while(iterator.hasNext())
		{
			PlayerClientHandler client = iterator.next();
			client.sendMessage(gameUpdate.createCopy());
		}
		
	}

	/**
	 * Create the game update to send to the clients.
	 *
	 * @return
	 */
	private Element buildGameUpdate()
	{
		Element root = documentFactory.createElement("gameupdate");
		
		// Start with the deck size.
		Element deckElement = documentFactory.createElement("deck");
		deckElement.setText("" + model.getDeck().getRemainingCards());
		root.add(deckElement);
		
		// Move onto the game state.
		Element gameStateElement = documentFactory.createElement("gamestate");
		gameStateElement.setText("" + model.getGameState());
		root.add(gameStateElement);
		
		// Now do the players.
		Element playersElement = documentFactory.createElement("players");
		
		Iterator<Player> iterator = model.getPlayers().iterator();
		while(iterator.hasNext())
		{
			Player player = iterator.next();
			playersElement.add(player.getXMLRepresentation());
		}
		root.add(playersElement);
		
		// Include the set caller if appropriate.
		if(model.getGameState() == GameConstants.GAME_STATE_SET_CALLED)
		{
			Element setCallerElement = documentFactory.createElement("setcaller");
			setCallerElement.setText("" + model.getSetCaller().getId());
			root.add(setCallerElement);
		}
		
		// Then the card table.
		Element cardTableElement = model.getCardTable().getRepresentation();
		root.add(cardTableElement);
		
		return root;
	}
}
