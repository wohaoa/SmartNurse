package com.magicare.smartnurse.activity.mpandroidchart;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.SportsBean;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.LogUtil;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OldUserSleepInfoFragment1 extends Fragment implements
		OnClickListener {
	View mView;
	private ComboLineColumnChartData data;
	private Button btn_previour;
	private Button btn_next;
	List<SportsBean> sportsBeans;
	List<SportsBean> testBeans;
	int position = 0;
	private TextView tv_mile;
	private TextView tv_step;
	private TextView tv_calorie;
	ComboLineColumnChartView mchart;
	LinearLayout line_block;
	private TextView tv_sleeplong;
	private TextView tv_deepsleep;
	// 图标每条线的颜色
	private final String COLOR_OFFLINE = "#fff4de52";
	private final String COLOR_CALORIE = "#ffd76532";
	private final String COLOR_SLEEP = "#ff74a741";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_oldusersleepinfo1, null);
		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null) {
			parent.removeView(mView);// 先移除
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		btn_previour = (Button) mView.findViewById(R.id.btn_previour);
		btn_previour.setEnabled(true);
		btn_previour.setTextColor(getResources().getColor((R.color.chart_btn_enable)));
		btn_next = (Button) mView.findViewById(R.id.btn_next);
		btn_next.setEnabled(false);
		btn_next.setTextColor(getResources().getColor((R.color.chart_btn_nonenable)));
		tv_mile = (TextView) mView.findViewById(R.id.tv_mile);
		tv_step = (TextView) mView.findViewById(R.id.tv_step);
		tv_calorie = (TextView) mView.findViewById(R.id.tv_calorie);
		tv_sleeplong = (TextView) mView.findViewById(R.id.tv_sleeplong);
		tv_deepsleep = (TextView) mView.findViewById(R.id.tv_deepsleep);
		btn_previour.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		mchart = (ComboLineColumnChartView) mView.findViewById(R.id.chart);
		mchart.setZoomType(ZoomType.HORIZONTAL);
		mchart.setPadding(0, 20, 20, 0);
		line_block = (LinearLayout) mView.findViewById(R.id.line_block);
	}

	/**
	 * 添加
	 * 
	 * @param index
	 */

	List<AxisValue> axisValues;

	public void addDate(List<SportsBean> sportsBeans, int position) {
		testBeans = new ArrayList<SportsBean>();

		// if(position == 0){
		// sportsBeans.clear();
		// String time = DateUtil.getDatePerviousAndNext(position);
		// for(int i = 0; i < 120; i++){
		// SportsBean bean = new SportsBean();
		// bean.setStart_time(DateUtil.addMinute(time, i * 10));
		// bean.setEnd_time(DateUtil.addMinute(time, i * 10 + 10));
		// if(i % 3 == 0){
		// bean.setMode(255);
		// }else{
		// bean.setMode(254);
		// }
		// bean.setState(0);
		// bean.setCalorie(25);
		// bean.setSleep_quantity(30);
		// bean.setStep(1);
		// bean.setMeter(1);
		//
		// sportsBeans.add(bean);
		// }
		// }

		if (position == 0) {
			btn_next.setEnabled(false);
			btn_next.setTextColor(getResources().getColor((R.color.chart_btn_nonenable)));
		}

		for (int i = 0; i < sportsBeans.size(); i++) {

			if (sportsBeans.get(i).getStart_time().split(" ")[0]
					.equals(DateUtil.getDatePerviousAndNext(position)
							.split(" ")[0])) {
				testBeans.add(sportsBeans.get(i));
			}
		}
		sportsBeans.clear();
		sportsBeans.addAll(testBeans);
		LogUtil.info(OldUserSleepInfoFragment1.class,
				"addDate sportsBeans.size=" + sportsBeans.size());

		line_block.removeAllViews();
		setSportsData(sportsBeans); // 汇总步数，米数，卡路里 睡眠数据
		this.position = position;

		String temp_start, temp_end;
		StringBuffer time;
		char x;
		float maxVertical = 0; // 纵坐标的最大值

		// 把每段时间归为分钟数为整十的时间
		for (int j = 0; j < sportsBeans.size(); j++) {

			time = new StringBuffer(sportsBeans.get(j).getStart_time()); // 获得每个开始点的时间
			x = time.charAt(15);
			time.deleteCharAt(15);
			time.insert(15, "0");

			// 将秒字段舍弃为0,这样好统一比较
			time.deleteCharAt(17);
			time.deleteCharAt(17); // 这里要删两次17的位置。。容易搞混。。。
			time.insert(17, "0");
			time.insert(18, "0");

			// 四舍五入每段起始点时间为整十数
			if (Character.getNumericValue(x) < 5) {
				temp_start = time.toString();
				sportsBeans.get(j).setStart_time(time.toString());
			} else {
				temp_start = DateUtil.addMinute(time.toString(), 10);
				sportsBeans.get(j).setStart_time(temp_start);
			}

			if (sportsBeans.get(j).getState() == 0) { // 在线时间段
				sportsBeans.get(j).setEnd_time(
						DateUtil.addMinute(temp_start, 10));

			} else {// 离线时间段
				time = new StringBuffer(sportsBeans.get(j).getEnd_time()); // 获得每个结束点的时间
				x = time.charAt(15);
				time.deleteCharAt(15);
				time.insert(15, "0");
				if (Character.getNumericValue(x) < 5) {
					temp_end = time.toString();
					sportsBeans.get(j).setEnd_time(temp_end);
				} else {
					temp_end = DateUtil.addMinute(time.toString(), 10);
					sportsBeans.get(j).setEnd_time(temp_end);
				}
				int time_value = (int) DateUtil.getMinuteDiff(temp_start,
						temp_end) / 10;
				sportsBeans.get(j).setTime_value(time_value); // 离线时的睡眠运动数据打点个数
			}
			
			// 计算Y轴最大值
			if (sportsBeans.get(j).getCalorie() > sportsBeans.get(j).getSleep_quantity()) {
				maxVertical = (maxVertical > sportsBeans.get(j).getCalorie()) ? maxVertical : sportsBeans.get(j).getCalorie();
			} else {
				maxVertical = (maxVertical > sportsBeans.get(j).getSleep_quantity()) ? maxVertical : sportsBeans.get(j).getSleep_quantity();
			}			
		}

		// 格式化横坐标值和加点
		if (sportsBeans != null && sportsBeans.size() > 0) {

			// 格式化横坐标值
			axisValues = new ArrayList<AxisValue>();
			List<Line> lines = new ArrayList<Line>(); // 所有曲线的集合(按条)

			String first = sportsBeans.get(0).getStart_time(); // 第一段时间片的开始时刻
			LogUtil.info(OldUserSleepInfoFragment1.class, "开始点的时间:" + first);

			String last = sportsBeans.get(sportsBeans.size() - 1).getEnd_time();// 最后一段时间片的结束时刻
			LogUtil.info(OldUserSleepInfoFragment1.class, "结束点的时间:" + last);

			long diff = 0;
			diff = DateUtil.getMinuteDiff(first, last);
			int size = (int) (diff / 10 + 1);
			LogUtil.info(OldUserSleepInfoFragment1.class, "点的个数:" + size);

			String everypoint1 = first;
			for (int i = 0; i < size; i++) {
				// 格式化横坐标
				if (i == 0) {
					String point = DateUtil.formathhmm(everypoint1, true);
					LogUtil.info(OldUserSleepInfoFragment1.class, "第0个横坐标:"
							+ point);
					axisValues.add(new AxisValue(i, point.toCharArray()));
				} else {
					everypoint1 = DateUtil.addMinute(everypoint1, 10);
					String point = DateUtil.formathhmm(everypoint1, false);
					LogUtil.info(OldUserSleepInfoFragment1.class, "第" + i
							+ "个横坐标:" + point);
					axisValues.add(new AxisValue(i, point.toCharArray()));
				}
			}

			// 绘制两条线
			for (int j = 0; j < 2; j++) {
				List<PointValue> values = new ArrayList<PointValue>();
				for (int i = 0; i < size; i++) {
					String tempScale = DateUtil.addMinute(first, 10 * i);
					values.add(new PointValue(i, 0));
					for (SportsBean sportsBean : sportsBeans) {
						if (sportsBean.getStart_time().equals(tempScale)) {							
							// 在线时间
							if(sportsBean.getState() == 0){
								if (j == 0) {
									values.set(i, new PointValue(i, sportsBean.getCalorie()));
								} else {
									values.set(i, new PointValue(i, sportsBean.getSleep_quantity()));
								}
							}else{ // 离线时间								
								for(int k = 0; k < sportsBean.getTime_value(); k++){
									if(i >= values.size()){
										if (j == 0) {
											values.add(i, new PointValue(i, sportsBean.getCalorie()));
										} else {
											values.add(i, new PointValue(i, sportsBean.getSleep_quantity()));
										}
									}else{
										if (j == 0) {
											values.set(i, new PointValue(i, sportsBean.getCalorie()));
										} else {
											values.set(i, new PointValue(i, sportsBean.getSleep_quantity()));
										}
									}
									i++;
								}
							}						
							break;
						}
					}
				}
				View view = View.inflate(getActivity(),
						R.layout.linearlayout_item, null);
				ImageView imageView = (ImageView) view
						.findViewById(R.id.iv_image);
				TextView textView = (TextView) view.findViewById(R.id.tv_lable);
				Line line = new Line(values);
				if (j == 0) {
					line.setColor(Color.parseColor(COLOR_CALORIE));
					imageView.setBackgroundColor(Color
							.parseColor(COLOR_CALORIE));
					textView.setText("卡路里");
				} else if (j == 1) {
					line.setColor(Color.parseColor(COLOR_SLEEP));
					imageView.setBackgroundColor(Color.parseColor(COLOR_SLEEP));
					textView.setText("睡眠");
				}
				line_block.addView(view);
				line.setShape(ValueShape.CIRCLE);
				line.setCubic(false);
				line.setFilled(false);
				line.setHasLabels(false);
				line.setHasLabelsOnlyForSelected(false);
				line.setHasLines(true);
				line.setHasPoints(true);
				lines.add(line);
			}			
			
//			for (int j = 0; j < 2; j++) { // 两条线
//				// 点的集合
//				List<PointValue> values = new ArrayList<PointValue>();
//				List<SportsBean> beans = new ArrayList<SportsBean>();
//
//				beans.addAll(sportsBeans);
//				for (int i = 0; i < size; i++) {
//					String everypoint = DateUtil.addMinute(first, 10 * i);
//
//					for (SportsBean sportsBean : beans) {
//
//						if (sportsBean.getCalorie() > sportsBean
//								.getSleep_quantity()) {
//							maxVertical = (maxVertical > sportsBean
//									.getCalorie()) ? maxVertical : sportsBean
//									.getCalorie();
//						} else {
//							maxVertical = (maxVertical > sportsBean
//									.getSleep_quantity()) ? maxVertical
//									: sportsBean.getSleep_quantity();
//						}
//
//						if (sportsBean.getState() == 1) {
//							// 如果为离线时间片
//							if (sportsBean.getStart_time().equals(everypoint)) {
//								for (int k = 0; k < sportsBean.getTime_value(); k++) {
//									if (j == 0) {
//										values.add(new PointValue(i, sportsBean
//												.getCalorie()));
//									} else if (j == 1) {
//										values.add(new PointValue(i, sportsBean
//												.getSleep_quantity()));
//									}
//									i++;
//								}
//								beans.remove(sportsBean);
//								break;
//							}
//						} else {
//							// 如果为在线时间片
//							if (sportsBean.getStart_time().equals(everypoint)) { // 结束时间匹配时间片的end_time
//								if (j == 0) {
//									values.add(new PointValue(i, sportsBean
//											.getCalorie()));
//								} else if (j == 1) {
//									values.add(new PointValue(i, sportsBean
//											.getSleep_quantity()));
//								}
//								beans.remove(sportsBean);
//								break;
//							} else {
//								if (j == 0) {
//									values.add(new PointValue(i, (float) 0));
//								} else if (j == 1) {
//									values.add(new PointValue(i, (float) 0));
//								}
//							}
//						}
//					}
//				}
//
//				View view = View.inflate(getActivity(),
//						R.layout.linearlayout_item, null);
//				ImageView imageView = (ImageView) view
//						.findViewById(R.id.iv_image);
//				TextView textView = (TextView) view.findViewById(R.id.tv_lable);
//				Line line = new Line(values);
//				if (j == 0) {
//					// line.setColor(Color.rgb(255, 127, 79));
//					// imageView.setBackgroundColor(Color.rgb(255, 127, 79));
//					line.setColor(Color.parseColor(COLOR_CALORIE));
//					imageView.setBackgroundColor(Color
//							.parseColor(COLOR_CALORIE));
//					textView.setText("卡路里");
//				} else if (j == 1) {
//					// line.setColor(Color.rgb(59, 203, 62));
//					// imageView.setBackgroundColor(Color.rgb(59, 203, 62));
//					line.setColor(Color.parseColor(COLOR_SLEEP));
//					imageView.setBackgroundColor(Color.parseColor(COLOR_SLEEP));
//					textView.setText("睡眠");
//				}
//				line_block.addView(view);
//				line.setShape(ValueShape.CIRCLE);
//				line.setCubic(false);
//				line.setFilled(false);
//				line.setHasLabels(false);
//				line.setHasLabelsOnlyForSelected(false);
//				line.setHasLines(true);
//				line.setHasPoints(false);
//				lines.add(line);
//			}

			// 绘制离线的标签说明
			View view = View.inflate(getActivity(), R.layout.linearlayout_item,
					null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
			TextView textView = (TextView) view.findViewById(R.id.tv_lable);
			imageView.setBackgroundColor(Color.parseColor(COLOR_OFFLINE));
			textView.setText("离线状态");
			line_block.addView(view);

			List<Column> columns = new ArrayList<Column>(); // 所有柱状图的集合

			List<SubcolumnValue> subColumnvalues = new ArrayList<SubcolumnValue>();

			List<SubcolumnValue> subColumnvalues1 = new ArrayList<SubcolumnValue>();
			subColumnvalues1.add(new SubcolumnValue((float) 0, Color.rgb(255,
					127, 77)));

			List<SportsBean> beans1 = new ArrayList<SportsBean>();

			beans1.addAll(sportsBeans);

			for (int i = 0; i < size; i++) {
				String every = DateUtil.addMinute(first, 10 * i);

				for (SportsBean sportsBean : beans1) {
					if (sportsBean.getState() == 1) {
						// 如果为离线时间片
						if (sportsBean.getStart_time().equals(every)) {
							for (int k = 0; k <= sportsBean.getTime_value(); k++) {
								i++;
								subColumnvalues.clear();
								subColumnvalues.add(new SubcolumnValue(
										maxVertical + 30, Color
												.parseColor(COLOR_OFFLINE)));// 只有一个值
								columns.add(new Column(subColumnvalues));
							}
							beans1.remove(sportsBean);
							break;
						}
					}
				}
				columns.add(new Column(subColumnvalues1));
			}

			setDate(lines, columns, axisValues, size, maxVertical + 60);
		} else {
			initDate();
		}

	}

	private void initDate() {

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		List<Line> lines = new ArrayList<>();
		List<Column> columns = new ArrayList<>();
		List<SubcolumnValue> subColumnvalues1 = new ArrayList<SubcolumnValue>();
		subColumnvalues1.add(new SubcolumnValue(800, Color
				.parseColor("#00000000")));

		String first = DateUtil.getDatePerviousAndNext(position).split(" ")[0]
				+ " 00:00:00"; // 第一段时间片的开始时刻
		String last = DateUtil.getDatePerviousAndNext(position).split(" ")[0]
				+ " 24:00:00"; // 最后一段时间片的结束时刻

		long diff = 0;
		diff = DateUtil.getMinuteDiff(first, last);

		int size = (int) (diff / 10 + 1);

		String everypoint1 = first;

		for (int i = 0; i < size; i++) {
			// 格式化横坐标
			if (i == 0) {
				String point = DateUtil.formathhmm(everypoint1, true);
				axisValues.add(new AxisValue(i, point.toCharArray()));
			} else {
				everypoint1 = DateUtil.addMinute(everypoint1, 10);
				String point = DateUtil.formathhmm(everypoint1, false);
				axisValues.add(new AxisValue(i, point.toCharArray()));
			}

			columns.add(new Column(subColumnvalues1));
		}

		// 绘制 指示图标 卡路里 睡眠
		for (int i = 0; i < 3; i++) {

			View view = View.inflate(getActivity(), R.layout.linearlayout_item,
					null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
			TextView textView = (TextView) view.findViewById(R.id.tv_lable);

			if (i == 0) {
				// imageView.setBackgroundColor(Color.rgb(255, 127, 79));
				imageView.setBackgroundColor(Color.parseColor(COLOR_CALORIE));
				textView.setText("卡路里");
			} else if (i == 1) {
				// imageView.setBackgroundColor(Color.rgb(59, 203, 62));
				imageView.setBackgroundColor(Color.parseColor(COLOR_SLEEP));
				textView.setText("睡眠");
			} else if (i == 2) {
				// imageView.setBackgroundColor(Color.rgb(59, 203, 62));
				imageView.setBackgroundColor(Color.parseColor(COLOR_OFFLINE));
				textView.setText("离线状态");
			}

			line_block.addView(view);
		}

		// List<SubcolumnValue> valuesColumn = new ArrayList<SubcolumnValue>();
		// Column column = new Column(valuesColumn);
		// columns.add(column);

		lines.add(new Line());
		setDate(lines, columns, axisValues, size, 800);
		// resetViewport(axisValues.size());
	}

	public void setDate(List<Line> lines, List<Column> columns,
			List<AxisValue> axisValues, float maxHorizontal, float maxVertical) {

		LineChartData lineChartData = new LineChartData(lines);
		ColumnChartData columnChartData = new ColumnChartData(columns);
		columnChartData.setFillRatio(1.0f);

		List<AxisValue> axisValues2 = new ArrayList<AxisValue>();
		AxisValue axisValue = new AxisValue(maxVertical);
		axisValues2.add(axisValue);

		lineChartData.setBaseValue(0);
		columnChartData.setBaseValue(0);

		data = new ComboLineColumnChartData(columnChartData, lineChartData);
		data.setAxisXBottom(new Axis(axisValues).setHasLines(true)
				.setMaxLabelChars(7).setLineColor(Color.rgb(227, 227, 227))
//				.setTextColor(Color.rgb(102, 102, 102)));
				.setTextColor(getResources().getColor(R.color.chart_scale_text)));
		data.setAxisYLeft(new Axis().setHasLines(true)
				.setLineColor(Color.rgb(227, 227, 227))
//				.setTextColor(Color.rgb(102, 102, 102)));
				.setTextColor(getResources().getColor(R.color.chart_scale_text)));				

		mchart.setComboLineColumnChartData(data);
		// mchart.setBackgroundColor(Color.parseColor("#33D2D2D2"));
		Viewport viewport = mchart.getMaximumViewport();
		viewport.right = maxHorizontal;
		viewport.top = maxVertical;
		viewport.bottom = 0; // Y轴的起始位置
		mchart.setMaximumViewport(viewport);
		mchart.setCurrentViewport(viewport);

		resetViewport(axisValues.size());
	}

	private void resetViewport(int size) {

		Viewport v = new Viewport(mchart.getMaximumViewport());
		v.left = 0;
		if (size <= 24) {
			v.right = size;
		} else {
			v.right = 25;
		}
		mchart.setCurrentViewport(v);
		mchart.setViewportCalculationEnabled(false);
	}

	public void setSportsData(List<SportsBean> sportsBeans) {
		float metertotal = 0;
		float steptotal = 0;
		float calorietotal = 0;
		float sleepTime = 0; // 总共的睡眠时间

		for (SportsBean sportsBean : sportsBeans) {
			metertotal += sportsBean.getMeter();
			steptotal += sportsBean.getStep();
			calorietotal += sportsBean.getCalorie();
			// 统计睡眠时间
			if (sportsBean.getMode() == 254) {
				sleepTime += DateUtil.getMinuteDiff(sportsBean.getStart_time(),
						sportsBean.getEnd_time());
			}
		}
		tv_mile.setText((int) metertotal + "米");
		tv_step.setText((int) steptotal + "步");
		tv_calorie.setText((int) calorietotal + "KJ");
		tv_sleeplong.setText((int) sleepTime + "min");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_previour:
			// initDate();
			sportsdata.showSportsData(mHandler, false);
			break;

		case R.id.btn_next:
			// initDate();
			sportsdata.showSportsData(mHandler, true);
			break;

		}
	}

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			sportsdata = (SportsData) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();

		}
	};

	Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			sportsBeans = (List<SportsBean>) msg.obj;
			position = msg.arg1;
			addDate(sportsBeans, position);

			if (position != 0) {
				btn_next.setEnabled(true);
				btn_next.setTextColor(getResources().getColor((R.color.chart_btn_enable)));

			} else {
				btn_next.setEnabled(false);
				btn_next.setTextColor(getResources().getColor((R.color.chart_btn_nonenable)));
			}

		};

	};

	public void onDetach() {

		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	SportsData sportsdata;

	public interface SportsData {
		public void showSportsData(Handler handler, boolean isFlag);
	}
}