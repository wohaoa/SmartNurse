package com.magicare.smartnurse.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.ConcernHistoryAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.bean.ExhortBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.CircleImageView;

public class ConcernHistory extends BaseActivity {
	private ListView lv_concern;
	private TextView child_name;
	// private ConcernBean concern;
	private CircleImageView iv_photo;
	private Button btn_back;
	private Context mContext;
	private UserBean userBean;

	private final int REFRESH_CONCERN = 0;
	private int concernCount=0;
	private ConcernHistoryAdapter mAdapter;
	private List<ConcernBean> concernlist = new ArrayList<ConcernBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concernhistory);
		mContext = ConcernHistory.this;

		lv_concern = (ListView) findViewById(R.id.lv_concern);
		child_name = (TextView) findViewById(R.id.child_name);
		iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
		btn_back = (Button) findViewById(R.id.btn_back);

		btn_back.setOnClickListener(this);

		initConcern();
	}

	/**
	 * 叮嘱详情初始化
	 */
	private void initConcern() {
		userBean = (UserBean) getIntent().getSerializableExtra("userbean");
		LogUtil.info("lhw", "userBean=" + userBean.toString());
		String photopath = FileUtils.SDPATH + userBean.getOld_id() + ".JPEG";
		File file = new File(photopath);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
			iv_photo.setImageBitmap(bitmap);
		}

		child_name.setText(userBean.getName() + "之" + userBean.getChild_relation() + userBean.getChild_name() + "的叮嘱");
		mHandler.sendEmptyMessage(REFRESH_CONCERN);

	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case REFRESH_CONCERN: // 在线程中刷新叮嘱详情界面
				

                concernCount=loadConcernByOld();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * Function:拉取叮嘱消息
	 * 
	 */
	private int loadConcernByOld() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		PromptManager.showProgressDialog(ConcernHistory.this, "正在加载");

		client.getExhortByOld(mContext, ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				userBean.getOld_id(), new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");

							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									ExhortBean databean = JSON.parseObject(baseBean.getData(), ExhortBean.class);
									concernlist = JSON.parseArray(databean.getList(), ConcernBean.class);
									mAdapter = new ConcernHistoryAdapter(ConcernHistory.this, concernlist, lv_concern);
									lv_concern.setAdapter(mAdapter);
									
								} else {
									PromptManager.showToast(mContext, false, errors);
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(), false, errors);
							PromptManager.closeProgressDialog();
						}
					}
				});
		return concernCount;
	}

}