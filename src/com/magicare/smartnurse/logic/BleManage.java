package com.magicare.smartnurse.logic;

import java.util.Arrays;

import android.content.Context;
import android.util.Log;
import com.magicare.smartnurse.ble.FatScaleDataUtil;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

public class BleManage {

	private int ERROR_COUNT = 3;
	private Context mContext;

	public BleManage(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public float setReceiveResultData(String str) {
		if (checkReturnDate(str, ERROR_COUNT) != 0) {
			return 0;
		}
		// 正确了则重新计数
		ERROR_COUNT = 0;
		if (str.contains("688510")) {
			return analysis(str);
		} else {
			PromptManager.showToast(mContext, false,"收到数据错误！请重新连接");
			return 0;
		}
	}

	/**
	 * 若返回数据错误，则再次发送数据，不超过3次
	 * 
	 * @param str
	 * @param count2
	 */
	private float checkReturnDate(String str, int count2) {
		if (str.contains("fd31")) {
			// 错误信息
			ERROR_COUNT++;
//			PromptManager.showToast(mContext,false, "第" + ERROR_COUNT + "次数据错误,重新发送数据!");
			// 重新发送一次数据
			// write(BLEConstants.UUID_CHARACTERISTIC, getWriteData());

			if (count2 == 3) {
				// 三次收到错误信息
//				PromptManager.showToast(mContext, false,"蓝牙连接错误");
				return 2;
			} else {
				return 1;
			}
		}
		if (str.contains("fd33")) {
			// 脂肪错误
//			PromptManager.showToast(mContext, false,"秤提示脂肪错误");
			// 重新发送一次数据
			ERROR_COUNT++;
			// write(BLEConstants.UUID_CHARACTERISTIC, getWriteData());
			if (count2 == 3) {
				// 三次收到错误信息
				PromptManager.showToast(mContext, false,"蓝牙连接错误");
				return 2;
			} else {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 解析数据
	 * 
	 * @param str
	 */
	private float analysis(String str) {
		str = str.substring(6, str.length());
		byte[] data = FatScaleDataUtil.hexStringToByte(str);
		String str1 = FatScaleDataUtil.byteToHexString(data[0]);
		float weight = FatScaleDataUtil.getHightAndLowSum(data[4], data[5]);
		if ("cf".equals(str1)) {
			weight = weight * 0.1f;
		} else if ("ce".equals(str1)) {
			weight = weight * 0.1f;
		} else if ("cb".equals(str1)) {
			weight = weight * 0.01f;
		} else if ("ca".equals(str1)) {
			weight = weight * 1000;
		}
		return weight;

	}

	public byte[] getWriteData() {
		StringBuilder sb = new StringBuilder("68050e");
		sb.append("00").append("01").append("00").append(Integer.toHexString(160)).append(Integer.toHexString(60))
				.append("01");
		String data = sb.toString();
		data += "00000000000000000000";
		String lastS = FatScaleDataUtil.getHexStringSum(data);
		byte[] conncetByte = FatScaleDataUtil.hexStringToByte(lastS);
		Log.i("info", "写入前的byte数组：" + Arrays.toString(conncetByte));
		return conncetByte;
	}

	protected void analyseData(String data) {
		// TODO Auto-generated method stub
		PromptManager.closeProgressDialog();
		LogUtil.info("smarhit", "返回的结果:" + data);
		StringBuilder sb = new StringBuilder(data);
		String str = sb.toString().replaceAll("\\s", "");
		setReceiveResultData(str);
	}

}
