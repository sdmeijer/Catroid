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
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class PcBroadcastTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.uitest.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0

	// needed for testdevices
	// unavailable device is paired with a name that starts with 'SWEET'
	// e.g. SWEETHEART

	//  private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
	//  private static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private Solo solo;
	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	public PcBroadcastTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.goBackToHome(getInstrumentation());
		UiTestUtils.prepareStageForTest();
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	public void testNXTFunctionality() {
		createTestproject(projectName);
	}

	private void createTestproject(String projectName) {
		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript(firstSprite);

		SendToPcBrick sendToPcBrick = new SendToPcBrick(firstSprite);
		startScript.addBrick(sendToPcBrick);

	}
}
