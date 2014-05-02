package com.crazybee.quicksetting;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;

public class QuickSettingTools {

	private static Context mContext;

	public QuickSettingTools(Context context) {
		this.mContext = context;
	}

	public int getIconId(int nameid, int status) {

		if (nameid == R.string.gt_qs_bluetooth) {
			switch (status) {
			case 0:
				return R.drawable.action_bluetooth_off_default;
			case 1:
				return R.drawable.action_bluetooth_default;
			}
		} else if (nameid == R.string.gt_qs_cmcc) {
			switch (status) {
			case 0:
				return R.drawable.action_mobiledata_off_default;
			case 1:
				return R.drawable.action_mobiledata_default;
			}
		} else if (nameid == R.string.gt_qs_add) {
			return R.drawable.ic_add;
		} else if (nameid == R.string.gt_qs_bright) {
			switch (status) {
			case 1:
				return R.drawable.action_brightness_auto_blue;
			case 0:
				return R.drawable.action_brightness_auto_default;
			}
		} else if (nameid == R.string.gt_qs_electricity) {
			return R.drawable.action_battery_default;
		} else if (nameid == R.string.gt_qs_wifi) {
			switch (status) {
			case 0:
				return R.drawable.action_wifi_off_default;
			case 1:
				return R.drawable.action_wifi_4_default;
			}
		} else if (nameid == R.string.gt_qs_autowhirl) {
			switch (status) {
			case 0:
				return R.drawable.action_rotation_off_default;
			case 1:
				return R.drawable.action_rotation_default;
			}
		} else if (nameid == R.string.gt_qs_gps) {
			switch (status) {
			case 1:
				return R.drawable.action_gps_default;
			case 0:
				return R.drawable.action_gps_off_default;
			}
		} else if (nameid == R.string.gt_qs_airport) {
			switch (status) {
			case 0:
				return R.drawable.action_airplane_off_default;
			case 1:
				return R.drawable.action_airplane_default;
			}
		} else if (nameid == R.string.gt_qs_silence) {
			switch (status) {
			case 1:
				return R.drawable.action_silence_default;
			case 0:
				return R.drawable.action_silence_off_default;
			}
		} else if (nameid == R.string.gt_qs_vibrate) {
			switch (status) {
			case 1:
				return R.drawable.action_vibrate_default;
			case 0:
				return R.drawable.action_vibrate_off_default;
			}
		} else if (nameid == R.string.gt_qs_app_manage) {
			return R.drawable.action_app_off_default;
		} else if (nameid == R.string.gt_qs_task_manage) {
			return R.drawable.action_app_off_default;
		} else if (nameid == R.string.gt_qs_local) {
			return R.drawable.action_locale_default;
		} else if (nameid == R.string.gt_qs_putin) {
			return R.drawable.action_keyboard_default;
		} else if (nameid == R.string.qs_torch) {
			return R.drawable.action_torch_default;
		}
		return 0;

	}

	public int getStatus(int nameid) {
		if (nameid == R.string.gt_qs_bluetooth) {
			return openOrCloseBluetooth();
		} else if (nameid == R.string.gt_qs_cmcc) {
			return openOrCloseMobileData();
		} else if (nameid == R.string.gt_qs_bright) {
			return isAutoBrightness();
		} else if (nameid == R.string.gt_qs_wifi) {
			return openOrCloseWifi();
		} else if (nameid == R.string.gt_qs_autowhirl) {
			return openOrCloseAutoWhirl();
		} else if (nameid == R.string.gt_qs_gps) {
			return openOrCloseGPS();
		} else if (nameid == R.string.gt_qs_airport) {
			return openOrCloseAirportMode();
		} else if (nameid == R.string.gt_qs_silence) {
			return OpenorCloseSilence();
		}/* else if (nameid == R.string.gt_qs_vibrate) {
			return openOrCloseVibrate();
		}*/
		return 0;

	}

	public int OpenorCloseSilence() {
		return currStatusSilence() ? 1 : 0;
	}

	/*public int openOrCloseVibrate() {
		return currStatusVibrate() ? 1 : 0;
	}*/

	public boolean currStatusSilence() {
		AudioManager mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		if (current == 0)
			return true;
		else
			return false;
	}

	private int openOrCloseAirportMode() {
		// TODO Auto-generated method stub
		boolean datastatus = false;
		 //ContentResolver cr = getContentResolver();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        datastatus = Settings.System.getInt(mContext.getContentResolver(), 
	                Settings.System.AIRPLANE_MODE_ON, 0) == 1;          
	    } else {
	        datastatus = Settings.Global.getInt(mContext.getContentResolver(), 
	                Settings.Global.AIRPLANE_MODE_ON, 0) ==1 ;
	    }     
	
//		boolean isEnabled = Settings.System.getInt(
//				mContext.getContentResolver(),
//				Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		if (datastatus) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int openOrCloseBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter.isEnabled()) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int openOrCloseMobileData() {
		Method dataConnSwitchmethod;
		boolean isEnabled = false;
        Class telephonyManagerClass;
        Object ITelephonyStub;
        Class ITelephonyClass;
        
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);

        if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED){
            isEnabled = true;
        }else{
            isEnabled = false;  
        }   
     
		boolean enabled;
		ConnectivityManager localConnectivityManager = (ConnectivityManager) mContext
				.getSystemService("connectivity");
		try {
			Method localMethod = Class.forName(
					localConnectivityManager.getClass().getName())
					.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
			localMethod.setAccessible(true);
			enabled = ((Boolean) localMethod.invoke(localConnectivityManager,
					new Object[0])).booleanValue();
		} catch (Exception localException) {
			localException.printStackTrace();
			enabled = false;
		}
		if (isEnabled) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int isAutoBrightness() {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(
					mContext.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		if (automicBrightness) {
			return 1;
		} else {
			return 0;
		}
	}

	public int openOrCloseWifi() {
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			return 1;
		} else {
			return 0;
		}
	}

	public int openOrCloseAutoWhirl() {
		int flag = Settings.System.getInt(mContext.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0);
		return flag;
	}

	private int openOrCloseGPS() {
		LocationManager alm = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return 1;
		} else {
			return 0;
		}
	}
}
