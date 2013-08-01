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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.io.Connection;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.io.PcConnectionManager.Broadcast;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SendToPcBrick extends SendBeginBrick implements Broadcast {
	private static final long serialVersionUID = 1L;
	private transient Connection connection;
	private transient HashMap<String, String> availableIps;
	private transient Spinner sendSpinner;
	private transient String ipToConnect;
	private transient int selectedItem;
	private transient Context context;
	private transient ArrayAdapter<String> dataAdapter;

	public SendToPcBrick(Sprite sprite) {
		this.sprite = sprite;
		sendSpinner = null;
		context = null;
	}

	public SendToPcBrick() {
		sendSpinner = null;
		context = null;
	}

	@Override
	public int getRequiredResources() {
		return CONNECTION_TO_PC;
	}

	@Override
	public Brick clone() {
		return new SendToPcBrick(getSprite());
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_send_to_pc, null);
		view = getViewWithAlpha(alphaValue);
		if (context != null) {
			this.context = context;
			PcConnectionManager.getInstance(context).addToConnectionRequestList(this);
		}
		initializeView(view, context);
		setCheckboxView(R.id.brick_send_to_pc_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				if (!checked) {
					for (Brick currentBrick : adapter.getCheckedBricks()) {
						currentBrick.setCheckedBoolean(false);
					}
				}
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		sendSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String current = (String) sendSpinner.getItemAtPosition(position);
				if (!current.equals(parent.getContext().getString(R.string.scan))
						&& !current.equals(parent.getContext().getString(R.string.empty))) {
					String key = (String) sendSpinner.getItemAtPosition(position);
					if (availableIps.containsKey(key)) {
						ipToConnect = availableIps.get(key);
					}
				} else {
					ipToConnect = null;
				}
				if (sendSpinner.getSelectedItem().toString().equals(parent.getContext().getString(R.string.scan))) {
					selectedItem = -1;
					PcConnectionManager.getInstance(context).broadcast();
				} else {
					selectedItem = sendSpinner.getSelectedItemPosition();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		return view;
	}

	public void initializeSpinner(View view, boolean prototype) {
		sendSpinner = (Spinner) view.findViewById(R.id.brick_ips_spinner);
		sendSpinner.setFocusableInTouchMode(false);
		sendSpinner.setFocusable(false);
		ArrayList<String> spinnerIpList = new ArrayList<String>();
		if (dataAdapter == null) {
			if (prototype) {
				spinnerIpList.add(context.getString(R.string.new_ip_list));
			} else {
				if (PcConnectionManager.getInstance(context).getActualIpList().size() == 0) {
					spinnerIpList.add(context.getString(R.string.empty));
					spinnerIpList.add(context.getString(R.string.scan));
				} else {
					spinnerIpList.addAll(0, PcConnectionManager.getInstance(context).getActualIpList());
					spinnerIpList.add(context.getString(R.string.scan));
				}
			}
			dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerIpList);
		} else {
			Iterator<Entry<String, String>> it = availableIps.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> pairs = it.next();
				dataAdapter.add(pairs.getKey());
			}
		}
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sendSpinner.setAdapter(dataAdapter);
		sendSpinner.setSelection(selectedItem);
	}

	public void initializeView(View view, Context context) {
		if (availableIps == null) {
			availableIps = new HashMap<String, String>();
		}
		if (context != null) {
			this.context = context;
		}
		initializeSpinner(view, false);
		sendSpinner.setClickable(true);
		sendSpinner.setEnabled(true);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_send_to_pc_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		TextView ifSendLabel = (TextView) view.findViewById(R.id.brick_send_to_pc_label);
		ifSendLabel.setTextColor(ifSendLabel.getTextColors().withAlpha(alphaValue));
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_send_to_pc, null);
		initializePrototypeView(prototypeView, context);
		return prototypeView;
	}

	public void initializePrototypeView(View prototypeView, Context context) {
		availableIps = new HashMap<String, String>();
		if (context != null) {
			this.context = context;
		}
		initializeSpinner(prototypeView, true);
		sendSpinner.setClickable(false);
		sendSpinner.setEnabled(false);
	}

	public String stripPort(String ipWithPort) {
		int pos = ipWithPort.indexOf(":");
		if (pos != -1) {
			ipWithPort = ipWithPort.substring(1, pos);
		}
		return ipWithPort;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		Action action = ExtendedActions.sendBegin(this, connection);
		sequence.addAction(action);
		return null;
	}

	@Override
	public void setIpList(HashMap<String, String> ips) {
		@SuppressWarnings("unchecked")
		final HashMap<String, String> ipList = (HashMap<String, String>) ips.clone();
		Activity activity = (Activity) context;
		if (context != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (sendSpinner.getSelectedItemPosition() < (sendSpinner.getCount() - 1)) {
						selectedItem = sendSpinner.getSelectedItemPosition();
					}
					dataAdapter.clear();
					Iterator<Entry<String, String>> it = ipList.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, String> pairs = it.next();
						String ip = pairs.getKey();
						dataAdapter.add(ip);
						availableIps.put(pairs.getKey(), stripPort(pairs.getValue()));
					}
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sendSpinner.setAdapter(dataAdapter);
					if (ipList.size() == 0) {
						dataAdapter.add(context.getString(R.string.empty));
					}
					if (selectedItem != -1
							&& !sendSpinner.getItemAtPosition(selectedItem).equals(context.getString(R.string.scan))) {
						sendSpinner.setSelection(selectedItem);
					}
					dataAdapter.add(context.getString(R.string.scan));
				}
			});
		} else {
			if (ipList.size() == 0) {
				return;
			}
			Iterator<Entry<String, String>> it = ipList.entrySet().iterator();
			availableIps = new HashMap<String, String>();
			Entry<String, String> pairs = it.next();
			ipToConnect = stripPort(pairs.getValue());
			while (it.hasNext()) {
				pairs = it.next();
				availableIps.put(pairs.getKey(), stripPort(pairs.getValue()));
			}
		}
	}

	public HashMap<String, String> getIpServerList() {
		return availableIps;
	}

	public String getIp() {
		return ipToConnect;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	public interface ConnectionRequest {
		public void addToConnectionRequestList(SendToPcBrick brick);

		public ArrayList<String> getActualIpList();
	}
}
