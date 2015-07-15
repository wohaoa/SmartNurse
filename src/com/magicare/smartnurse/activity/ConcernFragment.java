package com.magicare.smartnurse.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.ConcernAdapter;
import com.magicare.smartnurse.adapter.WarningDealWithAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 叮嘱功能
 * 
 * @author Rice
 * 
 */
public class ConcernFragment extends Fragment {
	private View mView;
	private Context mContext;
	private ListView lv_concern;
	private List<ConcernBean> concernlist = new ArrayList<ConcernBean>();
	private int concernCount = 0;
	private List<ConcernBean> beans = new ArrayList<ConcernBean>();
	public static final String action = "ConcernFragment action";
	private ConcernAdapter mConcernAdapter;

	public static final int REFRESH = 0X02;
	private long mRefreshTime = 15 * 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.info("smarhit", "onCreateView");
		mView = View.inflate(getActivity(), R.layout.fragment_concern, null);
		this.mContext = getActivity();
		initview();
		return mView;
	}

	private void initview() {
		lv_concern = (ListView) mView.findViewById(R.id.lv_concern);
		lv_concern.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ConcernActivity.class);
				intent.putExtra("concernbean", concernlist.get(position));
				mHandler.removeMessages(REFRESH);
				startActivity(intent);
			}
		});
		mConcernAdapter = new ConcernAdapter(mContext, concernlist);
		lv_concern.setAdapter(mConcernAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		// 刷新用户状态信息
		LogUtil.info("smarhit", "onResume");
		mHandler.removeMessages(REFRESH);
		mHandler.sendEmptyMessage(REFRESH);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(REFRESH);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:// 刷新叮嘱信息
				getConcerns(); // 获取当前叮嘱信息
				mHandler.removeMessages(REFRESH);
				mHandler.sendEmptyMessageDelayed(REFRESH, mRefreshTime); // 每30秒重新获取
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 
	 * Function:获取所有的叮嘱信息
	 * 
	 */
	private void getConcerns() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getConcern(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {

						if (isSuccess) {
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(mContext, false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									// 清空原来的数据，方便一次性插入
									concernlist.clear();
									List<ConcernBean> list = JSON.parseArray(baseBean.getData(), ConcernBean.class);
									concernlist.addAll(list);
									concernCount = 0;
									if (list != null && list.size() > 0) {
										concernCount = getConcernCount(list);
									}
									mConcernAdapter.notifyDataSetChanged();
									Intent intent = new Intent(action);
									intent.putExtra("data", concernCount + "");
									//发送广播，在MainActivity接收并判断当前是否有叮嘱记录
									getActivity().sendBroadcast(intent);
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

	public int getConcernCount(List<ConcernBean> list) {
		int concerncount = 0;
		for (ConcernBean a : list) {
			if (a.getStatus() == 0)
				concerncount++;
		}
		return concerncount;

	}
}
