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
package org.catrobat.catroid.pocketcode.uitest.content.interaction;

import java.util.ArrayList;

import org.catrobat.catroid.pocketcode.ProjectManager;
import org.catrobat.catroid.pocketcode.R;
import org.catrobat.catroid.pocketcode.content.Project;
import org.catrobat.catroid.pocketcode.content.Script;
import org.catrobat.catroid.pocketcode.content.Sprite;
import org.catrobat.catroid.pocketcode.content.StartScript;
import org.catrobat.catroid.pocketcode.content.bricks.Brick;
import org.catrobat.catroid.pocketcode.content.bricks.HideBrick;
import org.catrobat.catroid.pocketcode.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.pocketcode.content.bricks.ShowBrick;
import org.catrobat.catroid.pocketcode.ui.MainMenuActivity;
import org.catrobat.catroid.pocketcode.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.pocketcode.uitest.util.UiTestUtils;

public class ScriptDeleteTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private ArrayList<Brick> brickListToCheck;

	public ScriptDeleteTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createTestProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testAddLooksCategoryBrick() {
		String brickSetLookText = solo.getString(R.string.brick_set_look);
		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		solo.clickOnText(solo.getString(R.string.brick_when_started));

		solo.clickOnScreen(200, 200);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}
		assertTrue("Set look brick was not added", solo.searchText(brickSetLookText));

		UiTestUtils.addNewBrick(solo, R.string.brick_set_size_to);
		assertTrue("Set size to brick was not added", solo.searchText(solo.getString(R.string.brick_set_size_to)));
	}

	public void testDeleteScript() {
		String brickWhenStartedText = solo.getString(R.string.brick_when_started);
		String buttonDeleteText = solo.getString(R.string.delete);
		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		solo.clickOnScreen(200, 200);
		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}
		int numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in list", 2, numberOfScripts);

		solo.clickLongOnText(brickWhenStartedText);
		solo.clickOnText(buttonDeleteText);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(200);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 3, UiTestUtils.getScriptListView(solo).getChildCount());

		solo.clickLongOnText(solo.getString(R.string.brick_broadcast_receive));
		solo.clickOnText(buttonDeleteText);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(200);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in list", 0, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 0, UiTestUtils.getScriptListView(solo).getChildCount());

		UiTestUtils.addNewBrick(solo, R.string.brick_hide);
		solo.clickOnText(brickWhenStartedText);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 2, UiTestUtils.getScriptListView(solo).getChildCount());
	}

	private void createTestProject(String projectName) {
		double size = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript(firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new SetSizeToBrick(firstSprite, size));

		for (Brick brick : brickListToCheck) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
	}
}
