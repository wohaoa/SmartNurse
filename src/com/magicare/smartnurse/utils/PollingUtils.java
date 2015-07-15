package com.magicare.smartnurse.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class PollingUtils {

	/**
	 * 
	 * Function:开启轮询服务
	 * 
	 * @param context
	 *            ：上下文对象
	 * @param seconds
	 *            ：轮训间隔的时间，单位秒
	 * @param clazz
	 *            ：目标对象
	 * @param action
	 *            ：动作
	 */
	public static void startPollingService(Context context, int seconds, Class<?> clazz, String action) {
		// 获取AlarmManager系统服务
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// 包装需要执行Service的Intent
		Intent intent = new Intent(context, clazz);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 触发服务的起始时间
		long triggerAtTime = SystemClock.elapsedRealtime();

		// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, seconds * 1000, pendingIntent);
	}

	/**
	 * 
	 * Function:停止轮询服务
	 * 
	 * @param context
	 *            ：上下文对象
	 * @param clazz
	 *            ：目标对象
	 * @param action
	 *            ：动作
	 */
	public static void stopPollingService(Context context, Class<?> clazz, String action) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, clazz);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 取消正在执行的服务
		manager.cancel(pendingIntent);
	}

}