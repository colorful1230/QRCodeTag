package com.qrcode.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.CaptureActivityHandler;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.URIResultHandler;
import com.google.zxing.client.result.ResultParser;
import com.qrcode.PreferenceConfig;
import com.qrcode.R;
import com.qrcode.utils.ParserXML;
import com.qrcode.utils.Utils;
import com.qrcode.utils.XmlEncryption;

/**
 * 获取并识别二维码内容
 * 
 * @author zhaolin
 * 
 */

public class CaptureQRCodeActivity extends BaseActivity implements
		SurfaceHolder.Callback {

	private ViewfinderView viewfinderView;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private BeepManager beepManager;
	private Result lastResult;
	private Result saveResultToShow;

	private TextView statusView;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private boolean hasSurface;

	static class CurrentModel {
		String name;
		boolean wifi;
		boolean mobileData;
		boolean bluetooth;
		boolean synchro;
		boolean mute;
		boolean vibrate;
		boolean flightMode;
		boolean gps;
		boolean touch;
	}

	public ViewfinderView getViewfinderView() {
		return this.viewfinderView;
	}

	public Handler getHandler() {
		return this.handler;
	}

	public CameraManager getCameraManager() {
		return this.cameraManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置窗口全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		hasSurface = false;
		beepManager = new BeepManager(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		statusView = (TextView) findViewById(R.id.status_view);

		handler = null;
		lastResult = null;

		SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();

		if (hasSurface) {
			initCamera(mSurfaceHolder);
		} else {
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();

		decodeFormats = null;
		characterSet = null;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (null != handler) {
			handler.quitSynchronously();
			handler = null;
		}
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (null == holder) {

		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		hasSurface = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (null != lastResult) {
				finish();
				return true;
			}
		} else if (KeyEvent.KEYCODE_FOCUS == keyCode
				|| KeyEvent.KEYCODE_CAMERA == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decodeOrStoreSavedbitmap(Bitmap bitmap, Result result) {
		if (null == handler) {
			saveResultToShow = result;
		} else {
			if (null != result) {
				saveResultToShow = result;
			}
			if (null != saveResultToShow) {
				Message message = Message
						.obtain(handler, R.id.decode_succeeded);
				handler.sendMessage(message);
			}
			saveResultToShow = null;
		}
	}

	/**
	 * 识别二维码后显示结果
	 * 
	 * @param rawResult
	 *            二维码内容
	 * @param barcode
	 *            从摄像头获取的二维码图片
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		lastResult = rawResult;

		URIResultHandler resultHandler = new URIResultHandler(this,
				ResultParser.parseResult(rawResult));

		if (null == barcode) {
			handleDecodeInternally(rawResult, resultHandler, null);
		} else {
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, rawResult);
			if (PreferenceConfig.KEY_BULK_MODE_ENABLE) {
				Toast.makeText(this, R.string.msg_bulk_mode_scanned,
						Toast.LENGTH_SHORT).show();
			} else {
				handleDecodeInternally(rawResult, resultHandler, barcode);
			}
		}
	}

	/**
	 * 在图上的识别点画点
	 * 
	 * @param barcode
	 *            捕获图像的位图
	 * @param rawResult
	 *            储存需要画点的结果
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (null != points && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2,
					barcode.getHeight());
			canvas.drawRect(border, paint);

			if (2 == points.length) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (4 == points.length
					&& (BarcodeFormat.UPC_A == rawResult.getBarcodeFormat() || BarcodeFormat.EAN_13 == rawResult
							.getBarcodeFormat())) {
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	/**
	 * 画线
	 * 
	 * @param canvas
	 * @param paint
	 * @param a
	 * @param b
	 */
	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	/**
	 * 显示结果
	 * 
	 * @param result
	 * @param resultHandler
	 * @param barcode
	 */
	private void showResult(Result result, URIResultHandler resultHandler,
			Bitmap barcode) {

		String showText = "设置成功" + "\n";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence displayContents = resultHandler.getDisplayContents();

		String xml = XmlEncryption.decryption(displayContents.toString());
		if (null == barcode) {
			builder.setIcon(R.drawable.ic_launcher);
		}
		if (!ParserXML.isXmlLegal(xml)) {
			Toast.makeText(CaptureQRCodeActivity.this,
					getResources().getString(R.string.capture_wrong),
					Toast.LENGTH_SHORT).show();
			CaptureQRCodeActivity.this.finish();
		} else {
			List<Map<String, String>> list = ParserXML.getList(xml);

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).containsKey("MODEL")) {
					showText = showText + list.get(i).get("MODEL").toString()
							+ "\n";
				} else if (list.get(i).containsKey("WIFI")) {
					showText = showText + "WIFI" + "\t"
							+ list.get(i).get("WIFI").toString() + "\n";
				} else if (list.get(i).containsKey("MOBILE")) {
					showText = showText + Utils.MOBILE + "\t"
							+ list.get(i).get("MOBILE").toString() + "\n";
				} else if (list.get(i).containsKey("BLUETOOTH")) {
					showText = showText + Utils.BLUETOOTH + "\t"
							+ list.get(i).get("BLUETOOTH").toString() + "\n";
				} else if (list.get(i).containsKey("SYNCHRO")) {
					showText = showText + Utils.SYNCHRO + "\t"
							+ list.get(i).get("SYNCHRO").toString() + "\n";
				} else if (list.get(i).containsKey("MUTE")) {
					showText = showText + Utils.MUTE + "\t"
							+ list.get(i).get("MUTE").toString() + "\n";
				} else if (list.get(i).containsKey("VIBRATE")) {
					showText = showText + Utils.VIBRATE + "\t"
							+ list.get(i).get("VIBRATE").toString() + "\n";
				} else if (list.get(i).containsKey("FLIGHT")) {
					showText = showText + Utils.FLIGHT + "\t"
							+ list.get(i).get("FLIGHT").toString() + "\n";
				} else if (list.get(i).containsKey("TOUCH")) {
					showText = showText + Utils.TOUCH + "\t"
							+ list.get(i).get("TOUCH").toString();
				}

			}
			setModel(parserModel(list));
			showToast(showText);
			CaptureQRCodeActivity.this.finish();

		}
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param rawResult
	 * @param resultHandler
	 * @param bitmap
	 *            二维码截图
	 */
	private void handleDecodeInternally(Result rawResult,
			URIResultHandler resultHandler, Bitmap bitmap) {
		statusView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		showResult(rawResult, resultHandler, bitmap);
	}

	/**
	 * 初始化相机
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);
			if (null == handler) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
			decodeOrStoreSavedbitmap(null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayFrameworkBugMessageAndExit();
		}

	}

	/**
	 * 相机无法使用时调用
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	public CurrentModel parserModel(List<Map<String, String>> list) {
		CurrentModel model = new CurrentModel();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).containsKey("MODEL")) {
				model.name = list.get(i).get("MODEL");
			}
			if (list.get(i).containsKey("WIFI")) {
				if (list.get(i).get("WIFI").equals("ON")) {
					model.wifi = true;
				} else {
					model.wifi = false;
				}
			} else if (list.get(i).containsKey("MOBILE")) {
				if (list.get(i).get("MOBILE").equals("ON")) {
					model.mobileData = true;
				} else {
					model.mobileData = false;
				}
			} else if (list.get(i).containsKey("BLUETOOTH")) {
				if (list.get(i).get("BLUETOOTH").equals("ON")) {
					model.bluetooth = true;
				} else {
					model.bluetooth = false;
				}
			} else if (list.get(i).containsKey("SYNCHRO")) {
				if (list.get(i).get("SYNCHRO").equals("ON")) {
					model.synchro = true;
				} else {
					model.synchro = false;
				}
			} else if (list.get(i).containsKey("MUTE")) {
				if (list.get(i).get("MUTE").equals("ON")) {
					model.mute = true;
				} else {
					model.mute = false;
				}
			} else if (list.get(i).containsKey("VIBRATE")) {
				if (list.get(i).get("VIBRATE").equals("ON")) {
					model.vibrate = true;
				} else {
					model.vibrate = false;
				}
			} else if (list.get(i).containsKey("FLIGHT")) {
				if (list.get(i).get("FLIGHT").equals("ON")) {
					model.flightMode = true;
				} else {
					model.flightMode = false;
				}
			} else if (list.get(i).containsKey("TOUCH")) {
				if (list.get(i).get("TOUCH").equals("ON")) {
					model.touch = true;
				} else {
					model.touch = false;
				}
			}
		}
		return model;
	}

	private void setModel(CurrentModel model) {
		setWifi(model.wifi);
		setBluetooth(model.bluetooth);
		setRingMode(model.mute, model.vibrate);
		setSynchro(model.synchro);
		setSynchro(model.synchro);
		setMobileDate(model.mobileData);
		setFlightMode(model.flightMode);
		setTouch(model.touch);
	}

	/**
	 * 设置wifi状态
	 * 
	 * @param b
	 */
	private void setWifi(boolean b) {
		WifiManager wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
		if (true == wifiManager.isWifiEnabled()) {
			if (true == b) {

			} else {
				wifiManager.setWifiEnabled(false);
			}
		} else {
			if (false == b) {

			} else {
				wifiManager.setWifiEnabled(true);
			}
		}

	}

	/**
	 * 设置移动数据状态，需要用到反射
	 * 
	 * @param b
	 */
	private void setMobileDate(boolean b) {
		ConnectivityManager connManager;
		connManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> cmClass = connManager.getClass();
		Class<?>[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		// 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络
		Method method;
		try {
			method = cmClass.getMethod("setMobileDataEnabled", argClasses);
			method.invoke(connManager, b);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置蓝牙状态
	 * 
	 * @param b
	 */
	private void setBluetooth(boolean b) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (true == bluetoothAdapter.isEnabled()) {
			if (true == b) {

			} else {
				bluetoothAdapter.disable();
			}
		} else {
			if (false == b) {

			} else {
				bluetoothAdapter.enable();
			}
		}

	}

	/**
	 * 设置同步
	 * 
	 * @param b
	 */
	private void setSynchro(boolean b) {
		ContentResolver.setMasterSyncAutomatically(b);
	}

	/**
	 * 设置铃声模式
	 * 
	 * @param mute
	 * @param vibrate
	 */
	private void setRingMode(boolean mute, boolean vibrate) {
		AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		if (false == mute && false == vibrate) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_OFF);
			audioManager.setVibrateSetting(
					AudioManager.VIBRATE_TYPE_NOTIFICATION,
					AudioManager.VIBRATE_SETTING_OFF);
		} else if (true == mute && false == vibrate) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (false == mute && true == vibrate) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_ON);
			audioManager.setVibrateSetting(
					AudioManager.VIBRATE_TYPE_NOTIFICATION,
					AudioManager.VIBRATE_SETTING_ON);
		} else if (true == mute && true == vibrate) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
	}

	/**
	 * 设置飞行模式
	 * 
	 * @param b
	 */
	private void setFlightMode(boolean b) {
		if (true == b) {
			Settings.System.putInt(this.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 1);

		} else {
			Settings.System.putInt(this.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
		}
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", b);
		this.sendBroadcast(intent);

	}

	/**
	 * 设置触摸震动
	 * 
	 * @param b
	 */
	private void setTouch(boolean b) {
		if (true == b) {
			Settings.System.putInt(getContentResolver(),
					Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
		} else {
			Settings.System.putInt(getContentResolver(),
					Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
		}
	}

	private void showToast(String text) {
		LayoutInflater inflater = LayoutInflater
				.from(CaptureQRCodeActivity.this);
		View view = inflater.inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		TextView textView = (TextView) view.findViewById(R.id.text);

		textView.setText(text);

		Toast toast = new Toast(CaptureQRCodeActivity.this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
