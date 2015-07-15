package com.magicare.smartnurse.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.SyncStateContract.Helpers;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.mpandroidchart.CollectRecordFragment;
import com.magicare.smartnurse.activity.mpandroidchart.CollectRecordFragment.CollectRecordData;
import com.magicare.smartnurse.activity.mpandroidchart.OldUserHealthInfoFragment1;
import com.magicare.smartnurse.activity.mpandroidchart.OldUserHealthInfoFragment1.HealthDataClickListener;
import com.magicare.smartnurse.activity.mpandroidchart.OldUserSleepInfoFragment1;
import com.magicare.smartnurse.activity.mpandroidchart.OldUserSleepInfoFragment1.SportsData;
import com.magicare.smartnurse.activity.mpandroidchart.WarinHistoryFragment;
import com.magicare.smartnurse.activity.mpandroidchart.WarinHistoryFragment.WarinData;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.bean.SportsBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.dao.DBHealth;
import com.magicare.smartnurse.database.dao.DBSports;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.database.dao.DBWarning;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.net.NetUtil;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.CircleImageView;
import com.magicare.smartnurse.view.SearchFrameLayout;

public class UserDetailActivity extends BaseActivity implements OnClickListener, SportsData, WarinData,
		CollectRecordData, HealthDataClickListener {
	private TextView tv_oldname, tv_oldaccounts, tv_oldage, tv_oldsex, tv_oldadress; // 地点

	CircleImageView imageView;
	private TextView tv_locationadress; // 绿色地址
	private TextView tv_status; // 绿色状态
	private TextView tv_time;
	private TextView tv_name;
	private TextView tv_changephoto;
	private TextView tv_sex;
	private TextView tv_age;
	private TextView tv_height;
	private TextView tv_birthday;
	private TextView tv_account;
	private TextView tv_bracelet;
	private TextView tv_region; // 区域
	private TextView tv_bed;
	private TextView tv_phone;
	private TextView tv_childname;
	private TextView tv_childphone;
	private TextView tv_note;
	private TextView tv_oldmore;
	private TextView tv_timelabl;
	private ImageView tv_battery;
	
	boolean[] booleans = new boolean[] { false, false, false, false };

	Button btn_weight, btn_blood, btn_heartrate, btn_bloodsugar, btn_warning, btn_collecthistory, btn_sport, btn_back,
			btn_refresh, btn_concernhistory;

	private List<Fragment> mFragmentsList;
	private int currentTab;
	private ArrayList<Integer> arrayList = new ArrayList<Integer>(); // 按钮id的arraylist
	private boolean isFlag, isFragment;
	private UserBean userbean;
	private SearchFrameLayout fl_search;
	private int healthPosition = 0; // 体重、血压、心率、血糖曲线月数的序号，0表示当前月，-1表示上个月
	int index;
	int position;
	int page = 1;
	int pageRecord = 1;
	int tempage = 1; // 数据库页数
	int tempCollectPage = 1;
	public static final int PAGENUM = 5;
	DBUser dbUser;
	DBHealth dbHealth;
	DBSports dbSports;
	DBWarning dbWarning;
	List<HealthBean> healthBeans = new ArrayList<HealthBean>();
	List<SportsBean> sportsBeans = new ArrayList<SportsBean>();
	List<WarningBean> warningBeans = new ArrayList<WarningBean>();
	List<HealthBean> collectRecordBeans = new ArrayList<HealthBean>();
	private OldUserHealthInfoFragment1 oldUserHealthInfoFragment;
	private OldUserSleepInfoFragment1 oldUserSleepInfoFragment;
	private WarinHistoryFragment warinHistoryFragment;
	private CollectRecordFragment collectRecordFragment;
	private int id;
	String isMonitor; // 判断是否是监控页面来的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_userdetail);
		userbean = (UserBean) getIntent().getSerializableExtra("userbean");
		isMonitor = getIntent().getStringExtra("fromview");
		dbUser = DBUser.getInstance(this);
		dbHealth = DBHealth.getInstance(this);
		dbSports = DBSports.getInstance(this);
		dbWarning = DBWarning.getInstance(this);
		
		tv_battery = (ImageView) findViewById(R.id.tv_battery);
		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_changephoto = (TextView) findViewById(R.id.tv_changephoto);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_age = (TextView) findViewById(R.id.tv_age);
		tv_height = (TextView) findViewById(R.id.tv_height);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_bracelet = (TextView) findViewById(R.id.tv_bracelet);
		tv_region = (TextView) findViewById(R.id.tv_region);
		tv_bed = (TextView) findViewById(R.id.tv_bed);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_childname = (TextView) findViewById(R.id.tv_childname);
		tv_childphone = (TextView) findViewById(R.id.tv_childphone);
		tv_note = (TextView) findViewById(R.id.tv_note);
		imageView = (CircleImageView) findViewById(R.id.imageview);
		tv_oldmore = (TextView) findViewById(R.id.tv_oldmore);
		tv_oldname = (TextView) findViewById(R.id.tv_oldname);
		tv_oldaccounts = (TextView) findViewById(R.id.tv_oldaccounts);
		tv_oldage = (TextView) findViewById(R.id.tv_oldage);
		tv_oldsex = (TextView) findViewById(R.id.tv_oldsex);
		tv_oldadress = (TextView) findViewById(R.id.tv_oldadress);

		btn_weight = (Button) findViewById(R.id.btn_weight);
		btn_blood = (Button) findViewById(R.id.btn_blood);
		btn_heartrate = (Button) findViewById(R.id.btn_heartrate);
		btn_bloodsugar = (Button) findViewById(R.id.btn_bloodsugar);
		btn_warning = (Button) findViewById(R.id.btn_warning);
		btn_collecthistory = (Button) findViewById(R.id.btn_collecthistory);
		btn_sport = (Button) findViewById(R.id.btn_sport);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_concernhistory = (Button) findViewById(R.id.btn_concernhistory);

		btn_sport.setPadding(20, 0, 20, 0);
		btn_warning.setPadding(20, 0, 20, 0);
		btn_warning.setText(ConfigManager.getStringValue(getApplicationContext(), ConfigManager.WARNING_BUTTON));
		btn_collecthistory.setPadding(20, 0, 20, 0);

		tv_locationadress = (TextView) findViewById(R.id.tv_loccationadress);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_timelabl = (TextView) findViewById(R.id.tv_timelabl);

		btn_back.setOnClickListener(this);
		btn_refresh.setOnClickListener(this);
		imageView.setOnClickListener(this);
		btn_weight.setOnClickListener(this);
		btn_blood.setOnClickListener(this);
		btn_heartrate.setOnClickListener(this);
		btn_bloodsugar.setOnClickListener(this);
		btn_warning.setOnClickListener(this);
		btn_collecthistory.setOnClickListener(this);
		btn_concernhistory.setOnClickListener(this);
		btn_sport.setOnClickListener(this);
		tv_oldmore.setOnClickListener(this);
		tv_changephoto.setOnClickListener(this);

		fl_search = (SearchFrameLayout) findViewById(R.id.fl_search);

		System.out.println("isMonitor" + isMonitor);

		if (TextUtils.isEmpty(isMonitor)) {
			setUserInfo();

		} else {
			searchUserData(userbean.getOld_id() + "");

		}
		initFragment();
		loadingPhoto();
	}

	public void setUserInfo() {
		if (userbean == null) {
			Toast.makeText(getApplicationContext(), "没有老人信息", 1).show();
			finish();
		}
		tv_name.setText(userbean.getName());
		tv_sex.setText(userbean.getGender());
		tv_age.setText(userbean.getAge() + "");
		tv_height.setText(userbean.getHeight() + "");
		tv_birthday.setText(userbean.getBirthday());
		tv_account.setText(userbean.getOld_sn());
		tv_bracelet.setText(userbean.getBracelet_id());
		tv_region.setText(userbean.getPension_areaname()); // 区域
		tv_bed.setText(userbean.getBed());
		tv_phone.setText(userbean.getMobile());
		tv_childname.setText(userbean.getChild_name());
		tv_childphone.setText(userbean.getChild_mobile());
		tv_note.setText(userbean.getNote());
		tv_oldname.setText(userbean.getName());
		tv_oldaccounts.setText(userbean.getOld_sn());
		tv_oldage.setText(userbean.getAge() + "");
		tv_oldsex.setText(userbean.getGender());
		tv_oldadress.setText(userbean.getBed());
		if (userbean != null
				&& (!TextUtils.isEmpty(userbean.getCurrentStatus()) && !TextUtils.isEmpty(userbean.getRefreshTime()))) {
			tv_locationadress.setVisibility(View.VISIBLE);
			tv_status.setVisibility(View.VISIBLE);
			tv_time.setVisibility(View.VISIBLE);
			tv_timelabl.setVisibility(View.VISIBLE);
			tv_locationadress.setText(userbean.getCurrentLocation());
			tv_status.setText(userbean.getCurrentStatus());
			tv_time.setText(DateUtil.formatDate(userbean.getRefreshTime(), "yyyy-MM-dd HH:mm"));
			if (userbean.getBattery() > 0 && userbean.getBattery() < 30) {
				tv_battery.setVisibility(View.VISIBLE);
			}
		} else {
			tv_locationadress.setVisibility(View.INVISIBLE);
			tv_status.setVisibility(View.INVISIBLE);
			tv_time.setVisibility(View.INVISIBLE);
			tv_timelabl.setVisibility(View.INVISIBLE);
			tv_battery.setVisibility(View.GONE);
		}

		setImagePhoto();
	}

	public void setImagePhoto() {
		String photopath = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
		File file = new File(photopath);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
			imageView.setImageBitmap(bitmap);
			fl_search.setImageBitmap(bitmap);
		}
	}

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int CUT_PHOTO_REQUEST_CODE = 2;
	private static final int SELECTIMG_SEARCH = 3;
	private String path = "";
	private Uri photoUri;

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});
		}

	}

	public void photo() {
		try {
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String sdcardState = Environment.getExternalStorageState();
			String sdcardPathDir = android.os.Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				// 有sd卡，是否有myImage文件夹
				File fileDir = new File(sdcardPathDir);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				// 是否有headImg文件
				file = new File(sdcardPathDir + userbean.getOld_sn() + ".JPEG");
			}
			if (file != null) {
				path = file.getPath();
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String imagePath;

	private void startPhotoZoom(Uri uri) {
		try {
			if (!FileUtils.isFileExist("")) {
				FileUtils.createSDDir("");
			}
			imagePath = FileUtils.SDPATH + userbean.getOld_sn() + ".JPEG";
			Uri imageUri = Uri.parse("file:///sdcard/smartnurse/" + userbean.getOld_sn() + ".JPEG");
			Intent intent = new Intent("com.android.camera.action.CROP");
			// 照片URL地址
			intent.setDataAndType(uri, "image/*");

			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 480);
			intent.putExtra("outputY", 480);
			// 输出路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			// 输出格式
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			// 不启用人脸识别
			intent.putExtra("noFaceDetection", false);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == -1) {// 拍照
				startPhotoZoom(photoUri);
			}
			break;
		case RESULT_LOAD_IMAGE:
			if (resultCode == RESULT_OK && null != data) {// 相册返回
				Uri uri = data.getData();
				if (uri != null) {
					startPhotoZoom(uri);
				}
			}
			break;
		case CUT_PHOTO_REQUEST_CODE:
			if (resultCode == RESULT_OK && null != data) {// 裁剪返回
				Bitmap bitmap = BitmpUtils.getLoacalBitmap(imagePath);
				bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
				imageView.setImageBitmap(bitmap);
				fl_search.setImageBitmap(bitmap);
				FileUtils.saveBitmap(bitmap, userbean.getOld_sn());
				updatePhoto();
				String sdcardPathDir = android.os.Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
				FileUtils.deleteDir(sdcardPathDir);

			}
			break;
		case SELECTIMG_SEARCH:
			break;
		}
	}

	private void updatePhoto() {
		String url = FileUtils.SDPATH + userbean.getOld_sn() + ".JPEG";
		LogUtil.info("smarhit", "userbean==  " + userbean.toString());
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.updateHeadPortrait(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
				url, userbean.getOld_id(), new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							LogUtil.info("smarhit", "上传头像json=" + json);
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
									LogUtil.info("smarhit", "上传图片后的avatar_url==" + avatar_url);
									userbean.setIsUpdatePhoto(1);
									db.updateUpdatePhotoStatus(userbean);
									db.close();
									PromptManager.showToast(getApplicationContext(), true, "新头像已经上传!");
									ConfigManager.setBooleanValue(UserDetailActivity.this, Constants.ISPHOTOCHANGE,
											true);
								} else {
									PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(), false, errors);
						}
					}
				});
	}

	public void changeImage(int id) {
		switch (id) {
		case R.id.btn_weight:
			changeStatus(btn_weight, Color.rgb(255, 127, 79));
			break;
		case R.id.btn_blood:
			changeStatus(btn_blood, Color.rgb(102, 151, 234));
			break;
		case R.id.btn_heartrate:
			changeStatus(btn_heartrate, Color.rgb(218, 112, 214));
			break;
		case R.id.btn_bloodsugar:
			changeStatus(btn_bloodsugar, Color.rgb(255, 62, 129));
			break;
		case R.id.btn_warning:
			btn_warning.setBackgroundResource(R.drawable.btn_white_rectangle);
			btn_warning.setTextColor(Color.rgb(59, 205, 62));
			btn_warning.setPadding(20, 0, 20, 0);
			break;
		case R.id.btn_sport:
			btn_sport.setBackgroundResource(R.drawable.btn_white_rectangle);
			btn_sport.setTextColor(Color.rgb(255, 87, 34));
			btn_sport.setPadding(20, 0, 20, 0);
			break;
		case R.id.btn_collecthistory:
			btn_collecthistory.setBackgroundResource(R.drawable.btn_white_rectangle);
			btn_collecthistory.setTextColor(Color.rgb(21, 194, 156));
			btn_collecthistory.setPadding(20, 0, 20, 0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.imageview:
			break;
		case R.id.btn_refresh:
			searchUserData(userbean.getOld_id() + "");
			break;
		case R.id.btn_weight:
			id = R.id.btn_weight;
			index = 0;
			reset();
			changeTab(0);
			getDate(id);
			break;
		case R.id.btn_blood:
			id = R.id.btn_blood;
			index = 1;
			reset();
			changeTab(0);
			getDate(id);
			break;
		case R.id.btn_heartrate:
			id = R.id.btn_heartrate;
			index = 2;
			reset();
			changeTab(0);
			getDate(id);
			break;
		case R.id.btn_bloodsugar:
			id = R.id.btn_bloodsugar;
			index = 3;
			reset();
			changeTab(0);
			getDate(id);
			break;
		case R.id.btn_warning:
			id = R.id.btn_warning;
			getloactionWarining(userbean.getOld_id() + "");
			reset();
			restHealth();
			changeTab(2);
			getDate(id);
			break;
		case R.id.btn_sport:
			id = R.id.btn_sport; // 按钮id为"运动睡眠"
			position = 0; // 日期偏移量

			dbSports.open();
			sportsBeans = dbSports.getSportsByUserIdandDate(userbean.getOld_id() + "",
					DateUtil.getDatePerviousAndNext(position).split(" ")[0]);
			dbSports.close();

			reset();// 重置按钮样式
			restHealth(); // 重置健康数据
			changeTab(1); // 切换到运动睡眠图表
			getDate(id); // 获取数据，先判断是否首次点击
			break;
		case R.id.btn_collecthistory:
			id = R.id.btn_collecthistory;
			getloactionCollect(userbean.getOld_id() + "");
			reset();
			restHealth();
			changeTab(3);
			getDate(id);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.tv_oldmore:
			restHealth();
			reset();
			fl_search.startAnim(false);
			isFlag = false;
			break;
		case R.id.btn_concernhistory:
			Intent intent = new Intent(this, ConcernHistory.class);
			intent.putExtra("userbean", userbean);
			startActivity(intent);
			break;

		case R.id.tv_changephoto:
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				new PopupWindows(UserDetailActivity.this, imageView);
			} else {
				PromptManager.showToast(getApplicationContext(), false, "sdcard已拔出，不能选择照片");
			}
			break;
		}
	}

	boolean isClickHealth = true; // 判断是否是第一次点击
	boolean isClickSport = true; // 判断是否是第一次点击
	boolean isClickCollect = true; // 判断是否是第一次点击
	boolean isClickWarn = true; // 判断是否是第一次点击

	public void getDate(int resid) {
		String userid = userbean.getOld_id() + "";
		switch (resid) {
		case R.id.btn_weight:
		case R.id.btn_blood:
		case R.id.btn_heartrate:
		case R.id.btn_bloodsugar:
			if (isClickHealth) { // 第一次点击
				getHealthData(userid); // 获取健康数据
				isClickHealth = false;
			} else {
				if (mHandler != null) {
					mHandler.sendEmptyMessage(resid); // 起线程,adddata到图表
				}
			}
			break;
		case R.id.btn_sport:
			if (isClickSport) {
				getSportData(userid, DateUtil.getDatePerviousAndNext(position), 0, null);
				// getSportData(userid, "2015-04-16", 0, null);
				isClickSport = false;
			} else {
				if (mHandler != null) {
					mHandler.sendEmptyMessage(resid); // 起线程,adddata到图表
				}
			}
			break;
		case R.id.btn_collecthistory:
			if (isClickCollect) {
				getCollectRecord(userid, null);
				isClickCollect = true;
			} else {
				if (mHandler != null) {
					mHandler.sendEmptyMessage(resid); // 起线程,adddata到图表
				}
			}
			break;
		case R.id.btn_warning:
			if (isClickWarn) {
				getWarinData(userid, null);
				isClickWarn = true;
			} else {
				if (mHandler != null) {
					mHandler.sendEmptyMessage(resid); // 起线程,adddata到图表
				}
			}
			break;
		}
		changeImage(resid); // 改变按钮颜色
	}

	/** 重置颜色 **/
	public void reset() {
		btn_sport.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_warning.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_collecthistory.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_sport.setTextColor(Color.rgb(255, 255, 255));
		btn_warning.setTextColor(Color.rgb(255, 255, 255));
		btn_collecthistory.setTextColor(Color.rgb(255, 255, 255));
		btn_sport.setPadding(20, 0, 20, 0);
		btn_warning.setPadding(20, 0, 20, 0);
		btn_collecthistory.setPadding(20, 0, 20, 0);
	}

	/** 重置健康数据 **/
	public void restHealth() {
		btn_weight.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_weight.setTextColor(Color.rgb(255, 255, 255));
		btn_blood.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_blood.setTextColor(Color.rgb(255, 255, 255));
		btn_bloodsugar.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_bloodsugar.setTextColor(Color.rgb(255, 255, 255));
		btn_heartrate.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		btn_heartrate.setTextColor(Color.rgb(255, 255, 255));
		arrayList.clear(); // 清除list中保存的所有按钮id
		oldUserHealthInfoFragment.removeAll(); // 清除图表的所有健康数据
		for (int i = 0; i < booleans.length; i++) {
			booleans[i] = false;
		}
	}

	public void changeStatus(Button button, int color) {
		if (!booleans[index]) {
			booleans[index] = true;
			button.setBackgroundResource(R.drawable.btn_white_rectangle);
			button.setTextColor(color);
		} else {
			booleans[index] = false;
			button.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
			button.setTextColor(Color.rgb(255, 255, 255));
		}
		button.setPadding(10, 0, 10, 0);
	}

	public void initFragment() {
		mFragmentsList = new ArrayList<Fragment>();
		oldUserHealthInfoFragment = new OldUserHealthInfoFragment1();
		oldUserSleepInfoFragment = new OldUserSleepInfoFragment1();
		warinHistoryFragment = new WarinHistoryFragment();

		collectRecordFragment = new CollectRecordFragment();
		mFragmentsList.add(oldUserHealthInfoFragment);
		mFragmentsList.add(oldUserSleepInfoFragment); // 运动睡眠的图表

		mFragmentsList.add(warinHistoryFragment);
		mFragmentsList.add(collectRecordFragment);
		// 默认显示第一页
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.vPager, mFragmentsList.get(0));

		ft.commitAllowingStateLoss();
	}

	private void showTab(int idx) {
		for (int i = 0; i < mFragmentsList.size(); i++) {
			Fragment fragment = mFragmentsList.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}
		currentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// 设置切换动画
		if (index > currentTab) {
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
		} else {
			ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
		}
		return ft;
	}

	public Fragment getCurrentFragment() {
		return mFragmentsList.get(currentTab);
	}

	/**
	 * 切换
	 * 
	 * @param i
	 */
	public void changeTab(int i) {
		if (!isFragment) {
			Fragment fragment = mFragmentsList.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(i);
			getCurrentFragment().onPause(); // 暂停当前tab
			// getCurrentFragment().onStop(); // 暂停当前tab
			if (fragment.isAdded()) {
				// fragment.onStart(); // 启动目标tab的onStart()
				fragment.onResume(); // 启动目标tab的onResume()
			} else {
				ft.add(R.id.vPager, fragment);
			}
			showTab(i); // 显示目标tab
			ft.commitAllowingStateLoss();
		}
	}

	private void filterHealthData(){ // 过滤一下数据，保证数据是当前选择月份的数据
		if(healthBeans != null && healthBeans.size() > 0){
			String date = DateUtil.getMonPreviousOrNext(healthPosition);
			String curYear = date.split("-")[0];
			String curMon = date.split("-")[1];
			List<HealthBean> tempBeans = new ArrayList<HealthBean>();
			
			for(HealthBean healthBean : healthBeans){
				String year = healthBean.getCollect_time().split(" ")[0].split("-")[0];
				String mon = healthBean.getCollect_time().split(" ")[0].split("-")[1];
				if(year.equals(curYear) && mon.equals(curMon)){
					tempBeans.add(healthBean);
				}
			}
			healthBeans = tempBeans;
			tempBeans = null;
		}
	}

	/**
	 * 获取老人健康信息
	 */
	public void getHealthData(final String userid) {
		
		final String tempDate = DateUtil.getMonPreviousOrNext(healthPosition).split("-")[0]
				+ "-" + DateUtil.getMonPreviousOrNext(healthPosition).split("-")[1];
		
		clear(); // 清空原来所有记录
		
		if(healthPosition != 0){
			PromptManager.showProgressDialog(UserDetailActivity.this, "正在加载");
			dbHealth.open();
			healthBeans = dbHealth.getHealthInfoByUserIdAndDate(userid, tempDate, 1); // 获得已经上传至服务器的用户健康信息
			dbHealth.close();
			if(healthBeans != null && healthBeans.size() > 0){
				filterHealthData();
				PromptManager.closeProgressDialog();
				if (mHandler != null) {
					mHandler.removeMessages(id);
					mHandler.sendEmptyMessage(id); // 起线程,adddata到图表
				}
				return;
			}
		}
		
		// 没有网
		if (!(NetUtil.isNetWorkConnected(this) || NetUtil.is3GConnectivity(this)) && healthPosition == 0) {
			PromptManager.showToast(getApplicationContext(), false, "亲，您的网络出现了异常!");
			dbHealth.open();
			healthBeans = dbHealth.getHealthInfoByUserIdAndDate(userid, tempDate, 1); // 获得已经上传至服务器的用户健康信息
			dbHealth.close();
			filterHealthData();
			PromptManager.closeProgressDialog();
			if (mHandler != null) {
				mHandler.removeMessages(id);
				mHandler.sendEmptyMessage(id); // 起线程,adddata到图表
			}			
			return;
		}
		
		if(healthPosition == 0){ // healthPosition != 0 的情况已经启动了进度条提示框所以不再启动了
			PromptManager.showProgressDialog(UserDetailActivity.this, "正在加载");
		}
		HttpClientUtil clientUtil = HttpClientUtil.getInstance();

		/* 根据id获取老人所有健康记录(采集时间,采集人,护士id,体重,血压,心率,血糖,老人id) */
		clientUtil.getMonthOldUserHealthInfo(this, ConfigManager.getStringValue(this, Constants.ACCESS_TOKEN), userid, tempDate,
				new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean bean = JSON.parseObject(json, BaseBean.class);
								if (bean.getStatus() == 0) {
									final List<HealthBean> healths = JSON.parseArray(bean.getData(), HealthBean.class);
									healthBeans.addAll(healths);
									filterHealthData();
									dbHealth.open();
									dbHealth.deleteHealthInfoByUserIdandDate(userbean.getOld_id() + "", tempDate);
									for(HealthBean healthBean : healthBeans){								
										healthBean.setIsUpdate(1);
									}					
									dbHealth.insert(healthBeans);
									healthBeans = dbHealth.getHealthInfoByUserIdAndDate(userid, tempDate, 1); // 为了统一时间排序，从数据库中重新取一次
									dbHealth.close();
									if (mHandler != null) {
										mHandler.removeMessages(id);
										mHandler.sendEmptyMessage(id); // 起线程,adddata到图表
									}
								}
							}
						}else{
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getApplicationContext(), false, errors);
						}
					}
				});
	}

	/**
	 * 获取老人睡眠信息和老人运动信息
	 * 
	 * @param userid
	 *            老人id
	 * @param data_start
	 *            时间
	 */
	String dateStr;

	public void getSportData(final String userId, final String data_start, final int postion, final Handler handler) {
		String tempdate = data_start.split(" ")[0];
		sportsBeans.clear();

		// 根据id和date获取老人运动睡眠信息
		if (handler != null) {
			dbSports.open();
			sportsBeans.clear();
			sportsBeans = dbSports.getSportsByUserIdandDate(userId, tempdate);
			for (SportsBean sport : sportsBeans) {
				LogUtil.info(UserDetailActivity.class, "line790:" + sport.toString());
			}
			dbSports.close();
		}

		// 判断是否是当天，如果是当天的则一律从网上更新
		if (tempdate.equals(DateUtil.getDatePerviousAndNext(0).split(" ")[0])) {
			dateStr = tempdate;
		} else {
			if (sportsBeans != null && sportsBeans.size() > 0) {
				if (handler == null) {
					mHandler.sendEmptyMessage(R.id.btn_sport); // 起线程,adddata到图表
				} else {
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = postion;
					msg.obj = sportsBeans;
					handler.sendMessage(msg);
				}
				return;
			} else {
				dateStr = tempdate;
			}
		}

		// 当天在没有网络的情况下，显示数据库中的数据
		if (!(NetUtil.isNetWorkConnected(this) || NetUtil.is3GConnectivity(this))) {
			dbSports.open();
			sportsBeans = dbSports.getSportsByUserIdandDate(userId, tempdate);
			dbSports.close();

			PromptManager.closeProgressDialog();
			if (handler == null) {
				mHandler.sendEmptyMessage(R.id.btn_sport); // 起线程,adddata到图表
			} else {
				Message msg = new Message();
				msg.what = 0;
				msg.arg1 = postion;
				msg.obj = sportsBeans;
				handler.sendMessage(msg);
			}

			return;
		}

		PromptManager.showProgressDialog(UserDetailActivity.this, "正在加载");

		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		clientUtil.QueryAndGetOldUserSportsInfo(this, ConfigManager.getStringValue(this, Constants.ACCESS_TOKEN),
				userId, dateStr, new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								SportsBean bean = JSON.parseObject(json, SportsBean.class);
								if (bean.getData().trim().equals("0") || bean.getData().equals("[]")) {
									if (handler == null) {
										mHandler.sendEmptyMessage(R.id.btn_sport); // 起线程,adddata到图表
									} else {
										Message msg = new Message();
										msg.what = 0;
										msg.arg1 = postion;
										msg.obj = sportsBeans;
										handler.sendMessage(msg);
									}
									return;
								}
								if (bean.getStatus() == 0) {
									final List<SportsBean> sports = JSON.parseArray(bean.getData(), SportsBean.class);
									new Thread(new Runnable() {
										@Override
										public void run() {
											
											dbSports.open();
											dbSports.deleteSportsInfoByUserIdandDate(userId, dateStr);
											dbSports.insert(sports, dateStr);
											sportsBeans.clear();
											sportsBeans.addAll(sports);
											dbSports.close();
											if (handler != null) {
												Message msg = new Message();
												msg.what = 0;
												msg.arg1 = postion;
												msg.obj = sportsBeans;
												handler.sendMessage(msg);
											} else {
												mHandler.sendEmptyMessage(R.id.btn_sport); // 起线程,adddata到图表
											}
										}
									}).start();
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(), false, errors);
							PromptManager.closeProgressDialog();
						}
					}
				});
	}

	/**
	 * 获取报警记录
	 * 
	 * @param oldid
	 * @param handler
	 */
	public void getWarinData(final String oldid, final Handler handler) {

		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		clientUtil.getOldAlartHistoryInfo(this, ConfigManager.getStringValue(this, Constants.ACCESS_TOKEN), oldid, page
				+ "", PAGENUM + "", new IOperationResult() {
			@Override
			public void operationResult(boolean isSuccess, String json, String errors) {
				if (isSuccess) {
					PromptManager.closeProgressDialog();
					if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
						PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
					} else {
						BaseBean bean = JSON.parseObject(json, BaseBean.class);
						if (bean.getData().trim().equals("0") || bean.getData().equals("[]")) {
							if (handler == null) {
								mHandler.sendEmptyMessage(R.id.btn_warning);
							} else {
								handler.sendEmptyMessage(0);
								PromptManager.showToast(getApplicationContext(), false, "没有数据");
							}
							return;
						}
						
						if (bean.getStatus() == 0) {
							final List<WarningBean> warnings = JSON.parseArray(bean.getData(), WarningBean.class);
							if (page == 1) {
								warningBeans.clear();
							}
							warningBeans.addAll(warnings);
							page = page + 1;
							if (handler == null) {
								mHandler.sendEmptyMessage(R.id.btn_warning); // 起线程,adddata到图表
							} else {
								Message message = new Message();
								message.what = 0;
								message.obj = warningBeans;
								handler.sendMessage(message);
							}
						}
					}

				} else {
					PromptManager.showToast(getApplicationContext(), false, errors);
					getloactionWarining(oldid);
					if (handler != null) {
						Message message = new Message();
						message.what = 0;
						message.obj = warningBeans;
						handler.sendMessage(message);
					}
					PromptManager.closeProgressDialog();
				}
			}
		});
	}

	/**
	 * 获取本地的报警信息
	 * 
	 * @param oldid
	 */
	public void getloactionWarining(String oldid) {

		if ((NetUtil.isNetWorkConnected(this) || NetUtil.is3GConnectivity(this))) {
			return;
		}
		if (tempage == 1) {
			warningBeans.clear();
			page = 1;
		}
		dbWarning.open();
		List<WarningBean> warningTemps = dbWarning.getWarningInfoByOldid(oldid, (tempage - 1) * PAGENUM, PAGENUM);
		warningBeans.addAll(warningTemps);
		dbWarning.close();
		tempage = tempage + 1;
	}

	/**
	 * 获取本地记录
	 * 
	 * @param oldid
	 */
	public void getloactionCollect(String oldid) {

		if ((NetUtil.isNetWorkConnected(this) || NetUtil.is3GConnectivity(this))) {
			return;
		}
		if (tempCollectPage == 1) {
			collectRecordBeans.clear();
			pageRecord = 1;
		}
		dbHealth.open();
		List<HealthBean> warningTemps = dbHealth.getHealthInfoByUserId(oldid, (tempCollectPage - 1) * PAGENUM, PAGENUM);
		collectRecordBeans.addAll(warningTemps);
		dbHealth.close();
		tempCollectPage = tempCollectPage + 1;
	}

	/**
	 * 获取老人信息
	 * 
	 * @param id
	 */
	public void searchUserData(String id) {
		LogUtil.info("lhw", "userid=" + id);
		PromptManager.showProgressDialog(this, "正在更新");
		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		clientUtil.QueryAndGetOldUserInfo(this, ConfigManager.getStringValue(this, Constants.ACCESS_TOKEN), id, "",
				new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {

						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								UserBean bean = JSON.parseObject(json, UserBean.class);
								if (bean.getStatus() == 400) {
									PromptManager.showToast(getApplicationContext(), false, "无外区域权限");
									return;
								}
								if (bean.getStatus() == 0) {
									UserBean user = JSON.parseObject(bean.getData(), UserBean.class);
									if (user != null) {
										userbean = user;
										loadingPhoto();
										UserDetailActivity.this.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												setUserInfo();
												dbUser.open();
												dbUser.updateOldInfo(userbean);
												dbUser.close();

												PromptManager.showToast(getApplicationContext(), true, "更新成功");
												ConfigManager.setBooleanValue(UserDetailActivity.this,
														Constants.ISPHOTOCHANGE, true);
											}
										});
									}
								} else {
									PromptManager.showToast(getApplicationContext(), false, "更新失败");
								}
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getApplicationContext(), false, errors);
						}
					}
				});
	}

	/**
	 * 获取采集记录
	 */
	public void getCollectRecord(final String userid, final Handler handler) {

		HttpClientUtil clientUtil = HttpClientUtil.getInstance();
		clientUtil.getCollectRecord(this, ConfigManager.getStringValue(this, Constants.ACCESS_TOKEN), userid,
				pageRecord + "", PAGENUM + "", new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean bean = JSON.parseObject(json, BaseBean.class);
								if (bean.getData().equals("[]")) {
									if (handler == null) {
										isClickCollect = true;
										changeImage(R.id.btn_collecthistory);
										if (mHandler != null) {
											mHandler.sendEmptyMessage(R.id.btn_collecthistory);
										}
									} else {
										handler.sendEmptyMessage(0);
										PromptManager.showToast(getApplicationContext(), false, "没有数据");
									}
									return;
								}
								
								if (bean.getStatus() == 0) {
									final List<HealthBean> healths = JSON.parseArray(bean.getData(), HealthBean.class);
									for (HealthBean healthBean : healths) {
										dbUser.open();
										healthBean.setUser(dbUser.getUserInfoById(userbean.getOld_id() + ""));
										dbUser.close();
									}
									if (pageRecord == 1) {
										collectRecordBeans.clear();
									}
									collectRecordBeans.addAll(healths);
									pageRecord = pageRecord + 1;
									if (handler == null) {
										isClickCollect = true;
										changeImage(R.id.btn_collecthistory);
										if (mHandler != null) {
											mHandler.sendEmptyMessage(R.id.btn_collecthistory);
										}
									} else {
										Message message = new Message();
										message.what = 0;
										message.obj = collectRecordBeans;
										handler.sendMessage(message);
									}
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(), false, errors);
							PromptManager.closeProgressDialog();
						}
					}
				});
	}

	/**
	 * 清楚原来的
	 */
	public void clear() {
		// index = 0;
		page = 1;
		position = 0;
		arrayList.clear();
		oldUserHealthInfoFragment.removeAll();
		warningBeans.clear();
		sportsBeans.clear();
		if(healthBeans != null){
			healthBeans.clear();
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!isFlag) {
				fl_search.startAnim(true);
				isFlag = true;
			}
			switch (msg.what) {
			case R.id.btn_weight:
			case R.id.btn_blood:
			case R.id.btn_heartrate:
			case R.id.btn_bloodsugar:
				addRemove(id);
				break;

			case R.id.btn_sport:
				oldUserSleepInfoFragment.addDate(sportsBeans, position); // 给运动睡眠的图表加入数据
				break;

			case R.id.btn_warning:
				warinHistoryFragment.addData(warningBeans);
				break;
			case R.id.btn_collecthistory:
				collectRecordFragment.addData(collectRecordBeans);
				break;
			}
		};
	};

	public void addRemove(int id) {
		boolean isFlag = false;
		int current = 0;
		for (int i = 0; i < arrayList.size(); i++) {
			if (id == arrayList.get(i)) {
				isFlag = true;
				current = i;
				break;
			}
		}

		if (!isFlag) {
			arrayList.add(id);
			if (id == R.id.btn_blood) {
				arrayList.add(id);
			}
			oldUserHealthInfoFragment.addDate(healthBeans, index, healthPosition);
		} else {
			oldUserHealthInfoFragment.removeDataSet(current, id);
			arrayList.remove(current);
			if (id == R.id.btn_blood) {
				arrayList.remove(current);
			}
		}
//		oldUserHealthInfoFragment.setArrayList(arrayList);
	}

	long clicktime;

	@Override
	public void showSportsData(Handler handler, boolean isFlag) {

		if (System.currentTimeMillis() - clicktime < 300) {
			return;
		}
		clicktime = System.currentTimeMillis();

		if (position != 0) {
			if (isFlag) {
				position = position + 1;
			} else {
				position = position - 1;
			}
		} else {
			if (!isFlag) {
				position = position - 1;
			}
		}
		getSportData(userbean.getOld_id() + "", DateUtil.getDatePerviousAndNext(position), position, handler);
	}

	@Override
	public void showWarinData(Handler handler) {

		getWarinData(userbean.getOld_id() + "", handler);
	}

	private void loadingPhoto() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		String localAddress = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
		client.loadingPhoto(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN),
				localAddress, userbean.getAvatar_url(), new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						setImagePhoto();
					}
				});
	}

	@Override
	public void showCollectRecord(Handler handler) {

		getCollectRecord(userbean.getOld_id() + "", handler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isFragment = true;
		mHandler = null;
	}

	@Override
	public void showHealthData(int healthPosition) {
		// TODO Auto-generated method stub
		isClickHealth = false;
		clear();
		restHealth();
		this.healthPosition = healthPosition;
		index = 0;
		id = R.id.btn_weight;
		changeStatus(btn_weight, Color.rgb(255, 127, 79));
		arrayList.add(R.id.btn_weight);
		getHealthData(userbean.getOld_id() + "");
	}
}
