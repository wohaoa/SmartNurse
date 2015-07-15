package com.magicare.smartnurse.activity.mpandroidchart;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.CollectFragment;
import com.magicare.smartnurse.activity.mpandroidchart.WarinHistoryFragment.WarinData;
import com.magicare.smartnurse.adapter.CollectRecordAdapter;
import com.magicare.smartnurse.adapter.WarningAdapter;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.dao.DBHealth;
import com.magicare.smartnurse.database.dao.DBUser;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

public class CollectRecordFragment extends Fragment {
	View mView;
	PullToRefreshListView mylist;

	CollectRecordAdapter mRecordAapter;

	List<HealthBean> list_health;

	CollectRecordData collectRecordData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mView = View.inflate(getActivity(), R.layout.activity_collecthistory, null);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mylist = (PullToRefreshListView) mView.findViewById(R.id.mylist);
		// TextView textView = new TextView(getActivity());
		// textView.setText("点击查看更多");
		// textView.setBackgroundColor(Color.rgb(240, 241, 238));
		// textView.setLayoutParams(new AbsListView.LayoutParams(
		// LayoutParams.FILL_PARENT, 70));
		// textView.setGravity(Gravity.CENTER_HORIZONTAL |
		// Gravity.CENTER_VERTICAL);
		// mylist.addFooterView(textView);
		// textView.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// collectRecordData.showCollectRecord(mHandler);
		// }
		// });

		mylist.setMode(Mode.PULL_UP_TO_REFRESH);

		mylist.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				collectRecordData.showCollectRecord(mHandler);

			}
		});

	}

	// public void getData() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	//
	// // 所有测量记录
	// DBHealth dbHealth = DBHealth.getInstance(getActivity());
	// dbHealth.open();
	// list_health = dbHealth.getHealthInfoByUserId(old_id);
	//
	// dbHealth.close();
	// mHandler.sendEmptyMessage(1);
	//
	// }
	// }).start();
	// }

	/**
	 * 设置数据
	 */
	public void addData(List<HealthBean> collectRecord) {

		mRecordAapter = new CollectRecordAdapter(getActivity(), collectRecord);
		mRecordAapter.setType(1);
		mylist.setAdapter(mRecordAapter);

	}

	List<HealthBean> collectRecord;
	android.os.Handler mHandler = new android.os.Handler() {
		public void handleMessage(android.os.Message msg) {
			mylist.onRefreshComplete();
			collectRecord = (List<HealthBean>) msg.obj;
			if (mRecordAapter == null) {
				mRecordAapter = new CollectRecordAdapter(getActivity(), collectRecord);
				mRecordAapter.setType(1);
				mylist.setAdapter(mRecordAapter);
			} else {
				mRecordAapter.notifyDataSetChanged();
			}
		};
	};

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			collectRecordData = (CollectRecordData) activity;
		} catch (Exception e) {
			// TODO: handle exception
		}

	};

	public void onDetach() {

		super.onDetach();

		try {

			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");

			childFragmentManager.setAccessible(true);

			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);

		}
	}

	public interface CollectRecordData {
		public void showCollectRecord(android.os.Handler handler);
	}

}
