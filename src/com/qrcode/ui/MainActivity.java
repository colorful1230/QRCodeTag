package com.qrcode.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qrcode.R;
import com.qrcode.utils.Utils;

/**
 * 
 * @author zhaolin
 * 
 */
public class MainActivity extends BaseActivity {

	private ListView defaultModelListView;
	private ListView userModelListView;
	private SimpleAdapter defaultAdapter;
	private SimpleAdapter userAdapter;

	private Map<String, Object> map;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
	private String modelName;
	static final int MAX_COUNT = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		defaultModelListView = (ListView) findViewById(R.id.default_setting_list);
		userModelListView = (ListView) findViewById(R.id.user_setting_list);

		defaultAdapter = new SimpleAdapter(this, list, R.layout.model_item,
				new String[] { "item_name" }, new int[] { R.id.model_text });

		userAdapter = new SimpleAdapter(this, userList, R.layout.model_item,
				new String[] { "item_name" }, new int[] { R.id.model_text });

		defaultModelListView.setAdapter(defaultAdapter);
		userModelListView.setAdapter(userAdapter);

		defaultModelListView
				.setOnItemClickListener(new defaultListViewOnItemClickListener());
		userModelListView
				.setOnItemClickListener(new userListViewOnItemClickListener());
		setDefaultList();
		readList();

		registerForContextMenu(userModelListView);
	}

	private void setDefaultList() {

		setInitialModel();
		setFlightModel();

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("item_name", getResources().getString(R.string.model_initial));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("item_name", getResources().getString(R.string.model_sleep));
		list.add(map);

	}

	private void setUserList(String s) {

		map = new HashMap<String, Object>();
		map.put("item_name", s);
		userList.add(map);
		SharedPreferences sp = getSharedPreferences("user_pref", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(s, s);
		editor.commit();
		userAdapter.notifyDataSetChanged();
	}

	public void scanQRCode(View view) {
		Intent intent = new Intent(MainActivity.this,
				CaptureQRCodeActivity.class);
		startActivity(intent);
	}

	public void addUserModel(View view) {
		final EditText editText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(getResources().getString(R.string.main_input));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(editText);

		builder.setNegativeButton(getResources().getString(R.string.ok),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (userList.size() < MAX_COUNT) {
							modelName = editText.getText().toString().trim();
							if (TextUtils.isEmpty(modelName)) {
								Toast.makeText(MainActivity.this, "名称不能为空",
										Toast.LENGTH_SHORT).show();
							} else if (true == modelIsExisted(modelName)) {
								Toast.makeText(MainActivity.this, "已存在",
										Toast.LENGTH_SHORT).show();
							} else {
								setUserList(modelName);
								initialModel(modelName);
								Intent intent = new Intent(MainActivity.this,
										SettingActivity.class);
								intent.putExtra("model", modelName);
								startActivity(intent);
							}

						} else {
							Toast.makeText(
									MainActivity.this,
									getResources().getString(R.string.main_max),
									Toast.LENGTH_SHORT).show();
						}

					}
				});
		builder.setPositiveButton(getResources().getString(R.string.cancal),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		builder.show();
	}

	private void readList() {

		Map<String, Object> map;

		SharedPreferences sp = getSharedPreferences("user_pref", MODE_PRIVATE);
		Map<String, ?> spMap = sp.getAll();

		for (Map.Entry<String, ?> entry : spMap.entrySet()) {
			String k = entry.getKey().toString();
			map = new HashMap<String, Object>();
			map.put("item_name", k);
			userList.add(map);
		}

	}

	private void removeUserModel(int id) {
		SharedPreferences sp = getSharedPreferences("user_pref", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		String tmp = userList.get(id).get("item_name").toString();
		userList.remove(id);
		userAdapter.notifyDataSetChanged();
		editor.remove(tmp);
		editor.commit();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		final EditText editText = new EditText(this);
		final AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.edit:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("重命名");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setView(editText);
			builder.setNegativeButton(getResources().getString(R.string.ok),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String newName = editText.getText().toString()
									.trim();
							String oldName = userList.get(info.position)
									.get("item_name").toString();
							if (true == modelIsExisted(newName)) {
								Toast.makeText(MainActivity.this, "已存在",
										Toast.LENGTH_SHORT).show();
							} else {
								SharedPreferences sp = getSharedPreferences(
										"user_pref", MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.remove(oldName);
								editor.putString(newName, newName);
								editor.commit();

								String nawPath = "/data/data/com.qrcode/shared_prefs/"
										+ newName + ".xml";
								File file = new File(
										"/data/data/com.qrcode/shared_prefs/"
												+ oldName + ".xml");
								File newFile = new File(nawPath);
								file.renameTo(newFile);
								userList.clear();
								userAdapter.notifyDataSetChanged();
								readList();
							}

						}
					});
			builder.setPositiveButton(
					getResources().getString(R.string.cancal),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			builder.show();

			break;
		case R.id.delete:
			String name = userList.get(info.position).get("item_name")
					.toString().trim();
			System.out.println(name);
			removeUserModel(info.position);
			File file = new File("/data/data/com.qrcode/shared_prefs/" + name
					+ ".xml");
			if (file.exists()) {
				file.delete();
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting_menu, menu);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_about:
			showToast();
			break;
		case R.id.menu_exit:
			MainActivity.this.finish();
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class defaultListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			intent.putExtra("model", list.get(arg2).get("item_name").toString());
			startActivity(intent);
		}

	}

	private class userListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			intent.putExtra("model", userList.get(arg2).get("item_name")
					.toString());
			startActivity(intent);
		}

	}

	private boolean modelIsExisted(String name) {
		boolean b = false;
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).get("item_name").toString().trim().equals(name)) {
				b = true;
			} else {
				b = false;
			}
		}
		return b;
	}

	private void showToast() {
		LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		View view = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		TextView textView = (TextView) view.findViewById(R.id.text);

		textView.setText(getResources().getString(R.string.about));

		Toast toast = new Toast(MainActivity.this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void setInitialModel() {
		File file = new File("/data/data/com.qrcode/shared_prefs/初始模式.xml");
		if (!file.exists()) {
			SharedPreferences sp = getSharedPreferences("初始模式", MODE_PRIVATE);

			SharedPreferences.Editor editor = sp.edit();
			editor.putString("Model", "初始模式");
			editor.putBoolean(Utils.WIFI, false);
			editor.putBoolean(Utils.MOBILE, true);
			editor.putBoolean(Utils.BLUETOOTH, false);
			editor.putBoolean(Utils.SYNCHRO, false);
			editor.putBoolean(Utils.MUTE, false);
			editor.putBoolean(Utils.VIBRATE, true);
			editor.putBoolean(Utils.FLIGHT, false);
			editor.putBoolean(Utils.TOUCH, true);
			editor.commit();
		}
	}

	private void setFlightModel() {
		File file = new File("/data/data/com.qrcode/shared_prefs/睡眠模式.xml");
		if (!file.exists()) {
			SharedPreferences sp = getSharedPreferences("睡眠模式", MODE_PRIVATE);

			SharedPreferences.Editor editor = sp.edit();
			editor.putString("Model", "睡眠模式");
			editor.putBoolean(Utils.WIFI, false);
			editor.putBoolean(Utils.MOBILE, false);
			editor.putBoolean(Utils.BLUETOOTH, false);
			editor.putBoolean(Utils.SYNCHRO, false);
			editor.putBoolean(Utils.MUTE, true);
			editor.putBoolean(Utils.VIBRATE, false);
			editor.putBoolean(Utils.FLIGHT, true);
			editor.putBoolean(Utils.TOUCH, false);
			editor.commit();
		}
	}

	private void initialModel(String name) {
		File file = new File(Utils.PATH + name + ".xml");
		if (!file.exists()) {
			SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);

			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(Utils.WIFI, false);
			editor.putBoolean(Utils.MOBILE, false);
			editor.putBoolean(Utils.BLUETOOTH, false);
			editor.putBoolean(Utils.SYNCHRO, false);
			editor.putBoolean(Utils.MUTE, false);
			editor.putBoolean(Utils.VIBRATE, false);
			editor.putBoolean(Utils.FLIGHT, false);
			editor.putBoolean(Utils.TOUCH, false);
			editor.commit();
		}
	}

}
