/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qrcode.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.qrcode.R;

/**
 * This class encodes data from an Intent into a QR code, and then displays it
 * full screen so that another person can scan it with their device.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class EncodeActivity extends BaseActivity {

	private static final String USE_VCARD_KEY = "USE_VCARD";

	private QRCodeEncoder qrCodeEncoder;

	private Button backButton;
	private Button savebutton;
	private TextView encodeTitle;
	private ImageView imageView;
	private Bitmap bitmap;

	private String modelName;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent intent = getIntent();
		if (intent == null) {
			finish();
		} else {
			String action = intent.getAction();
			if (Intents.Encode.ACTION.equals(action)
					|| Intent.ACTION_SEND.equals(action)) {
				setContentView(R.layout.encode);
			} else {
				finish();
			}
		}

		modelName = getIntent().getStringExtra("model");

		backButton = (Button) findViewById(R.id.encode_back_button);
		savebutton = (Button) findViewById(R.id.encode_save_button);
		encodeTitle = (TextView) findViewById(R.id.encode_title);

		encodeTitle.setText(modelName);

		backButton.setOnClickListener(new backbuttonListener());
		savebutton.setOnClickListener(new saveButtonListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// This assumes the view is full screen, which is a good assumption
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		int smallerDimension = width < height ? width : height;
		smallerDimension = smallerDimension * 7 / 8;

		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		try {
			boolean useVCard = intent.getBooleanExtra(USE_VCARD_KEY, false);
			qrCodeEncoder = new QRCodeEncoder(this, intent, smallerDimension,
					useVCard);
			bitmap = qrCodeEncoder.encodeAsBitmap();
			if (bitmap == null) {
				showErrorMessage(R.string.msg_encode_contents_failed);
				qrCodeEncoder = null;
				return;
			}

			imageView = (ImageView) findViewById(R.id.image_view);
			imageView.setImageBitmap(bitmap);

		} catch (WriterException e) {
			showErrorMessage(R.string.msg_encode_contents_failed);
			qrCodeEncoder = null;
		}
	}

	private void showErrorMessage(int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		builder.show();
	}

	private class backbuttonListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EncodeActivity.this.finish();
		}
	}

	private class saveButtonListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			savePicture(modelName, bitmap);
		}

	}

	private void savePicture(String name, Bitmap bitmap) {
		boolean isSDcardExisted = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (false == isSDcardExisted) {
			Toast.makeText(EncodeActivity.this, "未找到SD卡，保存失败",
					Toast.LENGTH_SHORT).show();
		} else {
			String dir = Environment.getExternalStorageDirectory().getPath()
					+ "/qrcodetag/";

			File fileDir = new File(dir);

			if (!fileDir.exists()) {
				fileDir.mkdir();
			} else {
				String filePath = dir + name + ".png";
				File fileImg = new File(filePath);
				try {
					fileImg.createNewFile();
					FileOutputStream out = null;
					out = new FileOutputStream(fileImg);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(EncodeActivity.this, "已保存到" + filePath,
						Toast.LENGTH_LONG).show();
			}
		}

	}
}
