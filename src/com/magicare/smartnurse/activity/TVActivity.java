package com.magicare.smartnurse.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.ActiveBean;
import com.magicare.smartnurse.bean.AreaActiveBean;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.TVAreaActiveEntity;
import com.magicare.smartnurse.bean.TVBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.DensityUtil;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

public class TVActivity extends BaseActivity {

	private static final int ANIMATION_SAMLL = 1;
	private static final int ANIMATION_BIG = 2;
	private static final int ITEM_TEN = 3;
	private static final int ITEM_FIVE = 2;
	private static final int ITEM_THREE = 1;

	// handler 消息id
	private static final int HANDLER_DRAW_CHART = 0;
	private static final int HANDLER_DRAW_PIE = 1;
	private static final int HANDLER_DRAW_TIME = 2;
	private static final int HANDLER_GET_CHART_DATA = 3;
	private static final int HANDLER_GET_PIE_DATA = 4;

	private static final int TIME_REFRESH_CHART = 10 * 1000; // 更新图表的时间
	private static final int TIME_REFRESH_PIE = 10 * 1000; // 更新饼图的时间

	private final float CHART_X_MAX = 36 + 1; // 曲线图x轴的最大坐标值,x轴的值多加一点，显示上更好看
	private float CHART_Y_MAX = 100; // 曲线图y轴的最大坐标值，会动态改变
	private float CHART_Y_MIN = 0; // 曲线图y轴的最小坐标值，会动态改变

	private final String COLOR_LINE_SPORT = "#fffc9325"; // 运动曲线的颜色
	private final String COLOR_LINE_SLEEP = "#ff50ab32"; // 睡眠曲线的颜色
	private final String COLOR_LINE_STOP = "#ff38a6dd"; // 静止曲线的颜色
	private final String COLOR_CHART_TEXT = "#ffffffff"; // 曲线图表x、y轴坐标的颜色
	private final String COLOR_CHART_COLUM = "#888D94AF"; // 曲线图表中的柱状图的颜色
	private final String COLOR_PIE_SPORT = "#fffc9325"; // 运动饼图的颜色
	private final String COLOR_PIE_SLEEP = "#ff50ab32"; // 睡眠饼图的颜色
	private final String COLOR_PIE_STOP = "#ff38a6dd"; // 静止饼图的颜色
	private final String COLOR_PIE_ALARM = "#ffff5959"; // 报警饼图的颜色
	private final String COLOR_PIE_NORMAL_TEXT = "#ff99b1bc"; // 正常情况下饼图内文字的颜色
	private final String COLOR_PIE_UNNORMAL_TEXT = "#ffff5959"; // 非正常情况下饼图内文字的颜色，比如有报警

	private int mPieItem = 0; // 当前饼图的个数
	private int pageCount = 0;// 有多少页

	private String mTime;
	private String mDate;

	private Timer mTimer; // 更新时间

	private FrameLayout mChartParent; // 图表上面一层，用于添加图表中的标签
	private ChartLabelView mChartLabelView01, mChartLabelView02,
			mChartLabelView03; // 曲线图表的三个标签view，分别是运动、睡眠、静止
	private LinearLayout topLayout;
	private ComboLineColumnChartView chartView;
	private LinearLayout bottomLayout;
	private TextView persons;
	private TextView timeTextView;
	private TextView warning;
	private RelativeLayout warningLayout;
	private RelativeLayout moreLayout;
	private TextView more;
	private FrameLayout pagerLayout;
	private View scaleView;
	private ViewPager viewPager;
	private ImageView last;
	private ImageView next;
	private List<GridView> gridViews = new ArrayList<GridView>();
	private List<TVBean> beans;
	private Context mContext;
	private List<ActiveBean> mActiveBeans = new ArrayList<ActiveBean>(); // 曲线图的数据结构，运动、静止、睡眠人数时刻数据
	private List<AreaActiveBean> mAreaActiveBeans = new ArrayList<AreaActiveBean>(); // 饼图数据
	private VPAdapter mAdapter; // ViewPager 的适配器
	private String mPensionName; // 养老院名字
	private int mAlarmNum = 0; // 总的报警人次
	private TVAreaActiveEntity mAreaActiveEntity;

	private ObjectAnimator scaleXAnimator = null;
	private ObjectAnimator scaleYAnimator = null;
	private AnimatorSet animatorSet = new AnimatorSet();

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DRAW_CHART:
				drawChart();

				// 更新总人数
				int personCount = 0;
				if (mActiveBeans != null && mActiveBeans.size() > 0) {
					personCount = mActiveBeans.get(mActiveBeans.size() - 1)
							.getMove_num()
							+ mActiveBeans.get(mActiveBeans.size() - 1)
									.getSleep_num()
							+ mActiveBeans.get(mActiveBeans.size() - 1)
									.getStop_num();
					persons.setText(getString(R.string.collection_persons,
							mPensionName, personCount + ""));
					String personsText = persons.getText().toString();
					SpannableStringBuilder personBuilder = new SpannableStringBuilder(
							personsText);
					personBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
							personsText.indexOf("（"), personsText.length(),
							Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					persons.setText(personBuilder);
				}

				mHandler.removeMessages(HANDLER_GET_CHART_DATA);
				mHandler.sendEmptyMessageDelayed(HANDLER_GET_CHART_DATA,
						TIME_REFRESH_CHART);
				break;
			case HANDLER_DRAW_PIE:
				drawGridView();
				mHandler.removeMessages(HANDLER_GET_PIE_DATA);
				mHandler.sendEmptyMessageDelayed(HANDLER_GET_PIE_DATA,
						TIME_REFRESH_PIE);
				break;
			case HANDLER_DRAW_TIME:
				timeTextView.setText(getString(R.string.collection_time,
						format.format(new Date(System.currentTimeMillis())),
						mDate));
				String timeText = timeTextView.getText().toString();
				SpannableStringBuilder timeBuilder = new SpannableStringBuilder(
						timeText);
				timeBuilder.setSpan(
						new ForegroundColorSpan(Color.parseColor("#809EB8")),
						timeText.indexOf(" "), timeText.length(),
						Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				timeBuilder.setSpan(new RelativeSizeSpan(0.8f), timeText.indexOf(" "),
						timeText.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				timeTextView.setText(timeBuilder);
				break;
			case HANDLER_GET_CHART_DATA:
				getActive();
				break;
			case HANDLER_GET_PIE_DATA:
				getAreaActive();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_users_collection);

		mContext = this;

		init();
		initViewPager();

		registerListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		PromptManager.showProgressDialog(this, "正在加载...");
		
		getActive(); // 获取曲线图表数据
		getAreaActive(); // 获取饼图数据

		startTime();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 停止更新
		mHandler.removeMessages(HANDLER_DRAW_CHART);
		mHandler.removeMessages(HANDLER_DRAW_PIE);
		mHandler.removeMessages(HANDLER_DRAW_TIME);
		mHandler.removeMessages(HANDLER_GET_CHART_DATA);
		mHandler.removeMessages(HANDLER_GET_PIE_DATA);
		mTimer.cancel();
	}	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void startTime() {
		mTimer = new Timer(true);
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(HANDLER_DRAW_TIME);
			}
		}, 0, 1000);
	}

	@SuppressLint("NewApi")
	private void registerListener() {

		warningLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MonitorActivity.class);
				intent.putExtra("onlyWarning", true); // 只显示报警列表，老人状态信息不显示
				startActivity(intent);
			}
		});

		warningLayout.setOnHoverListener(new OnHoverListener() {

			@Override
			public boolean onHover(View v, MotionEvent event) {

				scaleAnimation(v, event);
				return false;
			}
		});

		moreLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, TVSettingsActivity.class);
				startActivity(intent);
				TVActivity.this.finish();

			}
		});
		moreLayout.setOnHoverListener(new OnHoverListener() {

			@Override
			public boolean onHover(View v, MotionEvent event) {

				scaleAnimation(v, event);
				return false;
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				if (arg0 > 0) {
					if (last.getVisibility() != View.VISIBLE)
						last.setVisibility(View.VISIBLE);
				} else {
					last.setVisibility(View.GONE);
				}
				if (arg0 < pageCount - 1) {
					if (next.getVisibility() != View.VISIBLE)
						next.setVisibility(View.VISIBLE);
				} else {
					next.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (scaleView != null) {
					scaleView.setVisibility(View.GONE);
					scaleView = null;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
			}
		});
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
			}
		});

	}

	private void initViewPager() {

		pagerLayout = (FrameLayout) findViewById(R.id.collection_viewpager_layout);

		last = (ImageView) findViewById(R.id.collection_last);
		next = (ImageView) findViewById(R.id.collection_next);

		viewPager = (ViewPager) findViewById(R.id.collection_viewpager);
		viewPager.setOffscreenPageLimit(0);
		viewPager.setPageMargin(getResources().getDimensionPixelSize(
				R.dimen.collection_viewpager_margin));
		mAdapter = new VPAdapter();
		viewPager.setAdapter(mAdapter);
		if (pageCount > 1) {
			next.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {

		mPensionName = ConfigManager.getStringValue(mContext,
				ConfigManager.PENSION_NAME);

		topLayout = (LinearLayout) findViewById(R.id.collection_top);
		chartView = (ComboLineColumnChartView) findViewById(R.id.collection_chart);
		bottomLayout = (LinearLayout) findViewById(R.id.collection_bottom);

		chartView.setZoomEnabled(false); // tv端不支持缩放操作
		
		// 加载完数据之前，先隐藏图表
		chartView.setVisibility(View.INVISIBLE);

		mChartParent = (FrameLayout) findViewById(R.id.chart_parent);

		more = (TextView) findViewById(R.id.collection_more);
		persons = (TextView) findViewById(R.id.collection_persons);
		persons.setText(getString(R.string.collection_persons, mPensionName,
				"0")); // 默认人数为0
		String personsText = persons.getText().toString();
		SpannableStringBuilder personBuilder = new SpannableStringBuilder(
				personsText);
		personBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
				personsText.indexOf("（"), personsText.length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		persons.setText(personBuilder);
		timeTextView = (TextView) findViewById(R.id.collection_time);
		mDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System
				.currentTimeMillis()));

		warningLayout = (RelativeLayout) findViewById(R.id.collection_warning_layout);
		warning = (TextView) findViewById(R.id.collection_warning);
		warning.setText(getString(R.string.collection_warning, "2"));
		moreLayout = (RelativeLayout) findViewById(R.id.collection_more_layout);

		mTimer = new Timer();
	}

	private void drawGridView() {
		AreaActiveBean bean = new AreaActiveBean();
		List<AreaActiveBean> unnormalBeans = new ArrayList<AreaActiveBean>();
		List<AreaActiveBean> normalBeans = new ArrayList<AreaActiveBean>();

//		if(mAreaActiveBeans.size() >= 3){
//			for (int i = 0; i < mAreaActiveBeans.size(); i++) {
//				if (i == 2) {
//					mAreaActiveBeans.get(i).setAlarm_num(0);
//					mAreaActiveBeans.get(i).setMove_num(40);
//					mAreaActiveBeans.get(i).setSleep_num(30);
//					mAreaActiveBeans.get(i).setStop_num(50);
//					mAreaActiveBeans.get(i).setPeople_num(120);
//					mAreaActiveBeans.get(i).setPension_areaid(1);
//	
//					bean = mAreaActiveBeans.get(i);
//				}
//			}
//		}

		// mAreaActiveBeans.remove(4);

//		bean = mAreaActiveBeans.get(0);
//		
//		bean.setSleep_num(200);
//		bean.setMove_num(100);
//		bean.setStop_num(333);
//		bean.setPeople_num(100);
//		
//		for (int i = 0; i < 15; i++) {
//			mAreaActiveBeans.add(bean);
//		}

		// 将报警的区域排在前面
		for (AreaActiveBean bean1 : mAreaActiveBeans) {
			if (bean1.getAlarm_num() > 0) {
				unnormalBeans.add(bean1);
			} else {
				normalBeans.add(bean1);
			}
		}

		mAreaActiveBeans.clear();
		mAreaActiveBeans.addAll(unnormalBeans);
		mAreaActiveBeans.addAll(normalBeans);

		int size = mAreaActiveBeans.size();
		int numColumns = 0;
		if (size < 5 && size >= 0) {
			mPieItem = ITEM_THREE;
			numColumns = 3;
			pageCount = size % 3 == 0 ? size / 3 : size / 3 + 1;
		} else if (size >= 5 && size < 10) {
			mPieItem = ITEM_FIVE;
			numColumns = 5;
			pageCount = size % 5 == 0 ? size / 5 : size / 5 + 1;
		} else if (size >= 10) {
			mPieItem = ITEM_TEN;
			numColumns = 5;
			pageCount = size % 10 == 0 ? size / 10 : size / 10 + 1;
		}
		gridViews = new ArrayList<GridView>(pageCount);
		int k = 0;
		for (int i = 0; i < pageCount; i++) {
			View view = getLayoutInflater().inflate(
					R.layout.collection_viewpager_item, null);
			GridView gridView = (GridView) view
					.findViewById(R.id.collection_gridview);
			List<AreaActiveBean> gvBeans = new ArrayList<AreaActiveBean>();
			if (size < 10) {
				int value = k + numColumns;
				for (int j = k; j < value && j < size; j++) {
					gvBeans.add(mAreaActiveBeans.get(j));
				}
				k = k + numColumns;
			} else {
				int value = k + 2 * numColumns;
				for (int j = k; j < value && j < size; j++) {
					gvBeans.add(mAreaActiveBeans.get(j));
				}
				k = k + 2 * numColumns;
				gridView.setHorizontalSpacing(getResources()
						.getDimensionPixelSize(
								R.dimen.collection_gridview_item10_spacing));
				gridView.setVerticalSpacing(getResources()
						.getDimensionPixelSize(
								R.dimen.collection_gridview_item10_spacing));
			}
			gridView.setNumColumns(numColumns);
			gridView.setAdapter(new GVAdapter(gvBeans));

			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					AreaActiveBean bean = mAreaActiveBeans.get(position);
					Intent intent = new Intent(mContext, MonitorActivity.class);
					intent.putExtra("pension_areaid", bean.getPension_areaid());
					startActivity(intent);
				}

			});
			gridViews.add(gridView);
		}

		mAdapter.notifyDataSetChanged();
		for (GridView gridView : gridViews) {
			GVAdapter adapter = (GVAdapter) gridView.getAdapter();
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
		
		if (pageCount > 1) {
			next.setVisibility(View.VISIBLE);
		}		

		// 统计有多少个报警
//		int alarmCount = 0;
//		for (AreaActiveBean tempBean : mAreaActiveBeans) {
//			alarmCount += tempBean.getAlarm_num();
//		}
		if (mAlarmNum > 0) {
			warningLayout.setVisibility(View.VISIBLE);
			warning.setText(getString(R.string.collection_warning, ""
					+ mAlarmNum));
		} else {
			warningLayout.setVisibility(View.INVISIBLE);
		}
	}

	@SuppressLint("NewApi")
	private void startAnimation(View view, final int animationFlag,
			final AreaActiveBean bean, int position) {
		
		if(animatorSet.isRunning()){
			animatorSet.cancel();
		}
		
		ViewHolder viewHolder = null;
		if (scaleView == null) {
			switch (mPieItem) {
			case ITEM_THREE:
				scaleView = findViewById(R.id.collection_scale_view3);
				break;
			case ITEM_FIVE:
				scaleView = findViewById(R.id.collection_scale_view5);
				break;
			case ITEM_TEN:
				scaleView = findViewById(R.id.collection_scale_view10);
				break;
			}
			viewHolder = new ViewHolder();
			getConvertView(scaleView, viewHolder, bean, position);
			scaleView.setTag(viewHolder);
			scaleView.setVisibility(View.VISIBLE);
		} else {
			viewHolder = (ViewHolder) scaleView.getTag();
		}

		int[] viewLocation = new int[2];
		int[] pagerLocation = new int[2];
		pagerLayout.getLocationOnScreen(pagerLocation);
		view.getLocationOnScreen(viewLocation);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) scaleView
				.getLayoutParams();
		params.leftMargin = viewLocation[0] - pagerLocation[0];
		params.topMargin = viewLocation[1] - pagerLocation[1];
		params.width = view.getWidth();
		params.height = view.getHeight();
		params.gravity = Gravity.TOP;
		scaleView.setLayoutParams(params);
		scaleView.setLeft(viewLocation[0]);
		scaleView.setTop(viewLocation[1]);

		fillDatas(viewHolder, bean);

		if (bean.getAlarm_num() > 0) {
			scaleView
					.setBackgroundResource(R.drawable.collection_gridview_item_unnormal_bg);
		} else {
			scaleView
					.setBackgroundResource(R.drawable.collection_gridview_item_normal_bg);
		}

		if (animationFlag == ANIMATION_SAMLL) {
			scaleXAnimator = ObjectAnimator.ofFloat(scaleView, "scaleX", 1.05f,
					1.0f);
			scaleYAnimator = ObjectAnimator.ofFloat(scaleView, "scaleY", 1.05f,
					1.0f);
		} else if (animationFlag == ANIMATION_BIG) {
			scaleXAnimator = ObjectAnimator.ofFloat(scaleView, "scaleX", 1.0f,
					1.05f);
			scaleYAnimator = ObjectAnimator.ofFloat(scaleView, "scaleY", 1.0f,
					1.05f);
		}
		animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
		animatorSet.setDuration(250);
		animatorSet.start();
	}

	private void drawChart() {

		List<Line> lines = new ArrayList<Line>();
		List<Column> columns = new ArrayList<Column>();

		// 对数据进行筛选
		List<ActiveBean> tempBeans = new ArrayList<ActiveBean>();
		String yesterDay = DateUtil.getDatePerviousAndNext(-1);
		String today = DateUtil.getDatePerviousAndNext(0);

		// 注意服务器的数据必须是 14:00 14:10 14:20 14:30 14:40 14:50 15:00 这样的整十分钟格式，
		// 而且必须是从10分钟到50分钟都有的连续数据，否则数据出错
		for (int i = 0; i < mActiveBeans.size(); i++) {
			ActiveBean bean = mActiveBeans.get(i);

			if (bean.getData_time().split(" ")[0]
					.equals(yesterDay.split(" ")[0])) {
				int hour = Integer.valueOf(bean.getData_time().split(" ")[1]
						.split(":")[0]);
				int min = Integer.valueOf(bean.getData_time().split(" ")[1]
						.split(":")[1]);
				if (hour > 11 && hour < 24 && min == 50) { // 显示五十分钟的那一条，也就是每个小时的最后十分钟
					tempBeans.add(bean);
				}
			} else if (bean.getData_time().split(" ")[0].equals(today
					.split(" ")[0])) {

				if (i == mActiveBeans.size() - 1) { // 如果是最后一条
					tempBeans.add(bean);
				} else {
					ActiveBean lastBean = mActiveBeans
							.get(mActiveBeans.size() - 1);

					int lastHour = Integer.valueOf(lastBean.getData_time()
							.split(" ")[1].split(":")[0]);
					int curHour = Integer.valueOf(bean.getData_time()
							.split(" ")[1].split(":")[0]);
					int curMin = Integer
							.valueOf(bean.getData_time().split(" ")[1]
									.split(":")[1]);

					// 如果不是最后一条，那么先比较是不是跟最后一条处于同一小时，如果不是那么显示第50分钟那一条
					if (curHour < lastHour && curMin == 50) {
						tempBeans.add(bean);
					}
				}
			}
		}

		mActiveBeans.clear();
		mActiveBeans = tempBeans;

		// 绘制横坐标x轴
		List<AxisValue> xValues = new ArrayList<AxisValue>();

		for (int i = 0; i < CHART_X_MAX; i++) {
			switch (i) {
			case 0:
				xValues.add(new AxisValue(i, "12点".toCharArray()));
				break;
			case 12:
				xValues.add(new AxisValue(i, "0点".toCharArray()));
				break;
			case 24:
				xValues.add(new AxisValue(i, "12点".toCharArray()));
				break;
			case 36:
				xValues.add(new AxisValue(i, "24点".toCharArray()));
				break;
			default:
				xValues.add(new AxisValue(i, "".toCharArray()));
				break;
			}
		}
		float maxX = 0, maxY = 0, minY = 0;

		if (mActiveBeans.size() > 0) {

			List<PointValue> sportValues = new ArrayList<PointValue>();
			List<PointValue> sleepValues = new ArrayList<PointValue>();
			List<PointValue> stopValues = new ArrayList<PointValue>();

			minY = mActiveBeans.get(0).getMove_num();

			for(ActiveBean bean : mActiveBeans){
				maxY = Math.max(bean.getMove_num(), maxY);
				maxY = Math.max(bean.getSleep_num(), maxY);
				maxY = Math.max(bean.getStop_num(), maxY);

				minY = Math.min(bean.getMove_num(), minY);
				minY = Math.min(bean.getSleep_num(), minY);
				minY = Math.min(bean.getStop_num(), minY);				
			}
			
			for (ActiveBean bean : mActiveBeans) {
				int hour = Integer.valueOf(bean.getData_time().split(" ")[1]
						.split(":")[0]);
				int min = Integer.valueOf(bean.getData_time().split(" ")[1]
						.split(":")[1]);
				float x = 0;

				// 昨天的数据和今天的数据转换成x轴坐标的方式不一样
				if (bean.getData_time().split(" ")[0].equals(yesterDay
						.split(" ")[0])) {
					// x = (hour - 12) * 6 + min / 10;
					x = hour - 12;
				} else {
					// x = hour * 6 + min / 10 + 12 *6;
					x = hour + 12;
				}

				maxX = x;

				sportValues.add(new PointValue(x, bean.getMove_num()));
				sleepValues.add(new PointValue(x, bean.getSleep_num() + maxY - 0.2f * maxY));
				stopValues.add(new PointValue(x, bean.getStop_num() + 2 * maxY));
				
//				sportValues.add(new PointValue(x, bean.getMove_num()));
//				sleepValues.add(new PointValue(x, bean.getSleep_num()));
//				stopValues.add(new PointValue(x, bean.getStop_num()));				
			}

			xValues.get(mActiveBeans.size() - 1).setLabel(
					(mActiveBeans.get(mActiveBeans.size() - 1).getData_time()
							.split(" ")[1].split(":")[0] + "点").toCharArray());

//			maxY *= 3;
			
			CHART_Y_MAX = 3 * maxY + 3 * maxY * 0.1f; // Y轴最大值增幅在10%左右
//			CHART_Y_MAX = maxY + maxY * 0.1f; // Y轴最大值增幅在10%左右
			CHART_Y_MIN = minY - minY * 0.1f; // Y轴最小值减幅在10%左右

			for (int i = 0; i < 3; i++) { // 添加运动、睡眠、静止曲线

				Line line = new Line();
//				SimpleLineChartValueFormatter formatter = new SimpleLineChartValueFormatter();
				line.setShape(ValueShape.CIRCLE);
				line.setCubic(true);
				line.setFilled(false);
				line.setAreaTransparency(30);
				line.setHasLabels(false);
				line.setHasLines(true);
				line.setHasPoints(false);
//				line.setFormatter(formatter);

				switch (i) {
				case 0: // 运动曲线
					line.setValues(sportValues);
//					formatter.setPrependedText("运动 ".toCharArray());
					line.setColor(Color.parseColor(COLOR_LINE_SPORT));
					break;
				case 1: // 睡眠曲线
					line.setValues(sleepValues);
//					formatter.setPrependedText("睡眠 ".toCharArray());
					line.setColor(Color.parseColor(COLOR_LINE_SLEEP));
					break;
				case 2: // 静止曲线
					line.setValues(stopValues);
//					formatter.setPrependedText("静止 ".toCharArray());
					line.setColor(Color.parseColor(COLOR_LINE_STOP));
					break;
				default:
					break;
				}

				lines.add(line);
			}

			List<SubcolumnValue> subcolumnValues;
			for (int i = 0; i < maxX; i++) {
				subcolumnValues = new ArrayList<SubcolumnValue>();
				subcolumnValues.add(new SubcolumnValue(0, Color
						.parseColor(COLOR_CHART_COLUM)));
				columns.add(new Column(subcolumnValues));
			}

			// 绘制最后那一条柱状图，当做分界线
			subcolumnValues = new ArrayList<SubcolumnValue>();
			subcolumnValues.add(new SubcolumnValue(CHART_Y_MAX, Color
					.parseColor(COLOR_CHART_COLUM)));
			columns.add(new Column(subcolumnValues));
		}

		LineChartData lineChartData = new LineChartData(lines);

		ColumnChartData columnChartData = new ColumnChartData(columns);
		columnChartData.setFillRatio((float) 0.01);

		ComboLineColumnChartData data = new ComboLineColumnChartData(
				columnChartData, lineChartData);

		data.setAxisXBottom(new Axis(xValues).setTextColor(Color
				.parseColor(COLOR_CHART_TEXT)));
		
		List<AxisValue> yValues1 = new ArrayList<AxisValue>();
		yValues1.add(new AxisValue(CHART_Y_MAX, "".toCharArray()));
		
		// 不显示左边的Y轴，Y轴里面的值，系统会自己算
//		data.setAxisYLeft(new Axis(yValues1).setTextColor(Color
//				.parseColor(COLOR_CHART_TEXT))
//				.setMaxLabelChars(0)
//				);

		chartView.setComboLineColumnChartData(data);

		Viewport viewport = chartView.getMaximumViewport();
		viewport.left = 0;
		viewport.right = CHART_X_MAX;
		viewport.top = CHART_Y_MAX;
		viewport.bottom = CHART_Y_MIN;

		chartView.setCurrentViewport(viewport);
		chartView.setMaximumViewport(viewport);

		// 最后一个点的x，y值，以此来算标签的位置
		float firstX = chartView.getChartComputator().computeRawX(
				xValues.get(0).getValue());
		float firstMoveY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(0).getMove_num());
		float firstSleepY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(0).getSleep_num() + maxY - 0.2f * maxY);
		float firstStopY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(0).getStop_num() + 2 * maxY);
		
		float lastX = chartView.getChartComputator().computeRawX(
				xValues.get(mActiveBeans.size() - 1).getValue());
		float lastMoveY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(mActiveBeans.size() - 1).getMove_num());
		float lastSleepY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(mActiveBeans.size() - 1).getSleep_num() + maxY - 0.2f * maxY);
		float lastStopY = chartView.getChartComputator().computeRawY(
				mActiveBeans.get(mActiveBeans.size() - 1).getStop_num() + 2 * maxY);

		// 标签与最后那个点需要一定的偏移
		lastX += 20;
		lastMoveY -= 10;
		lastSleepY -= 10;
		lastStopY -= 10;
		
		// 标签高度33，这个可以通过ChartLabelView中的top与bottom之差求得，根据设置的字体大小而改变
		final int labelHeight = 47;
		final int labelWidth = 90;
		final int labelMargin = 8; // 如果两个标签挨得很近，至少需要10个的距离

		firstX -= labelWidth * 0.9f;
//		firstMoveY -= 10;
//		firstSleepY -= 10;
//		firstStopY -= 10;
		
		float yValues[] = { lastMoveY, lastSleepY, lastStopY };
		List<String> tables = new ArrayList<String>();
		tables.add("move");
		tables.add("sleep");
		tables.add("stop");

		for (int i = yValues.length - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (yValues[j + 1] < yValues[j]) {
					float temp = yValues[j + 1];
					yValues[j + 1] = yValues[j];
					yValues[j] = temp;

					String table = tables.get(j + 1);
					tables.set(j + 1, tables.get(j));
					tables.set(j, table);
				}
			}
		}

		if (yValues[1] - yValues[0] < labelHeight) {
			yValues[0] -= labelMargin + labelHeight - (yValues[1] - yValues[0]);
		}

		if (yValues[2] - yValues[1] < labelHeight) {
			yValues[2] += labelMargin + labelHeight - (yValues[2] - yValues[1]);
		}

		for (int i = 0; i < yValues.length; i++) {
			if (tables.get(i).equals("move")) {
				lastMoveY = yValues[i];
			} else if (tables.get(i).equals("sleep")) {
				lastSleepY = yValues[i];
			} else if (tables.get(i).equals("stop")) {
				lastStopY = yValues[i];
			}
		}

		// 添加运动曲线的标签
		if (mChartLabelView01 != null) {
			mChartParent.removeView(mChartLabelView01);
		}

		mChartLabelView01 = new ChartLabelView(mContext,
				COLOR_LINE_SPORT, "#ffffffff", COLOR_LINE_SPORT, "运动",
						mActiveBeans.get(mActiveBeans.size() - 1)
								.getMove_num() + "", firstX, firstMoveY, lastX, lastMoveY);
		mChartParent.addView(mChartLabelView01);

		// 添加睡眠曲线的标签
		if (mChartLabelView02 != null) {
			mChartParent.removeView(mChartLabelView02);
		}
		mChartLabelView02 = new ChartLabelView(mContext,
				COLOR_LINE_SLEEP, "#ffffffff" , COLOR_LINE_SLEEP, "睡眠",
						mActiveBeans.get(mActiveBeans.size() - 1)
								.getSleep_num() + "", firstX, firstSleepY, lastX, lastSleepY);
		mChartParent.addView(mChartLabelView02);

		// 添加静止曲线的标签
		if (mChartLabelView03 != null) {
			mChartParent.removeView(mChartLabelView03);
		}
		mChartLabelView03 = new ChartLabelView(mContext,
				COLOR_LINE_STOP, "#ffffffff", COLOR_LINE_STOP, "静止",
						mActiveBeans.get(mActiveBeans.size() - 1)
								.getStop_num() + "", firstX, firstStopY, lastX, lastStopY);
		mChartParent.addView(mChartLabelView03);
	}

	/**
	 * 
	 * Function: 监控屏拉取活动统计曲线图
	 * 
	 */
	private void getActive() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getActive(mContext,
				ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json,
							String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json)
									|| !json.startsWith("{")) {
								PromptManager.showToast(mContext, false,
										"数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json,
										BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 获得每十分钟所有活动、静止、睡眠的人数
									mActiveBeans = JSON.parseArray(
											baseBean.getData(),
											ActiveBean.class);
									if (mActiveBeans != null && mActiveBeans.size() > 0) {
										mHandler.removeMessages(HANDLER_DRAW_CHART);
										mHandler.sendEmptyMessage(HANDLER_DRAW_CHART);
									}
									
									PromptManager.closeProgressDialog();
									chartView.setVisibility(View.VISIBLE);
									
								} else {
									PromptManager.closeProgressDialog();
									PromptManager.showToast(mContext, false, errors);							
								}
							}
						} else {
							PromptManager.closeProgressDialog();
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
		client.getAreaActive(mContext,
				ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json,
							String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json)
									|| !json.startsWith("{")) {
								PromptManager.showToast(mContext, false,
										"数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json,
										BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 获得分区域的活动数据
									
									mAreaActiveEntity = JSON.parseObject(
											baseBean.getData(),
											TVAreaActiveEntity.class);
									
									mAlarmNum = mAreaActiveEntity.getAlarm_num();									
									mAreaActiveBeans = mAreaActiveEntity.getArea_list();
									
//									if(mAreaActiveBeans != null && mAreaActiveBeans.size() > 0){
//										for(AreaActiveBean bean : mAreaActiveBeans){
//											LogUtil.info("hujian", "areabean: " + bean.toString());
//										}										
//									}
									
//									mAreaActiveBeans = JSON.parseArray(
//											mAreaActiveEntity.getArea_list(),
//											AreaActiveBean.class);
									
									if (mAreaActiveBeans != null
											&& mAreaActiveBeans.size() > 0) {
										mHandler.removeMessages(HANDLER_DRAW_PIE);
										mHandler.sendEmptyMessage(HANDLER_DRAW_PIE);
									} else {
										mAreaActiveBeans = new ArrayList<AreaActiveBean>();
									}
								} else {
									PromptManager.showToast(mContext, false,
											errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});
	}

	private class VPAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return gridViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(gridViews.get(position), 0);
			return gridViews.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

	private class GVAdapter extends BaseAdapter {
		private List<AreaActiveBean> gvBeans;

		public GVAdapter(List<AreaActiveBean> gvBeans) {
			this.gvBeans = gvBeans;
		}

		@Override
		public int getCount() {
			return gvBeans.size();
		}

		@Override
		public Object getItem(int position) {
			return gvBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			AreaActiveBean bean = gvBeans.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getConvertView(null, viewHolder, bean, position);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			fillDatas(viewHolder, bean);

			return convertView;
		}

	}

	@SuppressLint("NewApi")
	private View getConvertView(View view, ViewHolder viewHolder,
			final AreaActiveBean bean, final int position) {
		View convertView = null;
		if (mPieItem == ITEM_THREE) {
			if (view == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.collection_gridview_item3, null);
			} else {
				convertView = view;
			}
			viewHolder.chart = (PieChartView) convertView
					.findViewById(R.id.collection_chart_three);
			// viewHolder.state = (TextView) convertView
			// .findViewById(R.id.collection_state_three);
			viewHolder.normalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_normal_three);
			viewHolder.region = (TextView) convertView
					.findViewById(R.id.collection_region_three);
			viewHolder.persons = (TextView) convertView
					.findViewById(R.id.collection_region_persons_three);
			viewHolder.movement = (TextView) convertView
					.findViewById(R.id.collection_region_movement_three);
			viewHolder.motionless = (TextView) convertView
					.findViewById(R.id.collection_region_motionless_three);
			viewHolder.sleep = (TextView) convertView
					.findViewById(R.id.collection_region_sleep_three);
			viewHolder.unnormalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_unnormal_three);
			viewHolder.alarm = (TextView) convertView
					.findViewById(R.id.collection_region_alarm_three);
		} else if (mPieItem == ITEM_FIVE) {
			if (view == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.collection_gridview_item5, null);
			} else {
				convertView = view;
			}
			viewHolder.chart = (PieChartView) convertView
					.findViewById(R.id.collection_chart_five);
			viewHolder.normalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_normal_five);
			viewHolder.region = (TextView) convertView
					.findViewById(R.id.collection_region_five);
			viewHolder.persons = (TextView) convertView
					.findViewById(R.id.collection_region_persons_five);
			viewHolder.movement = (TextView) convertView
					.findViewById(R.id.collection_region_movement_five);
			viewHolder.motionless = (TextView) convertView
					.findViewById(R.id.collection_region_motionless_five);
			viewHolder.sleep = (TextView) convertView
					.findViewById(R.id.collection_region_sleep_five);
			viewHolder.unnormalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_unnormal_five);
			viewHolder.alarm = (TextView) convertView
					.findViewById(R.id.collection_region_alarm_five);
		} else if (mPieItem == ITEM_TEN) {
			if (view == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.collection_gridview_item10, null);
			} else {
				convertView = view;
			}
			viewHolder.chart = (PieChartView) convertView
					.findViewById(R.id.collection_chart_ten);
			viewHolder.normalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_normal_ten);
			viewHolder.region = (TextView) convertView
					.findViewById(R.id.collection_region_ten);
			viewHolder.persons = (TextView) convertView
					.findViewById(R.id.collection_region_persons_ten);
			viewHolder.movement = (TextView) convertView
					.findViewById(R.id.collection_region_movement_ten);

			viewHolder.motionless = (TextView) convertView
					.findViewById(R.id.collection_region_motionless_ten);

			viewHolder.sleep = (TextView) convertView
					.findViewById(R.id.collection_region_sleep_ten);

			viewHolder.unnormalLayout = (LinearLayout) convertView
					.findViewById(R.id.collection_region_unnormal_ten);
			viewHolder.alarm = (TextView) convertView
					.findViewById(R.id.collection_region_alarm_ten);
		}
		if (view == null) {
			convertView.setOnHoverListener(new OnHoverListener() {

				@Override
				public boolean onHover(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_HOVER_ENTER:
						if (bean.getAlarm_num() > 0) {
							v.setBackgroundResource(R.drawable.collection_gridview_item_unnormal_bg);
						} else {
							v.setBackgroundResource(R.drawable.collection_gridview_item_normal_bg);
						}
						startAnimation(v, ANIMATION_BIG, bean, position);
						break;
					case MotionEvent.ACTION_HOVER_EXIT:
						if (bean.getAlarm_num() > 0) {
							v.setBackgroundResource(R.drawable.collection_gridview_item_unnormal_bg);
						} else {
							v.setBackgroundResource(R.drawable.collection_gridview_item_normal_bg);
						}
						startAnimation(v, ANIMATION_SAMLL, bean, position);
						break;
					}
					return false;
				}
			});
			convertView
					.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

						@Override
						public void onViewDetachedFromWindow(View v) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onViewAttachedToWindow(View v) {
							// TODO Auto-generated method stub
							if (bean.getAlarm_num() > 0) {
								v.setBackgroundResource(R.drawable.collection_gridview_item_unnormal_bg);
							} else {
								v.setBackgroundResource(R.drawable.collection_gridview_item_normal_bg);
							}
						}
					});
		}
		return convertView;
	}

	private void fillDatas(ViewHolder viewHolder, AreaActiveBean bean) {
		List<SliceValue> sliceValues = new ArrayList<SliceValue>();
		PieChartData pieData = new PieChartData();
		pieData.setCenterCircleScale(0.85f);
		pieData.setHasCenterCircle(true);

		if (bean.getAlarm_num() > 0) {
			viewHolder.normalLayout.setVisibility(View.GONE);
			viewHolder.unnormalLayout.setVisibility(View.VISIBLE);
			viewHolder.region.setText(bean.getArea_name());
			if (mPieItem == ITEM_THREE) {
				viewHolder.persons.setText(getString(
						R.string.collection_region_persons,
						bean.getPeople_num() + ""));
				String personsText = viewHolder.persons.getText().toString();
				SpannableStringBuilder personsBuilder = new SpannableStringBuilder(
						personsText);
				
				personsBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ffa1a7a1")),
				 	1, personsText.length(),
				 	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				personsBuilder.setSpan(new RelativeSizeSpan(0.66f), personsText.indexOf("人"),
						personsText.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				
				viewHolder.persons.setText(personsBuilder);
				viewHolder.alarm.setText(getString(
						R.string.collection_region_alarm,
						"" + bean.getAlarm_num()));
			} else if (mPieItem == ITEM_FIVE || mPieItem == ITEM_TEN) {
				if (mPieItem == ITEM_FIVE) {
					viewHolder.persons.setText(bean.getPeople_num() + "");
				} else if (mPieItem == ITEM_TEN) {
					viewHolder.persons.setVisibility(View.GONE);
					// viewHolder.persons.setText(bean.getPeople_num() + "");
					// viewHolder.persons
					// .setTextColor(Color.parseColor("#FF5959"));
				}
				viewHolder.alarm.setText(bean.getAlarm_num() + "");
			}
			sliceValues.add(new SliceValue(bean.getAlarm_num(), Color
					.parseColor(COLOR_PIE_ALARM)));
			// if (mPieItem != ITEM_TEN) { // 十个排列模式的时候不显示饼图中间的文字
			pieData.setCenterText1("报警");
			pieData.setCenterText1Color(Color
					.parseColor(COLOR_PIE_UNNORMAL_TEXT));
			// }
		} else {
			viewHolder.normalLayout.setVisibility(View.VISIBLE);
			viewHolder.unnormalLayout.setVisibility(View.GONE);
			if (mPieItem != ITEM_TEN) {
				// viewHolder.state.setText(bean.getState());
				// viewHolder.state.setTextColor(Color.parseColor("#99b1bc"));
			}
			viewHolder.region.setText(bean.getArea_name());
			if (mPieItem == ITEM_THREE) {
				viewHolder.persons.setText(getString(
						R.string.collection_region_persons,
						bean.getPeople_num() + ""));
				String personsText = viewHolder.persons.getText().toString();
				SpannableStringBuilder timeBuilder = new SpannableStringBuilder(
						personsText);
				timeBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
						personsText.indexOf("人"),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				timeBuilder.setSpan(new RelativeSizeSpan(0.66f),
						personsText.indexOf("人"), personsText.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				viewHolder.persons.setText(timeBuilder);
				viewHolder.movement.setText(getString(
						R.string.collection_region_movement, bean.getMove_num()
								+ ""));
				viewHolder.motionless.setText(getString(
						R.string.collection_region_motionless,
						bean.getStop_num() + ""));
				viewHolder.sleep.setText(getString(
						R.string.collection_region_sleep, bean.getSleep_num()
								+ ""));
			} else if (mPieItem == ITEM_FIVE || mPieItem == ITEM_TEN) {
				if (mPieItem == ITEM_FIVE) {
					viewHolder.persons.setText(bean.getPeople_num() + "");
				} else if (mPieItem == ITEM_TEN) {
					viewHolder.persons.setVisibility(View.VISIBLE);
					viewHolder.persons.setText(bean.getPeople_num() + "");
					viewHolder.persons.setTextColor(Color.WHITE);
				}
				viewHolder.movement.setText(bean.getMove_num() + "");
				viewHolder.motionless.setText(bean.getStop_num() + "");
				viewHolder.sleep.setText(bean.getSleep_num() + "");
			}
			sliceValues.add(new SliceValue(bean.getMove_num(), Color
					.parseColor(COLOR_PIE_SPORT)));
			sliceValues.add(new SliceValue(bean.getSleep_num(), Color
					.parseColor(COLOR_PIE_SLEEP)));
			sliceValues.add(new SliceValue(bean.getStop_num(), Color
					.parseColor(COLOR_PIE_STOP)));
			if (mPieItem != ITEM_TEN) {
				pieData.setCenterText1("正常");
				pieData.setCenterText1Color(Color
						.parseColor(COLOR_PIE_NORMAL_TEXT));
			}
		}

		// 添加饼图数据
		viewHolder.chart.setChartRotationEnabled(false); // 设置不能旋转
		viewHolder.chart.setValueTouchEnabled(false); // 设置为不可点击
		viewHolder.chart.setInteractive(false);

		switch (mPieItem) {
		case ITEM_THREE:
			pieData.setCenterText1FontSize(DensityUtil.dip2px(mContext, 13));
			break;
		case ITEM_FIVE:
			pieData.setCenterText1FontSize(DensityUtil.dip2px(mContext, 10));
			break;
		case ITEM_TEN:
			pieData.setCenterText1FontSize(DensityUtil.dip2px(mContext, 5));
			break;
		default:
			break;
		}
		pieData.setValues(sliceValues);
		viewHolder.chart.setPieChartData(pieData);
	}

	private void scaleAnimation(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_HOVER_ENTER:
			scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.05f);
			scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 1.05f);
			break;
		case MotionEvent.ACTION_HOVER_EXIT:
			scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", 1.05f, 1.0f);
			scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", 1.05f, 1.0f);
			break;
		case MotionEvent.ACTION_HOVER_MOVE:
			return;
		}
		AnimatorSet set = new AnimatorSet();
		set.playTogether(scaleXAnimator, scaleYAnimator);
		set.setDuration(250);
		set.start();
	}

	private static class ViewHolder {
		private PieChartView chart;
		private TextView region;
		private TextView persons;
		private LinearLayout normalLayout;
		private TextView movement;
		private TextView motionless;
		private TextView sleep;
		private LinearLayout unnormalLayout;
		private TextView alarm;
	}

	private class ChartLabelView extends View {

		private String mBackColor;
		private String mTextColor1, mTextColor2;
		private String mTextString1, mTextString2;
		private float mX1, mX2;
		private float mY1, mY2;
		private final float labelMargin = 10; // 字体上下左右的大概间距
		private final int mFontSize = 35; // 字体大小

		public ChartLabelView(Context context, String textColor1, String textColor2,
				String backColor, String text1, String text2, float x1, float y1,
				float x2, float y2) {
			super(context);
			mTextColor1 = textColor1;
			mTextColor2 = textColor2;
			mBackColor = backColor;
			mTextString1 = text1;
			mTextString2 = text2;
			mX1 = x1;
			mY1 = y1;
			mX2 = x2;
			mY2 = y2;
		}

		private void drawTextAndBackground(Canvas canvas, String text, 
				float x, float y, String textColor, boolean hasBackGround) {

			Paint backgroundPaint = new Paint();
			backgroundPaint.setAntiAlias(true);
			backgroundPaint.setColor(Color.parseColor(mBackColor));
			
			Paint textPaint = new Paint();
			textPaint.setAntiAlias(true); // 必须添加防锯齿属性，否则画出来的字体显示很怪
			Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
			textPaint.setTypeface(font);
			textPaint.setTextSize(mFontSize);
			textPaint.setColor(Color.parseColor(textColor));
			FontMetrics fontMetrics = new FontMetrics();

			textPaint.getFontMetrics(fontMetrics);

			final float labelWidth = textPaint.measureText(
					text.toCharArray(), 0, text.length());

			final int labelHeight = (int) Math.abs(fontMetrics.ascent);
			float left = x - labelMargin;
			float right = x + labelWidth + labelMargin;

			float top;
			float bottom;

			top = y - labelHeight - 5;
			bottom = y + labelMargin;

			if(hasBackGround){
				canvas.drawRect(new RectF(left, top, right, bottom),
						backgroundPaint);
			}
//			LogUtil.info("hujian", "top: " + top + " bottom: " + bottom + " left: " + left + " right: " + right);
			canvas.drawText(text, x, y, textPaint);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			drawTextAndBackground(canvas, mTextString1, mX1, mY1, mTextColor1, false);
			drawTextAndBackground(canvas, mTextString2, mX2, mY2, mTextColor2, true);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		
		new AlertDialog.Builder(this)
			.setMessage("确认退出应用吗？")
			.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					TVActivity.this.finish();
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			})
			.show();
	}
}