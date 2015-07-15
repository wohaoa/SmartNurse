package com.magicare.smartnurse.activity.mpandroidchart;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.UserDetailActivity;
import com.magicare.smartnurse.activity.mpandroidchart.OldUserSleepInfoFragment1.SportsData;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.utils.DateUtil;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OldUserHealthInfoFragment1 extends Fragment implements OnClickListener{
	View mView;
	LineChartView chart;
	boolean isInit;
	private LineChartData data;
	int xnum; // 表示横坐标的个数
	int ynum = 200;
	Integer[] colors = new Integer[] { Color.rgb(255, 127, 79),
			Color.rgb(102, 151, 234), Color.rgb(218, 112, 214),
			Color.rgb(255, 62, 129) };
	String[] strs = new String[] { "体重", "血压", "心率", "血糖" };
	private LinearLayout line_block;

//	private Map<Integer, String> map = new HashMap<Integer, String>();
	
	private Button mBtnPrevious, mBtnNext;
	
	private UserDetailActivity mActivity;
	
	private int healthPosition = 0; // 体重、血压、心率、血糖曲线月数的序号，0表示当前月，-1表示上个月

//	public void setArrayList(ArrayList<Integer> arrayList) {
//		map.clear();
//		for (int i = 0; i < arrayList.size(); i++) {
//			if (R.id.btn_weight == arrayList.get(i)) {
//				map.put(i, strs[0]);
//			} else if (R.id.btn_blood == arrayList.get(i)) {
//				map.put(i, strs[1]);
//			} else if (R.id.btn_heartrate == arrayList.get(i)) {
//				map.put(i, strs[2]);
//			} else if (R.id.btn_bloodsugar == arrayList.get(i)) {
//				map.put(i, strs[3]);
//			}
//		}
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.fragment_olduserhealthinfo1, null);
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
		chart = (LineChartView) mView.findViewById(R.id.chart);
		line_block = (LinearLayout) mView.findViewById(R.id.line_block);
		mBtnPrevious = (Button)mView.findViewById(R.id.btn_previour);
		mBtnPrevious.setTextColor(getResources().getColor(R.color.chart_btn_enable));
		mBtnPrevious.setOnClickListener(this);
		mBtnNext = (Button)mView.findViewById(R.id.btn_next);
		mBtnNext.setEnabled(false);
		mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_nonenable));
		mBtnNext.setOnClickListener(this);
		initDate();
		chart.setPadding(0, 20, 20, 0);
		// chart.setZoomType(ZoomType.HORIZONTAL);
		chart.setOnValueTouchListener(new ValueTouchListener());
		chart.setZoomType(ZoomType.HORIZONTAL);
	}

	private void initDate() {
		xnum = 1;
		ynum = 200;
		isInit = false;

		List<AxisValue> axisValues = new ArrayList<AxisValue>();
		List<Line> lines = new ArrayList<>();

		List<PointValue> values = new ArrayList<PointValue>();
		for (int j = 0; j < xnum; ++j) {
			String curDate = DateUtil.getMonPreviousOrNext(healthPosition).split("-")[0]
					+ "-" + DateUtil.getMonPreviousOrNext(healthPosition).split("-")[1] + "-01 00:00:00";
			String initdate = DateUtil.formatDate(
					curDate, true);
			axisValues.add(new AxisValue(j, initdate.toCharArray()));
		}
		Line line = new Line(values);
		lines.add(line);
		setDate(lines, axisValues);
		line_block.removeAllViews();
	}

	public void setDate(List<Line> lines, List<AxisValue> axisValues) {
		data = new LineChartData();
		data.setLines(lines);
		data.setAxisXBottom(new Axis(axisValues).setHasLines(true)
				.setMaxLabelChars(5).setLineColor(Color.rgb(227, 227, 227))
//				.setTextColor(Color.rgb(102, 102, 102)));
				.setTextColor(getResources().getColor(R.color.chart_scale_text)));
		data.setAxisYLeft(new Axis().setHasLines(true)
				.setLineColor(Color.rgb(227, 227, 227))
//				.setTextColor(Color.rgb(102, 102, 102)));
				.setTextColor(getResources().getColor(R.color.chart_scale_text)));
		data.setAxisYRight(new Axis().setFormatter(new HeightValueFormatter())
				.setName("血糖 MMOL/L").setTextColor(colors[3]));
		data.setBaseValue(Float.NEGATIVE_INFINITY);
		chart.setLineChartData(data);
		resetViewport();
	}

	private void resetViewport() {
		// Reset viewport height range to (0,100)
		final Viewport v = new Viewport(chart.getMaximumViewport());
		
		v.bottom = 0;
		v.top = v.height() + 10;
		v.left = 0;
		v.right = xnum;
		chart.setMaximumViewport(v);
		chart.setCurrentViewport(v);
	}

	/**
	 * 添加
	 * 
	 * @param index
	 */
	List<AxisValue> axisValues;
	String temp;
	List<Map<Integer, Integer>> xToIndex = new ArrayList<Map<Integer, Integer>>(); // x轴的值跟healsBeans中的值的对应关系

	public void addDate(List<HealthBean> healthBeans, int index, int position) {

		healthPosition = position;
		
		if(position == 0){
			mBtnNext.setEnabled(false);
			mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_nonenable));
		}else{
			mBtnNext.setEnabled(true);
			mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_enable));			
		}
		
		// 把时间倒序过来
//		if(healthBeans != null && healthBeans.size() > 0){
//			List<HealthBean> tempBeans = new ArrayList<HealthBean>();
//			for(int i = healthBeans.size() - 1; i > -1; i--){
//				tempBeans.add(healthBeans.get(i));
//			}
//			healthBeans = tempBeans;
//			tempBeans = null;
//		}
		
		if (healthBeans != null && healthBeans.size() > 0) {
			
			int count = 0;// 线的条数
			float temp = 0; // 通用的Y轴值
			float tempDis = 0;// 血压的
			
			
//			try {
//				healthBeans = DateUtil1.getListHealth(healthBeans);
//				beans = healthBeans;
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			int startDay = Integer.parseInt(healthBeans.get(0).getCollect_time().split(" ")[0].split("-")[2]);
			int endDay = Integer.parseInt(healthBeans.get(healthBeans.size() - 1).getCollect_time().split(" ")[0].split("-")[2]);			
			
			if (healthBeans.size() > 0) {
				xnum = (endDay - startDay + 1) * 3; // 每天三个点
			} else {
				xnum = 1;
			}
			
			axisValues = new ArrayList<AxisValue>();
			List<Line> lines = data.getLines();
			if (!isInit) {
				lines.remove(0);
				isInit = true;
			}
			if (index == 1) {
				count = 2;
			} else {
				count = 1;
			}

			xToIndex.clear();
			
			for(int i = 0; i < count; i++){ // 只有血压的模块才会遍历两次
				List<PointValue> values = new ArrayList<PointValue>();
				String tempDate = null;
				for(int j = 0; j < xnum; j++){
//					if(j % 4 == 0){ // 画时间点
//						try{
//							tempDate = DateUtil.getDatePerviousAndNext(healthBeans.get(0).getCollect_time(), j / 4);
//						}catch(Exception e){
//							
//						}
//						if(tempDate != null && i == 0){ // 画两根线时，横坐标保证只添加一次
//							axisValues.add(new AxisValue(j, DateUtil
//								.formatDate(tempDate, false)
//								.toCharArray()));
//						}
//					}else{
					{
						try{
							tempDate = DateUtil.getDatePerviousAndNext(healthBeans.get(0).getCollect_time(), j / 3);
						}catch(Exception e){
							
						}
						if(tempDate == null){
							tempDate = "2015-01-01 12:01";
						}
						if(i == 0){ // 画两根线时，横坐标保证只添加一次
							axisValues.add(new AxisValue(j, DateUtil
								.formatDate(tempDate, false)
								.toCharArray()));
							axisValues.add(new AxisValue(j + 1, DateUtil
									.formatDate(tempDate, false)
									.toCharArray()));
							axisValues.add(new AxisValue(j + 2, DateUtil
									.formatDate(tempDate, false)
									.toCharArray()));
						}
						
						for(int k = 0; k < healthBeans.size(); k++){
							String collectTime = healthBeans.get(k).getCollect_time().split(" ")[0];
							if(tempDate.split(" ")[0].equals(collectTime)){
								int pointCount = 1;
								if((k + 1) < healthBeans.size()
									&& healthBeans.get(k + 1).getCollect_time().split(" ")[0]
											.equals(tempDate.split(" ")[0])){
									pointCount++;
									if((k + 2) < healthBeans.size()
										&& healthBeans.get(k + 2).getCollect_time().split(" ")[0]
												.equals(tempDate.split(" ")[0])){
										pointCount++;
									}
								}
								
								for(int h = k; h < pointCount + k; h++){
									if (index == 0) {
										temp = healthBeans.get(h).getWeight();
									} else if (index == 1) {
										temp = healthBeans.get(h).getSystolic_pressure();
										tempDis = healthBeans.get(h).getDiastolic_pressure();
									} else if (index == 2) {
										temp = healthBeans.get(h).getHeart_rate();
									} else if (index == 3) {
										temp = (float) (healthBeans.get(h).getBlood_sugar() * 10.0);
									}
									
									if (i == 0) { // 除了血压以外，都只有一根线
										if (((int) temp) != 0) {								
											PointValue pointValue = new PointValue(j + (h - k), temp);
											switch (index) {
											case 0: // 体重
												pointValue.setLabel((healthBeans.get(h).getCollect_time()
														+ " 体重: " + temp + " kg").toCharArray());
												break;
											case 1: // 血压
												pointValue.setLabel((healthBeans.get(h).getCollect_time()
														+ " 血压 高压: " + temp + " mmHg").toCharArray());
												break;
											case 2: // 心率
												pointValue.setLabel((healthBeans.get(h).getCollect_time()
														+ " 心率: " + temp + " BPM").toCharArray());												
												break;
											case 3: // 血糖
												pointValue.setLabel((healthBeans.get(h).getCollect_time()
														+ " 血糖: " + temp / 10.0 + " MOL/L").toCharArray());												
												break;
											default:
												break;
											}										
											values.add(pointValue);
										}
									} else if (i == 1) { // 血压，低压
										if (((int) tempDis) != 0) {										
											PointValue pointValue = new PointValue(j + (h - k), tempDis);
											pointValue.setLabel((healthBeans.get(h).getCollect_time()
													+ " 血压 低压: " + tempDis + " mmHg").toCharArray());
											values.add(pointValue);											
										}
									}
								}
								break;
							}
						}
						j += 2;
					}
				}
				Line line = new Line(values);
				line.setColor(colors[index]);
				line.setShape(ValueShape.CIRCLE);
				line.setCubic(false);
				line.setFilled(false);
				line.setHasLabels(false);
				line.setHasLabelsOnlyForSelected(false);
				line.setHasLines(true);
				line.setHasPoints(true);
				lines.add(line);
			}

			setDate(lines, axisValues);
			View view = View.inflate(getActivity(), R.layout.linearlayout_item,
					null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
			imageView.setBackgroundColor(colors[index]);
			TextView textView = (TextView) view.findViewById(R.id.tv_lable);
			textView.setText(strs[index]);
			line_block.addView(view);

		} else {

			initDate();
		}
	}

	public void removeAll() {
		initDate();
	}

	/**
	 * 移除单个
	 * 
	 * @param index
	 */
	public void removeDataSet(int index, int id) {
		List<Line> lines = data.getLines();
		if (lines.size() == 1) {
			lines.remove(0);
		} else {
			lines.remove(index);
			if (id == R.id.btn_blood) {
				lines.remove(index);
			}

		}
		if (lines.size() == 0) {
			isInit = false;
			initDate();
			return;
		}
		setDate(lines, axisValues);

		try {
			line_block.removeViewAt(index);
		} catch (Exception e) {
			line_block.removeViewAt(index - 1);
		}

	}

	private class ValueTouchListener implements LineChartOnValueSelectListener {

		@Override
		public void onValueSelected(int lineIndex, int pointIndex,
				PointValue value) {
			
			PromptManager.showToast(
					getActivity(),
					true, String.valueOf(value.getLabel()));
//			
//			LogUtil.info("hujian", "onValueSelected label: " + String.valueOf(value.getLabel()));
//			
//			DecimalFormat df = new DecimalFormat("#0.0");
//			if (map.get(lineIndex).equals("血糖")) {
//				PromptManager.showToast(
//						getActivity(),
//						true,
//						pointBeans.get(pointIndex).getCollect_time() + " "
//								+ map.get(lineIndex) + ":"
//								+ df.format(value.getY() / 10.0) + "MOL/L");
//			} else if (map.get(lineIndex).equals("血压")) {
//				PromptManager.showToast(
//						getActivity(),
//						true,
//						pointBeans.get(pointIndex).getCollect_time()
//								+ " "
//								+ map.get(lineIndex)
//								+ " 高压:"
//								+ df.format(pointBeans.get(pointIndex)
//										.getSystolic_pressure())
//								+ "mmHg 低压:"
//								+ df.format(pointBeans.get(pointIndex)
//										.getDiastolic_pressure()) + "mmHg");
//
//			} else if (map.get(lineIndex).equals("心率")) {
//				PromptManager.showToast(
//						getActivity(),
//						true,
//						pointBeans.get(pointIndex).getCollect_time() + " "
//								+ map.get(lineIndex) + ":"
//								+ df.format(value.getY()) + "BPM");
//			} else if (map.get(lineIndex).equals("体重")) {
//				PromptManager.showToast(
//						getActivity(),
//						true,
//						pointBeans.get(pointIndex).getCollect_time() + " "
//								+ map.get(lineIndex) + ":"
//								+ df.format(value.getY()) + "kg");
//			}

		}

		@Override
		public void onValueDeselected() {
			// TODO Auto-generated method stub

		}

	}
	
	@Override
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.btn_previour:
			healthPosition--;
			if(healthPosition == 0){
				mBtnNext.setEnabled(false);
				mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_nonenable));
			}else{
				mBtnNext.setEnabled(true);
				mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_enable));
			}
			mActivity.showHealthData(healthPosition);
			break;
		case R.id.btn_next:
			healthPosition++;
			if(healthPosition == 0){
				mBtnNext.setEnabled(false);
				mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_nonenable));
			}else{
				mBtnNext.setEnabled(true);
				mBtnNext.setTextColor(getResources().getColor(R.color.chart_btn_enable));
			}			
			mActivity.showHealthData(healthPosition);
			break;
		default:
			break;
		}
	}
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			mActivity = (UserDetailActivity) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();

		}
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
	
	public interface HealthDataClickListener{
		public void showHealthData(int healthPosition);
	}
}

/**
 * Recalculated height values to display on axis.
 */
class HeightValueFormatter extends SimpleAxisValueFormatter {

	@Override
	public int formatValueForAutoGeneratedAxis(char[] formattedValue,
			float value, int autoDecimalDigits) {
		float scaledValue = value * 0.1f;

		return super.formatValueForAutoGeneratedAxis(formattedValue,
				scaledValue, 1);
	}

}