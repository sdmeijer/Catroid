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

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MicrophoneGrabber extends Thread {
	private static MicrophoneGrabber instance = null;

	public static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	public static final int sampleRate = 16000;
	public static final int frameByteSize = 512;
	public static final int bytesPerSample = 2;

	private ArrayList<microphoneListener> microphoneListenerList = new ArrayList<microphoneListener>();
	private boolean isRecording;
	public boolean isPaused = false;
	private AudioRecord audioRecord;
	private byte[] buffer;

	@Override
	public MicrophoneGrabber clone() {
		MicrophoneGrabber newGrabber = new MicrophoneGrabber();
		newGrabber.microphoneListenerList.addAll(this.microphoneListenerList);
		newGrabber.isRecording = false;
		newGrabber.isPaused = this.isPaused;
		newGrabber.audioRecord = this.audioRecord;
		MicrophoneGrabber.instance = newGrabber;
		return newGrabber;
	}

	private MicrophoneGrabber() {
		int recBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRate, channelConfiguration,
				audioEncoding, recBufSize);
		buffer = new byte[frameByteSize];
	}

	public static MicrophoneGrabber getInstance() {
		if (instance == null) {
			instance = new MicrophoneGrabber();
		}
		return instance;
	}

	public void registerListener(microphoneListener listener) {
		synchronized (microphoneListenerList) {
			microphoneListenerList.add(listener);
			if (!isRecording && !isPaused) {
				if (this.isAlive()) {
					isRecording = true;
				} else {
					MicrophoneGrabber newGrabber = this.clone();
					newGrabber.start();
				}
			}
		}
		return;
	}

	public void unregisterListener(microphoneListener listener) {
		synchronized (microphoneListenerList) {
			if (microphoneListenerList.contains(listener)) {
				microphoneListenerList.remove(listener);
			}
			if (microphoneListenerList.size() == 0) {
				isRecording = false;
			}
		}
		return;
	}

	@Override
	public void run() {

		isRecording = true;
		audioRecord.startRecording();

		while (isRecording) {
			int offset = 0;
			int shortRead = 0;

			while (offset < frameByteSize) {
				shortRead = audioRecord.read(buffer, offset, frameByteSize - offset);
				offset += shortRead;
			}

			final byte[] broadcastBuffer = buffer.clone();
			final ArrayList<microphoneListener> dataListener = new ArrayList<microphoneListener>(microphoneListenerList);
			for (microphoneListener listener : dataListener) {
				listener.onMicrophoneData(broadcastBuffer);
			}
		}

		audioRecord.stop();
	}

	public boolean isRecording() {
		return this.isAlive() && isRecording;
	}

	public static double[] audioByteToDouble(byte[] samples) {
		double[] micBufferData = new double[samples.length / bytesPerSample];

		final double amplification = 1000.0;
		for (int index = 0, floatIndex = 0; index < samples.length - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
			double sample = 0;
			for (int b = 0; b < bytesPerSample; b++) {
				int v = samples[index + b];
				if (b < bytesPerSample - 1 || bytesPerSample == 1) {
					v &= 0xFF;
				}
				sample += v << (b * 8);
			}
			double sample32 = amplification * (sample / 32768.0);
			micBufferData[floatIndex] = sample32;
		}
		return micBufferData;
	}

	public interface microphoneListener {
		public void onMicrophoneData(byte[] recievedBuffer);
	}
}
