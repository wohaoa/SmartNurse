package com.magicare.smartnurse.activity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.MonitorFragment.IControlMenu;
import com.magicare.smartnurse.adapter.SerachAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.RegionBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.database.dao.DBRegion;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.service.UpdateBitmapService;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.ClearEditText;
import com.magicare.smartnurse.view.ClearEditText.ClearDate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

/**
 * 
 * @author scott
 * 
 *         Function:查询
 */
public class QueryFragment extends Fragment implements OnClickListener, OnItemClickListener, ClearDate {

	private View mView;
	DBUser dbUser;
	ClearEditText et_serach;
	Button btn_serach;
	GridView gv_serach;
	List<UserBean> userBeans = new ArrayList<UserBean>();
	List<UserBean> tempuserBeans = new ArrayList<UserBean>();
	SerachAdapter serachAdapter;
	boolean isFlag = true;
	String temp;

	private IControlMenu mControlmenu;

	public void setOnControlMenuListener(IControlMenu controlMenu) {
		this.mControlmenu = controlMenu;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = View.inflate(getActivity(), R.layout.fragment_query, null);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		dbUser = DBUser.getInstance(getActivity());
		et_serach = (ClearEditText) mView.findViewById(R.id.et_serach);
		et_serach.setClearDate(this);
		gv_serach = (GridView) mView.findViewById(R.id.gv_serach);
		btn_serach = (Button) mView.findViewById(R.id.btn_serach);
		btn_serach.setOnClickListener(this);
		// btn_back.setOnClickListener(this);
		gv_serach.setOnItemClickListener(this);
		getData();

		// 根据输入框输入值的改变来过滤搜索
		et_serach.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				// filterData(s.toString());
				// serach(s.toString());
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
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		isFlag = false;
		tempuserBeans = new ArrayList<UserBean>();
		if (TextUtils.isEmpty(filterStr)) {
			tempuserBeans = userBeans;
		} else {
			tempuserBeans.clear();
			for (UserBean bean : userBeans) {
				String name = bean.getName();
				if (name.indexOf(filterStr.toString()) != -1) {
					tempuserBeans.add(bean);
				}
			}
		}
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (ConfigManager.getBooleanValue(getActivity(), Constants.ISPHOTOCHANGE, false)) {
			if (serachAdapter != null) {
				ConfigManager.setBooleanValue(getActivity(), Constants.ISPHOTOCHANGE, false);
				System.out.println("isFlag" + isFlag);
				if (isFlag) {
					getData();
				} else {

					serach(et_serach.getText().toString().trim());
				}
			}
		}

		// 隐藏状态
		// WindowManager.LayoutParams attr =
		// getActivity().getWindow().getAttributes();
		// attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getActivity().getWindow().setAttributes(attr);
		// getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		// if (ConfigManager.getBooleanValue(getActivity(),
		// ConfigManager.NETWORK_STARTUS, false)) {
		// /* 开启服务检查是否有需要上传的图片 */
		// Intent mServiceIntent = new Intent(getActivity(),
		// UpdateBitmapService.class);
		// mServiceIntent.putExtra(Constants.ISLOADING, false);
		// getActivity().startService(mServiceIntent);
		// }
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stubonPause
		super.onPause();
		LogUtil.info("smarhit", "---queryFragment onPause()!");
	}

	public void getData() {
		isFlag = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				dbUser.open();
				userBeans = dbUser.getAllUserInfo();
				dbUser.close();
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	public void serach(final String idorname) {
		isFlag = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				dbUser.open();
				tempuserBeans = dbUser.getUserInfoByNameOrId(idorname);
				dbUser.close();
				if (tempuserBeans != null && tempuserBeans.size() > 0) {
					mHandler.sendEmptyMessage(1);
				} else {
					mHandler.sendEmptyMessage(-1);
				}

			}
		}).start();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_serach:

			// temp = et_serach.getText().toString().trim();
			// if (temp == null || temp.equals("")) {
			// PromptManager.showToast(getActivity(), false, "请输入老人ID或老人名字！");
			// } else {
			// // serach(temp);
			// filterData(temp);
			// }
			et_serach.setText("");

			getAllUserInfo();
			break;

		// case R.id.btn_back:
		//
		// if (userBeans != null && userBeans.size() > 0) {
		// mHandler.sendEmptyMessage(0);
		// }
		// break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UserBean bean;
		if (isFlag) {
			bean = userBeans.get(position);
		} else {
			bean = tempuserBeans.get(position);
		}
		Intent intent = new Intent(getActivity(), UserDetailActivity.class);

		intent.putExtra("userbean", bean);

		startActivity(intent);

	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (serachAdapter == null) {
					serachAdapter = new SerachAdapter(getActivity(), userBeans);
					gv_serach.setAdapter(serachAdapter);
				} else {
					serachAdapter.updateListView(userBeans);
				}
				isFlag = true;
				break;
			case 1:
				if (serachAdapter == null) {
					serachAdapter = new SerachAdapter(getActivity(), tempuserBeans);
					gv_serach.setAdapter(serachAdapter);
				} else {
					serachAdapter.updateListView(tempuserBeans);
				}

				break;

			case -1:
				PromptManager.showToast(getActivity(), true, "对不起,没有老人信息");
				break;
			default:
				break;
			}

		};

	};

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (userBeans != null && userBeans.size() > 0) {
			mHandler.sendEmptyMessage(0);
		}
	}

	private void getAllUserInfo() {

		int nurseid = ConfigManager.getIntValue(getActivity(), ConfigManager.NURSE_ID);
		DBRegion regionDb = DBRegion.getInstance(getActivity());
		regionDb.open();
		RegionBean regionBean = regionDb.queryRegionInfoByNurseID(nurseid + "");
		regionDb.close();
		PromptManager.showProgressDialog(getActivity(), "正在更新");
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.getAllOldUserInfo(getActivity(), ConfigManager.getStringValue(getActivity(), Constants.ACCESS_TOKEN),
				regionBean.getPension_areaid(), new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									List<UserBean> list = JSON.parseArray(baseBean.getData(), UserBean.class);
									userBeans = list;
									if (list != null && list.size() > 0) {
										DBUser db = DBUser.getInstance(getActivity());
										db.open();
										db.deleteAll();
										db.insert(list);
										userBeans = db.getAllUserInfo(); // 每次显示的数据都从数据库中拿，以保证数据顺序一致
										db.close();
									}
									mHandler.sendEmptyMessage(0);

								} else {
									PromptManager.closeProgressDialog();
									PromptManager.showToast(getActivity(), false, baseBean.getInfo());
								}
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getActivity(), false, errors);
						}
					}
				});
	}

}
