package com.magicare.smartnurse.activity;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.FeedBean;
import com.magicare.smartnurse.bean.LoginEntity;
import com.magicare.smartnurse.bean.PensionBean;
import com.magicare.smartnurse.bean.RegionBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.database.dao.DBFeed;
import com.magicare.smartnurse.database.dao.DBNurse;
import com.magicare.smartnurse.database.dao.DBRegion;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.service.UpdateBitmapService;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author scott
 * 
 *         Function:登录界面
 * 
 */
public class LoginActivity extends BaseActivity {

	private EditText et_username;
	private EditText et_password;
	private Button btn_login;
	private LinearLayout tv_about;
	private TextView mTvBracelet, mTvForget;

	private RegionBean regionBean;

	private LoginEntity loginEntity;
	
	private int mLoadedPhoto = 0; // 已下载的老人头像数量，用以判断是否都下载完了

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		File fileDir = new File(FileUtils.SDPATH);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		// 保存当前应用的版本号
		try {
			PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			// 当前版本的版本号
			int versionCode = info.versionCode;
			ConfigManager.setIntValue(getApplicationContext(), ConfigManager.VERSIONCODE, versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (ConfigManager.getBooleanValue(getApplicationContext(), ConfigManager.LOGIN_SUCCESS, false)) {
			if(getResources().getDimensionPixelSize(R.dimen.tv_or_pingban) == 1
					&& ConfigManager.getIntValue(this, ConfigManager.AREAID) == 0){ 
				// TV端,并且必须是管理员账户，是否是管理员账户是根据pension_areaid==0来决定的
				changeView(TVActivity.class, null, true);
			}else{
				changeView(MainActivity.class, null, true);
			}
//			changeView(TVActivity.class, null, true);
		} else {
			initview();
		}

	}

	private void initview() {
		// TODO Auto-generated method stub
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
		tv_about = (LinearLayout) findViewById(R.id.tv_about);
		mTvBracelet = (TextView)findViewById(R.id.tv_bracelet);
		mTvForget = (TextView)findViewById(R.id.tv_forget);
		
		btn_login.setOnClickListener(this);
		tv_about.setOnClickListener(this);
		mTvBracelet.setOnClickListener(this);
		mTvForget.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_login:
			login();
			break;
		case R.id.tv_about:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_bracelet:
			changeView(BraceletAnimation.class, null, true);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * Function:用户登录
	 */
	private void login() {

		HttpClientUtil client = HttpClientUtil.getInstance();
		String username = et_username.getText().toString().trim();
		String pwd = et_password.getText().toString().trim();

		if (TextUtils.isEmpty(username)) {
			PromptManager.showToast(getApplicationContext(), false, "亲，用户名不能为空!");
			return;
		}

		if (TextUtils.isEmpty(pwd)) {
			PromptManager.showToast(getApplicationContext(), false, "亲，密码不能为空!");
			return;
		}

		PromptManager.showProgressDialog(this, "正在登录，请稍后....");
		
		client.login(this, username, pwd,
				ConfigManager.getStringValue(getApplicationContext(), ConfigManager.JPUSH_REGISTER_ID),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
//							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									loginEntity = JSON.parseObject(baseBean.getData(), LoginEntity.class);
									LogUtil.info("rice", loginEntity.toString());
									saveData(loginEntity);
									getAllUserInfo();
								} else {
									PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
									PromptManager.closeProgressDialog();
								}
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getApplicationContext(), false, errors);
						}
					}
				});

	}

	private void saveData(LoginEntity entity) {
		DBNurse db_nurse = DBNurse.getInstance(getApplicationContext());
		db_nurse.open();
		db_nurse.insert(entity.getNurse());
		db_nurse.close();

		regionBean = entity.getPension_area();
		regionBean.setNurse_id(entity.getNurse().getNurse_id() + "");
		DBRegion db_region = DBRegion.getInstance(getApplicationContext());
		db_region.open();
		db_region.insert(regionBean);
		db_region.close();
		
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		String szImei = TelephonyMgr.getDeviceId(); 
		
		ConfigManager.setIntValue(getApplicationContext(), ConfigManager.AREAID, regionBean.getPension_areaid());
		ConfigManager.setStringValue(getApplicationContext(), ConfigManager.AREA_RANGE, regionBean.getRange());
		// 养老院的名字
		ConfigManager.setStringValue(getApplicationContext(), ConfigManager.PENSION_NAME, entity.getPension().getName());
		ConfigManager.setStringValue(getApplicationContext(), Constants.ACCESS_TOKEN, entity.getAccess_token());
		ConfigManager.setIntValue(getApplicationContext(), ConfigManager.NURSE_ID, entity.getNurse().getNurse_id());
		ConfigManager.setStringValue(getApplicationContext(), ConfigManager.NURSE_NAME, entity.getNurse()
				.getNurse_name());
		ConfigManager.setStringValue(getApplicationContext(), ConfigManager.IMEI, szImei);
		
		if(entity.getPension().getWord_type()==1){
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.WARNING_NAME, "呼叫服务");
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.WARNING_BUTTON, "服务记录");
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.CHART_NAME, "服务");
		}else{
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.WARNING_NAME, "主动报警");
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.WARNING_BUTTON, "报警记录");
			ConfigManager.setStringValue(getApplicationContext(), ConfigManager.CHART_NAME, "报警");
		}
		
	}

	private void getAllUserInfo() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getAllOldUserInfo(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
				regionBean.getPension_areaid(), new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									List<UserBean> list = JSON.parseArray(baseBean.getData(), UserBean.class);
									if (list != null && list.size() > 0) {
										DBUser db = DBUser.getInstance(getApplicationContext());
										db.open();
										db.deleteAll(); // 先清一下数据库，解决不同账号切换的时候会遗留上个账号的老人信息
										db.insert(list);
										db.close();
										getFeedListInfo();
									} else {
										PromptManager.closeProgressDialog();
										PromptManager.showToast(getApplicationContext(), true, "该区域没有老人信息!");
									}
								} else {
									PromptManager.closeProgressDialog();
									PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(), false, errors);
						}

					}
				});

	}

	/**
	 * 
	 * Function:更新反馈信息
	 * 
	 */
	private void getFeedListInfo() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getFeedListInfo(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
//							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									List<FeedBean> list = JSON.parseArray(baseBean.getData(), FeedBean.class);
									if (list != null && list.size() > 0) {
										DBFeed db = DBFeed.getInstance(getApplicationContext());
										db.open();
										db.deleteAll();
										db.insert(list);
										db.close();
									}
									// 下载老人的头像
									loadingNursePhoto();
									loadingUserPhoto();
									// 表示登录成功了
									ConfigManager.setBooleanValue(getApplicationContext(), ConfigManager.LOGIN_SUCCESS,
											true);
								} else {
									PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
								}
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getApplicationContext(), false, errors);
						}
					}
				});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// // 检查是否有新的版本
		// if (NetUtil.is3GConnectivity(getApplicationContext())
		// || NetUtil.isNetWorkConnected(getApplicationContext())) {
		// UpgradeApp upgrade = new UpgradeApp(this);
		// upgrade.checkVersionCode();
		// }
	}
	
	/**
	 * Function:同步更新已下载的头像数量
	 */
	private synchronized void addLoadedPhoto(){
		mLoadedPhoto++;
	}

	/**
	 * 
	 * Function:下载老人的头像
	 */
	private void loadingUserPhoto() {
//		Intent mServiceIntent = new Intent(LoginActivity.this, UpdateBitmapService.class);
//		mServiceIntent.putExtra(Constants.ISLOADING, true);
//		LoginActivity.this.startService(mServiceIntent);

		DBUser db = DBUser.getInstance(this);
		db.open();
		final List<UserBean> list_user = db.getAllUserInfo();
		db.close();
		HttpClientUtil client = HttpClientUtil.getInstance();
		for (int i = 0; i < list_user.size(); i++) {
			final UserBean userbean = list_user.get(i);
			String localAddress = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
			client.loadingPhoto(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
					localAddress, userbean.getAvatar_url(), new IOperationResult() {

						@Override
						public void operationResult(boolean isSuccess, String json, String errors) {
							// TODO Auto-generated method stub
							LogUtil.info("smarhit", "json=" + json);
							if (json.equals("ok")) {
								DBUser dbuser = DBUser.getInstance(LoginActivity.this);
								dbuser.open();
								userbean.setIsUpdatePhoto(1);
								long count = dbuser.updateUpdatePhotoStatus(userbean);
								LogUtil.info("smarhit", "count=" + count);
								dbuser.close();
								addLoadedPhoto();
								if(mLoadedPhoto == list_user.size()){
									PromptManager.closeProgressDialog();
									if(getResources().getDimensionPixelSize(R.dimen.tv_or_pingban) == 1
										&& ConfigManager.getIntValue(LoginActivity.this, ConfigManager.AREAID) == 0){ 
										// TV端,并且必须是管理员账户，是否是管理员账户是根据pension_areaid==0来决定的){
										changeView(TVActivity.class, null, true);
									}else{
										changeView(MainActivity.class, null, true);
									}
//									changeView(TVActivity.class, null, true);
								}
							}
						}
					});
		}
	}

	private void loadingNursePhoto() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		String localAddress = FileUtils.SDPATH
				+ ConfigManager.getIntValue(getApplicationContext(), ConfigManager.NURSE_ID) + ".JPEG";
		client.loadingPhoto(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
				localAddress, loginEntity.getNurse().getAvatar_url(), new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						// TODO Auto-generated method stub
						LogUtil.info("smarhit", "json=" + json);
//						PromptManager.closeProgressDialog();
//						changeView(MainActivity.class, null, true);
					}
				});
	}

}