package com.magicare.smartnurse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author:scott
 * 
 *               Function:日志统一管理类
 * 
 *               Date:2014年5月12日
 */
public class LogUtil {

	/**
	 * 是否开启日志
	 */
	protected static boolean isEnable = true;

	/**
	 * 开发阶段
	 */
	private static final int DEVELOP = 0;
	/**
	 * 内部测试阶段
	 */
	private static final int DEBUG = 1;
	/**
	 * 公开测试
	 */
	private static final int BATE = 2;
	/**
	 * 正式版
	 */
	private static final int RELEASE = 3;

	/**
	 * 当前阶段标示
	 */
	private static int currentStage = DEVELOP;

	private static String LOG_PATH = "/sdcard/framedemo/";
	private static String FileName = "log.txt";

	public static boolean isEnable() {
		return isEnable;
	}

	public static void setEnable(boolean isEnable) {
		LogUtil.isEnable = isEnable;
	}

	/**
	 * 
	 * <p>
	 * function :打印日志到控制台
	 * </p>
	 * 
	 * @param msg
	 *            :
	 */
	public static void info(String msg) {
		if (isEnable) {
			info(LogUtil.class, msg);
		}
	}

	public static void info(Class clazz, String msg) {
		switch (currentStage) {
		case DEVELOP:
			if (isEnable) {
				// 控制台输出
				Log.i(clazz.getSimpleName(), msg);
			}
			break;
		case DEBUG:
			// 在应用下面创建目录存放日志
			writeToSD(clazz.getSimpleName(), msg);
			break;
		case BATE:
			// 写日志到sdcard
			break;
		case RELEASE:
			// 一般不做日志记录
			break;
		}
	}
	
	public static void info(String TAG, String msg) {
		switch (currentStage) {
		case DEVELOP:
			if (isEnable) {
				// 控制台输出
				Log.i(TAG, msg);
			}
			break;
		case DEBUG:
			// 在应用下面创建目录存放日志
			writeToSD(TAG, msg);
			break;
		case BATE:
			// 写日志到sdcard
			break;
		case RELEASE:
			// 一般不做日志记录
			break;
		}
	}

	public static void d(String TAG, String msg) {
		if (isEnable) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * 
	 * Function:将log写入SD卡中
	 * 
	 * @param tag
	 * @param message
	 */
	public static void writeToSD(String tag, String message) {

		StringBuilder sb = new StringBuilder();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(LOG_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos;
			File file = new File(LOG_PATH + FileName);
			try {
				if (file.exists()) {
					FileInputStream inputStream = new FileInputStream(file);
					if (inputStream != null) {
						InputStreamReader inputreader = new InputStreamReader(inputStream);
						BufferedReader buffreader = new BufferedReader(inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {

							sb.append(line + "\n");
						}
						inputStream.close();
					}

				}
				sb.append(tag + "--" + message);
				fos = new FileOutputStream(file);
				fos.write(sb.toString().getBytes());
				fos.flush();
				fos.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
