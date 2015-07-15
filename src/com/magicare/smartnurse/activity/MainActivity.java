package com.magicare.smartnurse.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.activity.MonitorFragment.IControlMenu;
import com.magicare.smartnurse.adapter.FragmentTabAdapter;
import com.magicare.smartnurse.adapter.FragmentTabAdapter.OnRgsExtraCheckedChangedListener;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.logic.UpgradeApp;
import com.magicare.smartnurse.service.UpdateBitmapService;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.LogUtil;

public class MainActivity extends BaseActivity implements IControlMenu {

	public static final String UPDATEPHOTO_ACTION = "com.magicare.smartnurse.updatephoto";

	private int concerncount = 0;

	private List<Fragment> fragments = new ArrayList<Fragment>();
	private RadioGroup rg_menu;
	// private FrameLayout fl_menu;
	private TextView iv_unconcern;
	FragmentTabAdapter tabAdapter;
	private MonitorFragment monitorFragment;
	private CollectFragment collectFragment;
	private QueryFragment querFragment;
	private MoreFragment morefFragment;

	private List<ConcernBean> concernlist = new ArrayList<ConcernBean>();
	private int concernCount = 0;

	private NetworkConnectChangedReceiver networkReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabhost_main);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		LogUtil.info("rice", "model ="+android.os.Build.MODEL);
		
		rg_menu = (RadioGroup) findViewById(R.id.rg_menu);
		// FrameLayout fl_menu=(FrameLayout)findViewById(R.id.fl_menu);
		iv_unconcern = (TextView) findViewById(R.id.iv_unconcern);
		monitorFragment = new MonitorFragment();
		monitorFragment.setOnControlMenuListener(this);
		querFragment = new QueryFragment();
		querFragment.setOnControlMenuListener(this);
		collectFragment = new CollectFragment();
		morefFragment = new MoreFragment();
		morefFragment.setOnControlMenuListener(this);
		fragments.add(monitorFragment);
		fragments.add(querFragment);
		fragments.add(collectFragment);
		fragments.add(morefFragment);

		tabAdapter = new FragmentTabAdapter(this, fragments, R.id.fl_main, rg_menu);
		tabAdapter.setOnRgsExtraCheckedChangedListener(new OnRgsExtraCheckedChangedListener() {

			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
				// TODO Auto-generated method stub
				if (index == 0) {
					// mHandler.sendEmptyMessageDelayed(0, 10000);
					monitorFragment.getWarning(MainActivity.this);
				}
			}
		});
		networkReceiver = new NetworkConnectChangedReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(UPDATEPHOTO_ACTION);
		filter.addAction(ConcernFragment.action);
		registerReceiver(networkReceiver, filter);

		// TestUtil.test1();
		IntentFilter concernfilter = new IntentFilter();
		concernfilter.addAction(ConcernFragment.action);
		concernfilter.addAction(ConcernActivity.action);
		registerReceiver(broadcastReceiver, concernfilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		menu(false);
		Fragment currentFragment = tabAdapter.getCurrentFragment();
		if (currentFragment instanceof MonitorFragment) {
			// mHandler.sendEmptyMessageDelayed(0, 10000);
			monitorFragment.getWarning(MainActivity.this);
		} else {
			menu(false);

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (networkReceiver != null) {
			unregisterReceiver(networkReceiver);
			networkReceiver = null;
		}
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
			broadcastReceiver = null;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				menu(true);
				break;

			default:
				break;
			}
		};

	};

	@Override
	public void menu(boolean isShow) {
		// TODO Auto-generated method stub
		if (isShow) {
			rg_menu.setVisibility(View.GONE);
			iv_unconcern.setVisibility(View.GONE);
		} else {
			rg_menu.setVisibility(View.VISIBLE);
			if (concerncount > 0)
				iv_unconcern.setVisibility(View.VISIBLE);
		}

	}

	private class NetworkConnectChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的连接状态
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					if (state == State.DISCONNECTED) {
						ConfigManager.setBooleanValue(context, ConfigManager.NETWORK_STARTUS, false);
					}
				}
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {// 这个监听网络连接的设置，包括wifi和移动数据
				NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (info != null) {
					if (NetworkInfo.State.CONNECTED == info.getState()) {
						ConfigManager.setBooleanValue(context, ConfigManager.NETWORK_STARTUS, true);
						// 如何有网就检测 版本更新
						UpgradeApp upgrade = new UpgradeApp(MainActivity.this);
						upgrade.checkVersionCode();
					} else if (NetworkInfo.State.DISCONNECTING == info.getState()) {
						ConfigManager.setBooleanValue(context, ConfigManager.NETWORK_STARTUS, false);
					}

				}
			} else if (UPDATEPHOTO_ACTION.equals(intent.getAction())) {
				// 停止上传头像服务
				context.stopService(new Intent(context, UpdateBitmapService.class));
			}
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConcernFragment.action.equals(intent.getAction())) {
				if (intent.getExtras().getString("data") != null) {
					concerncount = Integer.parseInt(intent.getExtras().getString("data"));
				} else {
					concerncount = 0;
				}
				if (concerncount == 0) {
					iv_unconcern.setVisibility(View.GONE);
				} else if (concerncount > 0 && rg_menu.getVisibility() == View.VISIBLE){
					iv_unconcern.setVisibility(View.VISIBLE);
				}
			}

		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (rg_menu.getVisibility() == View.VISIBLE) {
			showExitDialog();
		} else {
			monitorFragment.back();
			menu(false);
		}
		LogUtil.info("smarhit", "用户按了back");
	}

	/**
	 * 显示退出对话框
	 */
	private void showExitDialog() {
		AlertDialog.Builder versionBuilder = new AlertDialog.Builder(this);
		versionBuilder.setTitle("温馨提示:");
		versionBuilder.setMessage("您确定要退出程序吗?");
		versionBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				MainActivity.this.finish();
			}
		});

		versionBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		AlertDialog versionDialog = versionBuilder.create();
		versionDialog.setCancelable(false);
		versionDialog.show();
	}
}
