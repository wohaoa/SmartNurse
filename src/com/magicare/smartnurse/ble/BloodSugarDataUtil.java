package com.magicare.smartnurse.ble;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class BloodSugarDataUtil {

	/**
	 * 
	 * Function：连接蓝牙测试
	 * 
	 * @return
	 */
	public static byte[] connection() {
		return new byte[] { 0X53, 0X4E, 0X08, 0X00, 0X04, 0X01, 0X53, 0X49, 0X4E, 0X4F, 0X46 };
	}

	/**
	 * 
	 * Function:获取数据
	 * 
	 * @return
	 */
	public static byte[] getData() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X04, 0X00, 0X00, 0X0E };
	}

	/**
	 * 
	 * Function:获取历史数据
	 * 
	 * @return
	 */
	public static byte[] getHistoryData() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X05, 0X00, 0X00, 0X0F };

	}

	/**
	 * 
	 * Function:设置时间
	 * 
	 * @param currentTime
	 * @return
	 */
	public static byte[] setDatetime(long currentTime) {
		int result = 0;
		byte[] outData = new byte[12];
		outData[0] = 0X53;
		outData[1] = 0X4E;
		outData[2] = 0X09;
		outData[3] = 0X00;
		outData[4] = 0X04;
		outData[5] = 0X06;
		result += 0X09 + 0X00 + 0X04 + 0X06;

		SimpleDateFormat format = new SimpleDateFormat("yy MM dd HH mm");
		format.setTimeZone(TimeZone.getDefault());
		Date date = new Date(currentTime);
		String time = format.format(date);

		String[] times = time.split(" ");

		for (int i = 0; i < 5; i++) {
			// outData[i + 6] = Byte.valueOf(times[i], 16);
			// result += outData[i + 6];

			outData[i + 6] = (byte) ((Integer.parseInt(times[i])) & 0XFF);
			result += outData[i + 6];

		}
		outData[outData.length - 1] = (byte) (result & 0xFF);
		return outData;
	}

	/**
	 * 
	 * Function:获取设备的id
	 * 
	 * @return
	 */
	public static byte[] getDeviceId() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X07, 0X00, 0X00, 0X11 };
	}

	public static byte[] clearHistoryData() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X08, 0X00, 0X00, 0X12 };
	}

	/**
	 * 
	 * Function:修改校验码
	 * 
	 * @param code
	 *            :校验码
	 * @return
	 */
	public static byte[] updateVerifyCode(int code) {
		byte[] data = new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X09, 0X00, 0X05, 0X18 };
		data[7] = (byte) (code & 0XFF);
		int sum = 0X06 + 0X00 + 0X04 + 0X09 + 0X00 + code;
		data[data.length - 1] = (byte) (sum & 0XFF);
		return data;
	}

	/**
	 * 
	 * Function:关闭设备
	 * 
	 * @return
	 */
	public static byte[] turnoffDevice() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X0B, 0X02, 0X00, 0X17 };
	}

	/**
	 * 
	 * Function:关闭蓝牙
	 * 
	 * @return
	 */
	public static byte[] turnoffBluetooth() {
		return new byte[] { 0X53, 0X4E, 0X06, 0X00, 0X04, 0X0C, 0X02, 0X00, 0X16 };
	}
}
