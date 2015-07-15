package com.magicare.smartnurse.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的连接状态
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				if (state == State.CONNECTED) {
					// showWifiCconnected(context);
				}
			}
		}
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {// 这个监听网络连接的设置，包括wifi和移动数据
																					// 的打开和关闭
			NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (info != null) {
				if (NetworkInfo.State.CONNECTED == info.getState()) {
					Intent pushIntent = new Intent();
					// pushIntent.setClass(context, NotificationService.class);
				} else if (info.getType() == 1) {
					if (NetworkInfo.State.DISCONNECTING == info.getState()) {
						// showWifiDisconnected(context);
					}
				}
			}
		}
	}
}