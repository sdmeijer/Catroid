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
package org.catrobat.pocketcode.test.content.actions;

import java.io.File;
import java.io.IOException;

import org.catrobat.pocketcode.ProjectManager;
import org.catrobat.pocketcode.common.Constants;
import org.catrobat.pocketcode.content.Project;
import org.catrobat.pocketcode.content.actions.ChangeVolumeByNAction;
import org.catrobat.pocketcode.content.actions.ExtendedActions;
import org.catrobat.pocketcode.formulaeditor.Formula;
import org.catrobat.pocketcode.io.SoundManager;
import org.catrobat.pocketcode.io.StorageHandler;
import org.catrobat.pocketcode.test.R;
import org.catrobat.pocketcode.test.utils.TestUtils;
import org.catrobat.pocketcode.utils.UtilFile;

import android.test.InstrumentationTestCase;

public class ChangeVolumeByNActionTest extends InstrumentationTestCase {

	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private String projectName = "projectiName";
	private Formula louder = new Formula(10.6f);
	private Formula softer = new Formula(-20.3f);

	@Override
	protected void setUp() throws Exception {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
		UtilFile.deleteDirectory(directory);
		this.createTestProject();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		TestUtils.clearProject(projectName);
		SoundManager.getInstance().clear();
		super.tearDown();
	}

	public void testVolume() {
		assertEquals("Unexpected initial volume value", 70.0f, SoundManager.getInstance().getVolume());

		float volume = SoundManager.getInstance().getVolume();
		volume += louder.interpretDouble(null);

		ChangeVolumeByNAction action1 = ExtendedActions.changeVolumeByN(null, louder);
		action1.act(1.0f);
		assertEquals("Incorrect sprite volume after ChangeVolumeByNBrick executed", volume, SoundManager.getInstance()
				.getVolume());

		volume = SoundManager.getInstance().getVolume();
		volume += softer.interpretDouble(null);

		ChangeVolumeByNAction action2 = ExtendedActions.changeVolumeByN(null, softer);
		action2.act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeVolumeByNBrick executed", volume, SoundManager
				.getInstance().getVolume());
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {

		soundFile = TestUtils.saveFileToProject(projectName, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}
}
