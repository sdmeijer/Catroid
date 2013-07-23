/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SendBeginBrick;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.io.Command;
import org.catrobat.catroid.io.Connection;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class SendEndAction extends TemporalAction {

	private Sprite sprite;
	private SendBeginBrick sendBeginBrick;

	@Override
	protected void update(float percent) {
		ArrayList<Integer> commandList = sendBeginBrick.getCommandList();
		Command command = null;
		if (commandList.size() == 1) {
			int letter = sendBeginBrick.getCommandList().get(0);
			command = new Command(letter, Command.commandType.SINGLE_KEY);
		} else {
			int[] letters = new int[commandList.size()];
			Iterator<Integer> it = commandList.iterator();
			int i = 0;
			while (it.hasNext()) {
				letters[i] = it.next();
				i++;
			}
			command = new Command(letters, Command.commandType.SINGLE_KEY);
		}

		Connection connection = ((SendToPcBrick) sendBeginBrick).getConnection();
		connection.addCommand(command);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setSendBeginBrick(SendBeginBrick sendBeginBrick) {
		this.sendBeginBrick = sendBeginBrick;
	}
}
