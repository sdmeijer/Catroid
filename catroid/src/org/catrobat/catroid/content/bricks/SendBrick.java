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

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SendBrick extends NestingBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private transient int command;
	private transient View prototypeView;
	private transient char letter = 'x';
	private SendBeginBrick sendBeginBrick;
	private SendEndBrick sendEndBrick;

	private transient SendBrick copy;

	public SendBrick(Sprite sprite, SendBeginBrick beginBrick) {
		this.sprite = sprite;
		this.sendBeginBrick = beginBrick;
		sendBeginBrick.setSendBrick(this);
	}

	public SendBrick() {
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public SendBrick getCopy() {
		return copy;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SendBrick copyBrick = (SendBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_send, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_send_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textLetter = (TextView) view.findViewById(R.id.brick_send_prototype_text_view);
		EditText editLetter = (EditText) view.findViewById(R.id.brick_send_edit_text);

		textLetter.setVisibility(View.GONE);
		editLetter.setVisibility(View.VISIBLE);
		editLetter.setText(String.valueOf(letter));
		editLetter.setOnClickListener(this);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_send_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textX = (TextView) view.findViewById(R.id.brick_send_text_view);
			EditText editLetter = (EditText) view.findViewById(R.id.brick_send_edit_text);
			textX.setTextColor(textX.getTextColors().withAlpha(alphaValue));
			editLetter.setTextColor(editLetter.getTextColors().withAlpha(alphaValue));
			editLetter.getBackground().setAlpha(alphaValue);
			editLetter.setText(String.valueOf(letter));

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_send, null);
		TextView textLetter = (TextView) prototypeView.findViewById(R.id.brick_send_prototype_text_view);
		EditText editLetter = (EditText) prototypeView.findViewById(R.id.brick_send_edit_text);

		textLetter.setVisibility(View.GONE);
		editLetter.setVisibility(View.VISIBLE);
		editLetter.setText(String.valueOf(letter));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new SendBrick(sprite, sendBeginBrick);
	}

	@Override
	public void onClick(View view) {
		showSelectLetterDialog(view.getContext());
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
	}

	public void setSendEndBrick(SendEndBrick sendEndBrick) {
		this.sendEndBrick = sendEndBrick;
	}

	private void showSelectLetterDialog(final Context context) {
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
			}

			@Override
			protected boolean handleOkButton() {
				final char newLetter;
				try {
					newLetter = (input.getText().charAt(0));
				} catch (IndexOutOfBoundsException e) {
					dismiss();
					return false;
				}
				command = newLetter;
				letter = newLetter;
				return true;
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				super.onDismiss(dialog);
			}
		};

		editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_send_brick");
	}

	public int getCommand() {
		return command;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.send(sprite, this));
		return null;
	}

	@Override
	public boolean isInitialized() {
		if (sendBeginBrick == null || sendEndBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		Log.w("SendBrick", "Cannot create the IfLogic Bricks from here!");
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == sendBeginBrick || brick == sendEndBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts(boolean sorted) {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		if (sorted) {
			nestingBrickList.add(sendBeginBrick);
			nestingBrickList.add(this);
			nestingBrickList.add(sendEndBrick);
		} else {
			//nestingBrickList.add(this);
			nestingBrickList.add(sendBeginBrick);
			nestingBrickList.add(sendEndBrick);
		}

		return nestingBrickList;
	}
}
