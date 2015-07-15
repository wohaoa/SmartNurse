package com.magicare.smartnurse.ble;

public class BLEConstants {
	
	public static final String DEVICE_NAME="deviceName";
	public static final String CURRENT_DEVICE_TYPE="current_device_type";
	
	
	/** 脂肪秤 COD_WXC*/
	public static final String TYPE_FATSCALE_DEVICE_NAME = "COD_WXC";
	public static final String UUID_FATSCALE_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
	public static final String UUID_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";
	public static final String UUID_DESCRIPTER = "00002902-0000-1000-8000-00805f9b34fb";
	
	/**血压计*/
	public static final String TYPE_BLOODPRESSURE_DEVICE_NAME = "eBlood-Pressure";
	public static final String UUID_BLOOD_PRESSURE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static final String UUID_BLOOD_PRESSURE_CHARACTERISTIC = "0000fff4-0000-1000-8000-00805f9b34fb";
	public static final String UUID_BLOOD_PRESSURE_DESCRIPTER = "00002902-0000-1000-8000-00805f9b34fb";
	
	/**血糖仪*/
	public static final String TYPE_BLOODSUGAR_DEVICE_NAME = "Sinocare";
	public static final String UUID_BLOODSUGAR_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
	public static final String UUID_BLOODSUGAR_CHARACTERISTIC = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static final String UUID_BLOODSUGAR_DESCRIPTER = "00002902-0000-1000-8000-00805f9b34fb";
	
}