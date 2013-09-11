package com.qrcode.utils;

import java.util.List;
import java.util.Map;

/**
 * 将设置好的情景模式转化成XML
 * 
 * @author zhaolin
 * 
 */
public class GenerateXML {

	public final static String listToXML(List<Map<String, Object>> settingList,
			String name) {
		String xml = "<SETTING><MODEL>" + name + "</MODEL>";

		for (int i = 0; i < settingList.size(); i++) {
			if (settingList.get(i).get("item_name").equals(Utils.WIFI)) {
				if (settingList.get(i).get(Utils.WIFI).equals(true)) {
					xml = xml + "<WIFI>ON</WIFI>";
				} else {
					xml = xml + "<WIFI>OFF</WIFI>";
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.MOBILE)) {
				if (settingList.get(i).get(Utils.MOBILE).equals(true)) {
					xml = xml + "<MOBILE>ON</MOBILE>";
				} else {
					xml = xml + "<MOBILE>OFF</MOBILE>";
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.BLUETOOTH)) {
				if (settingList.get(i).get(Utils.BLUETOOTH).equals(true)) {
					xml = xml + "<BLUETOOTH>ON</BLUETOOTH>";
				} else {
					xml = xml + "<BLUETOOTH>OFF</BLUETOOTH>";
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.SYNCHRO)) {
				if (settingList.get(i).get(Utils.SYNCHRO).equals(true)) {
					xml = xml + "<SYNCHRO>ON</SYNCHRO>";
				} else {
					xml = xml + "<SYNCHRO>OFF</SYNCHRO>";
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.MUTE)) {
				if (settingList.get(i).get(Utils.MUTE).equals(true)) {
					xml = xml + "<MUTE>ON</MUTE>";
				} else {
					xml = xml + "<MUTE>OFF</MUTE>";
				}
			} else if (settingList.get(i).get("item_name")
					.equals(Utils.VIBRATE)) {
				if (settingList.get(i).get(Utils.VIBRATE).equals(true)) {
					xml = xml + "<VIBRATE>ON</VIBRATE>";
				} else {
					xml = xml + "<VIBRATE>OFF</VIBRATE>";
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.FLIGHT)) {
				if (settingList.get(i).get(Utils.FLIGHT).equals(true)) {
					xml = xml + "<FLIGHT>ON</FLIGHT>";
				} else {
					xml = xml + "<FLIGHT>OFF</FLIGHT>";
				}
			} else if (settingList.get(i).get("item_name").equals(Utils.TOUCH)) {
				if (settingList.get(i).get(Utils.TOUCH).equals(true)) {
					xml = xml + "<TOUCH>ON</TOUCH>";
				} else {
					xml = xml + "<TOUCH>OFF</TOUCH>";
				}
			}
		}
		xml = xml + "</SETTING>";
		return xml;
	}

}
