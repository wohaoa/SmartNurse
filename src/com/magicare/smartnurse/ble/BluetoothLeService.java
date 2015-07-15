package com.magicare.smartnurse.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import java.util.UUID;

import com.magicare.smartnurse.utils.LogUtil;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {

	private final static String TAG = "BluetoothLeService";
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	public BluetoothGatt mBluetoothGatt;
	/* 连接状态 */
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	/* 广播Action */
	public final static String ACTION_GATT_CLOSE = "com.magicare.bluetooth.le.ACTION_GATT_CLOSE";
	public final static String ACTION_NOTFOUNT_DEVICE = "com.magicare.bluetooth.le.ACTION_NOTFOUND_DEVICE";
	public final static String ACTION_GATT_CONNECTED = "com.magicare.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.magicare.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.magicare.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.magicare.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.magicare.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	/* DEVICE_TYPE:连接设备的类型 ,device_type=1:表示脂肪秤， 2:表示血压计 ，3:表示血糖仪 */
	private int DEVICE_TYPE = 1;

	private final IBinder mBinder = new LocalBinder();

	public String receiveData;

	public BluetoothDevice mDevice;
	// 检查设备时，需要
	public String mDeviceName;

	public String mCollectedDevice;

	private long mScanTime = 0;

	public boolean isBind = false;

	public boolean isStateChange = false;
	
	public String mName;

	protected BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			scandevice(device, rssi, scanRecord);
		}
	};

	private void scandevice(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				LogUtil.info("smarhit", "name:" + device.getName() + "  ,address:" + device.getAddress());
				if (device != null) {
					if (TextUtils.isEmpty(mDeviceName)) {
						if (BLEConstants.TYPE_FATSCALE_DEVICE_NAME.equals(device.getName())
								|| BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME.equals(device.getName())
								|| BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME.equals(device.getName())) {

							if (!(mCollectedDevice.equals(device.getName()))) {
								
								mDevice = device;
								LogUtil.info(BluetoothLeService.class, "name:" + device.getName() + "  ,address:"
										+ device.getAddress());
								// mHandler.post(mScanRunnable);
								LogUtil.info("smarhit",
										"name:" + device.getName() + "  ,address:" + device.getAddress() + "循环找到设备了");
								scanLeDevice(false);
								boolean isconnectok = connect();
								if (!isconnectok) {
									connect();
								}
							}

						}
					} else {
						if (mDeviceName.equals(device.getName())) {
							mDevice = device;
							LogUtil.info(BluetoothLeService.class,
									"name:" + device.getName() + "  ,address:" + device.getAddress() + "找到指定设备了");
							// mHandler.post(mScanRunnable);
							scanLeDevice(false);
							boolean isconnectok = connect();
							if (!isconnectok) {
								connect();
							}
//							if(BLEConstants.TYPE_FATSCALE_DEVICE_NAME.
//									equals(device.getName())){
//								
//								mHander.sendEmptyMessageAtTime(0, 8 * 1000);
//							}
						}
					}
				}
//				if (System.currentTimeMillis() - mScanTime > 10 * 1000) {
//					broadcastUpdate(ACTION_NOTFOUNT_DEVICE);
//					scanLeDevice(false);
//				}
			}
		}.start();
	}

//	private Handler mHander = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:// 重新扫描数据
//				if (mDevice != null && mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
//					if (!isStateChange) { // 蓝牙服务状态没有变化
//						scanLeDevice(false);
//						close();
//						if (mDevice != null) {
//							scanLeDevice(true);
//							LogUtil.info("smarhit", "蓝牙服务状态没有变化， 重新发送连接请求!");
//						}
//					} else { // 蓝牙服务已变化
//						scanLeDevice(false);
//						isStateChange = false;
//					}
//				}
//				break;
//			default:
//				break;
//			}
//		};
//	};

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
//			isStateChange = true;
			LogUtil.info(TAG, "蓝牙连接状态发生变化!onConnectionStateChange()");
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				mBluetoothGatt.discoverServices();
				broadcastUpdate(intentAction);
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				mBluetoothGatt.close();
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			LogUtil.info(TAG, "发现蓝牙服务onServicesDiscovered()");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				// enable notification
				enableNotify();
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LogUtil.info(TAG, "读取数据onCharacteristicRead()");
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			LogUtil.info(TAG, "写入数据onDescriptorWrite()");
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			LogUtil.info(TAG, "接收数据onCharacteristicChanged()");
			String str = "";
			switch (DEVICE_TYPE) {
			case 1:// 脂肪秤
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
				if (characteristic.getValue() != null) {
					LogUtil.info(TAG, characteristic.getStringValue(0));
				}
				break;
			case 2:// 血压计
				LogUtil.info(TAG, "数据长度：" + characteristic.getValue().length);
				for (int i = 0;; ++i) {
					if (i >= characteristic.getValue().length) {
						receiveData = str;
						broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
						LogUtil.info(TAG, "血压计结果数据：" + receiveData);
						return;
					}
					str = str + (0xFF & characteristic.getValue()[i]) + " ";
				}
			case 3:// 血糖仪
				for (int i = 0;; ++i) {
					if (i >= characteristic.getValue().length) {
						receiveData = str;
						broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
						LogUtil.info(TAG, "血糖仪结果数据：" + receiveData);
						return;
					}
					str = str + (0xFF & characteristic.getValue()[i]) + " ";
				}
			default:
				break;
			}

		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			LogUtil.info(TAG, "执行了onReadRemoteRssi()! rssi = " + rssi);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			LogUtil.info(TAG, "-----------onCharacteristicWrite---------status:" + status);

		};
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			LogUtil.info(TAG, "心率数据Received heart rate: %d" + heartRate);
			LogUtil.info(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, receiveData);
		} else {
			// 脂肪秤和血糖仪设备数据返回
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				String s = FatScaleDataUtil.byteToHexStringFormat(data);
				LogUtil.info(TAG, "原始数据：" + s);
				if (DEVICE_TYPE == 1) {
					intent.putExtra(EXTRA_DATA, s);
				} else {
					intent.putExtra(EXTRA_DATA, receiveData);
				}
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.info("smarhit", "service中的onBind()");
		isBind = true;
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.info("smarhit", "service中的onUnbind()");
		close();
		isBind = false;
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				LogUtil.info(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			LogUtil.info(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	public void scanLeDevice(boolean enable) {
		if (enable) {
			mScanTime = System.currentTimeMillis();
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	public void scanLeDevice(boolean enable, String deviceName) {
		this.mDeviceName = deviceName;
		if (enable) {
			mScanTime = System.currentTimeMillis();
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	public void scanLeDevice(String collectedDevice) {
		this.mCollectedDevice = collectedDevice;
		LogUtil.info("smarhit", collectedDevice + "数据已收集，开始扫描其他设备");
		mScanTime = System.currentTimeMillis();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect() {
		if (mBluetoothAdapter == null || mDevice == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		mBluetoothGatt = mDevice.connectGatt(BluetoothLeService.this, false, mGattCallback);
		LogUtil.info(TAG, "Trying to device create a new connection.");

		if (mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
			DEVICE_TYPE = 1;
		} else if (mDevice.getName().equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
			DEVICE_TYPE = 2;
		} else if (mDevice.getName().equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
			DEVICE_TYPE = 3;
		}
		if (mBluetoothGatt.connect()) {
			mConnectionState = STATE_CONNECTING;
			LogUtil.info(TAG, "device create a new connection ok.");
			return true;
		} else {
			return false;
		}

		// if (mBluetoothDeviceAddress != null &&
		// address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
		// LogUtil.info(TAG,
		// "Trying to use an existing mBluetoothGatt for connection.");
		// if (mBluetoothGatt.connect()) {
		// mConnectionState = STATE_CONNECTING;
		// return true;
		// } else {
		// return false;
		// }
		// }

		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
	}

	public boolean reConnect() {
		if (mBluetoothAdapter == null || mDevice == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		// if (mBluetoothDeviceAddress != null &&
		// address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
		// LogUtil.info(TAG,
		// "Trying to use an existing mBluetoothGatt for connection.");
		// if (mBluetoothGatt.connect()) {
		// mConnectionState = STATE_CONNECTING;
		// return true;
		// } else {
		// return false;
		// }
		// }

		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.

		mBluetoothGatt = mDevice.connectGatt(BluetoothLeService.this, true, mGattCallback);
		LogUtil.info(TAG, "Trying to reConnect.");
		if (mDevice.getName().equals(BLEConstants.TYPE_FATSCALE_DEVICE_NAME)) {
			DEVICE_TYPE = 1;
		} else if (mDevice.getName().equals(BLEConstants.TYPE_BLOODPRESSURE_DEVICE_NAME)) {
			DEVICE_TYPE = 2;
		} else if (mDevice.getName().equals(BLEConstants.TYPE_BLOODSUGAR_DEVICE_NAME)) {
			DEVICE_TYPE = 3;
		}

		if (mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				LogUtil.info(TAG, "Trying to reConnect. ok.");
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		LogUtil.info(TAG, "mBluetoothGatt is close  before");
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
		broadcastUpdate(ACTION_GATT_CLOSE);
		LogUtil.info(TAG, "mBluetoothGatt is close");
	}

	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized");
			return;
		}
		boolean isWrite = mBluetoothGatt.writeCharacteristic(characteristic);
		if (isWrite) {
			LogUtil.info(TAG, "写入数据成功");
		} else {
			LogUtil.info(TAG, "写入数据失败");
		}

	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LogUtil.info(TAG, "BluetoothAdapter not initialized");
			return;
		}
		LogUtil.info(TAG, "write set notification " + enabled);
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		if (descriptor != null) {
			LogUtil.info(TAG, "write descriptor");
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	public BluetoothGattService getSupportedGattService() {
		if (mBluetoothGatt == null) {
			LogUtil.info(TAG, "mBluetoothGatt is  null ,try reconnect!");
			boolean isconnectok = connect();
			if (!isconnectok) {
				connect();
			}
			return null;
		}
		String UUID_SERVICE;
		switch (DEVICE_TYPE) {
		case 1:// 脂肪秤
			UUID_SERVICE = BLEConstants.UUID_FATSCALE_SERVICE;
			break;
		case 2:// 血压计
			UUID_SERVICE = BLEConstants.UUID_BLOOD_PRESSURE_SERVICE;
			break;
		case 3:// 血糖仪
			UUID_SERVICE = BLEConstants.UUID_BLOODSUGAR_SERVICE;
			break;
		default:
			UUID_SERVICE = BLEConstants.UUID_FATSCALE_SERVICE;
			break;
		}

		LogUtil.info(TAG, "device type=" + DEVICE_TYPE);
		BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(UUID_SERVICE));
		return service;
	}

	/**
	 * Read the RSSI for a connected remote device.
	 * */
	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;

		return mBluetoothGatt.readRemoteRssi();
	}

	private void enableNotify() {
		BluetoothGattService service = getSupportedGattService();
		if (null == service) {
			LogUtil.info(TAG, "BluetoothGattService service is null");
			boolean isconnectok = connect();
			if (!isconnectok) {
				connect();
			}
			return;
		}
		BluetoothGattCharacteristic gattCharacteristic = null;
		switch (DEVICE_TYPE) {
		case 1:
			gattCharacteristic = service.getCharacteristic(UUID.fromString(BLEConstants.UUID_CHARACTERISTIC));
			break;
		case 2:
			gattCharacteristic = service.getCharacteristic(UUID
					.fromString(BLEConstants.UUID_BLOOD_PRESSURE_CHARACTERISTIC));
			break;
		case 3:
			gattCharacteristic = service
					.getCharacteristic(UUID.fromString(BLEConstants.UUID_BLOODSUGAR_CHARACTERISTIC));
			break;
		default:
			gattCharacteristic = service.getCharacteristic(UUID.fromString(BLEConstants.UUID_CHARACTERISTIC));
			break;
		}
		if (null == gattCharacteristic) {
			LogUtil.info(TAG, "BluetoothGattCharacteristic gattCharacteristic is null");
			return;
		}

		boolean bool = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
		LogUtil.info(TAG, "setCharacteristicNotification:" + bool);
		BluetoothGattDescriptor descriptor = null;
		switch (DEVICE_TYPE) {
		case 1:
			descriptor = gattCharacteristic.getDescriptor(UUID.fromString(BLEConstants.UUID_DESCRIPTER));
		case 2:
			descriptor = gattCharacteristic.getDescriptor(UUID.fromString(BLEConstants.UUID_BLOOD_PRESSURE_DESCRIPTER));
			break;
		case 3:
			descriptor = gattCharacteristic.getDescriptor(UUID.fromString(BLEConstants.UUID_BLOODSUGAR_DESCRIPTER));
			break;

		default:
			descriptor = gattCharacteristic.getDescriptor(UUID.fromString(BLEConstants.UUID_DESCRIPTER));
			break;
		}
		if (null != descriptor) {
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}

	}

}
