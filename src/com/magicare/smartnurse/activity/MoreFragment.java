package com.magicare.smartnurse.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.MonitorFragment.IControlMenu;
import com.magicare.smartnurse.adapter.MoreAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.RegionBean;
import com.magicare.smartnurse.database.dao.DBRegion;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.CircleImageView;

/**
 * 
 * @author scott
 * 
 *         Function:更多
 */
public class MoreFragment extends Fragment implements OnItemClickListener {

	private View mView;

	ListView mylist;
	private List<String> mlist;
	private MoreAdapter moreAdapter;
	private List<Fragment> mFragmentsList;
	private int currentTab;
	FragmentManager fragmentManager;
	UploadFragment uploadfragment;
	DeviceFragment deviceFragment;
	ConcernFragment concernFragment;
	AboutFragment aboutFragment;
	TvFragment tvFragment;
	PasswordFragment passwordFragment;
	CircleImageView iv_photo;
	TextView tv_nursename;
	Context mContext;

	private IControlMenu mControlmenu;


	public void setOnControlMenuListener(IControlMenu controlMenu) {
		this.mControlmenu = controlMenu;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 // 隐藏状态栏
		mView = View.inflate(getActivity(), R.layout.activity_more, null);
		mylist = (ListView) mView.findViewById(R.id.mylist);
		iv_photo = (CircleImageView) mView.findViewById(R.id.iv_photo);
		iv_photo.setOutColor(Color.parseColor("#DCE4D8"));
		int nurseid = ConfigManager.getIntValue(getActivity(), ConfigManager.NURSE_ID);
		String localAddress = FileUtils.SDPATH + nurseid + ".JPEG";
		File file = new File(localAddress);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(localAddress);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
			iv_photo.setImageBitmap(bitmap);
		}
		int areaId = ConfigManager.getIntValue(getActivity(), ConfigManager.AREAID);
		DBRegion db_region = DBRegion.getInstance(getActivity());
		db_region.open();
		RegionBean regionBean = db_region.queryRegionInfoByAreaID(areaId);
		db_region.close();

		String name = ConfigManager.getStringValue(getActivity(), ConfigManager.NURSE_NAME);
		tv_nursename = (TextView) mView.findViewById(R.id.tv_nursename);
		tv_nursename.setText(name + " (" + regionBean.getName() + ")");
		initData();

		fragmentManager = getChildFragmentManager();
		initFragment();
		moreAdapter = new MoreAdapter(getActivity(), mlist);
		mylist.setAdapter(moreAdapter);
		mylist.setOnItemClickListener(this);
		this.mContext = getActivity();
		IntentFilter concernfilter = new IntentFilter(ConcernFragment.action); 
        getActivity().registerReceiver(broadcastReceiver, concernfilter); 
        
		return mView;
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		LogUtil.info("smarhit", "-----MoreFragment onPause()");
	}

	public void initData() {
		mlist = new ArrayList<String>();
		mlist.add("叮嘱消息");
		mlist.add("设备链接");
		mlist.add("上传数据");
		mlist.add("关于我们");
//		mlist.add("投影到TV");
		mlist.add("修改密码");
		mlist.add("退出登录");
	}

	public void initFragment() {

		mFragmentsList = new ArrayList<Fragment>();
		concernFragment = new ConcernFragment();
		deviceFragment = new DeviceFragment();
		uploadfragment = new UploadFragment();
		aboutFragment = new AboutFragment();
//		tvFragment = new TvFragment();
		passwordFragment = new PasswordFragment();
		mFragmentsList.add(concernFragment);
		mFragmentsList.add(deviceFragment);
		mFragmentsList.add(uploadfragment);
		mFragmentsList.add(aboutFragment);
//		mFragmentsList.add(tvFragment);
		mFragmentsList.add(passwordFragment);

		// 默认显示第一页
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(R.id.vPager, mFragmentsList.get(0));
		ft.commitAllowingStateLoss();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		moreAdapter.setPosition(position);
		moreAdapter.notifyDataSetChanged();

		if (position < (mlist.size() - 1)) {
			changeTab(position);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("您是否退出登录?").setCancelable(false)
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							logout();
						}
					}).setNegativeButton("否", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void showTab(int idx) {
		for (int i = 0; i < mFragmentsList.size(); i++) {
			Fragment fragment = mFragmentsList.get(i);
			FragmentTransaction ft = fragmentManager.beginTransaction();
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}
		currentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 切换
	 * 
	 * @param i
	 */
	public void changeTab(int i) {
		Fragment fragment = mFragmentsList.get(i);
		FragmentTransaction ft = fragmentManager.beginTransaction();
		getCurrentFragment().onPause(); // 暂停当前tab
		// getCurrentFragment().onStop(); // 暂停当前tab
		if (fragment.isAdded()) {
			// fragment.onStart(); // 启动目标tab的onStart()
			fragment.onResume(); // 启动目标tab的onResume()
		} else {
			ft.add(R.id.vPager, fragment);
		}
		showTab(i); // 显示目标tab
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
		ft.commitAllowingStateLoss();
	}

	public Fragment getCurrentFragment() {
		return mFragmentsList.get(currentTab);
	}

	public void logout() {

		PromptManager.showProgressDialog(getActivity(), "请稍等");

		HttpClientUtil client = HttpClientUtil.getInstance();
		client.logout(getActivity(), ConfigManager.getStringValue(getActivity(), Constants.ACCESS_TOKEN),
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {
						// TODO Auto-generated method stub

						if (isSuccess) {
							if (TextUtils.isEmpty(json)) {
								PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								PromptManager.showToast(getActivity(), true, baseBean.getInfo());

								if (baseBean.getInfo().equals("success")) {
									startActivity();
								}
							}
						} else {
							PromptManager.showToast(getActivity(), false, errors);
							startActivity();
						}
						PromptManager.closeProgressDialog();
					}
				});
	}

	public void startActivity() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();
		ConfigManager.setBooleanValue(getActivity(), ConfigManager.LOGIN_SUCCESS, false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (currentTab == 2) {
			uploadfragment.getUploadData();
		}
		// 隐藏状态
//		WindowManager.LayoutParams attr = getActivity().getWindow().getAttributes();
//		attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getActivity().getWindow().setAttributes(attr);
//		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	
	   BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { 
    	   
	        @Override 
	        public void onReceive(Context context, Intent intent) { 
	            // TODO Auto-generated method stub 
	        	int count;
	        	TextView iv_unconcern=(TextView) mylist.getChildAt(0).findViewById(R.id.iv_unconcern);
	        	if(intent.getExtras().getString("data")!=null)
	        		count=Integer.parseInt(intent.getExtras().getString("data"));
	        	else
	        		count=0;
	        	if(count==0)
	        	{
	        		
	        		iv_unconcern.setVisibility(View.GONE);
	        	}
	        	else if(count>0)
	        		iv_unconcern.setVisibility(View.VISIBLE);
	        		
	        } 
	    };

}
