package gjset;

/* 
 *  LEGAL STUFF
 * 
 *  This file is part of gjSet.
 *  
 *  gjSet is Copyright 2008, 2009 Joyce Murton
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

import gjset.client.LocalEngineLink;
import gjset.client.gui.PlayerUI;
import gjset.engine.GameEngine;
import gjset.engine.LocalClientLink;

public class GjSetMain
{
	public static void main(String[] args)
	{
		LocalClientLink player = new LocalClientLink();
		LocalEngineLink engine = new LocalEngineLink();
		
		GameEngine gc = new GameEngine(player);
		PlayerUI gui = new PlayerUI(engine);
		
		player.setGUI(gui);
		engine.setEngine(gc);
	}

}
