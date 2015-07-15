package com.magicare.smartnurse.activity;

import com.iflytek.cloud.SpeechUtility;
import com.magicare.smartnurse.utils.ConfigManager;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;

public class MagicareApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
//		CrashHandler mCrasHandler = CrashHandler.getInstance();
//		mCrasHandler.init(getApplicationContext());
		SpeechUtility.createUtility(MagicareApplication.this, "appid="
				+ ConfigManager.XUNFEIID);
	}
}
