package com.elex.batterymanager.constant;

public class Constant {
	public final static Boolean DEBUG = true;
	public static final boolean DEV_INFO = false;
	public final static int TIME_TYPE_NORMAL = 5;

	public final static int REMAINING_TIME_UNAVAILABLE = -1;

	public static final String PACKAGE_NAME = "com.elex.batterymanager";
	public static final String PM_AUTO_CLEANUP = PACKAGE_NAME + ".auto_cleanup";
	public static final String ACTION_REMAINING_TIME_UPDATE = PACKAGE_NAME
			+ ".action.RemainingTimeUpate";
	public static final String ACTION_REMAINING_TIME_ONCREATE = PACKAGE_NAME
			+ ".action.RemainingTimeOnCreate";
	public static final String ACTION_MODE_MODIFIED = PACKAGE_NAME
			+ ".MODEMODIFIED";
	public static final String BATTERY_INFO_SHAREDPREFERENCE_NAME = "batteryInfoXmlName";
	public static final String BATTERY_PERCENT = "batteryPercent";
	public static final String BATTERY_REMAINING_TIME = "batteryRemainingTime";
	public static final String BATTERY_CHARGING_TIME = "batteryChargingTime";
	public static final String BATTERY_HEALTH = "batteryHealth";
	public static final String BATTERY_IS_CHARGING = "batteryIsCharging";
	public static final String BATTERY_SAVE_TIME = "batterySaveTime";
	public static final String BATTERY_SAVE_PROTECT_LEVEL = "batterySaveProtectLevel";
	public static final String MODE_FLAG = "modeFlag";
	public static final String BATTERY_MODE = "switch_mode";

	public static final int VERSION_CHINA = 1;
	public static final int VERSION_ENGLISH = 2;
	public static final int VERSION_NORMAL = 3;

}
