package com.crazybee.quicksetting;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crazybee.adapter.QuickSettingAdapter;
import com.crazybee.data.QuickSettingItem;
import com.crazybee.data.ViewHolder;
import com.crazybee.database.MyDBHelper;

public class QuickSettingActivity extends Activity implements
		OnItemLongClickListener, Callback {
	private GridView mygridview;
	private List<QuickSettingItem> quickSettingList = new ArrayList<QuickSettingItem>();

	private static final String TABLE_NAME = "quicksetting_list";
	private SQLiteDatabase db;
	private MyDBHelper databaseOpenHelper;
	private QuickSettingAdapter adapter;
	private PackageManager pm;
	private int clickcount = 0;
	private boolean lightOn;
	private Camera mCamera;
	private WakeLock wakeLock;
	private boolean previewOn;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private static final String WAKE_LOCK_TAG = "TORCH_WAKE_LOCK";

	private boolean fifteen_s;
	private boolean one_m;
	private boolean five_m;
	private Cursor cursor;
	private ViewHolder holder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_setting);
		pm = getPackageManager();
		fifteen_s = false;
		one_m = false;
		five_m = false;
		// getListData();
		runDatabase();
		initGridview();
		initSurfaceView();
	}

	private void initSurfaceView() {
		// TODO Auto-generated method stub
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
	}

	private void initCamera() {
		// TODO Auto-generated method stub
		if (mCamera == null) {
			try {
				mCamera = Camera.open();
			} catch (RuntimeException e) {
			}
		}
	}

	public void runDatabase() {
		databaseOpenHelper = new MyDBHelper(QuickSettingActivity.this,
				"quicksetting.db", 9);
		db = databaseOpenHelper.getWritableDatabase();
		// db query

		cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		int imagetype = 0;
		int nameid = 0;
		QuickSettingItem settingItem = null;

		if (cursor != null && cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				
				settingItem = new QuickSettingItem();
				imagetype = cursor.getInt(cursor.getColumnIndex("imagetype"));
				nameid = cursor.getInt(cursor.getColumnIndex("nameid"));
				String packagename = cursor.getString(cursor
						.getColumnIndex("packagename"));
				String classname = cursor.getString(cursor
						.getColumnIndex("classname"));

				settingItem.setNameId(nameid);
				settingItem.setImageType(imagetype);
				settingItem.setPackageName(packagename);
				settingItem.setClassName(classname);
				quickSettingList.add(settingItem);
			}
		}
		int listSize = quickSettingList.size();
		if (listSize > 0) {// 放置�?���?��添加的按钮，�?���?��按钮�?��存在
			QuickSettingItem list1 = new QuickSettingItem();
			list1.setNameId(R.string.gt_qs_add);
			list1.setImageType(1);
			quickSettingList.add(listSize, list1);
		}
	}

	private void initGridview() {
		// TODO Auto-generated method stub
		// 实例化一个�?配器
		adapter = new QuickSettingAdapter(QuickSettingActivity.this,
				quickSettingList);
		mygridview = (GridView) findViewById(R.id.quick_setting_gridview);
		mygridview.setAdapter(adapter);
		mygridview.setOnItemClickListener(new ItemClickListener());
		mygridview.setOnItemLongClickListener(this);
	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			clickcount++;
			if (position == parent.getCount() - 1) {

				Intent intent = new Intent();
				intent.setClass(QuickSettingActivity.this,
						QuickSettingSelectActivity.class);
				startActivity(intent);
				finish();
			} else {

				holder = (ViewHolder) view.getTag();
				Object tag = holder.groupItem.getTag();
				int type = 0;
				int nameid = 0;
				String packagename = null;
				if (tag instanceof QuickSettingItem) {
					// appName = (String) tag;
					QuickSettingItem info = (QuickSettingItem) tag;
					type = info.getImageType();
					nameid = info.getNameId();
					packagename = info.getpackageName();
				}

				RelativeLayout mView = (RelativeLayout) view;
				ImageView childImageView = null;
				TextView childTextView = null;
				View childview = null;
				for (int i = 0; i < mView.getChildCount(); i++) {
					childview = mView.getChildAt(i);
					if (childview instanceof ImageView) {
						childImageView = (ImageView) childview;
					}
					if (childview instanceof TextView) {
						childTextView = (TextView) childview;
					}
				}

				Drawable childDrawable = childImageView.getDrawable();
				String childText = childTextView.getText().toString();

				if (type == 1 && packagename == null) {
					String name = getString(nameid);
					goToByName(name, childImageView);
				} else if (type == 2 && packagename != null) {
					Intent intent = pm.getLaunchIntentForPackage(packagename);
					if (intent != null) {
						startActivity(intent);
					}
				}
			}

		}
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> parent,
			final View view, int position, long arg3) {
		// TODO Auto-generated method stub

		if (position != parent.getCount() - 1) {
			AlertDialog.Builder builder = new Builder(QuickSettingActivity.this);
			builder.setMessage(getString(R.string.delete_or_not));
			builder.setTitle(getString(R.string.tip));
			builder.setPositiveButton(getString(R.string.delete_yes),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							RelativeLayout mView = (RelativeLayout) view;
							ImageView childImageView = null;
							TextView childTextView = null;
							View childview = null;
							for (int i = 0; i < mView.getChildCount(); i++) {
								childview = mView.getChildAt(i);
								if (childview instanceof ImageView) {
									childImageView = (ImageView) childview;
								}
								if (childview instanceof TextView) {
									childTextView = (TextView) childview;
								}
							}

							Drawable childDrawable = childImageView
									.getDrawable();
							String childText = childTextView.getText()
									.toString();

							holder = (ViewHolder) view.getTag();
							Object tag = holder.groupItem.getTag();
							int type = 0;
							int nameid = 0;
							String nameindex = null;
							String packagename = null;
							QuickSettingItem info = null;
							if (tag instanceof QuickSettingItem) {
								// appName = (String) tag;
								info = (QuickSettingItem) tag;
								type = info.getImageType();
								nameid = info.getNameId();
								nameindex = String.valueOf(nameid);
								packagename = info.getpackageName();
							}
							QuickSettingItem qsi = info;
							MyDBHelper databaseOpenHelper = new MyDBHelper(
									QuickSettingActivity.this,
									"quicksetting.db", 9);
							SQLiteDatabase db = databaseOpenHelper
									.getWritableDatabase();
							if (type == 1) {
								db.delete(TABLE_NAME, "nameid =?",
										new String[] { nameindex });
							} else if (type == 2) {
								db.delete(TABLE_NAME, "packagename =?",
										new String[] { packagename });
								for (QuickSettingItem q : quickSettingList) {
									if (q.getpackageName() == packagename) {
										qsi = q;
									}
								}
							}

							db.close();
							quickSettingList.remove(qsi);
							BaseAdapter ba = (BaseAdapter) mygridview
									.getAdapter();
							ba.notifyDataSetChanged();
						}
					});

			builder.setNegativeButton(getString(R.string.delete_no),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
		}

		return false;
	}

	private void goToByName(String name, ImageView childImageView) {
		// TODO Auto-generated method stub
		String wifi = getResources().getString(R.string.gt_qs_wifi);
		String CMCC = getResources().getString(R.string.gt_qs_cmcc);
		String bluetooth = getResources().getString(R.string.gt_qs_bluetooth);
		String autobright = getResources().getString(R.string.gt_qs_bright);
		String gps = getResources().getString(R.string.gt_qs_gps);
//		String airport = getResources().getString(R.string.gt_qs_airport);
		String silence = getResources().getString(R.string.gt_qs_silence);
		String vibrate = getResources().getString(R.string.gt_qs_vibrate);
		String appManage = getResources().getString(R.string.gt_qs_app_manage);
		String taskManage = getResources()
				.getString(R.string.gt_qs_task_manage);
		String local = getResources().getString(R.string.gt_qs_local);
		String putin = getResources().getString(R.string.gt_qs_putin);
		String autowhirl = getResources().getString(R.string.gt_qs_autowhirl);
		String electricity = getResources().getString(
				R.string.gt_qs_electricity);
		String torch = getResources().getString(R.string.qs_torch);
		String timeout = getResources().getString(R.string.qs_screen_dormancy);

		if (name.equals(CMCC)) {
			setMobileDataEnabled(!isMobileDataEnabled(childImageView));
		} else if (name.equals(autobright)) {
			if (clickcount % 2 == 0) {
				stopAutoBrightness(QuickSettingActivity.this);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_brightness_auto_default));
				Toast.makeText(QuickSettingActivity.this,
						getString(R.string.close_autobright),
						Toast.LENGTH_SHORT).show();
			} else {
				startAutoBrightness(QuickSettingActivity.this);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_brightness_auto_blue));
				Toast.makeText(QuickSettingActivity.this,
						getString(R.string.open_autobright), Toast.LENGTH_SHORT)
						.show();
			}

		} else if (name.equals(wifi)) {
			WifiManager wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_wifi_1_default));
				Toast.makeText(this, getString(R.string.close_wifi),
						Toast.LENGTH_SHORT).show();
			} else {
				wifiManager.setWifiEnabled(true);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_wifi_4_default));
				Toast.makeText(this, getString(R.string.open_wifi),
						Toast.LENGTH_SHORT).show();
			}

		} else if (name.equals(gps)) {
			openGPSSettings(QuickSettingActivity.this);
		} else if (name.equals(silence)) {

			//swithRigntone(childImageView);

		} else if (name.equals(vibrate)) {
			//swithVibrate(childImageView);
		} else if (name.equals(appManage)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.setClassName("com.android.settings",
					"com.android.settings.ManageApplications");
			startActivity(intent);
		} else if (name.equals(taskManage)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.setClassName("com.android.settings",
					"com.android.settings.RunningServices");
			startActivity(intent);
		} else if (name.equals(local)) {
			startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
		} else if (name.equals(putin)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.setClassName("com.android.settings",
					"com.android.settings.LanguageSettings");
			startActivity(intent);
//		} else if (name.equals(airport)) {
//			OpenAirplane(QuickSettingActivity.this, childImageView);
		} else if (name.equals(bluetooth)) {
			OpenorCloseBluetooth(QuickSettingActivity.this, childImageView);
		} else if (name.equals(autowhirl)) {
			int flag = Settings.System.getInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION, 0);
			// 打开关闭，关闭打�?��
			if (flag == 1) {
				Settings.System.putInt(getContentResolver(),
						Settings.System.ACCELEROMETER_ROTATION, 0);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_rotation_off_default));
				Toast.makeText(this, getString(R.string.close_autowhirl),
						Toast.LENGTH_SHORT).show();
			} else if (flag == 0) {
				Settings.System.putInt(getContentResolver(),
						Settings.System.ACCELEROMETER_ROTATION, 1);
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_rotation_default));
				Toast.makeText(this, getString(R.string.open_autowhirl),
						Toast.LENGTH_SHORT).show();
			}

		} else if (name.equals(electricity)) {
			Intent intent = new Intent(
					"android.intent.action.POWER_USAGE_SUMMARY");
			startSettingActivity(this, intent);
			startActivity(intent);
		} else if (name.equals(torch)) {
			initCamera();
			startPreview();
			toggleLight(childImageView);
		} else if (name.equals(timeout)) {
			try {

				if (clickcount % 2 == 0) {
					int t = Settings.System.getInt(this.getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT);

					if (t == 1500) {
						childImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.action_screen_time_15s_blue));
						fifteen_s = true;
					} else if (t == 60000) {
						childImageView.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.action_screen_time_1m_blue));
						one_m = true;
					} else if (t == 300000) {
						childImageView.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.action_screen_time_5m_blue));
						five_m = true;
					}
				} else {
					if (fifteen_s) {
						Settings.System.putInt(this.getContentResolver(),
								Settings.System.SCREEN_OFF_TIMEOUT, 60000);
						childImageView.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.action_screen_time_1m_blue));
						fifteen_s = false;
						one_m = true;
						five_m = true;
					} else if (one_m) {
						Settings.System.putInt(this.getContentResolver(),
								Settings.System.SCREEN_OFF_TIMEOUT, 300000);
						childImageView.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.action_screen_time_5m_blue));
						one_m = false;
						fifteen_s = true;
						five_m = true;
					} else if (five_m) {
						Settings.System.putInt(this.getContentResolver(),
								Settings.System.SCREEN_OFF_TIMEOUT, 15000);
						childImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.action_screen_time_15s_blue));
						five_m = false;
						fifteen_s = true;
						one_m = true;
					}
				}

			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*public void swithRigntone(ImageView childImageView) {
		VibrateCommand vibrateCommand = new VibrateCommand(this);
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		SettingsControl.setVibrate(this, vibrateCommand);
		if (current > 0) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_silence_default));

		} else {
			mAudioManager.setStreamVolume(AudioManager.STREAM_RING, max,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_silence_off_default));

		}
	}*/

	/*public void swithVibrate(ImageView childImageView) {
		VibrateCommand vibrateCommand = new VibrateCommand(this);
		boolean status = false;
		if (vibrateCommand.getValue() != 5) {
			vibrateCommand.setValue(5);
			childImageView.setImageResource(R.drawable.action_vibrate_default);
		} else {
			status = true;
			vibrateCommand.setValue(2);
			childImageView
					.setImageResource(R.drawable.action_vibrate_off_default);
		}
		SettingsControl.setMobileData(this, vibrateCommand,
				vibrateCommand.getName(), status);
	}*/

	private void startPreview() {
		if (!previewOn && mCamera != null) {
			mCamera.startPreview();
			previewOn = true;
		}
	}

	private void toggleLight(ImageView childImageView) {
		if (lightOn) {
			turnLightOff();
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_torch_default));
		} else {
			turnLightOn();
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_torch_blue));
		}
	}

	private void turnLightOn() {
		// TODO Auto-generated method stub
		if (mCamera == null) {
			Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG);
			// Use the screen as a flashlight (next best thing)
			return;
		}

		lightOn = true;
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}

		List<String> flashModes = parameters.getSupportedFlashModes();
		// Check if camera flash exists
		if (flashModes == null) {
			// Use the screen as a flashlight (next best thing)
			return;
		}

		String flashMode = parameters.getFlashMode();
		if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			// Turn on the flash
			if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);

				startWakeLock();
			} else {
				Toast.makeText(this, "Flash mode (torch) not supported",
						Toast.LENGTH_LONG);
				// Use the screen as a flashlight (next best thing)
			}
		}
	}

	private void startWakeLock() {
		// TODO Auto-generated method stub
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					WAKE_LOCK_TAG);
		}
		wakeLock.acquire();
	}

	private void turnLightOff() {
		if (lightOn) {
			// set the background to dark
			lightOn = false;
			if (mCamera == null) {
				return;
			}

			Parameters parameters = mCamera.getParameters();
			if (parameters == null) {
				return;
			}

			List<String> flashModes = parameters.getSupportedFlashModes();
			String flashMode = parameters.getFlashMode();

			// Check if camera flash exists
			if (flashModes == null) {
				return;
			}

			if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
				// Turn off the flash
				if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(parameters);

					stopWakeLock();
				} else {
				}
			}
		}
	}

	private void stopWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
		}
	}

	protected void startSettingActivity(Context paramContext, Intent paramIntent) {
		try {
			paramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			paramContext.startActivity(paramIntent);
			return;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	private boolean isMobileDataEnabled(ImageView childImageView) {
		boolean enabled;
		ConnectivityManager localConnectivityManager = (ConnectivityManager) getSystemService("connectivity");
		try {
			Method localMethod = Class.forName(
					localConnectivityManager.getClass().getName())
					.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
			localMethod.setAccessible(true);
			enabled = ((Boolean) localMethod.invoke(localConnectivityManager,
					new Object[0])).booleanValue();
			if (enabled) {
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_mobiledata_off_default));
				Toast.makeText(QuickSettingActivity.this,
						getString(R.string.close_3G), Toast.LENGTH_SHORT)
						.show();
			} else {
				childImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.action_mobiledata_default));
				Toast.makeText(QuickSettingActivity.this,
						getString(R.string.open_3G), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			enabled = false;
		}
		return enabled;
	}

	private void setMobileDataEnabled(boolean enabled) {
		try {

			final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class
					.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass
					.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField
					.get(conman);
			final Class iConnectivityManagerClass = Class
					.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMobile(Context context) {

		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * �?��亮度自动调节
	 * 
	 * @param activity
	 */
	public void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}

	/**
	 * 停止自动亮度调节
	 */
	public void stopAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/**
	 * 打开GPS
	 * 
	 * @param context
	 */
	public void openGPSSettings(Context context) {
		// GPS 打开
		Intent myIntent = new Intent(
				"android.settings.LOCATION_SOURCE_SETTINGS");
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);
	}

	/**
	 * 打开关闭飞行模式
	 */
	public void OpenAirplane(Context context, ImageView childImageView) {
		
		boolean datastatus;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        datastatus = Settings.System.getInt(context.getContentResolver(), 
	                Settings.System.AIRPLANE_MODE_ON, 0) == 1;          
	    } else {
	        datastatus =  Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) ==1? true:false;
	       
	    }     
		boolean isEnabled = Settings.System.getInt(
				context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
				0) == 1;
		if (datastatus) {
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_airplane_off_default));
		} else if (!datastatus) {
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 1);
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_airplane_default));
		}

		Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		i.putExtra("state", !datastatus);
		context.sendBroadcast(i);
	}

	/**
	 * 打开关闭蓝牙
	 * 
	 */
	public void OpenorCloseBluetooth(Context context, ImageView childImageView) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter.isEnabled()) {
			adapter.disable();// 关闭蓝牙
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_bluetooth_off_default));
			Toast.makeText(QuickSettingActivity.this,
					getString(R.string.close_bluetooth), Toast.LENGTH_SHORT)
					.show();
		} else {
			adapter.enable();// 打开蓝牙
			childImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.action_bluetooth_default));
			Toast.makeText(QuickSettingActivity.this,
					getString(R.string.open_bluetooth), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 静音，震动，震动加响�?
	 */
	public void OpenorCloseHorn(Context context, int mode) {
		AudioManager hornManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int MaxVolume = hornManager.getRingerMode();
		if (mode == 0) {// 静音模式
			hornManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (mode == 1) {// 声音模式
			hornManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		} else if (mode == 2) {// 振动模式
			hornManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
		if (mCamera != null) {
			turnLightOff();
			stopPreview();
			mCamera.release();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mCamera != null) {
			stopPreview();
			mCamera.release();
			mCamera = null;
		}
		;
	}

	private void stopPreview() {
		if (previewOn && mCamera != null) {
			mCamera.stopPreview();
			previewOn = false;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder hold) {
		// TODO Auto-generated method stub
		try {
			if (mCamera == null) {
				initCamera();
			}
			if (surfaceHolder != null) {
				mCamera.setPreviewDisplay(surfaceHolder);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	public void getIconByname(String name) {

	}
}
