package com.qrcode.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ParserXML {

	public final static List<Map<String, String>> getList(String xml) {

		InputSource s = new InputSource(new StringReader(xml));
		Map<String, String> map;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String value = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(s);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("MODEL").item(0).getFirstChild()
					.getNodeValue();
			map.put("MODEL", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("WIFI").item(0).getFirstChild()
					.getNodeValue();
			map.put("WIFI", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("MOBILE").item(0).getFirstChild()
					.getNodeValue();
			map.put("MOBILE", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("BLUETOOTH").item(0)
					.getFirstChild().getNodeValue();
			map.put("BLUETOOTH", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("SYNCHRO").item(0).getFirstChild()
					.getNodeValue();
			map.put("SYNCHRO", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("MUTE").item(0).getFirstChild()
					.getNodeValue();
			map.put("MUTE", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("VIBRATE").item(0).getFirstChild()
					.getNodeValue();
			map.put("VIBRATE", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("FLIGHT").item(0).getFirstChild()
					.getNodeValue();
			map.put("FLIGHT", value);
			list.add(map);

			map = new HashMap<String, String>();
			value = doc.getElementsByTagName("TOUCH").item(0).getFirstChild()
					.getNodeValue();
			map.put("TOUCH", value);
			list.add(map);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	public final static boolean isXmlLegal(String xml) {
		boolean b = false;

		if (xml.contains("<SETTING>")
				&& xml.contains("<MODEL>")
				&& xml.contains("<WIFI>")
				&& xml.contains("<MOBILE>")
				&& xml.contains("<BLUETOOTH>")
				&& xml.contains("<SYNCHRO>")
				&& xml.contains("<MUTE>")
				&& xml.contains("<VIBRATE>")
				&& xml.contains("<FLIGHT>")
				&& xml.contains("<TOUCH>")) {
			b = true;
		} else {
			b = false;
		}

		return b;
	}

}
