package com.magicare.smartnurse.activity;

import java.util.List;
import java.util.UUID;

import com.magicare.smartnurse.ble.BluetoothLeService;
import com.magicare.smartnurse.ble.FatScaleDataUtil;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint("NewApi")
public abstract class BleBaseFragment extends Fragment {

	private static final int REQUEST_ENABLE_BT = 1;

	protected BluetoothAdapter mBluetoothAdapter;

	protected BluetoothLeService mBluetoothLeService;

	// 脂肪秤返回的错误次数
	protected int ERROR_COUNT = 0;

	protected Handler mHandler = new Handler();
	/* 发送数据 */
	protected Runnable mRunnable;

	protected String deviceName;

	protected boolean isConnection;

	protected MainActivity mContext;

	/* 连接成功后，由于设备反应比较慢， 需要延长1000毫秒再向设备发送数据 */
	protected static final long DELAYTTIME = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = (MainActivity) getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected void unBindService() {
		mBluetoothLeService.scanLeDevice(false);
		mContext.unbindService(mServiceConnection);
		mContext.unregisterReceiver(mGattStatusChangeReceiver);
		mContext.unregisterReceiver(mDataAnalysisReceiver);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@SuppressLint("NewApi")
	protected void initBle() {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(mContext, "不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
			// finish();
		}
		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			// Toast.makeText(mContext, "mBluetoothAdapter is null",
			// Toast.LENGTH_SHORT).show();
			// finish();
			return;
		}

		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				// Intent enableBtIntent = new
				// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				mBluetoothAdapter.enable();
			}
		}

		// 注册蓝牙连接状态改变广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_NOTFOUNT_DEVICE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CLOSE);
		mContext.registerReceiver(mGattStatusChangeReceiver, intentFilter);
		// 数据处理广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		mContext.registerReceiver(mDataAnalysisReceiver, filter);
		// 蓝牙数据通信服务
		Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
		mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	protected void write(String UUID_CHARACTERISTIC, byte[] data) {

		LogUtil.info(BluetoothLeService.class, "发送的数据:" + FatScaleDataUtil.byteToHexStringFormat(data));
		LogUtil.info("smarhit","UUID="+UUID_CHARACTERISTIC);
		LogUtil.info("smarhit","data="+data.toString());
		// if (!isConnection) {
		// mBluetoothLeService.connect(mDeviceAddress);
		// }
		BluetoothGattService service = mBluetoothLeService.getSupportedGattService();
		if (null == service) {
			// PromptManager.showToast(mContext, "service is null");
			// connectDevice();
			return;
		}
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC));
		LogUtil.info("smarhit","characteristic.getUuid()"+characteristic.getUuid().toString());
		boolean setValue = characteristic.setValue(data);
		LogUtil.info(BluetoothLeService.class, "  开始写入数据：isSuccess:" + setValue);
		mBluetoothLeService.wirteCharacteristic(characteristic);
	}

	protected void readDeviceByHand() {
		BluetoothGattService service = mBluetoothLeService.getSupportedGattService();
		if (service == null) {
			return;
		}
		List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
		if (gattCharacteristics == null) {
			return;
		}
		for (int i = 0; null != gattCharacteristics && i < gattCharacteristics.size(); i++) {
			BluetoothGattCharacteristic characteristic = gattCharacteristics.get(i);
			mBluetoothLeService.readCharacteristic(characteristic);
		}

	}

	protected ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				PromptManager.showToast(mContext, false, "初始化蓝牙失败！");
				// finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	/**
	 * 蓝牙状态变化广播
	 */
	protected BroadcastReceiver mGattStatusChangeReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				// PromptManager.showToast(mContext, "GATT连接成功");
				isConnection = true;
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				// PromptManager.showToast(mContext, "GATT断开连接");
				isConnection = false;
				mBluetoothLeService.reConnect();

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// PromptManager.showToast(mContext, "发现蓝牙服务");
				LogUtil.info("lhw","发现蓝牙服务 向蓝牙设备发送连接数据");
				sendDataToDevice();
			} else if (BluetoothLeService.ACTION_NOTFOUNT_DEVICE.equals(action)) {
				notFoundDevice();
			} else if (BluetoothLeService.ACTION_GATT_CLOSE.equals(action)) {
				gattClose();
			}
		}
	};

	/**
	 * 数据接收广播
	 */
	private final BroadcastReceiver mDataAnalysisReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String result = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				LogUtil.info("lhw","收到数据");
				analyseData(result);
			}

		}
	};

	/**
	 * 
	 * Function:分析蓝牙设备返回的数据
	 * 
	 * @param data
	 *            :设备返回的数据
	 */
	protected abstract void analyseData(String data);

	/**
	 * 
	 * Function:向设蓝牙备发送数据
	 */
	protected abstract void sendDataToDevice();

	/**
	 * 
	 * Function:没有搜索到设备
	 */
	protected abstract void notFoundDevice();

	/**
	 * 
	 * Function:Gatt关闭了
	 */
	protected abstract void gattClose();

}