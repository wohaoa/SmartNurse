package com.magicare.smartnurse.net;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.activity.LoginActivity;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * 
 * @author:scott Function:异步请求 Date:2014年5月12日
 */

public class NetAsyncTask extends AsyncTask<String, Integer, String> {

	private static final String TAG = "NetAsyncTask";
	private Context mContext;

	/** 请求的方式 */
	private ConnectWay mConnectWay;

	/** 回调接口 */
	private IOperationResult mOperationResult;

	private String mAccessToken;

	/** 请求认证 */
	private String mJson;

	/** 请求参数 */
	private Map<String, String> params;

	private boolean isFile;
	private String mLocalUrl;

	/**
	 * 
	 * Function:钩子函数
	 * 
	 * @param context
	 *            :上下文对象
	 * @param connectWay
	 *            ：请求的方式
	 * @param params
	 *            :请求参数是Map的形式
	 * @param operationResult
	 *            ：回调接口
	 * @param json
	 *            :上传的参数是JSON格式【注意params和json是互斥的， 其中有一个必定为空】
	 */
	public NetAsyncTask(Context context, ConnectWay connectWay, String accessToken, Map<String, String> params,
			IOperationResult operationResult, String json) {
		mContext = context;
		mConnectWay = connectWay;
		this.mAccessToken = accessToken;
		this.params = params;
		mOperationResult = operationResult;
		mJson = json;
	}

	public NetAsyncTask(Context context, ConnectWay connectWay, String accessToken, Map<String, String> params,
			IOperationResult operationResult, boolean isFile, String localUrl) {
		mContext = context;
		mConnectWay = connectWay;
		this.mAccessToken = accessToken;
		this.params = params;
		mOperationResult = operationResult;
		this.isFile = isFile;
		this.mLocalUrl = localUrl;
	}
	
//	public NetAsyncTask(Context context, ConnectWay connectWay, String accessToken, Map<String, Integer> params,
//			IOperationResult operationResult, boolean isFile, String localUrl, String ext) {
//		mContext = context;
//		mConnectWay = connectWay;
//		this.mAccessToken = accessToken;
//		this.params = params;
//		mOperationResult = operationResult;
//		this.isFile = isFile;
//		this.mLocalUrl = localUrl;
//	}
	
	

	/**
	 * 增加了网络判断
	 * 
	 * @param params
	 * @return
	 */
	public final AsyncTask<String, Integer, String> executeProxy(String... params) {
		if (NetUtil.isNetWorkConnected(mContext) || NetUtil.is3GConnectivity(mContext)) {
			ConfigManager.setBooleanValue(mContext.getApplicationContext(), ConfigManager.LOGIN_SUCCESS, true);
			return execute(params);
		} else {
			PromptManager.closeProgressDialog();
			if (ConfigManager.getBooleanValue(mContext.getApplicationContext(), ConfigManager.LOGIN_SUCCESS, false)) {
				PromptManager.showToast(mContext,false, "亲，您的网络出现了异常!");
			}
			ConfigManager.setBooleanValue(mContext.getApplicationContext(), ConfigManager.LOGIN_SUCCESS, false);
//			if (mOperationResult != null) {
//				mOperationResult.operationResult(false, null, "亲，您的网络出现了异常!");
//			}
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * 子线程执行该方法
	 */
	@Override
	protected String doInBackground(String... url) {
		if (mConnectWay == ConnectWay.GET) {
			if (isFile) {
				return NetUtil.loadingBitmap(mLocalUrl, url[0], mAccessToken);
			} else {
				return NetUtil.sendGet(mContext, url[0], mAccessToken);
			}
		} else {
			if (isFile) {
				return NetUtil.updateFile(mLocalUrl, url[0], mAccessToken, params);
			} else {
				if (TextUtils.isEmpty(mJson)) {
					return NetUtil.sendPost(mContext, url[0], mAccessToken, params);
				} else {
					return NetUtil.sendPost(mContext, url[0], mAccessToken, mJson);
				}
			}

		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (null == mOperationResult) {
			return;
		}
		LogUtil.info("1lhw", "result = "+result);
		/** 把数据抛给回调接口 */
		if (TextUtils.isEmpty(result)) {
			PromptManager.showToast(mContext,false, "亲，请检查一下网络状态， 稍后重试!");
			PromptManager.closeProgressDialog();
			return;
		} else {
			if (result.equals("ok")) {
				mOperationResult.operationResult(true, result, null);
			} else {
				if (!result.startsWith("{")) {
					PromptManager.showToast(mContext, false, "返回数据出错，请检查您的网络，重新操作一次！");
				} else {
					BaseBean baseBean = JSON.parseObject(result, BaseBean.class);
					if (baseBean.getStatus() == 200) {// access_token过期，重新登录
						Intent mIntent = new Intent(mContext, LoginActivity.class);
						mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(mIntent);
					} else {
						mOperationResult.operationResult(true, result, null);
					}
				}
			}
		}

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
