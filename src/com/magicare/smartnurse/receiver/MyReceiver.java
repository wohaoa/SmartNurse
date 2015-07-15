package com.magicare.smartnurse.receiver;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.activity.MonitorFragment;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.dao.DBWarning;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// 01-23 17:48:08.193: I/smarhit(17881): intent
		// action:cn.jpush.android.intent.NOTIFICATION_RECEIVED

		LogUtil.info("smarhit", "intent action:" + intent.getAction());
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			ConfigManager.setStringValue(context, ConfigManager.JPUSH_REGISTER_ID, regId);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			// 解析数据
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			LogUtil.info("smarhit", "推送数据:" + extra);
			WarningBean bean = JSON.parseObject(extra, WarningBean.class);
			// 插入数据库
			DBWarning db = DBWarning.getInstance(context);
			db.open();
			db.insert(bean);
			db.close();

			bundle = new Bundle();
			bundle.putSerializable("warningbean", bean);
			// 发送广播，通知监控界面更新数据
			sendBroadRecevce(context, MonitorFragment.JPUSH_MESSAGE_ACTION, bundle);
			JPushInterface.clearNotificationById(context, bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
		}
	}

	private void sendBroadRecevce(Context mcontext, String action, Bundle bundle) {
		Intent mIntent = new Intent(action);
		mIntent.putExtra("bundle", bundle);
		mcontext.sendBroadcast(mIntent);
	}

}
