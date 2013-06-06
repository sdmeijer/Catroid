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
package org.catrobat.pocketcode.test.content;

import java.util.Set;

import org.catrobat.pocketcode.ProjectManager;
import org.catrobat.pocketcode.common.MessageContainer;
import org.catrobat.pocketcode.content.BroadcastScript;
import org.catrobat.pocketcode.content.Project;
import org.catrobat.pocketcode.content.Script;
import org.catrobat.pocketcode.content.Sprite;
import org.catrobat.pocketcode.content.StartScript;
import org.catrobat.pocketcode.content.bricks.BroadcastBrick;
import org.catrobat.pocketcode.io.StorageHandler;
import org.catrobat.pocketcode.test.utils.TestUtils;

import android.test.AndroidTestCase;

public class MessageContainerTest extends AndroidTestCase {

	private final String projectName1 = "TestProject1";
	private final String projectName2 = "TestProject2";
	private final String projectName3 = "TestProject3";
	private final String broadcastMessage1 = "testBroadcast1";
	private final String broadcastMessage2 = "testBroadcast2";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createTestProjects();
	}

	@Override
	protected void tearDown() {
		TestUtils.deleteTestProjects(projectName1, projectName2, projectName3);
	}

	public void testLoadProject() {
		boolean loaded = ProjectManager.getInstance().loadProject(projectName1, getContext(), false);
		assertTrue("Project was not loaded successfully", loaded);
		if (loaded) {
			Set<String> keySet = MessageContainer.getMessages();
			assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));
		}
	}

	public void testLoadTwoProjects() {
		boolean loaded = ProjectManager.getInstance().loadProject(projectName1, getContext(), false);
		assertTrue("Project1 was not loaded successfully", loaded);

		Set<String> keySet = MessageContainer.getMessages();
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));

		loaded = ProjectManager.getInstance().loadProject(projectName2, getContext(), false);
		assertTrue("Project2 was not loaded successfully", loaded);
		keySet = MessageContainer.getMessages();
		assertEquals("Broadcast message is in the message container", false, keySet.contains(broadcastMessage1));
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage2));
	}

	public void testLoadCorruptedProjectAndCheckForBackup() {
		boolean loaded = ProjectManager.getInstance().loadProject(projectName1, getContext(), false);
		assertTrue("Project1 was not loaded successfully", loaded);

		Set<String> keySet = MessageContainer.getMessages();
		assertEquals("Broadcast message has the false position", true, keySet.contains(broadcastMessage1));

		loaded = ProjectManager.getInstance().loadProject(projectName3, getContext(), false);
		assertFalse("Corrupted project was loaded", loaded);
		keySet = MessageContainer.getMessages();
		assertEquals("Broadcast message is not in the message container", true, keySet.contains(broadcastMessage1));
	}

	private void createTestProjects() {
		Project project1 = new Project(getContext(), projectName1);

		Sprite sprite1 = new Sprite("cat");
		Script script1 = new StartScript(sprite1);
		BroadcastBrick brick1 = new BroadcastBrick(sprite1);
		brick1.setSelectedMessage(broadcastMessage1);
		script1.addBrick(brick1);
		sprite1.addScript(script1);

		BroadcastScript broadcastScript1 = new BroadcastScript(sprite1);
		broadcastScript1.setBroadcastMessage(broadcastMessage1);
		sprite1.addScript(broadcastScript1);

		project1.addSprite(sprite1);

		StorageHandler.getInstance().saveProject(project1);

		Project project2 = new Project(getContext(), projectName2);

		Sprite sprite2 = new Sprite("cat");
		Script script2 = new StartScript(sprite2);
		BroadcastBrick brick2 = new BroadcastBrick(sprite2);
		brick2.setSelectedMessage(broadcastMessage2);
		script2.addBrick(brick2);
		sprite2.addScript(script2);

		BroadcastScript broadcastScript2 = new BroadcastScript(sprite2);
		broadcastScript2.setBroadcastMessage(broadcastMessage2);
		sprite2.addScript(broadcastScript2);

		project2.addSprite(sprite2);

		StorageHandler.getInstance().saveProject(project2);

	}
}
