package com.qrcode.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.qrcode.R;
import com.qrcode.adapter.SettingListAdapter;
import com.qrcode.utils.GenerateXML;
import com.qrcode.utils.Utils;
import com.qrcode.utils.XmlEncryption;

/**
 * 设置情景状态
 * 
 * @author zhaolin
 * 
 */
public class SettingActivity extends BaseActivity {

	private static final int PICK_APP = 1;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private String modelName;
	private ListView listView;
	private SettingListAdapter adapter;
	private Button button;
	private Button backButton;
	private TextView settingTitle;

	public XmlEncryption xmlEncryption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		modelName = getIntent().getStringExtra("model").toString();
		list = setList(modelName);
		button = (Button) findViewById(R.id.button);
		backButton = (Button) findViewById(R.id.setting_back_button);
		settingTitle = (TextView) findViewById(R.id.setting_title);

		settingTitle.setText(modelName);

		listView = (ListView) findViewById(R.id.setting_list);

		adapter = new SettingListAdapter(this, list);
		listView.setAdapter(adapter);

		button.setOnClickListener(new buttonListener());
		backButton.setOnClickListener(new backButtonListener());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private List<Map<String, Object>> setList(String s) {

		SharedPreferences sp = getSharedPreferences(s, MODE_PRIVATE);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.setting_wifi));
		map.put("item_check", sp.getBoolean(Utils.WIFI, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name",
				getResources().getString(R.string.setting_mobile_data));
		map.put("item_check", sp.getBoolean(Utils.MOBILE, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name",
				getResources().getString(R.string.setting_bluetooth));
		map.put("item_check", sp.getBoolean(Utils.BLUETOOTH, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.setting_synchro));
		map.put("item_check", sp.getBoolean(Utils.SYNCHRO, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.setting_mute));
		map.put("item_check", sp.getBoolean(Utils.MUTE, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.setting_vibrate));
		map.put("item_check", sp.getBoolean(Utils.VIBRATE, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name",
				getResources().getString(R.string.setting_flight_mode));
		map.put("item_check", sp.getBoolean(Utils.FLIGHT, false));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.setting_touch));
		map.put("item_check", sp.getBoolean(Utils.TOUCH, false));
		list.add(map);

		return list;
	}

	@SuppressWarnings("unchecked")
	private void changeList(String s) {
		Map<Integer, Object> state = adapter.state;
		HashMap<String, Object> map;
		List<Map<String, Object>> settingList = new ArrayList<Map<String, Object>>();
		SharedPreferences sp = getSharedPreferences(s, MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		for (int i = 0; i < adapter.getCount(); i++) {
			map = (HashMap<String, Object>) adapter.getItem(i);
			if (null != state.get(i)) {

				String itemName = map.get("item_name").toString();
				boolean checked = true;
				map.put(itemName, checked);
				settingList.add(map);
			} else {
				String itemName = map.get("item_name").toString();
				boolean checked = false;
				map.put(itemName, checked);
				settingList.add(map);
			}

		}

		for (int i = 0; i < settingList.size(); i++) {
			if (settingList.get(i).get("item_name").equals(Utils.WIFI)) {
				if (settingList.get(i).get(Utils.WIFI).equals(true)) {
					editor.putBoolean(Utils.WIFI, true);
				} else {
					editor.putBoolean(Utils.WIFI, false);
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.MOBILE)) {
				if (settingList.get(i).get(Utils.MOBILE).equals(true)) {
					editor.putBoolean(Utils.MOBILE, true);
				} else {
					editor.putBoolean(Utils.MOBILE, false);
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.BLUETOOTH)) {
				if (settingList.get(i).get(Utils.BLUETOOTH).equals(true)) {
					editor.putBoolean(Utils.BLUETOOTH, true);
				} else {
					editor.putBoolean(Utils.BLUETOOTH, false);
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.SYNCHRO)) {
				if (settingList.get(i).get(Utils.SYNCHRO).equals(true)) {
					editor.putBoolean(Utils.SYNCHRO, true);
				} else {
					editor.putBoolean(Utils.SYNCHRO, false);
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.MUTE)) {
				if (settingList.get(i).get(Utils.MUTE).equals(true)) {
					editor.putBoolean(Utils.MUTE, true);
				} else {
					editor.putBoolean(Utils.MUTE, false);
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.VIBRATE)) {
				if (settingList.get(i).get(Utils.VIBRATE).equals(true)) {
					editor.putBoolean(Utils.VIBRATE, true);
				} else {
					editor.putBoolean(Utils.VIBRATE, false);
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.FLIGHT)) {
				if (settingList.get(i).get(Utils.FLIGHT).equals(true)) {
					editor.putBoolean(Utils.FLIGHT, true);
				} else {
					editor.putBoolean(Utils.FLIGHT, false);
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.TOUCH)) {
				if (settingList.get(i).get(Utils.TOUCH).equals(true)) {
					editor.putBoolean(Utils.TOUCH, true);
				} else {
					editor.putBoolean(Utils.TOUCH, false);
				}
			}
		}
		editor.commit();
	}

	class buttonListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			String toXML = settingToXML();
			String code = XmlEncryption.encryption(toXML);
			changeList(modelName);
			launchSearch(code);
			SettingActivity.this.finish();
		}
	}

	@SuppressWarnings("unchecked")
	private String settingToXML() {
		String XML = "";
		Map<Integer, Object> state = adapter.state;
		HashMap<String, Object> map;
		List<Map<String, Object>> settingList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < adapter.getCount(); i++) {
			map = (HashMap<String, Object>) adapter.getItem(i);
			if (null != state.get(i)) {

				String itemName = map.get("item_name").toString();
				boolean checked = true;
				map.put(itemName, checked);
				settingList.add(map);
			} else {
				String itemName = map.get("item_name").toString();
				boolean checked = false;
				map.put(itemName, checked);
				settingList.add(map);
			}

		}

		XML = GenerateXML.listToXML(settingList, modelName);
		return XML;
	}

	private void launchSearch(String text) {
		Intent intent = new Intent(Intents.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, text);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		intent.putExtra("model", modelName);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_APP:
				showTextAsBarcode(intent
						.getStringExtra(Browser.BookmarkColumns.URL));
				break;
			}
		}
	}

	private void showTextAsBarcode(String text) {

		if (text == null) {
			return; // Show error?
		}
		Intent intent = new Intent(Intents.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, text);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		startActivity(intent);
	}

	private class backButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SettingActivity.this.finish();
		}

	}
}
