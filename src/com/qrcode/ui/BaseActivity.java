package com.qrcode.ui;

import com.qrcode.AppManager;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity基础类
 * 
 * @author zhaolin
 * 
 */
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		AppManager.getAppManager().finishActivity(this);
	}

}
