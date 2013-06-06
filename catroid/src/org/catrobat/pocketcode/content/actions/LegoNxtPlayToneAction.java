/**
 *  Pocket Code: An on-device visual programming system for Android devices
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
package org.catrobat.pocketcode.content.actions;

import org.catrobat.pocketcode.LegoNXT.LegoNXT;
import org.catrobat.pocketcode.content.Sprite;
import org.catrobat.pocketcode.formulaeditor.Formula;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class LegoNxtPlayToneAction extends TemporalAction {

	private Formula hertz;
	private Formula durationInSeconds;
	private Sprite sprite;

	@Override
	protected void update(float percent) {
		LegoNXT.sendBTCPlayToneMessage(hertz.interpretInteger(sprite), durationInSeconds.interpretInteger(sprite));
	}

	public void setHertz(Formula hertz) {
		this.hertz = hertz;
	}

	public void setDurationInSeconds(Formula durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;

	}

}
