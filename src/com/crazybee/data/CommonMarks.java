package com.crazybee.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CommonMarks {

	public static boolean DOCKBAR_MOVE = false;
	// screensize
	public static float screenWidth;
	public static float screenHeight;
	public static int densityDpi;
	public static String msdkUID;
	public static String versionName;
	public static String language;
	public static int drawer_tab_width;
	public static int drawer_setting_width;
	public final static String PACKAGE_NAME = "com.launcher.GTlauncher2";
	public static boolean SWITCH_UPDATE_ICON_BY_CONTENT = true;
	public static String launcherPackageName;
	public static String defaultLauncherName;

	public static String phoneApkPackageName = "com.android.contacts";
	public static String phoneApkClassName = "com.android.contacts.activities.DialtactsActivity";
	public static String contactsApkPackageName = "com.android.contacts";
	public static String contactsApkClassName = "com.android.contacts.activities.PeopleActivity";
	public static String mmsApkPackageName = "com.android.mms";
	public static String mmsApkClassName = "com.android.mms.ui.ConversationList";
	public static String browserApkPackageName = "com.android.browser";
	public static String browserApkClassName = "com.android.browser.BrowserActivity";

	public static String GTWEATHER_PACKAGE_NAME = "com.launcher.GTlauncher.gtweather";
	public static String GTWEATHER_CLASS_NAME = "com.launcher.GTlauncher.gtweather.GTWeatherActivity";

	public static final String ACTION_RESTART_LAUNCHER = "com.launcher.GTlauncher2.action.RESTART";
	public static final String ACTION_APPLY_THEME = "com.launcher.GTlauncher2.APPLY_THEME";// theme
	public static final String ACTION_REFRESH_WEATHER = "com.town.gt.app.WEATHERCHANGED";// refresh weather
	public static final String ACTION_REFRESH_WETHER_UNIT = "com.town.gt.app.WEATHERUNITCHANGED";// change unit
	public static final String ACTION_CHANGE_TIME = "com.town.gt.app.TIMECHANGED";// changetime

	
	public static void setDeskEffect(Context context, int tap) {
		SharedPreferences preferences = context.getSharedPreferences(
				"DeskEffect", Context.MODE_PRIVATE);
		preferences.edit().putInt("DeskEffectItem", tap).commit();
	}

	
	public static int getDeskEffect(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"DeskEffect", Context.MODE_PRIVATE);
		int effect = preferences.getInt("DeskEffectItem", 0);
		return effect;
	}


	public static int getPrimaryID(String tabName, SQLiteDatabase db) {
		final String MY_QUERY = "SELECT MAX(tabId) FROM " + tabName;
		Cursor cur = db.rawQuery(MY_QUERY, null);
		cur.moveToFirst();
		int ID = cur.getInt(0);
		cur.close();
		return ID;
	}

	
	public static boolean checkedApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			@SuppressWarnings("unused")
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
	
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
	
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
	
		drawable.draw(canvas);
		return bitmap;
	}

	public static boolean isNet(Context context) {
		try {
			ConnectivityManager cManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}


	public static boolean isDirExist(String dirName) {
		File file = new File(dirName);
		if (file.exists())
			return true;
		return false;
	}

	public static String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return (c + "");// "#";
		}
	}


	public static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();

		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {

			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/**
	 * create file
	 * 
	 * @param context
	 */
	public static void initDataDir(Context context, String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * get apk version
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(),
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}


	/**
	 * restart launcher 
	 * 
	 * @param context
	 */
	public static void restartLauncher(Context context) {
		Intent intent = new Intent(ACTION_RESTART_LAUNCHER);
		context.sendBroadcast(intent);
	}

}
