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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RepeatBrick extends LoopBeginBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private int timesToRepeat;
	private transient CheckBox checkbox;
	private transient View view;
	private transient boolean checked;

	public RepeatBrick(Sprite sprite, int timesToRepeat) {
		this.sprite = sprite;
		this.timesToRepeat = timesToRepeat;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public RepeatBrick() {

	}

	@Override
	public void execute() {
		if (timesToRepeat <= 0) {
			Script script = loopEndBrick.getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(loopEndBrick));
			return;
		}
		loopEndBrick.setTimesToRepeat(timesToRepeat);
		super.setFirstStartTime();
	}

	@Override
	public Brick clone() {
		return new RepeatBrick(getSprite(), timesToRepeat);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.brick_repeat, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_repeat_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;

					if (!checked) {
						for (Brick currentBrick : adapter.getCheckedBricks()) {
							currentBrick.setCheckedBoolean(false);
						}
					}

					adapter.handleCheck(brickInstance, checked);
				}
			});
		}

		TextView text = (TextView) view.findViewById(R.id.brick_repeat_prototype_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_repeat_edit_text);
		edit.setText(timesToRepeat + "");

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_repeat, null);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_repeat_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(timesToRepeat));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					timesToRepeat = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_repeat_brick");
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}
}
