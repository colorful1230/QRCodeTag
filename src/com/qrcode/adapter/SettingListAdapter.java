package com.qrcode.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.qrcode.R;

/**
 * 自定义ListView
 * 
 * @author zhaolin
 * 
 */
public class SettingListAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	@SuppressLint("UseSparseArrays")
	public Map<Integer, Object> state = new HashMap<Integer, Object>();

	private static class ViewHolder {
		TextView itemName;
		CheckBox itemChecked;
	}

	public SettingListAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.setting_item, null);
		ViewHolder holder = new ViewHolder();
		holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
		holder.itemChecked = (CheckBox) convertView
				.findViewById(R.id.item_check);
		holder.itemName.setText((String) list.get(position).get("item_name"));
		if ((Boolean) list.get(position).get("item_check")) {
			state.put(position, true);
		}
		holder.itemChecked
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							state.put(position, isChecked);
						} else {
							state.remove(position);
						}
					}
				});
		holder.itemChecked.setChecked(state.get(position) == null ? false
				: true);
		return convertView;
	}

}
