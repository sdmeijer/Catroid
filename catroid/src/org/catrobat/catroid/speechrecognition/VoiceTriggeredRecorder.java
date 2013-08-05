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
package org.catrobat.catroid.speechrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.MicrophoneGrabber;
import org.catrobat.catroid.utils.MicrophoneGrabber.microphoneListener;
import org.catrobat.catroid.utils.Utils;

import android.util.Log;

public class VoiceTriggeredRecorder implements microphoneListener {

	public static final int ERROR_FILEIO = 1;

	private static final String TAG = VoiceTriggeredRecorder.class.getSimpleName();
	private boolean recordForFile;
	private VoiceTriggeredRecorderListener listener;
	private VoiceActivityDetection voiceDetection;
	byte[] buffer;
	byte[] preVoiceBuffer;
	byte[] totalByteBuffer;

	private final int frameByteSize = MicrophoneGrabber.frameByteSize;
	private final int fileHeaderOffset = 44;
	private final int preVoiceFramesForActivityDetection = 40;
	private final int preSilentFramesInVoiceFile = 10;

	private final int maxSilentFramesToIgnore = 10;
	private final int minVoiceFrames = 8;
	private final double silenceConfidence = 0.55d;

	private int totalReadBytes = fileHeaderOffset;
	private int ignoredFrames = 0;
	private int voiceFrames = 0;
	private int recordedPreFrames = 0;
	private byte[] silentFrame;

	public VoiceTriggeredRecorder(VoiceTriggeredRecorderListener listener) {
		voiceDetection = new VoiceActivityDetection();
		preVoiceBuffer = new byte[preVoiceFramesForActivityDetection * frameByteSize];
		totalByteBuffer = new byte[60 * 44100 * 2];
		silentFrame = new byte[frameByteSize];

		this.listener = listener;
		resetRecordState();
	}

	public void startRecording() {
		voiceDetection.resetState();
		MicrophoneGrabber.getInstance().registerListener(this);
	}

	public void stopRecording() {
		MicrophoneGrabber.getInstance().unregisterListener(this);
	}

	@Override
	public void onMicrophoneData(byte[] recievedBuffer) {
		boolean voiceDetected = voiceDetection.isFrameWithVoice(MicrophoneGrabber.audioByteToDouble(recievedBuffer));

		if (!voiceDetected && !recordForFile) {
			//Log.v(TAG, "silence...");
			System.arraycopy(preVoiceBuffer, frameByteSize, preVoiceBuffer, 0, (preVoiceFramesForActivityDetection - 1)
					* frameByteSize);
			System.arraycopy(recievedBuffer, 0, preVoiceBuffer, frameByteSize
					* (preVoiceFramesForActivityDetection - 1), frameByteSize);
			recordedPreFrames++;
			if (recordedPreFrames > preSilentFramesInVoiceFile) {
				recordedPreFrames = preSilentFramesInVoiceFile;
			}
			return;
		}

		if (voiceDetected && !recordForFile) {

			//Log.v(TAG, "detected");
			for (int i = 0; i < preVoiceFramesForActivityDetection - recordedPreFrames; i++) {
				System.arraycopy(silentFrame, 0, totalByteBuffer, i * frameByteSize, frameByteSize);
			}
			System.arraycopy(preVoiceBuffer, (preVoiceFramesForActivityDetection - recordedPreFrames) * frameByteSize,
					totalByteBuffer, (preVoiceFramesForActivityDetection - recordedPreFrames) * frameByteSize,
					recordedPreFrames * frameByteSize);

			totalReadBytes += preVoiceFramesForActivityDetection * frameByteSize;

			voiceDetection.setSensibility(VoiceActivityDetection.SENSIBILITY_HIGH);
			recordForFile = true;
		}

		if (!voiceDetected && recordForFile) {
			if (ignoredFrames < maxSilentFramesToIgnore) {
				//				Log.v(TAG, "recorded silence...");
				System.arraycopy(recievedBuffer, 0, totalByteBuffer, totalReadBytes, frameByteSize);
				totalReadBytes += frameByteSize;
				//just ignore real silence
				if (voiceDetection.lastConfidence <= silenceConfidence) {
					//					Log.v(TAG, "Real silence...");
					ignoredFrames++;
				}
				return;
			} else {
				for (int i = 1; i <= maxSilentFramesToIgnore; i++) {
					//System.arraycopy(silentFrame, 0, totalByteBuffer, totalReadBytes - i * frameByteSize, frameByteSize);
				}
			}

			if (voiceFrames < minVoiceFrames) {
				//Log.v(TAG, "reset");
				resetRecordState();
				return;
			}

			String speechRecordingFile = saveRecordingToNewFile();
			if (speechRecordingFile != null) {
				listener.onSpeechFileSaved(speechRecordingFile);
			} else {
				listener.onVoiceTriggeredRecorderError(ERROR_FILEIO);
			}
			resetRecordState();
			return;
		}

		//Log.v(TAG, "recording");
		ignoredFrames = 0;
		System.arraycopy(recievedBuffer, 0, totalByteBuffer, totalReadBytes, frameByteSize);
		totalReadBytes += frameByteSize;
		voiceFrames++;
	}

	private void resetRecordState() {
		recordForFile = false;
		totalReadBytes = fileHeaderOffset;
		ignoredFrames = 0;
		voiceDetection.setSensibility(VoiceActivityDetection.SENSIBILITY_LOW);
		recordedPreFrames = 0;
		voiceFrames = 0;
	}

	private String saveRecordingToNewFile() {

		String projectDirectoryName = Utils
				.buildProjectPath(ProjectManager.getInstance().getCurrentProject().getName());
		File recognizeDirectory = new File(projectDirectoryName + "/" + Constants.SOUND_RECOGNITION_DIRECTORY);
		recognizeDirectory.mkdir();

		// Save audio to file.
		if (!recognizeDirectory.exists()) {
			recognizeDirectory.mkdir();
		}

		String speechFilePath = Utils.buildPath(projectDirectoryName, Constants.SOUND_RECOGNITION_DIRECTORY,
				String.valueOf(System.currentTimeMillis()) + ".wav");

		int channels = 1;
		int bitsPerSample = 16;
		long longSampleRate = MicrophoneGrabber.sampleRate;
		long byteRate = bitsPerSample * MicrophoneGrabber.sampleRate * channels / 8;
		long totalAudioLen = totalReadBytes - fileHeaderOffset;
		long totalDataLen = totalAudioLen + 36;

		totalByteBuffer[0] = 'R'; // RIFF/WAVE header
		totalByteBuffer[1] = 'I';
		totalByteBuffer[2] = 'F';
		totalByteBuffer[3] = 'F';
		totalByteBuffer[4] = (byte) (totalDataLen & 0xff);
		totalByteBuffer[5] = (byte) ((totalDataLen >> 8) & 0xff);
		totalByteBuffer[6] = (byte) ((totalDataLen >> 16) & 0xff);
		totalByteBuffer[7] = (byte) ((totalDataLen >> 24) & 0xff);
		totalByteBuffer[8] = 'W';
		totalByteBuffer[9] = 'A';
		totalByteBuffer[10] = 'V';
		totalByteBuffer[11] = 'E';
		totalByteBuffer[12] = 'f'; // 'fmt ' chunk
		totalByteBuffer[13] = 'm';
		totalByteBuffer[14] = 't';
		totalByteBuffer[15] = ' ';
		totalByteBuffer[16] = 16; // 4 bytes: size of 'fmt ' chunk
		totalByteBuffer[17] = 0;
		totalByteBuffer[18] = 0;
		totalByteBuffer[19] = 0;
		totalByteBuffer[20] = 1; // format = 1
		totalByteBuffer[21] = 0;
		totalByteBuffer[22] = (byte) channels;
		totalByteBuffer[23] = 0;
		totalByteBuffer[24] = (byte) (longSampleRate & 0xff);
		totalByteBuffer[25] = (byte) ((longSampleRate >> 8) & 0xff);
		totalByteBuffer[26] = (byte) ((longSampleRate >> 16) & 0xff);
		totalByteBuffer[27] = (byte) ((longSampleRate >> 24) & 0xff);
		totalByteBuffer[28] = (byte) (byteRate & 0xff);
		totalByteBuffer[29] = (byte) ((byteRate >> 8) & 0xff);
		totalByteBuffer[30] = (byte) ((byteRate >> 16) & 0xff);
		totalByteBuffer[31] = (byte) ((byteRate >> 24) & 0xff);
		totalByteBuffer[32] = (byte) (2 * 16 / 8); // block align
		totalByteBuffer[33] = 0;
		totalByteBuffer[34] = (byte) bitsPerSample; // bits per sample
		totalByteBuffer[35] = 0;
		totalByteBuffer[36] = 'd';
		totalByteBuffer[37] = 'a';
		totalByteBuffer[38] = 't';
		totalByteBuffer[39] = 'a';
		totalByteBuffer[40] = (byte) (totalAudioLen & 0xff);
		totalByteBuffer[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		totalByteBuffer[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		totalByteBuffer[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		FileOutputStream out;
		try {
			out = new FileOutputStream(speechFilePath);
			try {
				out.write(totalByteBuffer, 0, totalReadBytes);
				out.close();
			} catch (IOException e) {
				Log.w(TAG, "VoiceRecorder couldn't write to " + speechFilePath);
				return null;
			}

		} catch (FileNotFoundException e1) {
			Log.w(TAG, "VoiceRecorder couldn't find " + speechFilePath);
			return null;
		}
		return speechFilePath;
	}

	public interface VoiceTriggeredRecorderListener {
		public void onVoiceTriggeredRecorderError(int errorCode);

		public void onSpeechFileSaved(String speechFilePath);
	}
}
