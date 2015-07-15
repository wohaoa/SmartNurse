package com.magicare.smartnurse.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicare.smartnurse.activity.MainActivity;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

public class UpdateBitmapService extends Service {

	private UserBean userbean;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			boolean isLoading = intent.getBooleanExtra(Constants.ISLOADING, false);
			LogUtil.info("smarhit", "开启了UpdateBitmapService isLoading=" + isLoading);
			if (isLoading) {
				loadingUserInfoPhoto();
			} else {
				updatePhoto();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void updatePhoto() {
		DBUser db = DBUser.getInstance(this);
		db.open();
		List<UserBean> list_user = db.getUpdatePhotoInfo(0);
		db.close();

		// if (list_user == null || list_user.size() == 0) {
		// LogUtil.info("smarhit", "没有需要上传的头像");
		// }
		for (int i = 0; i < list_user.size(); i++) {

			userbean = list_user.get(i);
			LogUtil.info("smarhit", "userbean=" + userbean.toString());
			String url = FileUtils.SDPATH + userbean.getOld_sn() + ".JPEG";
			// String url = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
			LogUtil.info("smarhit", "上传图片url=" + url);
			File file = new File(url);
			if (file.exists()) {
				HttpClientUtil client = HttpClientUtil.getInstance();
				client.updateHeadPortrait(this,
						ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN), url,
						userbean.getOld_id(), new IOperationResult() {

							@Override
							public void operationResult(boolean isSuccess, String json, String errors) {
								// TODO Auto-generated method stub
								if (isSuccess) {
									LogUtil.info("smarhit", "上传图片成功 json=" + json);

									if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
										PromptManager.showToast(getApplicationContext(), false, "上传头像失败，请检查网络!");
									} else {
										BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
										if (baseBean.getStatus() == 0) {
											JSONObject object = JSONObject.parseObject(baseBean.getData());
											String avatar_url = object.getString("avatar_url");
											DBUser db = DBUser.getInstance(getApplicationContext());
											db.open();
											userbean.setAvatar_url(avatar_url);
											userbean.setIsUpdatePhoto(1);
											db.updateUpdatePhotoStatus(userbean);
											db.close();
											PromptManager.showToast(getApplicationContext(), true, "新头像已经上传!");
										} else {
											PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
										}
									}
								} else {
									PromptManager.showToast(getApplicationContext(), false, errors);
								}
							}
						});

			} else {
				LogUtil.info("smarhit", "上传图片不存在");
			}
		}
		// 发送头像上传完成广播
		Intent intent = new Intent();
		intent.setAction(MainActivity.UPDATEPHOTO_ACTION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sendBroadcast(intent);
	}

	private void loadingUserInfoPhoto() {
		DBUser db = DBUser.getInstance(this);
		db.open();
		List<UserBean> list_user = db.getAllUserInfo();
		db.close();
		HttpClientUtil client = HttpClientUtil.getInstance();
		for (int i = 0; i < list_user.size(); i++) {
			userbean = list_user.get(i);
			String localAddress = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
			client.loadingPhoto(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
					localAddress, userbean.getAvatar_url(), new IOperationResult() {

						@Override
						public void operationResult(boolean isSuccess, String json, String errors) {
							// TODO Auto-generated method stub
							LogUtil.info("smarhit", "json=" + json);
							if (json.equals("ok")) {
								DBUser dbuser = DBUser.getInstance(UpdateBitmapService.this);
								dbuser.open();
								userbean.setIsUpdatePhoto(1);
								long count = dbuser.updateUpdatePhotoStatus(userbean);
								LogUtil.info("smarhit", "count=" + count);
								dbuser.close();
							}
						}
					});
		}

		// DBUser dbs = DBUser.getInstance(this);
		// dbs.open();
		// List<UserBean> list_users = dbs.getUpdatePhotoInfo(0);
		// dbs.close();
		//
		// for (int i = 0; i < list_users.size(); i++) {
		// UserBean bean = list_users.get(i);
		// LogUtil.info("smarhit", "下载完成后bean:"+bean.toString());
		// }

		// if (list_user == null || list_user.size() == 0) {
		// LogUtil.info("smarhit", "没有需要上传的头像");
		// }

		// 头像下载完成广播
		Intent intent = new Intent();
		intent.setAction(MainActivity.UPDATEPHOTO_ACTION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sendBroadcast(intent);

	}

}
