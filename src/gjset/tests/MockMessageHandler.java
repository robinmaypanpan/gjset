package gjset.tests;

import org.dom4j.Element;

import gjset.tools.MessageHandler;

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
 *
 */
public class MockMessageHandler implements MessageHandler
{
	private Element message;

	/**
	 * Stores the last message that was received.
	 *
	 * @param message
	 * @see gjset.tools.MessageHandler#handleMessage(org.dom4j.Element)
	 */
	public void handleMessage(Element message)
	{
		this.message = message;
	}

	/**
	 * Returns the last sent message.
	 *
	 * @return
	 */
	public Element getLastMessage()
	{
		return message;
	}

	/**
	 * Handle connection errors.
	 *
	 * @param e
	 * @see gjset.tools.MessageHandler#handleConnectionError(java.lang.Exception)
	 */
	public void handleConnectionError(Exception e)
	{
		//Nothing to do.
	}

}
