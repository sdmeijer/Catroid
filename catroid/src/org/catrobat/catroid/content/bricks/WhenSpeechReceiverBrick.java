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
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.RecognitionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class WhenSpeechReceiverBrick extends ScriptBrick implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private RecognitionScript script;
	private String recognitionKeyword = "";

	public WhenSpeechReceiverBrick(Sprite sprite, RecognitionScript script) {
		this.script = script;
		this.sprite = sprite;
		recognitionKeyword = script.getBroadcastMessage();
	}

	public WhenSpeechReceiverBrick(Sprite sprite, String keyword) {
		this.sprite = sprite;
		this.recognitionKeyword = keyword;
	}

	@Override
	public int getRequiredResources() {
		return (NETWORK_CONNECTION & SPEECH_TO_TEXT);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		WhenSpeechReceiverBrick copyBrick = (WhenSpeechReceiverBrick) clone();
		copyBrick.sprite = sprite;
		copyBrick.script = (RecognitionScript) script;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;

		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_when_speech_receive, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_when_speech_checkbox);

		TextView textHolder = (TextView) view.findViewById(R.id.brick_when_speech_prototype_text_view);
		EditText editText = (EditText) view.findViewById(R.id.brick_when_speech_edit_text);
		editText.setText(recognitionKeyword);

		textHolder.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);

		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (checkbox.getVisibility() == View.VISIBLE) {
					return;
				}
				ScriptActivity activity = (ScriptActivity) view.getContext();

				BrickTextDialog editDialog = new BrickTextDialog() {
					@Override
					protected void initialize() {
						input.setText(recognitionKeyword);
						input.setSelectAllOnFocus(true);
					}

					@Override
					protected boolean getPositiveButtonEnabled() {
						return true;
					}

					@Override
					protected TextWatcher getInputTextChangedListener(Button buttonPositive) {
						return new TextWatcher() {
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
							}

							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							}

							@Override
							public void afterTextChanged(Editable s) {
								if (s.toString().endsWith(" ") || s.toString().contains(" ")) {
									//TODO: prompt fail Dialog
									String oneWord = s.toString().substring(0, s.toString().indexOf(""));
									s.clear();
									s.insert(0, oneWord);
								}
							}
						};
					}

					@Override
					protected boolean handleOkButton() {
						recognitionKeyword = (input.getText().toString()).trim();
						script.setBroadcastMessage(recognitionKeyword);
						return true;
					}
				};

				editDialog.show(activity.getSupportFragmentManager(), "dialog_when_speech_brick");
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_when_speech_receive, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_when_speech_prototype_text_view);
		textSpeak.setText(recognitionKeyword);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		WhenSpeechReceiverBrick clonedBrick = new WhenSpeechReceiverBrick(getSprite(), this.script);
		clonedBrick.recognitionKeyword = this.recognitionKeyword;
		return clonedBrick;
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (script == null) {
			script = new RecognitionScript(sprite, recognitionKeyword);
		}
		return script;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_when_speech_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		return null;

	}

	@Override
	public String getBroadcastMessage() {
		if (script == null) {
			return recognitionKeyword;
		}
		return script.getBroadcastMessage();
	}
}
