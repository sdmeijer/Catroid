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
package org.catrobat.pocketcode.uitest.stage;

import java.io.File;

import org.catrobat.pocketcode.ProjectManager;
import org.catrobat.pocketcode.common.LookData;
import org.catrobat.pocketcode.common.ScreenValues;
import org.catrobat.pocketcode.content.Project;
import org.catrobat.pocketcode.content.Sprite;
import org.catrobat.pocketcode.content.StartScript;
import org.catrobat.pocketcode.content.WhenScript;
import org.catrobat.pocketcode.content.bricks.ComeToFrontBrick;
import org.catrobat.pocketcode.content.bricks.PlaceAtBrick;
import org.catrobat.pocketcode.content.bricks.SetBrightnessBrick;
import org.catrobat.pocketcode.content.bricks.SetGhostEffectBrick;
import org.catrobat.pocketcode.content.bricks.SetLookBrick;
import org.catrobat.pocketcode.content.bricks.SetSizeToBrick;
import org.catrobat.pocketcode.content.bricks.TurnLeftBrick;
import org.catrobat.pocketcode.io.StorageHandler;
import org.catrobat.pocketcode.stage.StageActivity;
import org.catrobat.pocketcode.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class ComplexStageTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private final int screenWidth = 480;
	private final int screenHeight = 800;

	private Solo solo;
	private Project project;

	public ComplexStageTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testShowTexture() {
		byte[] redPixel = { (byte) 237, 28, 36, (byte) 255 };
		byte[] redBrightnessPixel = { (byte) 109, 0, 0, (byte) 255 };
		byte[] greenPixel = { 34, (byte) 177, 76, (byte) 255 };
		byte[] yellowPixel = { (byte) 255, (byte) 242, 0, (byte) 255 };
		byte[] bluePixel = { 0, (byte) 162, (byte) 232, (byte) 255 };
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		byte[] blackPixel = { (byte) 0, (byte) 0, (byte) 0, (byte) 255 };
		byte[] blackBrightnessPixel = { (byte) 127, (byte) 127, (byte) 127, (byte) 255 };

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1400);
		byte[] screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -2, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -2, -2, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(yellowPixel, screenArray, -21, 21, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(whitePixel, screenArray, 0, 0, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(blackPixel, screenArray, -80, -80, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) + 21, (screenHeight / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21 - 40, 21 - 40, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, 21 - 41, 21 - 41, screenWidth,
				screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 21, (screenHeight / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(whitePixel, screenArray, -31, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) + 21, (screenHeight / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 21, (screenHeight / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redBrightnessPixel, screenArray, -21, -21, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redBrightnessPixel, screenArray, -21, -21 + 27, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 50, (screenHeight / 2) - 50);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(blackBrightnessPixel, screenArray, -54, 55, screenWidth,
				screenHeight);
		assertTrue("Just for FileTest", true);
	}

	private void createProject() {
		ScreenValues.SCREEN_HEIGHT = screenHeight;
		ScreenValues.SCREEN_WIDTH = screenWidth;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		Sprite yellowSprite = new Sprite("yellowSprite");
		StartScript yellowStartScript = new StartScript(yellowSprite);
		SetLookBrick yellowLookBrick = new SetLookBrick(yellowSprite);
		LookData yellowLookData = new LookData();
		String yellowImageName = "yellow_image.bmp";

		yellowLookData.setLookName(yellowImageName);

		yellowSprite.getLookDataList().add(yellowLookData);

		yellowLookBrick.setLook(yellowLookData);
		yellowStartScript.addBrick(yellowLookBrick);
		yellowStartScript.addBrick(new PlaceAtBrick(yellowSprite, -21, 21));

		yellowSprite.addScript(yellowStartScript);

		WhenScript yellowWhenScript = new WhenScript(yellowSprite);
		SetGhostEffectBrick yellowSetGhostEffectBrick = new SetGhostEffectBrick(yellowSprite, 100d);
		yellowWhenScript.addBrick(yellowSetGhostEffectBrick);

		yellowSprite.addScript(yellowWhenScript);

		// blue Sprite
		Sprite blueSprite = new Sprite("blueSprite");
		StartScript blueStartScript = new StartScript(blueSprite);
		SetLookBrick blueLookBrick = new SetLookBrick(blueSprite);
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueLookBrick.setLook(blueLookData);
		blueStartScript.addBrick(blueLookBrick);
		blueStartScript.addBrick(new PlaceAtBrick(blueSprite, 21, 21));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript(blueSprite);
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(blueSprite, 200d);
		blueWhenScript.addBrick(blueSetSizeToBrick);

		blueSprite.addScript(blueWhenScript);

		// green Sprite
		Sprite greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript(greenSprite);
		SetLookBrick greenLookBrick = new SetLookBrick(greenSprite);
		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";

		greenLookData.setLookName(greenImageName);

		greenSprite.getLookDataList().add(greenLookData);

		greenLookBrick.setLook(greenLookData);
		greenStartScript.addBrick(greenLookBrick);
		greenStartScript.addBrick(new PlaceAtBrick(greenSprite, 21, -21));

		greenSprite.addScript(greenStartScript);

		WhenScript greenWhenScript = new WhenScript(greenSprite);
		ComeToFrontBrick greenComeToFrontBrick = new ComeToFrontBrick(greenSprite);
		greenWhenScript.addBrick(greenComeToFrontBrick);

		greenSprite.addScript(greenWhenScript);

		// red Sprite
		Sprite redSprite = new Sprite("redSprite");
		StartScript redStartScript = new StartScript(redSprite);
		SetLookBrick redLookBrick = new SetLookBrick(redSprite);
		LookData redLookData = new LookData();
		String redImageName = "red_image.bmp";

		redLookData.setLookName(redImageName);

		redSprite.getLookDataList().add(redLookData);

		redLookBrick.setLook(redLookData);
		redStartScript.addBrick(redLookBrick);
		redStartScript.addBrick(new PlaceAtBrick(redSprite, -21, -21));

		redSprite.addScript(redStartScript);

		WhenScript redWhenScript = new WhenScript(redSprite);
		ComeToFrontBrick redComeToFrontBrick = new ComeToFrontBrick(redSprite);
		SetBrightnessBrick redSetBrightnessBrick = new SetBrightnessBrick(redSprite, 50d);
		TurnLeftBrick redTurnLeftBrick = new TurnLeftBrick(redSprite, 45d);
		redWhenScript.addBrick(redComeToFrontBrick);
		redWhenScript.addBrick(redSetBrightnessBrick);
		redWhenScript.addBrick(redTurnLeftBrick);

		redSprite.addScript(redWhenScript);

		// black Sprite
		Sprite blackSprite = new Sprite("blackSprite");
		StartScript blackStartScript = new StartScript(blackSprite);
		SetLookBrick blackLookBrick = new SetLookBrick(blackSprite);
		LookData blackLookData = new LookData();
		String blackImageName = "black_image.bmp";

		blackLookData.setLookName(blackImageName);

		blackSprite.getLookDataList().add(blackLookData);

		blackLookBrick.setLook(blackLookData);
		blackStartScript.addBrick(blackLookBrick);
		blackStartScript.addBrick(new PlaceAtBrick(blackSprite, -50, 50));

		blackSprite.addScript(blackStartScript);

		WhenScript blackWhenScript = new WhenScript(blackSprite);
		ComeToFrontBrick blackComeToFrontBrick = new ComeToFrontBrick(blackSprite);
		SetBrightnessBrick blackSetBrightnessBrick = new SetBrightnessBrick(blackSprite, 150d);
		blackWhenScript.addBrick(blackComeToFrontBrick);
		blackWhenScript.addBrick(blackSetBrightnessBrick);

		blackSprite.addScript(blackWhenScript);

		project.addSprite(blackSprite);
		project.addSprite(yellowSprite);
		project.addSprite(redSprite);
		project.addSprite(greenSprite);
		project.addSprite(blueSprite);

		StorageHandler.getInstance().saveProject(project);

		File yellowImageFile = UiTestUtils.saveFileToProject(project.getName(), yellowImageName,
				org.catrobat.pocketcode.uitest.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File greenImageFile = UiTestUtils.saveFileToProject(project.getName(), greenImageName,
				org.catrobat.pocketcode.uitest.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blueImageFile = UiTestUtils.saveFileToProject(project.getName(), blueImageName,
				org.catrobat.pocketcode.uitest.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File redImageFile = UiTestUtils.saveFileToProject(project.getName(), redImageName,
				org.catrobat.pocketcode.uitest.R.raw.red_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blackImageFile = UiTestUtils.saveFileToProject(project.getName(), blackImageName,
				org.catrobat.pocketcode.uitest.R.raw.black_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		yellowLookData.setLookFilename(yellowImageFile.getName());
		greenLookData.setLookFilename(greenImageFile.getName());
		blueLookData.setLookFilename(blueImageFile.getName());
		redLookData.setLookFilename(redImageFile.getName());
		blackLookData.setLookFilename(blackImageFile.getName());

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}