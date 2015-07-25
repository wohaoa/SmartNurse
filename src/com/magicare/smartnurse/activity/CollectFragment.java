package com.magicare.smartnurse.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.CollectRecordAdapter;
import com.magicare.smartnurse.adapter.CollectRecordAdapter.CollectionListener;
import com.magicare.smartnurse.adapter.SerachAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.ble.BLEConstants;
import com.magicare.smartnurse.ble.BloodSugarDataUtil;
import com.magicare.smartnurse.ble.BluetoothLeService;
import com.magicare.smartnurse.database.dao.DBHealth;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.logic.BleManage;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.DataFormatUtil;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.ClearEditText;
import com.magicare.smartnurse.view.ClearEditText.ClearDate;

/**
 * 
 * @author scott
 * 
 *         Function:采集
 */
@SuppressWarnings("ALL")
public class CollectFragment extends BleBaseFragment implements OnClickListener, CollectionListener, ClearDate {

	private View mView;
	private UserBean userbean;

	/********************************* 界面标题 **************************************/
	private LinearLayout layout_title;
	private ClearEditText et_search_keyword;
	private Button btn_collect_record;
	private Button btn_title_back;
	private Button btn_query;
	private Button btn_search_mask;

	/********************************* 采集界面 **************************************/
	private LinearLayout layout_mask;
	private FrameLayout fl_collect;
	private TextView tv_old_sn;
	private ImageView civ_photo;
	private TextView tv_name;
	private TextView tv_age;
	private TextView tv_gender;
	private TextView tv_location;
	private TextView tv_more;
	private TextView tv_weight;
	private TextView tv_bloodpressure;
	private TextView tv_heart_rate;
	private TextView tv_bloodsugar;
	private ImageView iv_totalprocess;
	private TextView tv_current_status;
	private ImageView iv_weight_default;
	private ImageView iv_bloodpressure_defalut;
	private ImageView iv_heart_defalut;
	private ImageView iv_bloodsugar_defalut;
	private ImageView iv_weight_loading;
	private ImageView iv_bloodpressure_loading;
	private ImageView iv_heart_loading;
	private ImageView iv_bloodsugar_loading;
	private Button btn_weight;
	private Button btn_bloodpressure;
	private Button btn_heart_rate;
	private Button btn_bloodsugar;
	private AnimationDrawable amin;
	private AnimationDrawable retestAmin;
	private ImageView iv_startcollect;
	private TextView tv_start_txt;
	private BleManage bleManage;
	/* 蓝牙是否获取到数据，如果isGetData=false，将重新发送连接命令 */
	private boolean isGetData = false;

	/********************************* 查询界面 **************************************/
	private LinearLayout layout_query;
	private GridView gv_user;
	private SerachAdapter mUserAdapter;
	/* 所以的用户信息 */
	private List<UserBean> list_users = new ArrayList<UserBean>();
	/* 筛选过后的用户信息 */
	private List<UserBean> filterDateList = null;

	/********************************* 采集记录界面 ******************************************/
	private LinearLayout layout_record;
	private Button btn_reoced_reback;
	private ListView lv_collect_record;
	private CollectRecordAdapter mRecordAapter;
	private List<HealthBean> list_health = new ArrayList<HealthBean>();
	// 选中用户的所以健康信息
	private List<HealthBean> selected_health = new ArrayList<HealthBean>();

	// 当前选中用户的最近一次采集健康信息
	private HealthBean healthBean, bean;

	// 标识当前采集的是一条全新的记录，还是重新采集的数据。【创建和修改】
	private boolean isNewData = true;

	// 控制重新测试按钮
	private boolean isRetestPause = false;

	// 是否点击了更多，【是否查看过详情】
	private boolean isGoDetail = false;

	// 计数,是否采集完一轮设备
	private int collectCount;

	// 设备中文名
	private String deviceName;
	
	private boolean iswakeLock = true;// 是否常亮
	private WakeLock wakeLock;

	/** Handler what */
	private static final int SUGAR_VERIFYCODE = 0;
	private static final int SUGAR_TIME = 1;
	private static final int SUGAR_DATA = 2;
	private static final int FATSCALE_RESCAN = 3;
	private static final int CLOSE_BLUETOOTH = 4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mView = inflater.inflate(R.layout.fragment_collect, container, false);
		initview();
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		isRetestPause = false;
		initBle();
		if (!isGoDetail) {
			layout_mask = (LinearLayout) mView.findViewById(R.id.layout_mask);
			layout_mask.setVisibility(View.VISIBLE);
			layout_mask.setOnClickListener(this);
			layout_title.setVisibility(View.VISIBLE);
			fl_collect.setVisibility(View.VISIBLE);
			layout_query.setVisibility(View.GONE);
			layout_record.setVisibility(View.GONE);
			collectHealthDefault();
		} else {
			isGoDetail = false;
		}

		// 隐藏状态
		WindowManager.LayoutParams attr = getActivity().getWindow().getAttributes();
		attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActivity().getWindow().setAttributes(attr);
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 根据输入框中的值来过滤数据并更新GridView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		filterDateList = new ArrayList<UserBean>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list_users;
		} else {
			filterDateList.clear();
			for (UserBean bean : list_users) {
				String name = bean.getName();
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(bean);
				}
			}
		}
		if (mUserAdapter == null) {
			mUserAdapter = new SerachAdapter(mContext, filterDateList);
			gv_user.setAdapter(mUserAdapter);
		} else {
			mUserAdapter.updateListView(filterDateList);
		}
	}

	private void initview() {
		layout_title = (LinearLayout) mView.findViewById(R.id.layout_title);
		et_search_keyword = (ClearEditText) mView.findViewById(R.id.et_search_keyword);
		btn_search_mask = (Button) mView.findViewById(R.id.btn_search_mask);
		btn_search_mask.setOnClickListener(this);
		et_search_keyword.setClearDate(CollectFragment.this);
		et_search_keyword.setOnClickListener(this);
		btn_collect_record = (Button) mView.findViewById(R.id.btn_collect_record);
		btn_title_back = (Button) mView.findViewById(R.id.btn_title_back);
		btn_query = (Button) mView.findViewById(R.id.btn_query);
		btn_title_back.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		btn_collect_record.setOnClickListener(this);

		/** 采集界面 **/
		layout_mask = (LinearLayout) mView.findViewById(R.id.layout_mask);
		layout_mask.setVisibility(View.VISIBLE);
		layout_mask.setOnClickListener(this);
		fl_collect = (FrameLayout) mView.findViewById(R.id.fl_collect);
		civ_photo = (ImageView) mView.findViewById(R.id.civ_photo);
		tv_old_sn = (TextView) mView.findViewById(R.id.tv_old_sn);
		tv_name = (TextView) mView.findViewById(R.id.tv_name);
		tv_age = (TextView) mView.findViewById(R.id.tv_age);
		tv_gender = (TextView) mView.findViewById(R.id.tv_gender);
		tv_location = (TextView) mView.findViewById(R.id.tv_location);

		tv_more = (TextView) mView.findViewById(R.id.tv_more);
		tv_more.setOnClickListener(this);
		tv_weight = (TextView) mView.findViewById(R.id.tv_weight);
		tv_bloodpressure = (TextView) mView.findViewById(R.id.tv_bloodpressure);
		tv_heart_rate = (TextView) mView.findViewById(R.id.tv_heart_rate);
		tv_bloodsugar = (TextView) mView.findViewById(R.id.tv_bloodsugar);

		tv_current_status = (TextView) mView.findViewById(R.id.tv_current_status);
		iv_totalprocess = (ImageView) mView.findViewById(R.id.iv_totalprocess);
		iv_weight_default = (ImageView) mView.findViewById(R.id.iv_weight_default);
		iv_bloodpressure_defalut = (ImageView) mView.findViewById(R.id.iv_bloodpressure_default);
		iv_heart_defalut = (ImageView) mView.findViewById(R.id.iv_heart_default);
		iv_bloodsugar_defalut = (ImageView) mView.findViewById(R.id.iv_bloodsugar_default);
		iv_weight_loading = (ImageView) mView.findViewById(R.id.iv_weight_loading);
		iv_bloodpressure_loading = (ImageView) mView.findViewById(R.id.iv_bloodpressure_loading);
		iv_heart_loading = (ImageView) mView.findViewById(R.id.iv_heart_loading);
		iv_bloodsugar_loading = (ImageView) mView.findViewById(R.id.iv_bloodsugar_loading);

		btn_weight = (Button) mView.findViewById(R.id.btn_weight);
		btn_bloodpressure = (Button) mView.findViewById(R.id.btn_bloodpressure);
		btn_bloodsugar = (Button) mView.findViewById(R.id.btn_bloodsugar);
		btn_heart_rate = (Button) mView.findViewById(R.id.btn_heart_rate);
		btn_weight.setOnClickListener(this);
		btn_bloodpressure.setOnClickListener(this);
		btn_bloodsugar.setOnClickListener(this);
		btn_heart_rate.setOnClickListener(this);

		iv_startcollect = (ImageView) mView.findViewById(R.id.iv_startcollect);
		tv_start_txt = (TextView) mView.findViewById(R.id.tv_start_txt);
		iv_startcollect.setOnClickListener(this);

		/***** 查询界面 *****/
		layout_query = (LinearLayout) mView.findViewById(R.id.layout_query);
		gv_user = (GridView) mView.findViewById(R.id.gv_user);
		gv_user.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 强制隐藏键盘
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				
//				PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//				wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "DPA");
//				if (iswakeLock) {
//					wakeLock.acquire();
//				}
				
				isRetestPause = false; // 重测按钮是否暂停状态
				layout_mask.setVisibility(View.GONE);
				fl_collect.setVisibility(View.VISIBLE);
				layout_query.setVisibility(View.GONE);
				userbean = filterDateList.get(position);
				// 设置头像
				String photopath = FileUtils.SDPATH + userbean.getOld_id() + ".JPEG";
				File file = new File(photopath);
				if (file.exists()) {
					Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
					bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
					civ_photo.setImageBitmap(bitmap);
				}
				tv_name.setText(userbean.getName());
				tv_old_sn.setText(userbean.getOld_sn());
				tv_age.setText(userbean.getAge() + "岁");
				tv_gender.setText(userbean.getGender());
				tv_location.setText(userbean.getPension_areaid() + "");
				// 查询该用户最近的采集记录
				DBHealth dbhealth = DBHealth.getInstance(mContext);
				dbhealth.open();
				selected_health = dbhealth.getHealthInfoByUserId(userbean.getOld_id() + "");
				dbhealth.close();
				if (selected_health != null && selected_health.size() > 0) {
					healthBean = selected_health.get(0);
				} else {
					healthBean = null;
				}
				// 设置界面展示
				collectHealthDefault();
				processDefalutStatus(); // 进度条默认状态

				// 当前已收集数据置空
				mBluetoothLeService.mCollectedDevice = "";
				mDelayHander.removeMessages(FATSCALE_RESCAN);
				mDelayHander.removeMessages(CLOSE_BLUETOOTH);
				isNewData = true;
				LogUtil.info("smarhit", "老人" + userbean.getOld_sn() + " 开始采集");
			}
		});

		/****** 采集记录 */
		layout_record = (LinearLayout) mView.findViewById(R.id.layout_record);
		btn_reoced_reback = (Button) mView.findViewById(R.id.btn_reoced_reback);
		btn_reoced_reback.setOnClickListener(this);
		lv_collect_record = (ListView) mView.findViewById(R.id.lv_collect_record);

		/* 蓝牙管理 */
		bleManage = new BleManage(mContext);
		mRunnable = new Runnable() {
			@Override
			public void run() {
				// 自动测试数据
				if (mBluetoothLeService.mDevice != null
						&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
					// 体重
					write(BLEConstants.UUID_CHARACTERISTIC, bleManage.getWriteData());
					readDeviceByHand();
				} else if (mBluetoothLeService.mDevice != null
						&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
					// 血压
				} else if (mBluetoothLeService.mDevice != null
						&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
					// 血糖
					// 检查蓝牙是连接正常
					write(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC, BloodSugarDataUtil.connection());
				}
			}
		};

		getAllUserInfo();

		// 根据输入框输入值的改变来过滤搜索
		et_search_keyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	/**
	 * 
	 * Function:界面顶部搜索部分的显示控制
	 * 
	 * @param isCollect
	 */
	private void showTitle(boolean isCollect) {
		if (isCollect) {
			btn_search_mask.setVisibility(View.VISIBLE);
			btn_query.setVisibility(View.GONE);
			btn_title_back.setVisibility(View.GONE);
			btn_collect_record.setVisibility(View.VISIBLE);
			et_search_keyword.setText("");
		} else {
			btn_search_mask.setVisibility(View.GONE);
			btn_query.setVisibility(View.VISIBLE);
			btn_title_back.setVisibility(View.VISIBLE);
			btn_collect_record.setVisibility(View.GONE);
		}
	}

	private Handler loadingHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (mUserAdapter == null) {
					mUserAdapter = new SerachAdapter(mContext, list_users);
					gv_user.setAdapter(mUserAdapter);
				} else {
					mUserAdapter.notifyDataSetChanged();
				}
				break;
			case 1:
				if (mRecordAapter == null) {
					mRecordAapter = new CollectRecordAdapter(mContext, list_health);
					mRecordAapter.setOnCollectionListener(CollectFragment.this);
					lv_collect_record.setAdapter(mRecordAapter);
				} else {
					mRecordAapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}

		};

	};

	/**
	 * 
	 * Function:获取所有用户信息
	 */
	public void getAllUserInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 所有老人信息
				list_users.clear();
				DBUser dbuser = DBUser.getInstance(mContext);
				dbuser.open();
				list_users.addAll(dbuser.getAllUserInfo());
				dbuser.close();
				loadingHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	private boolean isStart = true;
	private long systemTime = 0;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_search_mask:
			getAllUserInfo();
			// 关闭蓝牙通信
			if (mBluetoothLeService != null) {
				mBluetoothLeService.scanLeDevice(false);
				mBluetoothLeService.close();
			}
			layout_title.setVisibility(View.VISIBLE);
			layout_query.setVisibility(View.VISIBLE);
			fl_collect.setVisibility(View.GONE);
			showTitle(false);

			break;
		case R.id.btn_collect_record:

			// 所有测量记录
			DBHealth dbHealth = DBHealth.getInstance(mContext);
			dbHealth.open();
			list_health.clear();
			list_health.addAll(dbHealth.getAllHealthInfo());
			dbHealth.close();
			loadingHandler.sendEmptyMessage(1);
			// 关闭蓝牙通信
			if (mBluetoothLeService != null) {
				mBluetoothLeService.scanLeDevice(false);
				mBluetoothLeService.close();
			}
			layout_title.setVisibility(View.GONE);
			fl_collect.setVisibility(View.GONE);
			layout_query.setVisibility(View.GONE);
			layout_record.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_title_back:// 查询时头部的返回
			layout_title.setVisibility(View.VISIBLE);
			fl_collect.setVisibility(View.VISIBLE);
			layout_query.setVisibility(View.GONE);
			layout_record.setVisibility(View.GONE);
			if (userbean != null) {
				layout_mask.setVisibility(View.GONE);
			} else {
				layout_mask.setVisibility(View.VISIBLE);
			}
			showTitle(true);
			break;
		case R.id.btn_reoced_reback:// 采集记录中的返回
			layout_title.setVisibility(View.VISIBLE);
			fl_collect.setVisibility(View.VISIBLE);
			layout_query.setVisibility(View.GONE);
			layout_record.setVisibility(View.GONE);
			if (userbean != null) {
				layout_mask.setVisibility(View.GONE);
			} else {
				layout_mask.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_query:
			String temp = et_search_keyword.getText().toString().trim();
			if (TextUtils.isEmpty(temp)) {
				PromptManager.showToast(mContext, false, "请输入老人ID或老人名字");
				return;
			}
			list_users.clear();
			DBUser dbuser = DBUser.getInstance(mContext);
			dbuser.open();
			list_users.addAll(dbuser.getUserInfoByNameOrId(temp));
			dbuser.close();
			if (list_users != null && list_users.size() > 0) {
				mUserAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.tv_more:
			Intent intent = new Intent(getActivity(), UserDetailActivity.class);
			intent.putExtra("userbean", userbean);
			startActivity(intent);
			isGoDetail = true;
			break;
		case R.id.btn_weight:
			processDefalutStatus();
			isNewData = false;
			mDelayHander.removeMessages(CLOSE_BLUETOOTH);
			LogUtil.info("smarhit", "移除超时线程");
			mDelayHander.removeMessages(FATSCALE_RESCAN);
			LogUtil.info("smarhit", "移除脂肪秤重连线程");
			if (isRetestPause) {
				resetService();
				if (mBluetoothLeService != null) {
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
				}
				btn_weight.setText("重新测试");
				isRetestPause = false;
				if (retestAmin != null) {
					retestAmin.stop();
					iv_weight_loading.clearAnimation();
				}
				iv_weight_default.setVisibility(View.VISIBLE);
				iv_weight_loading.setVisibility(View.GONE);
			} else {
				tv_current_status.setText("正在连接脂肪秤");
				mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_FATSCALE_DEVICE_NAME);
				// 60秒后无响应断开连接
				LogUtil.info("smarhit", "开启超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
				btn_weight.setText("暂停");
				isRetestPause = true;
				retestAmin = (AnimationDrawable) iv_weight_loading.getBackground();
				retestAmin.start();
				iv_weight_default.setVisibility(View.GONE);
				iv_weight_loading.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.btn_bloodpressure:
			isNewData = false;
			processDefalutStatus();
			mDelayHander.removeMessages(CLOSE_BLUETOOTH);
			LogUtil.info("smarhit", "移除超时线程");
			if (isRetestPause) {
				resetService();
				if (mBluetoothLeService != null) {
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
					mBluetoothLeService.mDevice = null;
					mBluetoothLeService.mCollectedDevice = "";
					mBluetoothLeService.mDeviceName = "";
					deviceName = "";
				}
				btn_bloodpressure.setText("重新测试");
				isRetestPause = false;
				if (retestAmin != null) {
					retestAmin.stop();
					iv_bloodpressure_loading.clearAnimation();
				}
				iv_bloodpressure_defalut.setVisibility(View.VISIBLE);
				iv_bloodpressure_loading.setVisibility(View.GONE);
			} else {
				tv_current_status.setText("正在连接血压计");
				btn_bloodpressure.setText("暂停");
				mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME);
				// 60秒后无响应断开连接
				LogUtil.info("smarhit", "开启超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
				iv_bloodpressure_defalut.setVisibility(View.GONE);
				iv_bloodpressure_loading.setVisibility(View.VISIBLE);
				retestAmin = (AnimationDrawable) iv_bloodpressure_loading.getBackground();
				retestAmin.start();
				isRetestPause = true;
			}

			break;
		case R.id.btn_heart_rate:
			isNewData = false;
			processDefalutStatus();
			mDelayHander.removeMessages(CLOSE_BLUETOOTH);
			LogUtil.info("smarhit", "移除超时线程");
			if (isRetestPause) {
				resetService();
				if (mBluetoothLeService != null) {
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
					mBluetoothLeService.mDevice = null;
					mBluetoothLeService.mCollectedDevice = "";
					mBluetoothLeService.mDeviceName = "";
					deviceName = "";
				}
				btn_heart_rate.setText("重新测试");
				isRetestPause = false;
				if (retestAmin != null) {
					retestAmin.stop();
					iv_heart_loading.clearAnimation();
				}
				iv_heart_defalut.setVisibility(View.VISIBLE);
				iv_heart_loading.setVisibility(View.GONE);
			} else {
				tv_current_status.setText("正在连接血压计");
				btn_heart_rate.setText("暂停");
				mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME);
				// 60秒后无响应断开连接
				LogUtil.info("smarhit", "开启超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
				iv_heart_defalut.setVisibility(View.GONE);
				iv_heart_loading.setVisibility(View.VISIBLE);
				retestAmin = (AnimationDrawable) iv_heart_loading.getBackground();
				retestAmin.start();
				isRetestPause = true;
			}
			break;
		case R.id.btn_bloodsugar:
			isNewData = false;
			processDefalutStatus();
			mDelayHander.removeMessages(CLOSE_BLUETOOTH);
			LogUtil.info("smarhit", "移除超时线程");
			if (isRetestPause) {
				resetService();
				if (mBluetoothLeService != null) {
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
					mBluetoothLeService.mDevice = null;
					mBluetoothLeService.mCollectedDevice = "";
					mBluetoothLeService.mDeviceName = "";
					deviceName = "";
				}
				btn_bloodsugar.setText("重新测试");
				isRetestPause = false;
				if (retestAmin != null) {
					retestAmin.stop();
					iv_bloodsugar_loading.clearAnimation();
				}
				iv_bloodsugar_defalut.setVisibility(View.VISIBLE);
				iv_bloodsugar_loading.setVisibility(View.GONE);
			} else {
				mBluetoothLeService.mCollectedDevice = "";
				tv_current_status.setText("正在连接血糖仪");
				btn_bloodsugar.setText("暂停");
				mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME);
				LogUtil.info("smarhit", "开启超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
				iv_bloodsugar_defalut.setVisibility(View.GONE);
				iv_bloodsugar_loading.setVisibility(View.VISIBLE);
				retestAmin = (AnimationDrawable) iv_bloodsugar_loading.getBackground();
				retestAmin.start();
				isRetestPause = true;
			}

			break;
		case R.id.iv_startcollect:
			
			// 采集界面，如果有数据，点击开始按钮，弹出再次确认的窗口
			if (isStart && healthBean!=null && (healthBean.getWeight() > 0 || healthBean.getDiastolic_pressure() > 0 || healthBean.getBlood_sugar() > 0)) {
				// 弹出对话框
				showConfirmDialog();
			} else {
				newCollectData();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 弹出确认重新采集
	 */
	private void showConfirmDialog() {
		AlertDialog.Builder versionBuilder = new AlertDialog.Builder(mContext);
		versionBuilder.setTitle("温馨提示:");

		StringBuilder sb_msg = new StringBuilder("本组数据还有");

		if (healthBean!=null && healthBean.getWeight() <= 0) {
			sb_msg.append("体重");
		}

		if (healthBean!=null && healthBean.getDiastolic_pressure() <= 0) {
			sb_msg.append("、血压、心率");
		}

		if (healthBean!=null &&healthBean.getBlood_sugar() <= 0) {
			sb_msg.append("、血糖");
		}

		versionBuilder.setMessage(sb_msg + "未采集，是否开始下一组采集");
		versionBuilder.setPositiveButton("返回继续", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		versionBuilder.setNegativeButton("开始下一组", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				newCollectData();
			}
		});

		AlertDialog versionDialog = versionBuilder.create();
		versionDialog.setCancelable(false);
		versionDialog.show();
	}

	/**
	 * 
	 * Function:新的一轮数据采集
	 */
	private void newCollectData() {
		isNewData = true;
		resetService();
		mDelayHander.removeMessages(CLOSE_BLUETOOTH);
		LogUtil.info("smarhit", "移除超时线程");
		mDelayHander.removeMessages(FATSCALE_RESCAN);
		LogUtil.info("smarhit", "移除脂肪秤重连线程");
		// 控制连续点击
		if (System.currentTimeMillis() - systemTime > 1500) {
			tv_current_status.setText("设备正在连接...");
			if (amin != null) {
				amin.stop();
				iv_totalprocess.clearAnimation();
			}
			iv_totalprocess.setBackgroundResource(R.anim.collect_process);
			amin = (AnimationDrawable) iv_totalprocess.getBackground();
			// 蓝牙数据通信服务
			if (isStart) {
				healthBean = null;
				collectHealthDefault();
				tv_current_status.setText("设备正在连接...");
				mBluetoothLeService.scanLeDevice(true);
				// 60秒后无响应断开连接
				LogUtil.info("smarhit", "开启超时线程");
				mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
				tv_start_txt.setText("停止");
				isStart = false;
				amin.start();
			} else {
				if (amin != null) {
					amin.stop();
					iv_totalprocess.clearAnimation();
				}
				iv_totalprocess.setBackgroundResource(R.drawable.btn_collect_bg0);
				mDelayHander.removeMessages(CLOSE_BLUETOOTH);
				LogUtil.info("smarhit", "移除超时线程");
				mDelayHander.removeMessages(FATSCALE_RESCAN);
				LogUtil.info("smarhit", "移除脂肪秤重连线程");
				mBluetoothLeService.scanLeDevice(false);
				LogUtil.info("smarhit", "停止扫描");
				mBluetoothLeService.close();
				LogUtil.info("smarhit", "关闭蓝牙通信  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				processDefalutStatus(); // 设置进度提示默认状态
				isStart = true;
			}
			systemTime = System.currentTimeMillis();
			iv_weight_default.setVisibility(View.VISIBLE);
			iv_weight_loading.setVisibility(View.GONE);
			iv_bloodpressure_defalut.setVisibility(View.VISIBLE);
			iv_bloodpressure_loading.setVisibility(View.GONE);
			iv_heart_defalut.setVisibility(View.VISIBLE);
			iv_heart_loading.setVisibility(View.GONE);
			iv_bloodsugar_defalut.setVisibility(View.VISIBLE);
			iv_bloodsugar_loading.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * Function:采集界面默认的状态
	 */
	private void collectHealthDefault() {
		showTitle(true);

		tv_weight.setText("0KG");
		tv_bloodpressure.setText("0mmHg");
		tv_heart_rate.setText("0BPM");
		tv_bloodsugar.setText("0Mmol/L");

		btn_bloodpressure.setVisibility(View.INVISIBLE);
		btn_bloodsugar.setVisibility(View.INVISIBLE);
		btn_heart_rate.setVisibility(View.INVISIBLE);
		btn_weight.setVisibility(View.INVISIBLE);

		iv_weight_default.setImageResource(R.drawable.ic_original);
		iv_bloodpressure_defalut.setImageResource(R.drawable.ic_original);
		iv_heart_defalut.setImageResource(R.drawable.ic_original);
		iv_bloodsugar_defalut.setImageResource(R.drawable.ic_original);

		iv_totalprocess.setBackgroundResource(R.anim.collect_process);
		tv_current_status.setText("未连接任何设备");
	}

	@Override
	public void onPause() {
//		if (wakeLock != null) {
//			wakeLock.release();
//		}
		super.onPause();
		LogUtil.info("smarhit", "-----CollectFragment onPause()");
		isRetestPause = false;
		processDefalutStatus();
		if (mBluetoothLeService != null && mBluetoothLeService.isBind == true) {
			try {
				unBindService();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("ResourceType")
	@Override
	protected void analyseData(String data) {
		LogUtil.info("smarhit", "返回的结果:" + data);
		LogUtil.info("smarhit", "CollectedDevice=" + mBluetoothLeService.mCollectedDevice + " DeviceName=" + deviceName);
		isGetData = true; // 已经获取到数据
		// 停止蓝牙重连线程
		LogUtil.info("smarhit", "移除脂肪秤重连线程");
		mDelayHander.removeMessages(FATSCALE_RESCAN);
		btn_weight.setVisibility(View.VISIBLE);
		btn_bloodpressure.setVisibility(View.VISIBLE);
		btn_heart_rate.setVisibility(View.VISIBLE);
		btn_bloodsugar.setVisibility(View.VISIBLE);

		if (mBluetoothLeService.mDevice != null
				&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
			// 停止蓝牙重连线程
			LogUtil.info("smarhit", "移除脂肪秤重连线程");
			mDelayHander.removeMessages(FATSCALE_RESCAN);
			StringBuilder sb = new StringBuilder(data);
			String str = sb.toString().replaceAll("\\s", "");
			float weight = bleManage.setReceiveResultData(str);
			if (weight != 0) {
				// 关闭扫描
				mBluetoothLeService.scanLeDevice(false);
				mBluetoothLeService.close();
				tv_weight.setText(DataFormatUtil.floatFormat(weight) + "KG");

				isRetestPause = false;
				btn_weight.setText("重新采集");
				if (isNewData) {
					if (amin != null) {
						amin.stop();
						iv_totalprocess.clearAnimation();
					}
					iv_totalprocess.setBackgroundResource(R.anim.collect_process_270_360);
					amin = (AnimationDrawable) iv_totalprocess.getBackground();
					amin.start();
					isStart = true;

					if (processDefalutStatus() == 0) {
						bean = new HealthBean();
					}
					bean.setOld_id(userbean.getOld_id());
					bean.setOld_sn(userbean.getOld_sn());
					bean.setData_id(UUID.randomUUID().toString());
					bean.setWeight(Float.parseFloat(DataFormatUtil.floatFormat(weight)));
					bean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
					bean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
					bean.setNurse_name(ConfigManager.getStringValue(mContext, ConfigManager.NURSE_NAME));

					// 还需要赋值
					healthBean = bean;
					saveCollectData(bean, true);
					if (processDefalutStatus() != 3) {
						// 重新开启蓝牙扫描，连接其他设备
						mBluetoothLeService.scanLeDevice(BLEConstants.TYPE_FATSCALE_DEVICE_NAME);
						LogUtil.info("smarhit", "脂肪秤数据传输结束，开启扫描寻找其他设备  CollectedDevice="
								+ mBluetoothLeService.mCollectedDevice + " DeviceName=" + deviceName);
						// 重开动画效果，此时按下暂停键停止扫描
						isStart = false;
						tv_current_status.setText("设备正在连接...");
						tv_start_txt.setText("停止");
						if (amin != null) {
							amin.stop();
							iv_totalprocess.clearAnimation();
						}
						iv_totalprocess.setBackgroundResource(R.anim.collect_process);
						amin = (AnimationDrawable) iv_totalprocess.getBackground();
						amin.start();
						// 一分钟后无响应断开连接
						LogUtil.info("smarhit", "开启超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
								+ " DeviceName=" + deviceName);
						mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
					} else {
						processDefalutStatus();
						healthBean = null;
					}

				} else {
					iv_weight_default.setVisibility(View.VISIBLE);
					iv_weight_default.setImageResource(R.drawable.ic_collect_done);
					if (retestAmin != null) {
						retestAmin.stop();
						iv_weight_loading.clearAnimation();
					}
					iv_weight_loading.setVisibility(View.GONE);
					if (healthBean != null) {
						healthBean.setWeight(Float.parseFloat(DataFormatUtil.floatFormat(weight)));
						healthBean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
						healthBean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
						healthBean.setNurse_name(ConfigManager.getStringValue(mContext, ConfigManager.NURSE_NAME));
						saveCollectData(healthBean, false);
					}
					LogUtil.info("smarhit", "停止扫描  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
							+ " DeviceName=" + deviceName);
					tv_current_status.setText("脂肪秤采集完成,可继续采集其他设备");
				}

			} else {
				// tv_current_status.setText("正在测量中...");
				mHandler.postDelayed(mRunnable, DELAYTTIME); // 延迟一秒重新发送数据
				LogUtil.info("smarhit", "脂肪秤返回数据有误，重新发送参数数据  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
						+ " DeviceName=" + deviceName);
				if (amin != null) {
					amin.stop();
					iv_totalprocess.clearAnimation();
				}
				iv_totalprocess.setBackgroundResource(R.anim.collect_process);
			}

		} else if (mBluetoothLeService.mDevice != null
				&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {

			// 血压
			if (data == null)
				return;
			if (data.length() < 10) {
				// 实时显示舒张压
				if (data.startsWith("32")) {
					tv_bloodpressure.setText(data.substring(2) + "/0mmHg");
				}

			} else {
				// 最后测试的结果
				if (mBluetoothLeService != null) {
					mBluetoothLeService.scanLeDevice(false);
					LogUtil.info("smarhit", "血压计数据传输结束，关闭扫描");
					mBluetoothLeService.close();
					LogUtil.info("smarhit", "血压计数据传输结束，关闭蓝牙通信  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
							+ " DeviceName=" + deviceName);
				}
				String[] arrayOfString = data.split(" ");
				int systolic = Integer.parseInt(arrayOfString[2]);
				int diastolic = Integer.parseInt(arrayOfString[4]);
				int pulserate = Integer.parseInt(arrayOfString[8]);
				System.out.println("arrayOfString=" + arrayOfString.toString());
				tv_bloodpressure.setText(systolic + "/" + diastolic + "mmHg");
				tv_heart_rate.setText(pulserate + "BPM");
				isRetestPause = false;
				/* 设置button的显示 */
				if (retestAmin != null) {
					retestAmin.stop();
					iv_bloodpressure_loading.clearAnimation();
				}
				iv_bloodpressure_defalut.setVisibility(View.VISIBLE);
				iv_bloodpressure_loading.setVisibility(View.GONE);
				iv_heart_defalut.setVisibility(View.VISIBLE);
				iv_heart_loading.setVisibility(View.GONE);
				iv_bloodpressure_defalut.setImageResource(R.drawable.ic_collect_done);
				iv_heart_defalut.setImageResource(R.drawable.ic_collect_done);
				btn_bloodpressure.setVisibility(View.VISIBLE);
				btn_heart_rate.setVisibility(View.VISIBLE);
				btn_bloodpressure.setText("重新采集");
				btn_heart_rate.setText("重新采集");

				if (isNewData) {
					if (amin != null) {
						amin.stop();
						iv_totalprocess.clearAnimation();
					}
					iv_totalprocess.setBackgroundResource(R.anim.collect_process);
					amin = (AnimationDrawable) iv_totalprocess.getBackground();
					amin.start();
					isStart = true;
					if (processDefalutStatus() == 0) {
						bean = new HealthBean();
					}
					bean.setOld_id(userbean.getOld_id());
					bean.setOld_sn(userbean.getOld_sn());
					bean.setData_id(UUID.randomUUID().toString());
					bean.setSystolic_pressure(systolic);
					bean.setDiastolic_pressure(diastolic);
					bean.setHeart_rate(pulserate);
					bean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
					bean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
					bean.setNurse_name(ConfigManager.getStringValue(mContext, ConfigManager.NURSE_NAME));
					healthBean = bean;
					saveCollectData(bean, true);

					if (processDefalutStatus() != 3) {
						// 重新开启蓝牙扫描，连接其他设备
						mBluetoothLeService.scanLeDevice(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME);
						LogUtil.info("smarhit", "血压计数据传输结束，开启扫描寻找其他设备  CollectedDevice="
								+ mBluetoothLeService.mCollectedDevice + " DeviceName=" + deviceName);

						// 重开动画效果，此时按下停止键停止扫描
						isStart = false;
						tv_current_status.setText("设备正在连接...");
						tv_start_txt.setText("停止");
						if (amin != null) {
							amin.stop();
							iv_totalprocess.clearAnimation();
						}
						iv_totalprocess.setBackgroundResource(R.anim.collect_process);
						amin = (AnimationDrawable) iv_totalprocess.getBackground();
						amin.start();
						// 60秒后无响应断开连接
						mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
					} else {
						processDefalutStatus();
						healthBean = null;
					}
				} else {
					if (healthBean != null) {
						healthBean.setSystolic_pressure(systolic);
						healthBean.setDiastolic_pressure(diastolic);
						healthBean.setHeart_rate(pulserate);
						healthBean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
						healthBean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
						healthBean.setNurse_name(ConfigManager.getStringValue(mContext, ConfigManager.NURSE_NAME));
						saveCollectData(healthBean, false);
					}
					tv_current_status.setText("血压计采集完成");
					LogUtil.info("smarhit", "停止扫描  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
							+ " DeviceName=" + deviceName);
				}
			}

		} else if (mBluetoothLeService.mDevice != null
				&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
			// 血糖
			if (data == null)
				return;

			String[] results = data.split(" ");

			if (results.length > 5) {
				if (Integer.parseInt(results[0]) == 0X53 && Integer.parseInt(results[1]) == 0X4E
						&& Integer.parseInt(results[3]) == 0X00 && Integer.parseInt(results[4]) == 0X04) {
					int order = Integer.parseInt(results[5]);
					switch (order) {
						case 0X01:// 连接蓝牙测试
							// 设置校验码
							mDelayHander.sendEmptyMessageDelayed(SUGAR_VERIFYCODE, DELAYTTIME);
							break;
						case 0X02:
							int error1 = Integer.parseInt(results[6]);
							int error2 = Integer.parseInt(results[7]);
							if (error1 == 0X00 && error2 == 0X01) {
								// E-1错误
								PromptManager.showToast(mContext, false, "设备出现异常 E-1错误");
							} else if (error1 == 0X00 && error2 == 0X02) {
								// E-2错误
								PromptManager.showToast(mContext, false, "设备出现异常 E-2错误");
							} else if (error1 == 0X00 && error2 == 0X03) {
								// E-3错误
								PromptManager.showToast(mContext, false, "设备出现异常 E-3错误");
							} else if (error1 == 0X01 && error2 == 0X01) {
								// HI错误
								PromptManager.showToast(mContext, false, "设备出现异常 HI错误");
							} else if (error1 == 0X01 && error2 == 0X02) {
								// LO错误
								PromptManager.showToast(mContext, false, "设备出现异常 LO错误");
							}
							break;
						case 0X03:// 滴血闪烁
							PromptManager.showToast(mContext, false, "设备滴血闪烁！");
							mDelayHander.sendEmptyMessageDelayed(SUGAR_DATA, DELAYTTIME);
							// 获取数据
							// write(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC,
							// BloodSugarDataUtil.getData());
							break;
						case 0X04:// 获取数据
							// 关闭蓝牙通信
							if (mBluetoothLeService != null) {
								mBluetoothLeService.scanLeDevice(false);
								mBluetoothLeService.close();
							}
							float bloodsugar = (Float.parseFloat(results[12]) / 10);
							tv_bloodsugar.setText(bloodsugar + "Mmol/L");

							if (retestAmin != null) {
								retestAmin.stop();
								iv_bloodsugar_loading.clearAnimation();
							}
							iv_bloodsugar_defalut.setVisibility(View.VISIBLE);
							iv_bloodsugar_loading.setVisibility(View.GONE);
							iv_bloodsugar_defalut.setImageResource(R.drawable.ic_collect_done);
							btn_bloodsugar.setText("重新采集");
							isRetestPause = false;
							if (isNewData) {
								if (amin != null) {
									amin.stop();
									iv_totalprocess.clearAnimation();
								}
								iv_totalprocess.setBackgroundResource(R.anim.collect_process_270_360);
								amin = (AnimationDrawable) iv_totalprocess.getBackground();
								amin.start();
								isStart = true;

								if (processDefalutStatus() == 0) {
									bean = new HealthBean();
								}
								bean.setOld_id(userbean.getOld_id());
								bean.setOld_sn(userbean.getOld_sn());
								bean.setData_id(UUID.randomUUID().toString());
								bean.setBlood_sugar(bloodsugar);
								bean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
								bean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
								bean.setNurse_name(ConfigManager.getStringValue(mContext, ConfigManager.NURSE_NAME));
								healthBean = bean;
								saveCollectData(bean, true);

								if (processDefalutStatus() != 3) {
									// 重新开启蓝牙扫描，连接其他设备
									mBluetoothLeService.scanLeDevice(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME);
									LogUtil.info("smarhit", "血糖仪数据传输结束，开启扫描寻找其他设备  CollectedDevice="
											+ mBluetoothLeService.mCollectedDevice + " DeviceName=" + deviceName);
									// 重开动画效果，此时按下停止键停止扫描
									isStart = false;
									tv_current_status.setText("设备正在连接...");
									tv_start_txt.setText("停止");
									if (amin != null) {
										amin.stop();
										iv_totalprocess.clearAnimation();
									}
									iv_totalprocess.setBackgroundResource(R.anim.collect_process);
									amin = (AnimationDrawable) iv_totalprocess.getBackground();
									amin.start();
									// 一分钟后无响应断开连接
									mDelayHander.sendEmptyMessageDelayed(CLOSE_BLUETOOTH, 60 * 1000);
								} else {
									processDefalutStatus();
									healthBean = null;
								}

							} else {
								if (healthBean != null) {
									healthBean.setBlood_sugar(bloodsugar);
									healthBean.setCollect_time(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
									healthBean.setNurse_id(ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID));
									healthBean.setNurse_name(ConfigManager.getStringValue(mContext,
											ConfigManager.NURSE_NAME));
									saveCollectData(healthBean, false);
								}
								tv_current_status.setText("血糖仪采集完成");
								LogUtil.info("smarhit", "停止扫描  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
										+ " DeviceName=" + deviceName);
							}
							break;
						case 0X05:
							// PromptManager.showToast(mContext, "获取历史数据");
							break;
						case 0X07:// 设置时间返回
							// mDelayHander.sendEmptyMessageDelayed(SUGAR_DATA,
							// DELAYTTIME);
							break;
						case 0X09:// 设置校验码返回
							if (Integer.parseInt(results[6]) == 0X00) {
								// PromptManager.showToast(getApplicationContext(),
								// "校验码设置成功!");
								// mDelayHander.sendEmptyMessageDelayed(SUGAR_TIME,
								// DELAYTTIME);
								mDelayHander.sendEmptyMessageDelayed(SUGAR_DATA, DELAYTTIME);
							} else {
								// PromptManager.showToast(getApplicationContext(),
								// "校验码设置失败!");
							}
							break;
						case 0X0A:// 开始测试血糖返回
							PromptManager.showToast(mContext, true, "设备开始测试血糖");
							break;
						case 0X0B:// 设备关机
							PromptManager.showToast(mContext, true, "血糖仪设备已关机!");

							break;
						case 0X0C:// 设备蓝牙
							PromptManager.showToast(mContext, true, "血糖仪关闭设备蓝牙!");
							break;
						default:
							break;
					}
				} else {
					PromptManager.showToast(mContext, false, "设备发的错误命令");
				}
				btn_bloodsugar.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void sendDataToDevice() {
		if (mBluetoothLeService.mDevice != null) {
			switch (mBluetoothLeService.mDevice.getName()) {
			case BLEConstants.TYPE_FATSCALE_DEVICE_NAME:
				deviceName = "脂肪秤";
				// 解决脂肪称长时间获取不了数据的情况，10秒后进行重新连接
				mDelayHander.removeMessages(FATSCALE_RESCAN);
				mDelayHander.sendEmptyMessageDelayed(FATSCALE_RESCAN, 10 * 1000);
				LogUtil.info("lhw","sendDataToDevice 10秒后重连脂肪秤");
				break;
			case BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME:
				deviceName = "血压计";
				break;
			case BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME:
				deviceName = "血糖仪";
				break;
			}
			tv_current_status.setText("正在测量" + deviceName + ",请耐心等待...");
		}
		mHandler.postDelayed(mRunnable, DELAYTTIME);
		LogUtil.info("smarhit", "找到设备，移除超时线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice + " DeviceName="
				+ deviceName);
		mDelayHander.removeMessages(CLOSE_BLUETOOTH); // 找到设备，移除超时判定线程
	}

	private Handler mDelayHander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUGAR_VERIFYCODE:
				write(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC, BloodSugarDataUtil.updateVerifyCode(23));
				break;

			case SUGAR_TIME:
				// 设置时间
				write(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC,
						BloodSugarDataUtil.setDatetime(System.currentTimeMillis()));
				break;

			case SUGAR_DATA:
				write(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC, BloodSugarDataUtil.getData());
				break;

			case FATSCALE_RESCAN:// 重新扫描数据
				LogUtil.info("lhw","重连脂肪秤线程开始执行");
				if (mBluetoothLeService.mDevice != null
						&& mBluetoothLeService.mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
					LogUtil.info("smarhit", "运行脂肪秤重连线程");
					LogUtil.info("lhw","和脂肪秤已经配对");
					if (!isGetData) { // 没有得到数据
						LogUtil.info("lhw","没有得到脂肪秤数据");
						mBluetoothLeService.scanLeDevice(false);
						mBluetoothLeService.close();
//						mBluetoothAdapter.disable();
//						if (!mBluetoothAdapter.isEnabled()) {
//							mBluetoothAdapter.enable();
//						}
//						try {
//							Thread.sleep(10* 1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						if (mBluetoothLeService.mDevice != null) {
							mBluetoothLeService.scanLeDevice(true);
							LogUtil.info(BluetoothLeService.class, "没有收到脂肪秤数据， 重新发送连接请求!");
						}

					} else { // 已经得到了数据
						mBluetoothLeService.scanLeDevice(false);
						mBluetoothLeService.close();
						isGetData = false;
					}
				}
				break;

			case CLOSE_BLUETOOTH: // 超时关闭蓝牙扫描
				if (mBluetoothLeService != null) {
					LogUtil.info("smarhit", "运行超时关闭线程  CollectedDevice=" + mBluetoothLeService.mCollectedDevice
							+ " DeviceName=" + deviceName);
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
					tv_current_status.setText("连接超时，未扫描到设备");
				}
			default:
				break;
			}
		};
	};

	@Override
	public void goCollection(HealthBean bean) {
		layout_title.setVisibility(View.VISIBLE);
		fl_collect.setVisibility(View.VISIBLE);
		layout_query.setVisibility(View.GONE);
		layout_record.setVisibility(View.GONE);
		layout_mask.setVisibility(View.GONE);
		healthBean = bean;
		userbean = bean.getUser();
		tv_name.setText(userbean.getName());
		tv_old_sn.setText(userbean.getOld_sn());
		tv_age.setText(userbean.getAge() + "岁");
		tv_gender.setText(userbean.getGender());
		if (userbean.getBed() != null) {
			tv_location.setText(userbean.getBed());
		}

		if (healthBean.getWeight() != 0) {
			tv_weight.setText(healthBean.getWeight() + "KG");
			iv_weight_default.setImageResource(R.drawable.ic_collect_done);
			btn_weight.setVisibility(View.VISIBLE);
		} else {
			tv_weight.setText("0KG");
		}

		if (healthBean.getSystolic_pressure() != 0) {
			tv_bloodpressure.setText(healthBean.getSystolic_pressure() + "/" + healthBean.getDiastolic_pressure()
					+ "mmHg");
			tv_heart_rate.setText(healthBean.getHeart_rate() + "BPM");
			iv_bloodpressure_defalut.setImageResource(R.drawable.ic_collect_done);
			iv_heart_defalut.setImageResource(R.drawable.ic_collect_done);
			btn_bloodpressure.setVisibility(View.VISIBLE);
			btn_heart_rate.setVisibility(View.VISIBLE);
		} else {
			tv_bloodpressure.setText("0mmHg");
			tv_heart_rate.setText("0BPM");
		}

		if (healthBean.getBlood_sugar() != 0) {
			tv_bloodsugar.setText(healthBean.getBlood_sugar() + "Mmol/L");
			iv_bloodsugar_defalut.setImageResource(R.drawable.ic_collect_done);
			btn_bloodsugar.setVisibility(View.VISIBLE);
		} else {
			tv_bloodsugar.setText("0Mmol/L");
		}

		showTitle(true);
	}

	private long intervaltime;

	private void saveCollectData(HealthBean bean, boolean isAdd) {
		if (System.currentTimeMillis() - intervaltime > 5 * 1000) {
			DBHealth db = DBHealth.getInstance(mContext);
			db.open();
			if (isAdd) {
				if (selected_health == null) {
					selected_health = new ArrayList<HealthBean>();
				}
				selected_health.add(bean);
				db.insert(bean);
			} else {
				bean.setIsUpdate(0);
				db.updateHealthByUuid(bean);
			}
			db.close();

			updateCollectData(bean);
			intervaltime = System.currentTimeMillis();
		}

	}

	@Override
	public void clear() {

	}

	private void updateCollectData(final HealthBean bean) {

		bean.setUser(null);
		List<HealthBean> list = new ArrayList<HealthBean>();
		list.add(bean);
		String json = JSONArray.toJSONString(list);
		System.out.println("uploadjson" + json);
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.updateCollectData(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN), json,
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						// TODO Auto-generated method stub
						if (isSuccess) {
							BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
							if (baseBean.getStatus() == 0) {
								DBHealth dbHealth = DBHealth.getInstance(getActivity());
								dbHealth.open();
								bean.setIsUpdate(1);
								boolean isUpdate = dbHealth.updateHealthUpdateStatus(bean);
								dbHealth.close();
								if (isUpdate) {
									PromptManager.showToast(getActivity(), true, "采集数据已上传成功!");
								} else {
									PromptManager.showToast(getActivity(), false, "采集数据已上传失败!");
								}

							} else {
								PromptManager.showToast(getActivity(), false, baseBean.getInfo());
							}
						} else {
							PromptManager.showToast(getActivity(), false, errors);
						}
					}
				});
	}

	/**
	 * 
	 * Function:进度条默认状态
	 */
	private int processDefalutStatus() {
		if (amin != null) {
			amin.stop();
			iv_totalprocess.clearAnimation();
		}
		tv_current_status.setText("未连接任何设备");
		tv_start_txt.setText("开始");
		iv_totalprocess.setBackgroundResource(R.anim.collect_process);
		iv_totalprocess.setVisibility(View.GONE);
		iv_totalprocess.setVisibility(View.VISIBLE);
		amin = (AnimationDrawable) iv_totalprocess.getBackground();
		amin.stop();
		iv_totalprocess.clearAnimation();

		iv_weight_default.setVisibility(View.VISIBLE);
		iv_weight_loading.setVisibility(View.GONE);
		iv_bloodpressure_defalut.setVisibility(View.VISIBLE);
		iv_bloodpressure_loading.setVisibility(View.GONE);
		iv_heart_defalut.setVisibility(View.VISIBLE);
		iv_heart_loading.setVisibility(View.GONE);
		iv_bloodsugar_defalut.setVisibility(View.VISIBLE);
		iv_bloodsugar_loading.setVisibility(View.GONE);

		iv_weight_default.setImageResource(R.drawable.ic_original);
		iv_bloodpressure_defalut.setImageResource(R.drawable.ic_original);
		iv_heart_defalut.setImageResource(R.drawable.ic_original);
		iv_bloodsugar_defalut.setImageResource(R.drawable.ic_original);
		collectCount = 0;
		// 采集页面，选中某老人后，对其上一次采集的时间进行判断，如超过24小时，算新一次采集、界面不显示上一次采集数据
		if (healthBean != null
				&& DateUtil.DateToString(new Date(), "yyyy-MM-dd").equals(
						DateUtil.DateToString(DateUtil.StringTolong(healthBean.getCollect_time()), "yyyy-MM-dd"))) {
			btn_weight.setVisibility(View.VISIBLE);
			btn_bloodpressure.setVisibility(View.VISIBLE);
			btn_heart_rate.setVisibility(View.VISIBLE);
			btn_bloodsugar.setVisibility(View.VISIBLE);
			if (healthBean.getWeight() != 0) {
				collectCount = collectCount + 1;
				System.out.println("collectCount = " + collectCount);
				tv_weight.setText(healthBean.getWeight() + "KG");
				iv_weight_default.setImageResource(R.drawable.ic_collect_done);
				btn_weight.setText("重新采集");
			} else {
				btn_weight.setText("采集");
			}
			if (healthBean.getSystolic_pressure() != 0) {
				collectCount = collectCount + 1;
				System.out.println("collectCount = " + collectCount);
				tv_bloodpressure.setText(healthBean.getSystolic_pressure() + "/" + healthBean.getDiastolic_pressure()
						+ "mmHg");
				tv_heart_rate.setText(healthBean.getHeart_rate() + "BPM");
				iv_bloodpressure_defalut.setImageResource(R.drawable.ic_collect_done);
				iv_heart_defalut.setImageResource(R.drawable.ic_collect_done);
				btn_bloodpressure.setText("重新采集");
				btn_heart_rate.setText("重新采集");
			} else {
				btn_bloodpressure.setText("采集");
				btn_heart_rate.setText("采集");
			}
			if (healthBean.getBlood_sugar() != 0) {
				collectCount = collectCount + 1;
				System.out.println("collectCount = " + collectCount);
				tv_bloodsugar.setText(healthBean.getBlood_sugar() + "Mmol/L");
				iv_bloodsugar_defalut.setImageResource(R.drawable.ic_collect_done);
				btn_bloodsugar.setText("重新采集");
			} else {
				btn_bloodsugar.setText("采集");
			}
			if (collectCount == 3) { // 设备采集完一轮
				if (amin != null) {
					amin.stop();
					iv_totalprocess.clearAnimation();
				}
				System.out.println("进行新一轮采集");
				iv_totalprocess.setBackgroundResource(R.drawable.btn_collect_bg12);
				tv_current_status.setText("点击进行新一轮采集");
				tv_start_txt.setText("完成");
			}
		}
		return collectCount;
	}

	private void resetService() {
		mBluetoothLeService.mDevice = null;
		mBluetoothLeService.mCollectedDevice = "";
		mBluetoothLeService.mDeviceName = "";
	}

	@Override
	protected void notFoundDevice() {
		// TODO Auto-generated method stub
		PromptManager.showToast(mContext, false, "没有找到硬件设备， 请重试!");
		processDefalutStatus();
		// 关闭蓝牙通信
		if (mBluetoothLeService != null) {
			mBluetoothLeService.scanLeDevice(false);
			mBluetoothLeService.close();
		}
	}

	@Override
	protected void gattClose() {
		// TODO Auto-generated method stub
		if (isGetData = false) {
			PromptManager.showToast(mContext, false, "数据异常，请重新测试!");
			processDefalutStatus();
			// 关闭蓝牙通信
			if (mBluetoothLeService != null) {
				mBluetoothLeService.scanLeDevice(false);
				mBluetoothLeService.close();
			}
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		LogUtil.info("rice", "Collect onStop");
	}
	
	@Override
	public void onStart() {
		LogUtil.info("rice", "Collect onStart");
		super.onStart();
	}
	
	@Override
	public void onDestroyView() {
		LogUtil.info("rice", "Collect onDestroyView");
		super.onDestroyView();
	}

}
