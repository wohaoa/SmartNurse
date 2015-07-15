package com.magicare.smartnurse.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.UploadAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.database.dao.DBHealth;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class UploadFragment extends Fragment implements OnClickListener {

	private View mView;
	private ListView mylist;
	private UploadAdapter uploadAdapter;
	private DBHealth dbHealth;
	private List<HealthBean> list_healths = new ArrayList<HealthBean>();
	private List<HealthBean> list_newData;
	private Button btn_upload;
	private TextView tv_detail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = View.inflate(getActivity(), R.layout.fragment_upload, null);
		return mView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mylist = (ListView) mView.findViewById(R.id.mylist);
		dbHealth = DBHealth.getInstance(getActivity());
		btn_upload = (Button) mView.findViewById(R.id.btn_upload);
		tv_detail = (TextView) mView.findViewById(R.id.tv_detail);
		btn_upload.setOnClickListener(this);
		// getUploadData();
	}

	public void getUploadData() {
		new Thread() {
			public void run() {
				dbHealth.open();
				list_newData = dbHealth.getHealthInfoByUpdate(0);
				dbHealth.close();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				list_healths.clear();
				if (list_newData != null) {
					list_healths.addAll(list_newData);
				}
				String str = null;
				if (list_healths != null && list_healths.size() > 0) {
					HealthBean healthBean = list_healths.get(list_healths.size() - 1);
					str = "从 " + healthBean.getCollect_time() + "共有未上传数据" + list_healths.size() + "条";
				} else {
					str = "没有任何数据可以上传";
				}
				tv_detail.setText(str);
				if (uploadAdapter == null) {
					uploadAdapter = new UploadAdapter(getActivity(), list_healths);
					mylist.setAdapter(uploadAdapter);
				} else {
					uploadAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}

		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_upload:

			if (list_healths.size() <= 0) {
				PromptManager.showToast(getActivity(), false, "没有数据需要上传！");
				return;
			} else {

				String data = JSONArray.toJSONString(list_healths);
				LogUtil.info("smarhit", "json:" + data);
				HttpClientUtil client = HttpClientUtil.getInstance();
				client.updateCollectData(getActivity(),
						ConfigManager.getStringValue(getActivity(), Constants.ACCESS_TOKEN), data,
						new IOperationResult() {

							@Override
							public void operationResult(boolean isSuccess, String json, String errors) {
								// TODO Auto-generated method stub
								if (isSuccess) {
									if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
										PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
									} else {
										BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
										if (baseBean.getStatus() == 0) {
											DBHealth dbHealth = DBHealth.getInstance(getActivity());
											dbHealth.open();
											boolean isUpdate = dbHealth.updateHealthUpdateStatus(list_healths, 1);
											dbHealth.close();
											if (isUpdate) {
												list_healths.clear();
												handler.sendEmptyMessage(0);
												PromptManager.showToast(getActivity(), true, "数据已上传成功!");
											} else {
												PromptManager.showToast(getActivity(), false, "数据已上传失败!");
											}

										} else {
											PromptManager.showToast(getActivity(), false, baseBean.getInfo());
										}
									}
								} else {
									PromptManager.showToast(getActivity(), false, errors);
								}

							}
						});
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUploadData();
	}

}
