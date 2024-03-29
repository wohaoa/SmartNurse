package com.magicare.smartnurse.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.DynamicDisplayAdapter;
import com.magicare.smartnurse.adapter.WarningDealWithAdapter;
import com.magicare.smartnurse.adapter.WarningDealWithAdapter.FeedListener;
import com.magicare.smartnurse.adapter.WarningDealWithAdapter.ResolveListener;
import com.magicare.smartnurse.adapter.WarningDealWithAdapter.UserDetailListener;
import com.magicare.smartnurse.bean.ActiveBean;
import com.magicare.smartnurse.bean.AreaActiveBean;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.FeedBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.bean.WarningComparator;
import com.magicare.smartnurse.database.dao.DBFeed;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.logic.SynthesizerListenerImpl;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

/**
 * 
 * @author scott
 * 
 *         Function:监控界面
 */
@SuppressLint("HandlerLeak")
public class MonitorFragment extends Fragment implements ResolveListener, FeedListener, UserDetailListener {

	public static final String JPUSH_MESSAGE_ACTION = "com.magicare.smartnurse.jpush.message.action";
	/** Handler what */
	public static final int REFRESH_WARNNING = 0X01;
	public static final int REFRESH_USERSTATUS = 0X02;
	public static final int HIDE_SCREEN_ICON = 0X03;
	/** 刷新数据间隔时间 */
	private long mWarningRefreshTime = 10 * 1000;
	private long mUserStatusRefreshTime = 5 * 1000;
	private long mUserStatusRefreshTime1 = 3 * 60 * 1000;

	private View mView;
	private Context mContext;
	private FrameLayout fl_main;
	private ListView lv_warning;
	private WarningDealWithAdapter mWarningAdapter;
	private GridView gv_dynamic; // 用户头像的girdview
	private DynamicDisplayAdapter dynamicAdapter;
	private TextView tv_dynamic_title;
	private ImageView iv_fullscreen;
	private String IMEI;
	private boolean iswakeLock = true;// 是否常亮
	private WakeLock wakeLock;

	/* 警告信息 */
	private List<WarningBean> list_warning = new ArrayList<WarningBean>();
	// 警告排序
	private WarningComparator mWarningComparator;

	/* 老人信息 */
	private List<UserBean> list_active = new ArrayList<UserBean>();

	private JpushReceiver mReceiver;

	// 是否处于全屏
	private boolean isFullScreen = false;

	/******************************************** 讯飞语音部分 *****************/
	// 语音合成对象
	private SpeechSynthesizer mTts;
	// 默认发音人
	private String voicer = "xiaoli";
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	/******************************************** 讯飞语音部分 *****************/

	/**
	 * 
	 * @author scott
	 * 
	 *         Function:控制界面的全屏控制
	 */
	public interface IControlMenu {
		void menu(boolean isShow);
	}

	private IControlMenu mControlmenu;

	public void setOnControlMenuListener(IControlMenu controlMenu) {
		this.mControlmenu = controlMenu;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mView = inflater.inflate(R.layout.fragment_monitor, container, false);
		this.mContext = getActivity();
		mWarningComparator = new WarningComparator();
		initview();
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		isFullScreen = true;
		iv_fullscreen.setImageResource(R.drawable.btn_fullscreen_exit);
		iv_fullscreen.setVisibility(View.VISIBLE);
		mHandler.removeMessages(HIDE_SCREEN_ICON);
		mHandler.sendEmptyMessageDelayed(HIDE_SCREEN_ICON, 10 * 1000);
		// 刷新用户状态信息
		mHandler.removeMessages(REFRESH_USERSTATUS);
		mHandler.sendEmptyMessageDelayed(REFRESH_USERSTATUS, 1 * 1000);

		if (ConfigManager.getStringValue(getActivity().getApplicationContext(), ConfigManager.IMEI) != null
				&& !ConfigManager.getStringValue(getActivity().getApplicationContext(), ConfigManager.IMEI).equals("")) {
			IMEI = ConfigManager.getStringValue(getActivity().getApplicationContext(), ConfigManager.IMEI);
		} else {
			TelephonyManager TelephonyMgr = (TelephonyManager) getActivity().getSystemService(
					getActivity().TELEPHONY_SERVICE);
			IMEI = TelephonyMgr.getDeviceId();
			ConfigManager.setStringValue(getActivity().getApplicationContext(), ConfigManager.IMEI, IMEI);
		}
		LogUtil.info("rice", "Monitor onResume");
		LogUtil.info("rice", "IMEI =" + IMEI);

//		mHandler.removeMessages(REFRESH_CHART);
//		mHandler.sendEmptyMessageDelayed(REFRESH_CHART, 1 * 1000);

	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止更新
		mHandler.removeMessages(REFRESH_WARNNING);
		mHandler.removeMessages(REFRESH_USERSTATUS);
		mHandler.removeMessages(HIDE_SCREEN_ICON);
		LogUtil.info("rice", "Monitor onPause");

	}

	/**
	 * 
	 * Function:获取未处理和未反馈的报警信息
	 */
	public void getWarning(final Context context) {
		// 隐藏状态栏
		WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
		lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		((Activity) context).getWindow().setAttributes(lp);
		((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (dynamicAdapter != null) {
			dynamicAdapter.notifyDataSetChanged();
		}
		HttpClientUtil client = HttpClientUtil.getInstance();

		// 拉取本区域未处理的告警信息(根据区域id)
		client.getAlarmInfoByStatus(context, ConfigManager.getStringValue(context, Constants.ACCESS_TOKEN),
				ConfigManager.getIntValue(context, ConfigManager.AREAID), "2", new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									List<WarningBean> list = JSON.parseArray(baseBean.getData(), WarningBean.class);
									list_warning.clear();
									list_warning.addAll(list);
									// 告警排序
									Collections.sort(list_warning, mWarningComparator);
									changeBg(); // 改变背景色
									if (mWarningAdapter != null) {
										mWarningAdapter.notifyDataSetChanged();
									}
									if (list_active.size() == 0) {
										for (WarningBean bean : list) {
											refreshUserInfo(bean, false); // 刷新用户状态
										}
									} else {
										/* 同步老人状态 */
										List<WarningBean> warnings = null;
										for (int i = 0; i < list_active.size(); i++) {
											UserBean userbean = list_active.get(i);
											userbean.getList_warning().clear();
											warnings = new ArrayList<WarningBean>();
											for (int j = 0; j < list_warning.size(); j++) {
												WarningBean warningBean = list_warning.get(j);
												if (userbean.getOld_id() == warningBean.getOld_id()) {
													warnings.add(warningBean);
													// list_active.get(i).setRefreshTime(warningBean.getAlarm_time());
													list_active.get(i).setCurrentLocation(
															warningBean.getStation_detail());
													list_active.get(i).setCurrentStatus(warningBean.getActive_mod());
												}
											}
											userbean.getList_warning().addAll(warnings);
										}
									}
									dynamicAdapter.notifyDataSetChanged();

									setRegionInfo();

									// 间隔30秒刷新一次报警信息
									mHandler.removeMessages(REFRESH_WARNNING);
									mHandler.sendEmptyMessageDelayed(REFRESH_WARNNING, 10 * 1000);

								} else {
									PromptManager.showToast(context, false, errors);
								}
							}

						} else {
							PromptManager.showToast(getActivity(), false, errors);
						}

					}
				});
	}

	/**
	 * 
	 * Function:更新老人状态信息
	 * 
	 */
	private void getUserStatusInfo() { // 可筛选出所有在该区域有警告的老人
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getUserStatusInfo(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				ConfigManager.getIntValue(mContext, ConfigManager.AREAID), new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(mContext, false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 清空原来的数据，方便一次性插入
									list_active.clear();
									// 解析数据
									List<UserBean> list = JSON.parseArray(baseBean.getData(), UserBean.class);
									// 循环判断哪些老人有报警信息
									for (int i = 0; i < list.size(); i++) {
										list.get(i).getList_warning().clear();
										for (int j = 0; j < list_warning.size(); j++) {
											if (list.get(i).getOld_id() == list_warning.get(j).getOld_id()) {
												list.get(i).getList_warning().add(list_warning.get(j));
											}
										}
									}
									list_active.addAll(list);
									setRegionInfo();
									dynamicAdapter.notifyDataSetChanged();
								} else {
									PromptManager.showToast(mContext, false, errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});

	}

	private void initview() {
		fl_main = (FrameLayout) mView.findViewById(R.id.fl_main);
		lv_warning = (ListView) mView.findViewById(R.id.lv_warning);

		// 控制全屏按钮的显示和消失
		lv_warning.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv_fullscreen.setVisibility(View.VISIBLE);
					mHandler.removeMessages(HIDE_SCREEN_ICON);
					mHandler.sendEmptyMessageDelayed(HIDE_SCREEN_ICON, 10000);
				}
				return false;
			}
		});
		// girdview头像列表
		gv_dynamic = (GridView) mView.findViewById(R.id.gv_dynamic); //
		if (dynamicAdapter == null) { // 老人头像列表的adapter
			dynamicAdapter = new DynamicDisplayAdapter(mContext, list_active);
			dynamicAdapter.exisitWarning(list_warning.size() != 0); // 设置是否存在告警信息
			gv_dynamic.setAdapter(dynamicAdapter);
		} else {
			dynamicAdapter.notifyDataSetChanged();
		}
		// 控制全屏按钮的显示和消失
		gv_dynamic.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv_fullscreen.setVisibility(View.VISIBLE);
					mHandler.removeMessages(HIDE_SCREEN_ICON);
					mHandler.sendEmptyMessageDelayed(HIDE_SCREEN_ICON, 10000);
				}
				return false;
			}
		});

		tv_dynamic_title = (TextView) mView.findViewById(R.id.tv_dynamic_title);
		iv_fullscreen = (ImageView) mView.findViewById(R.id.iv_fullscreen); // 全屏按钮
		iv_fullscreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mControlmenu != null) {
					isFullScreen = !isFullScreen;
					if (isFullScreen) {
						iv_fullscreen.setImageResource(R.drawable.btn_fullscreen_exit);
					} else {
						iv_fullscreen.setImageResource(R.drawable.btn_fullscreen);
					}
					mControlmenu.menu(isFullScreen);
				}

			}
		});

		// 点击头像列表的子项，并传入userbean和fromview的参数
		gv_dynamic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), UserDetailActivity.class);
				intent.putExtra("userbean", list_active.get(position));
				intent.putExtra("fromview", "monitorview");
				startActivity(intent);
			}
		});
		// 告警列表的adapter
		mWarningAdapter = new WarningDealWithAdapter(mContext, list_warning, true);
		mWarningAdapter.setOnResolveListener(MonitorFragment.this);
		mWarningAdapter.setOnFeedListener(MonitorFragment.this);
		mWarningAdapter.setOnUserDetailListener(MonitorFragment.this);
		lv_warning.setAdapter(mWarningAdapter);

		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(JPUSH_MESSAGE_ACTION);
		mReceiver = new JpushReceiver();
		mContext.registerReceiver(mReceiver, filter);

		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
			@Override
			public void onInit(int code) {
				if (code != ErrorCode.SUCCESS) {
					PromptManager.showToast(mContext, true, "初始化语音成功!");
				}
			}
		});
		setRegionInfo();
	}

	/**
	 * 
	 * Function:监控界面的返回键处理
	 */
	public void back() {
		isFullScreen = false;
		iv_fullscreen.setImageResource(R.drawable.btn_fullscreen);
		iv_fullscreen.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext.unregisterReceiver(mReceiver);
		LogUtil.info("rice", "Monitor onDestroy");
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_WARNNING:// 刷新报警信息
				// refreshWarning();
				getWarning(mContext);
				// 间隔30秒刷新一次报警信息
				mHandler.removeMessages(REFRESH_WARNNING);
				mHandler.sendEmptyMessageDelayed(REFRESH_WARNNING, mWarningRefreshTime);
				break;

			case REFRESH_USERSTATUS:// 刷新用户状态信息
				LogUtil.info("smarhit", "----------------更新了用户状态");
				// 更新用户当前状态
				getUserStatusInfo();
				// setRegionInfo();
				mHandler.removeMessages(REFRESH_USERSTATUS);
				mHandler.sendEmptyMessageDelayed(REFRESH_USERSTATUS, mUserStatusRefreshTime);
				break;

			case HIDE_SCREEN_ICON:
				iv_fullscreen.setVisibility(View.GONE);
				if (mControlmenu != null) {
					mControlmenu.menu(true);
				}
				break;

			default:
				break;
			}

		};

	};

	/**
	 * 
	 * Function:更新警告信息
	 */
	private void refreshWarning() {
		StringBuilder sb_warning_id = new StringBuilder("[");
		for (int i = 0; i < list_warning.size(); i++) {
			if (list_warning.get(i).isOutside()) {
				sb_warning_id.append("\"" + list_warning.get(i).getAlarm_id() + "\",");
			}
		}

		String warning_id = sb_warning_id.toString();
		String warning_ids = warning_id.substring(0, warning_id.length() - 1) + "]";
		HttpClientUtil client = HttpClientUtil.getInstance();
		if (warning_ids.length() > 10) {
			client.getOutsideWarning(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
					warning_ids, new IOperationResult() {

						@Override
						public void operationResult(boolean isSuccess, String json, String errors) {
							if (isSuccess) {
								if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
									PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
								} else {
									LogUtil.info("smarhit", "更新外区域的警告json=" + json);

									BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
									if (baseBean.getStatus() == 0) {
										List<WarningBean> list = JSON.parseArray(baseBean.getData(), WarningBean.class);
										for (int i = 0; i < list_warning.size(); i++) {
											for (int j = 0; j < list.size(); j++) {
												if (list.get(j).getAlarm_status() == 1) {
													if (list_warning.get(i).getAlarm_id()
															.equals(list.get(j).getAlarm_id())) {
														list_warning.remove(i);
													}
												}
											}
										}
										changeBg();
										if (mWarningAdapter != null) {
											mWarningAdapter.notifyDataSetChanged();
										}

									} else {
										PromptManager.showToast(mContext, false, errors);
									}
								}
							} else {
								PromptManager.showToast(mContext, false, errors);
							}

						}
					});
		}
		LogUtil.info("smarhit", "refersh更新了警告");
	}

	private String feed_reason;
	private FeedBean selectedFeed = null;
	private List<FeedBean> list_feed;

	class FeedBackDialogClick implements OnClickListener {

		private List<View> views;
		private int selectId;

		public FeedBackDialogClick(List<View> views) {
			this.views = views;
			this.selectId = -1;
		}

		public int getSelectId() {
			return selectId;
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			int id = view.getId(); // 与list_feed中的id对应
			if (selectId >= 0 && selectId < list_feed.size()) {
				Button button = (Button) views.get(selectId);
				button.setBackgroundResource(R.drawable.btn_feedback_message_normal);
				button.setTextColor(Color.BLACK); // 未选中时字体颜色变成黑色
			}
			if (id < (list_feed.size() - 1)) {
				Button button = (Button) view;
				button.setBackgroundResource(R.drawable.btn_feedback_message_pressed);
				button.setTextColor(Color.WHITE); // 选中时文字颜色变成白色
				EditText editText = (EditText) views.get(list_feed.size());
				editText.setVisibility(View.GONE);
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				View parentView = (View) views.get(list_feed.size() - 1).getParent();
				parentView.setVisibility(View.VISIBLE);
				parentView = (View) views.get(list_feed.size() - 3).getParent();
				parentView.setVisibility(View.VISIBLE);
				selectId = id;
			} else if (id == (list_feed.size() - 1)) { // 其他（需文字说明）
				EditText editText = (EditText) views.get(list_feed.size());
				editText.setVisibility(View.VISIBLE);
				editText.requestFocus();
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editText, 0);
				View parentView = (View) views.get(list_feed.size() - 1).getParent(); // 隐藏最后两排
				parentView.setVisibility(View.GONE);
				if (list_feed.size() > 4) { // 由于数量比较少，只隐藏一行
					parentView = (View) views.get(list_feed.size() - 3).getParent();
					parentView.setVisibility(View.GONE);
				}
				selectId = id;
			}
		}

	}

	/**
	 * 
	 * Function:反馈对话框
	 * 
	 * @param bean
	 *            ：需反馈的警告信息
	 */
	private void showFeedBackDialog(final WarningBean bean) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_feedback, null);

		DBFeed db = DBFeed.getInstance(mContext);
		db.open();
		list_feed = db.getAllFeedInfo();
		db.close();

		// 如果服务器数据有误，比如是双数，截掉最后一个
		if (list_feed != null && (list_feed.size() % 2 == 0)) {
			list_feed.remove(list_feed.size() - 1);
		}

		list_feed.add(new FeedBean(0, "其他(需文字说明)"));

		// 整个屏幕的80%左右的宽高
		int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.8);
		int dialogWidth = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8);

		if (list_feed.size() <= 4) { // 数量比较少的情况，只占60%
			dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6);
		}

		final Dialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.show();
		dialog.getWindow().setLayout(dialogWidth, dialogHeight);
		view.setBackgroundColor(Color.parseColor("#ffffff"));
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);

		LinearLayout container = (LinearLayout) view.findViewById(R.id.ll_container);

		List<View> clickViews = new ArrayList<View>();
		final FeedBackDialogClick clickListener = new FeedBackDialogClick(clickViews);
		int boundLeftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_bound_leftmargin); // 控件与左边界的间距
		int boundRightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_bound_rightmargin); // 控件与右边界的间距
		int boundTopMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_bound_topmargin); // 控件与上边界的间距
		int boundBottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_bound_bottommargin); // 控件与下边界的间距
		int topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_widget_topmargin); // 不同种类控件之间的间距
		int bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_widget_bottommargin);
		int buttonHeight = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_button_height); // 确认、取消两个按钮的高度
		int textHeight = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_title_textsize); // 处理结果
																										// 这几个字的高度
		int lines = list_feed.size() / 2;
		int messageMargin = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_message_margin); // 信息方块之间的间距
		int messageHeight = (dialogHeight - textHeight - 2 * buttonHeight - topMargin - boundTopMargin
				- boundBottomMargin - 3 * bottomMargin - lines * messageMargin)
				/ lines; // 中间每个信息块的高度，现在一共是四行
		int textSize = getResources().getDimensionPixelSize(R.dimen.dialog_feedback_message_textsize); // 那几个message文字的大小

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		TextView textView = new TextView(mContext);
		textView.setText("处理结果");
		params.leftMargin = boundLeftMargin;
		params.topMargin = boundTopMargin;
		params.bottomMargin = bottomMargin;
		textView.setLayoutParams(params);
		textView.setTextSize(textHeight);
		container.addView(textView);

		for (int i = 0; i < list_feed.size(); i++) {
			LinearLayout horizontalLayout = new LinearLayout(mContext);
			// params = new LinearLayout.LayoutParams(dialogWidth - leftMargin -
			// rightMargin - messageMargin, messageHeight);
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, messageHeight);
			params.bottomMargin = messageMargin;
			horizontalLayout.setOrientation(0); // 0是horizontal
			horizontalLayout.setLayoutParams(params);

			Button button1 = new Button(mContext);
			button1.setText(list_feed.get(i).getContent());
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, messageHeight, 1);
			params.gravity = Gravity.CENTER;
			params.leftMargin = boundLeftMargin;
			button1.setLayoutParams(params);
			button1.setBackgroundResource(R.drawable.btn_feedback_message_normal);
			button1.setId(i);
			button1.setOnClickListener(clickListener);
			button1.setTextSize(textSize);
			clickViews.add(button1);
			horizontalLayout.addView(button1);

			i++;

			Button button2 = new Button(mContext);
			button2.setText(list_feed.get(i).getContent());
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, messageHeight, 1);
			params.gravity = Gravity.CENTER;
			params.leftMargin = messageMargin;
			params.rightMargin = boundRightMargin;
			button2.setLayoutParams(params);
			button2.setBackgroundResource(R.drawable.btn_feedback_message_normal);
			button2.setId(i);
			if (!(getResources().getDimensionPixelSize(R.dimen.tv_or_pingban) == 1 && i == (list_feed.size() - 1))) { // tv端，其他（需文字说明）不可点击
				button2.setOnClickListener(clickListener);
			}
			button2.setTextSize(textSize);
			clickViews.add(button2);
			horizontalLayout.addView(button2);

			container.addView(horizontalLayout);
		}

		final EditText editText = new EditText(mContext);
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, messageHeight);
		if (list_feed.size() <= 4) { // 数量比较少的情况，编辑文字框的高度相应的减小
			params.height = messageHeight + messageMargin;
		} else {
			params.height = 2 * messageHeight + messageMargin;
		}
		params.leftMargin = boundLeftMargin;
		params.rightMargin = boundRightMargin;
		params.bottomMargin = messageMargin;
		editText.setLayoutParams(params);
		editText.setSingleLine(false);
		editText.setGravity(Gravity.LEFT | Gravity.TOP);
		editText.setHint("请注明报警原因");
		editText.setVisibility(View.GONE);
		editText.setBackgroundResource(R.drawable.btn_feedback_normal);
		editText.setTextSize(textSize);
		editText.setMaxLines(5); // 最多输入五行文字
		clickViews.add(editText);
		container.addView(editText);

		Button confirm = new Button(mContext);
		confirm.setText("确认");
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.bottomMargin = bottomMargin;
		params.height = buttonHeight;
		params.leftMargin = boundLeftMargin;
		params.rightMargin = boundRightMargin;
		confirm.setLayoutParams(params);
		confirm.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
		confirm.setTextSize(textSize);
		confirm.setTextColor(Color.rgb(255, 255, 255));
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				int selectId = clickListener.getSelectId(); // 该id为选中的那个message，与list_feed中的id对应
				if (selectId >= 0 && selectId < list_feed.size()) {
					if (selectId == (list_feed.size() - 1)) {
						feed_reason = editText.getText().toString().trim();
						if (TextUtils.isEmpty(feed_reason)) {
							PromptManager.showToast(mContext, false, "请输入文字说明!");
							return;
						}
					}
					feedBack(bean, list_feed.get(selectId));
					dialog.dismiss();
				}
			}
		});
		container.addView(confirm);

		Button cancel = new Button(mContext);
		cancel.setText("取消");
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.height = buttonHeight;
		params.leftMargin = boundLeftMargin;
		params.rightMargin = boundRightMargin;
		params.bottomMargin = boundBottomMargin;
		cancel.setLayoutParams(params);
		cancel.setBackgroundResource(R.drawable.btn_grey_rectangle_selector);
		cancel.setTextSize(textSize);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		container.addView(cancel);
	}

	// private void showFeedBackDailog(final WarningBean bean) {
	//
	// LayoutInflater inflater = (LayoutInflater)
	// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// View view = inflater.inflate(R.layout.dailog_feedback, null);
	//
	// final Dialog dialog = new AlertDialog.Builder(mContext).create();
	// dialog.show();
	// // 933
	// dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT);
	// view.setBackgroundColor(Color.parseColor("#ffffff"));
	// dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	// dialog.setContentView(view);
	// dialog.setCanceledOnTouchOutside(false);
	//
	// final EditText et_reason = (EditText) view.findViewById(R.id.et_reason);
	// Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
	// Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
	// final InputMethodManager imm = (InputMethodManager)
	// mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	// et_reason.setOnFocusChangeListener(new OnFocusChangeListener() {
	// public void onFocusChange(View view, boolean arg1) {
	// if (view.isFocused()) {
	// imm.showSoftInput(view, 0);
	// } else {
	// imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	// }
	// }
	// });
	// btn_cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// dialog.dismiss();
	//
	// }
	// });
	//
	// btn_ok.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// if (selectedFeed.getFeed_id() == 0) {
	// feed_reason = et_reason.getText().toString().trim();
	// if (TextUtils.isEmpty(feed_reason)) {
	// PromptManager.showToast(mContext, false, "请输入文字说明!");
	// return;
	// }
	// }
	// feedBack(bean, selectedFeed);
	// dialog.dismiss();
	// }
	// });
	//
	// RadioGroup rg_feed = (RadioGroup) view.findViewById(R.id.rg_feedgroup);
	//
	// DBFeed db = DBFeed.getInstance(mContext);
	// db.open();
	// list_feed = db.getAllFeedInfo();
	// db.close();
	// if (list_feed != null && list_feed.size() > 0) {
	// selectedFeed = list_feed.get(0);
	// }
	// list_feed.add(new FeedBean(0, "其他(需文字说明)"));
	//
	// for (int i = 0; i < list_feed.size(); i++) {
	// RadioButton rb = new RadioButton(mContext);
	// rb.setId(i);
	// LayoutParams layoutParams = new
	// LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
	// RadioGroup.LayoutParams.WRAP_CONTENT);
	// rb.setLayoutParams(layoutParams);
	// rb.setTextSize(24);
	// rb.setButtonDrawable(R.drawable.radio_selector);
	// rb.setTextColor(Color.parseColor("#333333"));
	// rb.setTop(24);
	// rb.setText(list_feed.get(i).getContent());
	// rb.setPadding(15, 5, 0, 5);
	// if (i == 0) {
	// rb.setChecked(true);
	// }
	//
	// rg_feed.addView(rb);
	// }
	//
	// rg_feed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(RadioGroup group, int checkedId) {
	// // TODO Auto-generated method stub
	//
	// int position = group.getCheckedRadioButtonId();
	// if (position == group.getChildCount() - 1) {
	// et_reason.setVisibility(View.VISIBLE);
	// } else {
	// et_reason.setVisibility(View.GONE);
	// }
	//
	// selectedFeed = list_feed.get(position);
	// }
	// });
	//
	// }

	/**
	 * 
	 * Function:反馈信息
	 * 
	 * @param bean
	 *            ：需要反馈的警告
	 * @param feed
	 *            ：反馈的信息
	 */
	private void feedBack(final WarningBean bean, final FeedBean feed) {

		PromptManager.showProgressDialog(mContext, "反馈中，请稍后...");
		HttpClientUtil client = HttpClientUtil.getInstance();
		int nurse_id = ConfigManager.getIntValue(mContext, ConfigManager.NURSE_ID);
		client.feedBackInfo(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN), feed.getFeed_id()
				+ "", feed_reason, nurse_id + "", bean.getAlarm_id(), new IOperationResult() {

			@Override
			public void operationResult(boolean isSuccess, String json, String errors) {
				if (isSuccess) {
					PromptManager.closeProgressDialog();
					if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
						PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
					} else {
						BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
						LogUtil.info("rice", "feedBack bean = " + baseBean.toString());
						LogUtil.info("rice", "feedBack feed_id = " + feed.getFeed_id());
						if (baseBean.getStatus() == 0) {
							list_warning.remove(bean);
							mWarningAdapter.notifyDataSetChanged();
							changeUserShowStatus(bean);
							// 根据条件替换背景颜色
							changeBg();
							PromptManager.showToast(mContext, true, "反馈成功!");
						} else if (baseBean.getStatus() == 3003) {
							// 移除
							PromptManager.showToast(mContext, false, baseBean.getInfo());
							list_warning.remove(bean);
							mWarningAdapter.notifyDataSetChanged();
							changeBg();
							changeUserShowStatus(bean);
						} else {
							PromptManager.showToast(mContext, false, baseBean.getInfo());
						}
					}
				} else {
					PromptManager.closeProgressDialog();
					PromptManager.showToast(getActivity(), false, errors);
				}
			}
		});
	}

	private void changeUserShowStatus(WarningBean bean) {
		boolean isOk = false;
		for (int i = 0; i < list_active.size(); i++) {
			if (list_active.get(i).getOld_id() == bean.getOld_id()) {
				for (WarningBean warnbean : list_active.get(i).getList_warning()) {
					if (warnbean.getAlarm_id().equals(bean.getAlarm_id())) {
						list_active.get(i).getList_warning().remove(warnbean);
						isOk = true;
						break;
					}
				}
			}
			if (isOk == true) {
				break;
			}
		}
		LogUtil.info("smarhit", "反馈或处理的时候，changeUserShowStatus");
		dynamicAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @author scott
	 * 
	 *         Function:激光推送广播
	 */
	private class JpushReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (JPUSH_MESSAGE_ACTION.equals(intent.getAction())) {
				Bundle bundle = intent.getBundleExtra("bundle");
				WarningBean bean = (WarningBean) bundle.getSerializable("warningbean");

				if (bean.getType() == 1) {// 主动报警
					String strWarning = "设备序列号：" + IMEI + " 报警ID:" + bean.getAlarm_id() + " 报警bean:" + bean.toString();
					updateLog(strWarning);
					LogUtil.info("rice", "主动报警" + strWarning);
					jpushWarning(bean);
				} else if (bean.getType() == 2) {// 报警信息已处理
					String strWarning = "设备序列号：" + IMEI + " 处理ID:" + bean.getAlarm_id() + " 处理bean:" + bean.toString();
					updateLog(strWarning);
					LogUtil.info("rice", "报警信息已处理" + strWarning);
					jpushWarningFeedback(bean);
				}
				setRegionInfo();
			}
		}
	}

	public void updateLog(String log) {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.uploadException(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN), log,
				new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						LogUtil.info("--上传日志返回的信息json:" + json);
						LogUtil.info("rice", "调用日志上传接口");
					}
				});
	}

	private void jpushWarning(WarningBean bean) {

		// 更新报警列表
		fl_main.setBackgroundResource(R.drawable.bg_state_unnormal);
		lv_warning.setVisibility(View.VISIBLE);

		boolean isOutside = false;

		if (list_active.size() == 0) {
			isOutside = refreshUserInfo(bean, true);
		} else {
			boolean isExistUser = false;
			// 更新老人状态显示
			for (int i = 0; i < list_active.size(); i++) {
				UserBean oldbean = list_active.get(i);
				if (oldbean.getOld_id() == bean.getOld_id()) {
					oldbean.getList_warning().add(0, bean);
					list_active.get(i).setRefreshTime(bean.getAlarm_time());
					list_active.get(i).setCurrentLocation(bean.getStation_detail());
					list_active.get(i).setCurrentStatus(bean.getActive_mod());
					isExistUser = true;
					break;
				}
			}

			if (!isExistUser) {
				isOutside = refreshUserInfo(bean, true);
			}
		}

		dynamicAdapter.notifyDataSetChanged();

		// 判断当前报警列表中是否有重复的报警信息
		boolean isExist = false;
		for (int i = 0; i < list_warning.size(); i++) {
			WarningBean oldWarnBean = list_warning.get(i);
			if (oldWarnBean.getAlarm_id().equals(bean.getAlarm_id())
					&& oldWarnBean.getAlarm_time().equals(bean.getAlarm_time())) {
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			LogUtil.info("smarhit", "isOutside=" + isOutside);
			bean.setOutside(isOutside);
			list_warning.add(0, bean);
			mWarningAdapter.notifyDataSetChanged();
			StringBuilder content = new StringBuilder(bean.getOld_name() + "在" + bean.getStation_detail() + "位置");
			switch (bean.getAlarm_type()) {
			case 1:
				content.append(ConfigManager.getStringValue(mContext, ConfigManager.WARNING_NAME) + "，请及时处理!");
				break;
			case 2:
				content.append("跌倒了，请及时处理!");
				break;
			case 3:
				content.append("走失报警，请及时处理!");
				break;
			default:
				break;
			}

			mEngineType = SpeechConstant.TYPE_CLOUD;
			// 设置参数
			setParam();
			mTts.startSpeaking(content.toString(), new SynthesizerListenerImpl());
		}

		setUserCurrentInfo(bean);
	}

	private void jpushWarningFeedback(WarningBean bean) {
		fl_main.setBackgroundResource(R.drawable.bg_state_unnormal);
		lv_warning.setVisibility(View.VISIBLE);
		// for (int i = 0; i < list_warning.size(); i++) {
		// WarningBean oldBean = list_warning.get(i);
		// if (oldBean.getAlarm_id().equals(bean.getAlarm_id())) {
		// // 更新报警列表
		// list_warning.remove(oldBean);
		// break;
		// }
		// }

		// LogUtil.info("rice", "反馈后的推送 list_warning = " + list_warning);
		//
		// if (list_warning.size() <= 0) {
		// fl_main.setBackgroundColor(mContext.getResources().getColor(R.color.monitor_background_color));
		// lv_warning.setVisibility(View.GONE);
		// }
		// mWarningAdapter.notifyDataSetChanged();

		if (list_active.size() == 0) {
			refreshUserInfo(bean, true);
		} else {
			// 更新老人状态显示
			boolean isExistUser = false;
			for (int i = 0; i < list_active.size(); i++) {
				UserBean oldbean = list_active.get(i);
				if (oldbean.getOld_id() == bean.getOld_id()) {
					isExistUser = true;
					list_active.get(i).setRefreshTime(bean.getAlarm_time());
					list_active.get(i).setCurrentLocation(bean.getStation_detail());
					list_active.get(i).setCurrentStatus(bean.getActive_mod());
					if (oldbean.getList_warning().size() == 1) {
						oldbean.getList_warning().clear();
					} else {
						for (int j = 0; j < oldbean.getList_warning().size(); j++) {
							WarningBean warnBean = oldbean.getList_warning().get(j);
							if (warnBean.getAlarm_id().equals(bean.getAlarm_id())) {
								// 更新报警列表
								oldbean.getList_warning().remove(warnBean);
								break;
							}
						}
					}
					break;
				}
			}

			if (!isExistUser) {
				refreshUserInfo(bean, true);
			}
		}
		dynamicAdapter.notifyDataSetChanged();
		setUserCurrentInfo(bean);
	}

	/**
	 * 
	 * Function:刷新用户当前状态
	 * 
	 * @param bean
	 *            ：推送的警告信息
	 * @param isFromJpush
	 *            ：是否来自激光推送的更新
	 */
	private boolean refreshUserInfo(WarningBean bean, boolean isFromJpush) {

		boolean isOutSide = false;
		DBUser dbuser = DBUser.getInstance(mContext);
		dbuser.open();
		UserBean userBean = dbuser.getUserInfoById(bean.getOld_id() + "");
		dbuser.close();

		if (userBean == null) { // 把报警信息中的用户信息写入DBUser表
			userBean = new UserBean();
			userBean.setOld_id(bean.getOld_id());
			userBean.setOld_sn(bean.getOld_sn());
			userBean.setName(bean.getOld_name());
			isOutSide = true;
		}

		if (isFromJpush) {
			if (bean.getType() == 1) {
				List<WarningBean> listwarning = new ArrayList<WarningBean>();
				listwarning.add(bean);
				userBean.setList_warning(listwarning);
			}
			// list_active.add(userBean);
		} else {
			boolean isExistUser = false;
			if (list_active.size() > 0) {
				for (int i = 0; i < list_active.size(); i++) {
					if (list_active.get(i).getOld_id() == bean.getOld_id()) {
						list_active.get(i).getList_warning().add(bean);
						isExistUser = true;
					}
				}
			}
			if (!isExistUser) {
				List<WarningBean> listwarning = new ArrayList<WarningBean>();
				listwarning.add(bean);
				userBean.setList_warning(listwarning);
				if (userBean.getRefreshTime() != null) {
					if (System.currentTimeMillis() - DateUtil.StringTolong(userBean.getRefreshTime()) < mUserStatusRefreshTime1) {
						// list_active.add(userBean);
					}
				} else {
					// list_active.add(userBean);
				}

			}
		}

		return isOutSide;
	}

	/**
	 * 
	 * Function:保存老人当前运动的状态信息
	 * 
	 * @param warnBean
	 *            ：推送的警告信息
	 */
	private void setUserCurrentInfo(WarningBean warnBean) {
		UserBean bean = new UserBean();
		bean.setOld_id(warnBean.getOld_id());
		bean.setOld_sn(warnBean.getOld_sn());
		bean.setRefreshTime(warnBean.getAlarm_time());
		bean.setCurrentLocation(warnBean.getStation_detail());
		bean.setCurrentStatus(warnBean.getActive_mod());
		DBUser dbuser = DBUser.getInstance(mContext);
		dbuser.open();
		dbuser.updateUserCurrentStatus(bean);
		dbuser.close();

	}

	@Override
	public void onWarningFeed(WarningBean bean) {
		// TODO Auto-generated method stub
		showFeedBackDialog(bean);
	}

	/**
	 * 处理报警信息
	 */
	@Override
	public void onResolve(final WarningBean bean) {
		// TODO Auto-generated method stub
		PromptManager.showProgressDialog(mContext, "正在处理，请稍后...");
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.resolve(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN), bean.getAlarm_id(),
				new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 更新报警列表
									if (list_warning.contains(bean)) {
										list_warning.remove(bean);
									}
									bean.setAlarm_status(1);
									list_warning.add(bean);
									mWarningAdapter.notifyDataSetChanged();
									lv_warning.setSelection(list_warning.size());
									PromptManager.showToast(mContext, true, "处理成功!");

								} else if (baseBean.getStatus() == 3003) {
									// 移除
									PromptManager.showToast(mContext, false, baseBean.getInfo());
									list_warning.remove(bean);
									mWarningAdapter.notifyDataSetChanged();
									changeBg();
									changeUserShowStatus(bean);
								} else {
									PromptManager.showToast(mContext, false, baseBean.getInfo());
								}
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getActivity(), false, errors);
						}
					}
				});
	}

	@Override
	public void userDetail(WarningBean bean) {
		// TODO Auto-generated method stub
		DBUser dbUser = DBUser.getInstance(mContext);
		dbUser.open();
		UserBean userBean = dbUser.getUserInfoById(bean.getOld_sn());
		dbUser.close();

		Intent intent = new Intent(getActivity(), UserDetailActivity.class);
		intent.putExtra("userbean", userBean);
		startActivity(intent);

	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {

		// 设置合成
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			// 设置发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		} else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// 设置发音人 voicer为空默认通过语音+界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		}
		mTts.setParameter(SpeechConstant.VOLUME, "100");
	}

	/**
	 * 
	 * Function:设置区域信息
	 */
	private void setRegionInfo() {
		// tv_dynamic_title.setText(Html.fromHtml("<font color='#f3ffb1'>"
		// + ConfigManager.getStringValue(mContext, ConfigManager.AREA_RANGE)
		// + "</font><font color= '#ffffff'>  共有  </font><font color='#f3ffb1'>"
		// + list_active.size()
		// + "</font><font color= '#ffffff'>  位老人在活动</font>"));

		tv_dynamic_title.setText(Html.fromHtml("" + ConfigManager.getStringValue(mContext, ConfigManager.AREA_RANGE)
				+ "  共有 " + list_active.size() + "  位老人在活动"));
	}

	/**
	 * 
	 * Function:改变背景色
	 */
	private void changeBg() {
		if (list_warning.size() > 0) {
			fl_main.setBackgroundResource(R.drawable.bg_state_unnormal);
			lv_warning.setVisibility(View.VISIBLE);
		} else {
			fl_main.setBackgroundColor(mContext.getResources().getColor(R.color.monitor_background_color));
			lv_warning.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * Function: 监控屏拉取活动统计曲线图
	 * 
	 */
	private void getActive() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getActive(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(mContext, false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 获得每十分钟所有活动、静止、睡眠的人数
									List<ActiveBean> lines = JSON.parseArray(baseBean.getData(), ActiveBean.class);
									LogUtil.info("rice", "lines =" + lines.toString());
								} else {
									PromptManager.showToast(mContext, false, errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});
	}

	/**
	 * 
	 * Function: 监控屏拉取分区域活动饼图
	 * 
	 */
	private void getAreaActive() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getAreaActive(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(mContext, false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 获得分区域的活动数据
									List<AreaActiveBean> pies = JSON.parseArray(baseBean.getData(),
											AreaActiveBean.class);
									LogUtil.info("rice", "pies =" + pies.toString());
								} else {
									PromptManager.showToast(mContext, false, errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});
	}

}
