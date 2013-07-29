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

import java.io.File;
import java.util.HashMap;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.utils.Utils;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.badlogic.gdx.scenes.scene2d.Action;

public class SpeakAction extends Action {

	private String text;
	private static int utteranceIdPool;
	private boolean executeOnce = true;
	private boolean speakFinished = false;

	private File pathToSpeechFile;

	@Override
	public boolean act(float delta) {
		if (executeOnce) {
			OnUtteranceCompletedListener listener = new OnUtteranceCompletedListener() {

				@Override
				public void onUtteranceCompleted(String utteranceId) {
					SoundManager.getInstance().playSoundFile(pathToSpeechFile.getAbsolutePath());
					speakFinished = true;
				}
			};
			HashMap<String, String> speakParameter = new HashMap<String, String>();
			speakParameter.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(utteranceIdPool++));
			PreStageActivity.textToSpeech(text, pathToSpeechFile, listener, speakParameter);
			executeOnce = false;
		}
		return speakFinished;
	}

	@Override
	public void restart() {
		executeOnce = true;
		speakFinished = false;
	}

	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		this.text = text;

		String fileName = Utils.md5Checksum(text);
		pathToSpeechFile = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, fileName + Constants.TEXT_TO_SPEECH_EXTENSION);
	}
}
