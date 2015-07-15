package com.magicare.smartnurse.activity;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.ble.BLEConstants;
import com.magicare.smartnurse.ble.BloodSugarDataUtil;
import com.magicare.smartnurse.ble.BluetoothLeService;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 设备连接
 * 
 * @author 波
 * 
 */
public class DeviceFragment extends BleBaseFragment implements OnClickListener {

	private View mView;
	private TextView tv_weight;
	private TextView tv_bloodpreesure;
	private TextView tv_heart;
	private TextView tv_bloodsugar;

	private ImageView iv_weight_loading;
	private ImageView iv_bloodpreesure_loading;
	private ImageView iv_heart_loading;
	private ImageView iv_bloodsugar_loading;
	private TextView how_to_connect, how_to_connect1, how_to_connect2, how_to_connect3;

	private AnimationDrawable checkDeviceAnima;

	private boolean checkWeight, checkBloodpreesure, checkHeart, checkBloodSugar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		mView = View.inflate(getActivity(), R.layout.fragment_device, null);
		initview();
		return mView;
	}

	private void initview() {
		// TODO Auto-generated method stub
		tv_weight = (TextView) mView.findViewById(R.id.tv_weight);
		tv_bloodpreesure = (TextView) mView.findViewById(R.id.tv_bloodpreesure);
		tv_heart = (TextView) mView.findViewById(R.id.tv_heart);
		tv_bloodsugar = (TextView) mView.findViewById(R.id.tv_bloodsugar);
		iv_weight_loading = (ImageView) mView.findViewById(R.id.iv_weight_loading);
		iv_bloodpreesure_loading = (ImageView) mView.findViewById(R.id.iv_bloodpreesure_loading);
		iv_heart_loading = (ImageView) mView.findViewById(R.id.iv_heart_loading);
		iv_bloodsugar_loading = (ImageView) mView.findViewById(R.id.iv_bloodsugar_loading);
		
		how_to_connect = (TextView) mView.findViewById(R.id.how_to_conncet);
		how_to_connect1 = (TextView) mView.findViewById(R.id.how_to_conncet1);
		how_to_connect2 = (TextView) mView.findViewById(R.id.how_to_conncet2);
		how_to_connect3 = (TextView) mView.findViewById(R.id.how_to_conncet3);
		
		tv_weight.setOnClickListener(this);
		tv_bloodpreesure.setOnClickListener(this);
		tv_heart.setOnClickListener(this);
		tv_bloodsugar.setOnClickListener(this);
		how_to_connect.setOnClickListener(this);
		how_to_connect1.setOnClickListener(this);
		how_to_connect2.setOnClickListener(this);
		how_to_connect3.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		initBle();
		switch (v.getId()) {
		case R.id.tv_weight:
			if (checkBloodSugar == false
					&& (checkBloodpreesure == false && iv_bloodpreesure_loading.getVisibility() == View.INVISIBLE)
					&& (checkHeart == false && iv_heart_loading.getVisibility() == View.INVISIBLE)) {
				mDelayHander.sendEmptyMessageDelayed(3, 10 * 1000);
				tv_weight.setTextColor(Color.parseColor("#000000"));
				tv_weight.setText("正在检测···");
				iv_weight_loading.setVisibility(View.VISIBLE);
				checkDeviceAnima = (AnimationDrawable) iv_weight_loading.getBackground();
				checkDeviceAnima.start();
				mDelayHander.sendEmptyMessageDelayed(0, 5000);
				checkWeight = true;
			} else {
				PromptManager.showToast(mContext,false, "亲，不能同时检测多个设备！");
			}

			break;
		case R.id.tv_bloodpreesure:
			if (checkWeight == false && checkBloodSugar == false
					&& (checkHeart == false && iv_heart_loading.getVisibility() == View.INVISIBLE)) {
				mDelayHander.sendEmptyMessageDelayed(3, 10 * 1000);
				tv_bloodpreesure.setTextColor(Color.parseColor("#000000"));
				tv_bloodpreesure.setText("正在检测···");
				iv_bloodpreesure_loading.setVisibility(View.VISIBLE);
				checkDeviceAnima = (AnimationDrawable) iv_bloodpreesure_loading.getBackground();
				checkDeviceAnima.start();
				mDelayHander.sendEmptyMessageDelayed(1, 5000);
				checkBloodpreesure = true;
			} else {
				PromptManager.showToast(mContext, false,"亲，不能同时检测多个设备！");
			}
			break;
		case R.id.tv_heart:
			if (checkWeight == false && checkBloodSugar == false
					&& (checkBloodpreesure == false && iv_bloodpreesure_loading.getVisibility() == View.INVISIBLE)) {
				mDelayHander.sendEmptyMessageDelayed(3, 10 * 1000);
				tv_heart.setTextColor(Color.parseColor("#000000"));
				tv_heart.setText("正在检测···");
				iv_heart_loading.setVisibility(View.VISIBLE);
				checkDeviceAnima = (AnimationDrawable) iv_heart_loading.getBackground();
				checkDeviceAnima.start();
				mDelayHander.sendEmptyMessageDelayed(1, 5000);
				checkHeart = true;
			} else {
				PromptManager.showToast(mContext, false,"亲，不能同时检测多个设备！");
			}
			break;
		case R.id.tv_bloodsugar:
			if (checkWeight == false
					&& (checkBloodpreesure == false && iv_bloodpreesure_loading.getVisibility() == View.INVISIBLE)
					&& (checkHeart == false && iv_heart_loading.getVisibility() == View.INVISIBLE)) {
				mDelayHander.sendEmptyMessageDelayed(3, 10 * 1000);
				tv_bloodsugar.setTextColor(Color.parseColor("#000000"));
				tv_bloodsugar.setText("正在检测···");
				iv_bloodsugar_loading.setVisibility(View.VISIBLE);
				checkDeviceAnima = (AnimationDrawable) iv_bloodsugar_loading.getBackground();
				checkDeviceAnima.start();
				mDelayHander.sendEmptyMessageDelayed(2, 5000);
				checkBloodSugar = true;
			} else {
				PromptManager.showToast(mContext, false,"亲，不能同时检测多个设备！");
			}
			break;
			
		case R.id.how_to_conncet:
		case R.id.how_to_conncet1:
		case R.id.how_to_conncet2:
		case R.id.how_to_conncet3:
			
			Intent intent = new Intent(getActivity(), ConnectActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private Handler mDelayHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (mBluetoothLeService != null)
					mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_FATSCALE_DEVICE_NAME);
				break;
			case 1:
				if (mBluetoothLeService != null)
					mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME);
				break;
			case 2:
				if (mBluetoothLeService != null)
					mBluetoothLeService.scanLeDevice(true, BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME);
				break;
			case 3:
				 if(mBluetoothLeService.mDevice == null || mBluetoothLeService == null){
					mBluetoothLeService.scanLeDevice(false);
					mBluetoothLeService.close();
					PromptManager.showToast(mContext,false, "没有找到硬件设备， 请重试!");
					iv_weight_loading.setVisibility(View.INVISIBLE);
					iv_bloodpreesure_loading.setVisibility(View.INVISIBLE);
					iv_heart_loading.setVisibility(View.INVISIBLE);
					iv_bloodsugar_loading.setVisibility(View.INVISIBLE);
				 }
				 if(checkWeight){
					 tv_weight.setText("超时，请重新检测");
					 checkWeight = false;
				 }
				 if(checkBloodSugar){
					 tv_bloodsugar.setText("超时，请重新检测");
					 checkBloodSugar = false;
				 }
				 if(checkHeart){
					 tv_heart.setText("超时，请重新检测");
					 checkHeart = false;
				 }
				 if(checkBloodpreesure){
					 tv_bloodpreesure.setText("超时，请重新检测");
					 checkBloodpreesure = false;
				 }
			default:
				break;
			}
		};
	};

	@Override
	protected void analyseData(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mBluetoothLeService != null) {
			mBluetoothLeService.scanLeDevice(false);
			mBluetoothLeService.close();
		}
		iv_weight_loading.setVisibility(View.INVISIBLE);
		iv_bloodpreesure_loading.setVisibility(View.INVISIBLE);
		iv_heart_loading.setVisibility(View.INVISIBLE);
		iv_bloodsugar_loading.setVisibility(View.INVISIBLE);
		tv_weight.setText("检测设备");
		checkWeight = false;
		tv_bloodpreesure.setText("检测设备");
		tv_heart.setText("检测设备");
		checkBloodpreesure = false;
		checkHeart = false;

		tv_bloodpreesure.setText("检测设备");
		checkBloodSugar = false;
	}

	@Override
	protected void sendDataToDevice() {
		// TODO Auto-generated method stub
		if (mBluetoothLeService != null) {
			if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
				mDelayHander.removeMessages(3);
				iv_weight_loading.setVisibility(View.INVISIBLE);
				tv_weight.setTextColor(Color.RED);
				tv_weight.setText("设备正常");
				checkWeight = false;
			} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
				mDelayHander.removeMessages(3);
				iv_bloodpreesure_loading.setVisibility(View.INVISIBLE);
				iv_heart_loading.setVisibility(View.INVISIBLE);
				tv_bloodpreesure.setText("设备正常");
				tv_heart.setText("设备正常");
				tv_bloodpreesure.setTextColor(Color.RED);
				tv_heart.setTextColor(Color.RED);
				checkBloodpreesure = false;
				checkHeart = false;
			} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
				mDelayHander.removeMessages(3);
				iv_bloodsugar_loading.setVisibility(View.INVISIBLE);
				tv_bloodsugar.setTextColor(Color.RED);
				tv_bloodsugar.setText("设备正常");
				checkBloodSugar = false;
			}
			if (checkDeviceAnima != null) {
				checkDeviceAnima.stop();
			}
			mBluetoothLeService.scanLeDevice(false);
			mBluetoothLeService.close();
			try {
				unBindService();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}

	}

	@Override
	protected void notFoundDevice() {
		// TODO Auto-generated method stub
		PromptManager.showToast(mContext,false, "没有找到硬件设备， 请重试!");
		iv_weight_loading.setVisibility(View.INVISIBLE);
		iv_bloodpreesure_loading.setVisibility(View.INVISIBLE);
		iv_heart_loading.setVisibility(View.INVISIBLE);
		iv_bloodsugar_loading.setVisibility(View.INVISIBLE);
		if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
			tv_weight.setText("检测设备");
			checkWeight = false;
		} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
			tv_bloodpreesure.setText("检测设备");
			tv_heart.setText("检测设备");
			checkBloodpreesure = false;
			checkHeart = false;

		} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
			tv_bloodpreesure.setText("检测设备");
			checkBloodSugar = false;
		}
		if (checkDeviceAnima != null) {
			checkDeviceAnima.stop();
		}
		mBluetoothLeService.scanLeDevice(false);
		mBluetoothLeService.close();
	}
	
	@Override
	protected void gattClose() {
		// TODO Auto-generated method stub
		PromptManager.showToast(mContext, false, "数据异常，请重新测试!");
		iv_weight_loading.setVisibility(View.INVISIBLE);
		iv_bloodpreesure_loading.setVisibility(View.INVISIBLE);
		iv_heart_loading.setVisibility(View.INVISIBLE);
		iv_bloodsugar_loading.setVisibility(View.INVISIBLE);
		if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
			tv_weight.setText("检测设备");
			checkWeight = false;
		} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
			tv_bloodpreesure.setText("检测设备");
			tv_heart.setText("检测设备");
			checkBloodpreesure = false;
			checkHeart = false;

		} else if (mBluetoothLeService.mDeviceName.equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
			tv_bloodpreesure.setText("检测设备");
			checkBloodSugar = false;
		}
		if (checkDeviceAnima != null) {
			checkDeviceAnima.stop();
		}
		mBluetoothLeService.scanLeDevice(false);
		mBluetoothLeService.close();
	}

}