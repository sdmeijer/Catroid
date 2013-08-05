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
package org.catrobat.catroid.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.catrobat.catroid.speechrecognition.VoiceTriggeredRecorder;
import org.catrobat.catroid.speechrecognition.VoiceTriggeredRecorder.VoiceTriggeredRecorderListener;
import org.catrobat.catroid.speechrecognition.WAVRecognizer;
import org.catrobat.catroid.speechrecognition.WAVRecognizer.SpeechFileToTextListener;

import android.media.MediaPlayer;
import android.util.Log;

public class UtilSpeechRecognition implements VoiceTriggeredRecorderListener, SpeechFileToTextListener {

	private static UtilSpeechRecognition instance = null;
	private static final String TAG = UtilSpeechRecognition.class.getSimpleName();
	private String lastBestAnswer = "";
	private ArrayList<String> lastAnswerSuggenstions = new ArrayList<String>();
	private HashMap<Thread, String> runningRecognition = new HashMap<Thread, String>();
	private VoiceTriggeredRecorder voiceRecorder = new VoiceTriggeredRecorder(this);
	private boolean debug = false;

	protected ArrayList<SpeechRecognizeListener> askerList = new ArrayList<SpeechRecognizeListener>();

	protected UtilSpeechRecognition() {

	}

	public static UtilSpeechRecognition getInstance() {
		if (instance == null) {
			instance = new UtilSpeechRecognition();
		}
		return instance;
	}

	public synchronized void onRecognitionResult(ArrayList<String> matches) {
		lastBestAnswer = "";
		lastAnswerSuggenstions.clear();

		if (matches == null) {
			return;
		}

		lastBestAnswer = matches.get(0);
		lastAnswerSuggenstions.addAll(matches);

		ArrayList<SpeechRecognizeListener> askerListCopy = new ArrayList<SpeechRecognizeListener>(askerList);
		for (SpeechRecognizeListener listener : askerListCopy) {
			listener.onRecognizedSpeech(lastBestAnswer, lastAnswerSuggenstions);
		}
	}

	public void registerContinuousSpeechListener(SpeechRecognizeListener asker) {
		synchronized (askerList) {
			askerList.add(asker);
			if (askerList.size() == 1) {
				voiceRecorder.startRecording();
			}
		}
		return;
	}

	public void unregisterContinuousSpeechListener(SpeechRecognizeListener asker) {
		synchronized (askerList) {
			if (askerList.contains(asker)) {
				askerList.remove(asker);

				if (askerList.size() == 0) {
					voiceRecorder.stopRecording();
				}
			} else if (debug) {
				Log.v(TAG, "Tried to remove not registered speechListener. " + asker.getClass().getSimpleName());
			}
		}
		return;
	}

	public String getLastBestAnswer() {
		return instance.lastBestAnswer;
	}

	public ArrayList<String> getLastAnswerSuggenstions() {
		return instance.lastAnswerSuggenstions;
	}

	public boolean isRecognitionRunning() {
		return runningRecognition.size() > 0;
	}

	@Override
	public void onSpeechFileSaved(String speechFilePath) {

		if (debug) {
			Log.v(TAG, "File" + speechFilePath + " saved, going recognizing");
			Log.v(TAG, "playing...");
			MediaPlayer player = new MediaPlayer();
			try {
				player.setDataSource(speechFilePath);
				player.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			player.start();
		}
		WAVRecognizer converter = new WAVRecognizer(speechFilePath, this);
		converter.start();
		runningRecognition.put(converter, speechFilePath);
	}

	@Override
	public void onFileRecognized(String speechFilePath, ArrayList<String> matches) {

		if (runningRecognition.containsKey(Thread.currentThread())) {
			runningRecognition.remove(Thread.currentThread());
		}

		if (matches == null || matches.size() == 0) {
			if (debug) {
				Log.v(TAG, "No results to File:" + speechFilePath);
			}
			return;
		}

		if (debug) {
			Log.v(TAG, "File" + speechFilePath + " recognized: " + matches.toString());
		}

		ArrayList<SpeechRecognizeListener> listenerListCopy = new ArrayList<UtilSpeechRecognition.SpeechRecognizeListener>(
				askerList);

		for (SpeechRecognizeListener listener : listenerListCopy) {
			listener.onRecognizedSpeech(matches.get(0), matches);
		}
	}

	@Override
	public void onFileToTextError(int errorCode, String errorMessage) {

	}

	@Override
	public void onVoiceTriggeredRecorderError(int errorCode) {
		voiceRecorder.stopRecording();
	}

	public interface SpeechRecognizeListener {
		public void onRecognizedSpeech(String bestAnswer, ArrayList<String> allAnswerSuggestions);
	}
}
