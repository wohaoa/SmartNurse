package com.magicare.smartnurse.activity.mpandroidchart;

import java.lang.reflect.Field;
import java.util.List;

import cn.jpush.android.data.w;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.WarningAdapter;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.dao.DBWarning;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WarinHistoryFragment extends Fragment {

	View mView;
	
	WarningAdapter warningAdapter;

	List<WarningBean> warningBeans;

	WarinData warinData;
	
	
	PullToRefreshListView mylist;
	
	TextView tv_type, tv_adress, tv_time;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mView = View.inflate(getActivity(), R.layout.fragment_warinhistory,
				null);
		

		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		tv_type = (TextView) mView.findViewById(R.id.tv_type);
		tv_adress = (TextView) mView.findViewById(R.id.tv_adress);
		tv_time = (TextView) mView.findViewById(R.id.tv_time);
		
		tv_type.setText(ConfigManager.getStringValue(getActivity(), ConfigManager.CHART_NAME)+"类型");
		tv_adress.setText(ConfigManager.getStringValue(getActivity(), ConfigManager.CHART_NAME)+"地点");
		tv_time.setText(ConfigManager.getStringValue(getActivity(), ConfigManager.CHART_NAME)+"时间");
		
		
		mylist = (PullToRefreshListView) mView.findViewById(R.id.mylist);
		mylist.setMode(Mode.PULL_UP_TO_REFRESH);
		
		mylist.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				warinData.showWarinData(mHandler);
				
			}
		});
		//mylist = (ListView) mView.findViewById(R.id.mylist);
//		TextView textView = new TextView(getActivity());
//		textView.setText("点击查看更多");
//		textView.setBackgroundColor(Color.rgb(240, 241, 238));
//		textView.setLayoutParams(new AbsListView.LayoutParams(
//				LayoutParams.FILL_PARENT, 70));
//		textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//		mylist.addFooterView(textView);
//		textView.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				warinData.showWarinData(mHandler);
//			}
//		});
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			mylist.onRefreshComplete();

			warningBeans = (List<WarningBean>) msg.obj;
			if (warningAdapter == null) {
				warningAdapter = new WarningAdapter(getActivity(), warningBeans);
				mylist.setAdapter(warningAdapter);
			} else {
				warningAdapter.notifyDataSetChanged();
			}
			
			

		}
	};

	/**
	 * 设置数据
	 */
	public void addData(List<WarningBean> warningBeans) {

		warningAdapter = new WarningAdapter(getActivity(), warningBeans);
		mylist.setAdapter(warningAdapter);

	}

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			warinData = (WarinData) activity;
		} catch (Exception e) {
			// TODO: handle exception
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

	public interface WarinData {
		public void showWarinData(Handler handler);
	}

}