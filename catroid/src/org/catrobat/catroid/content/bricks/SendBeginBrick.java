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
package org.catrobat.catroid.content.bricks;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.Connection;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public abstract class SendBeginBrick extends NestingBrick {
	private static final long serialVersionUID = 1L;

	protected SendBrick sendBrick;
	protected SendEndBrick sendEndBrick;
	private transient long beginSendTime;
	private transient ArrayList<Integer> commandList;

	private transient SendBeginBrick copy;

	protected SendBeginBrick() {
	}

	protected void setFirstStartTime() {
		beginSendTime = System.nanoTime();
	}

	public long getBeginSendTime() {
		return beginSendTime;
	}

	public void setBeginSendTime(long beginSendTime) {
		this.beginSendTime = beginSendTime;
	}

	public SendEndBrick getSendEndBrick() {
		return this.sendEndBrick;
	}

	public void setSendEndBrick(SendEndBrick sendEndBrick) {
		this.sendEndBrick = sendEndBrick;
	}

	public SendBrick getSendBrick() {
		return sendBrick;
	}

	public void setSendBrick(SendBrick sendBrick_) {
		this.sendBrick = sendBrick_;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == sendEndBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isInitialized() {
		if (sendEndBrick == null || sendBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		sendBrick = new SendBrick(sprite, this);
		sendEndBrick = new SendEndBrick(sprite, this, sendBrick);
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(this);
			nestingBrickList.add(sendBrick);
			nestingBrickList.add(sendEndBrick);
		} else {
			nestingBrickList.add(this);
			nestingBrickList.add(sendEndBrick);
		}

		return nestingBrickList;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//Action action = ExtendedActions.sendBegin(sprite, this);
		//sequence.addAction(action);
		return null;
	}

	@Override
	public abstract Brick clone();

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SendBeginBrick copyBrick = (SendBeginBrick) clone();
		copyBrick.sendBrick = null;
		copyBrick.sendEndBrick = null;
		copyBrick.sprite = sprite;
		copy = copyBrick;
		return copyBrick;
	}

	public SendBeginBrick getCopy() {
		return copy;
	}

	public void setCommand(int newCommand) {
		commandList.add(newCommand);
	}

	public ArrayList<Integer> getCommandList() {
		return commandList;
	}

	public void initializeCommandList() {
		commandList = new ArrayList<Integer>();
	}

	public abstract Connection getConnection();

}
