package com.magicare.smartnurse.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class ConfigManager {

	private final static String mXmlFile = "smartnurse_xml";
	//当前的网络状态
	public final static String NETWORK_STARTUS="network_status";
	
	// 脂肪秤的address
	public final static String FATSACLE_DEVICE_ADDDRESS = "fatsacle_device_address";
	// 激光推送的registerId
	public final static String JPUSH_REGISTER_ID = "jpush_registerid";
	public final static String NURSE_ID = "nurse_id";
	public final static String NURSE_NAME = "nurse_name";
	public final static String AREAID = "pension_areaid";
	public final static String AREA_RANGE="pension_range";
	public final static String PENSION_NAME="pension_name"; // 养老院的名字

	public final static String LOGIN_SUCCESS="login_success";
	public static final String VERSIONCODE = "versionCode";
	
	public final static String ISUPGRADE="isUpgrade";
	public final static String XUNFEIID = "54ceebba";
	public final static String WARNING_NAME = "warning_name";
	public final static String WARNING_BUTTON = "warning_button";
	public final static String CHART_NAME = "chart_name";
	public final static String NETWORK = "network";
	public final static String IMEI = "";
	
	

	/* 读取一个整数 */
	public static int getIntValue(Context context, String key) {
		try {
			SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);// 建立storexml.xml
			return store.getInt(key, 0);// 从codoon_config_xml中读取上次进度，存到string1中
		} catch (Exception e) {
			return 0;
		}
	}

	/* 设置一个整数 */
	public static void setIntValue(Context context, String key, int value) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putInt(key, value);
		editor.commit();

	}

	/* 读取一个长整数 */
	public static Long getLongValue(Context context, String key, long defalut) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);// 建立storexml.xml
		return store.getLong(key, defalut);// 从codoon_config_xml中读取上次进度，存到string1中
	}

	/* 设置一个长整数 */
	public static void setLongValue(Context context, String key, long value) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putLong(key, value);
		editor.commit();

	}

	/* 获取一个浮点数 */
	public static float getFloatValue(Context context, String key, float defalut) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);// 建立storexml.xml
		return store.getFloat(key, defalut);// 从codoon_config_xml中读取上次进度，存到string1中
	}

	/* 设置一个浮点数 */
	public static void setFloatValue(Context context, String key, float value) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/* 获取一个字符 */
	public static String getStringValue(Context context, String key) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);// 建立storexml.xml
		return store.getString(key, "");// 从codoon_config_xml中读取上次进度，存到string1中
	}

	/* 设置一个字符 */
	public static void setStringValue(Context context, String key, String value) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putString(key, value);
		editor.commit();

	}

	/* 获取一个布尔值 */
	public static boolean getBooleanValue(Context context, String key, boolean defalut) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		return store.getBoolean(key, defalut);
	}

	/* 设置一个布尔值 */
	public static void setBooleanValue(Context context, String key, boolean value) {
		SharedPreferences store = context.getSharedPreferences(mXmlFile, 0);
		SharedPreferences.Editor editor = store.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static Object getAppSource(Context mContext, String key) {
		ApplicationInfo ai = null;
		try {
			ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return ai.metaData.get(key);

	}

	/* 获取Imei */
	public static String getImei(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceidString = mTelephonyMgr.getDeviceId();
		if (deviceidString == null || deviceidString.length() == 0) {
			final String tmDevice, tmSerial, tmPhone, androidId;

			tmDevice = "" + mTelephonyMgr.getDeviceId();

			tmSerial = "" + mTelephonyMgr.getSimSerialNumber();

			androidId = ""
					+ android.provider.Settings.Secure.getString(context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

			deviceidString = deviceUuid.toString();
		}

		// 针对乐疯跑Android客户端的IMEI，在咕咚运动前面添加"24-" los:28 android:27
		return "27-" + mTelephonyMgr.getDeviceId();

	}

	/* 获取软件来源 */
	public static int getAppSource(Context mContext) {
		ApplicationInfo ai = null;
		try {
			ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bundle bundle = ai.metaData;

		int myAppSource = bundle.getInt("App_Source_Key");

		return myAppSource;
	}

}