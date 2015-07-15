package com.magicare.smartnurse.activity;

import cn.jpush.android.api.JPushInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 隐藏标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 // 隐藏状态栏
		 this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
//        //隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //定义全屏参数
//        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window= this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

	}

	protected void changeView(Class targetClass, Bundle bundle) {
		Intent intent = new Intent(this, targetClass);
		if (null != bundle) {
			intent.putExtra("bundle", bundle);
		}
		startActivity(intent);
		changeAnimation();
	}

	protected void changeView(Class targetClass, Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(this, targetClass);
		if (null != bundle) {
			intent.putExtra("bundle", bundle);
		}
		startActivity(intent);
		if (isFinish) {
			this.finish();
		}
		changeAnimation();

	}

	protected void changeAnimation() {
		// overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		changeAnimation();
	}

}
