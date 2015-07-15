package com.magicare.smartnurse.activity;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TVSettingsActivity extends BaseActivity{
	
	private Button mBtUpdate;
	private Button mBtLogout;
	private TextView mTvVersion;
	private FrameLayout mFrameUpdate, mFrameLogout;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvsettings);
		
		initView();
	}
	
	@SuppressLint("NewApi")
	private void initView(){
		
		mContext = this;
		
		mTvVersion = (TextView) findViewById(R.id.tvsettings_version);
		mBtUpdate = (Button)findViewById(R.id.tvsettings_update);
		mBtLogout = (Button)findViewById(R.id.tvsettings_logout);
		mFrameUpdate = (FrameLayout)findViewById(R.id.tvsettings_frame_update);
		mFrameLogout = (FrameLayout)findViewById(R.id.tvsettings_frame_logout);
		
		mBtUpdate.setOnHoverListener(new OnHoverListener() {
			
			@Override
			public boolean onHover(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_HOVER_ENTER){
					mFrameUpdate.setBackgroundResource(R.drawable.btn_selected_tv);
				}else if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
					mFrameUpdate.setBackgroundResource(R.drawable.btn_normal_tv_setting);
				}
				return true;
			}
		});
		
		mBtLogout.setOnHoverListener(new OnHoverListener() {
			
			@Override
			public boolean onHover(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_HOVER_ENTER){
					mFrameLogout.setBackgroundResource(R.drawable.btn_selected_tv);
				}else if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
					mFrameLogout.setBackgroundResource(R.drawable.btn_normal_tv_setting);
				}
				return true;
			}
		});
		
		mBtLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("您是否退出登录?").setCancelable(false)
						.setPositiveButton("是", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								logout();
							}
						}).setNegativeButton("否", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();				
			}
		});
	}
	
	private void logout() {

		PromptManager.showProgressDialog(mContext, "请稍等");

		HttpClientUtil client = HttpClientUtil.getInstance();
		client.logout(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						// TODO Auto-generated method stub

						if (isSuccess) {
							if (TextUtils.isEmpty(json)) {
								PromptManager.showToast(mContext, false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								PromptManager.showToast(mContext, true, baseBean.getInfo());

								if (baseBean.getInfo().equals("success")) {
									startLoginActivity();
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
//							startLoginActivity();
						}
						PromptManager.closeProgressDialog();
					}
				});
	}
	
	private void startLoginActivity(){
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
		finish();
		ConfigManager.setBooleanValue(mContext, ConfigManager.LOGIN_SUCCESS, false);		
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(mContext, TVActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
