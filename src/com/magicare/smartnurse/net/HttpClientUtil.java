package com.magicare.smartnurse.net;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;

/**
 * 
 * @author scott. Function:HTTP请求接口的工具类
 * 
 */
public class HttpClientUtil {

	private static HttpClientUtil httpClient = new HttpClientUtil();

	private HttpClientUtil() {
	}

	public synchronized static HttpClientUtil getInstance() {
		if (null != httpClient) {
			return httpClient;
		}
		return new HttpClientUtil();
	}

	// /**
	// *
	// * Function:注册信息
	// *
	// * @param mContext
	// * @param mobileNumber
	// * @param operationResult
	// */
	// public void register(Context mContext, String mAccessToken, String
	// mobileNumber, IOperationResult operationResult) {
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("mobile", mobileNumber);
	// NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST,
	// mAccessToken, params, operationResult, null);
	// task.executeProxy(Constants.HTTP_HOST + "/members");
	// }

	/**
	 * 登录
	 * 
	 * @param mContext
	 * @param userName
	 *            帐号
	 * @param passWord
	 *            密码
	 * @param ip_registerid
	 *            极光推送的id
	 * @param operationResult
	 */
	public void login(Context mContext, String userName, String passWord, String jp_registerid,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", userName);
		params.put("password", passWord);
		params.put("jp_registerid", jp_registerid);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, "", params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Index/login");
	}

	/**
	 * 修改密码
	 * 
	 * @param mContext
	 * @param old_passWord
	 *            老密码
	 * @param new_passWord
	 *            新密码
	 * @param operationResult
	 */
	public void alterPassword(Context mContext, String mAccessToken, String old_passWord, String new_passWord,
			IOperationResult operationResult) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("old_password", old_passWord);
		params.put("new_password", new_passWord);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/password");
	}

	/**
	 * 点击处理事件借口
	 * 
	 * @param mContext
	 * @param alarm_id
	 *            被处理的报警信息id
	 * @param operationResult
	 */
	public void resolve(Context mContext, String mAccessToken, String alarm_id, IOperationResult operationResult) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("alarm_id", alarm_id);

		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/resolve");
	}

	/**
	 * 反馈结果 列表
	 * 
	 * @param mContext
	 * @param operationResult
	 */
	public void getFeedListInfo(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/feedList");
	}

	/**
	 * 反馈结果处理接口
	 * 
	 * @param mContext
	 * @param feed_id
	 *            反馈信息id
	 * @param reason
	 *            反馈的原因
	 * @param nurse_id
	 *            护士Id
	 * @param alarm_id
	 *            被处理的报警信息id
	 * @param operationResult
	 */

	public void feedBackInfo(Context mContext, String mAccessToken, String feed_id, String reason, String nurse_id,
			String alarm_id, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		if (feed_id.equals("0")) {
			params.put("reason", reason);
		} else {
			params.put("feed_id", feed_id);
		}
		params.put("nurse_id", nurse_id);
		params.put("alarm_id", alarm_id);

		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/feedDeal");
	}

	/**
	 * 查询并且拉取老人信息
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人id
	 * @param bracelet_id
	 *            (老人Id和手环id 二选一) 手环id
	 * @param operationResult
	 */

	public void QueryAndGetOldUserInfo(Context mContext, String mAccessToken, String old_id, String bracelet_id,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		params.put("bracelet_id", bracelet_id);

		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getBaseData");
	}

	/**
	 * 查询并且拉取老人运动信息
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人id
	 * @param date_start
	 *            时间
	 * @param operationResult
	 */

	public void QueryAndGetOldUserSportsInfo(Context mContext, String mAccessToken, String old_id, String date_start,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		params.put("date_start", date_start);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getMoveSleep");
		// task.executeProxy(Constants.HTTP_HOST + "/Old/getMoveData");
	}

	// /**
	// * 查询并且拉取老人睡眠信息
	// *
	// * @param mContext
	// * @param old_id
	// * 老人id
	// * @param date_start
	// * 时间格式15 01 09
	// * @param operationResult
	// */
	//
	// public void QueryAndGetOldUserSleepInfo(Context mContext, String
	// mAccessToken, String old_id, String date_start,
	// IOperationResult operationResult) {
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("old_id", old_id);
	// params.put("date_start", date_start);
	// NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST,
	// mAccessToken, params, operationResult, null);
	// // task.executeProxy(Constants.HTTP_HOST + "/Old/getSleepData");
	// task.executeProxy(Constants.HTTP_HOST + "/Old/getMoveSleep");
	// }

	public void getMonthOldUserHealthInfo(Context mContext, String mAccessToken, String old_id, String date,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		params.put("month_date", date);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getMonthCollectData");		
	}
	
	/**
	 * 获取老人健康所有记录
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人id
	 * @param bracelet_id
	 *            手环id
	 * @param operationResult
	 */
	public void getAllOldUserHealthInfo(Context mContext, String mAccessToken, String old_id,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getAllData");
	}

	/**
	 * 获取老人健康所有记录
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人id
	 * @param bracelet_id
	 *            手环id
	 * @param operationResult
	 */
	public void getCollectRecord(Context mContext, String mAccessToken, String old_id, String page, String page_size,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		params.put("page", page);
		params.put("page_size", page_size);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getAllData");
	}

	/**
	 * 上传老人测量数据
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人id
	 * @param array
	 *            测出的数据用json传给服务器
	 * @param operationResult
	 */
	public void updateCollectData(Context mContext, String mAccessToken, String json, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data", json);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/saveCollectData");
	}

	/**
	 * 拉去未处理报警信息 和拉去未反馈报警信息
	 * 
	 * @param mContext
	 * @param pension_areaid
	 *            区域ID
	 * @param alarm_status
	 *            报警状态，2为未处理，3为所有
	 * @param operationResult
	 */
	public void getAlarmInfoByStatus(Context mContext, String mAccessToken, int pension_areaid, String alarm_status,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pension_areaid", pension_areaid + "");
		params.put("alarm_status", alarm_status);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/getAlarm");
	}

	/**
	 * 拉取老人报警信息记录
	 * 
	 * @param mContext
	 * @param old_id
	 *            老人编号
	 * @param page
	 *            页码
	 * @param page_num
	 *            数量
	 * @param operationResult
	 */
	public void getOldAlartHistoryInfo(Context mContext, String mAccessToken, String old_id, String page,
			String page_num, IOperationResult operationResult) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id);
		params.put("page", page);
		params.put("page_num", page_num);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Old/getAlarmData");

	}

//	/**
//	 * 获取区域的信息接口
//	 * 
//	 * @param mContext
//	 * @param nurse_id
//	 * @param operationResult
//	 */
//
//	public void getRegionInfoByNurseId(Context mContext, String mAccessToken, String nurse_id,
//			IOperationResult operationResult) {
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("nurse_id", nurse_id);
//		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
//		task.executeProxy(Constants.HTTP_HOST + "/User/getArea");
//
//	}

	/**
	 * 获取这个区域所有老人接口
	 * 
	 * @param mContext
	 * @param pension_areaid
	 * @param operationResult
	 */

	public void getAllOldUserInfo(Context mContext, String mAccessToken, int pension_areaid,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pension_areaid", pension_areaid + "");
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/getAreaOld");
	}

	/**
	 * 上传老人头像
	 * 
	 * @param mContext
	 * @param old_id
	 * @param localUrl
	 * @param operationResult
	 */
	public void updateHeadPortrait(Context mContext, String mAccessToken, String localUrl, int old_id,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id + "");
		LogUtil.info("smarhit", "updateHeadPortrait old_id=" + (old_id + "").toString());
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, true,
				localUrl);
		task.executeProxy(Constants.HTTP_HOST + "/Old/uploadAvatar?old_id=" + old_id);
	}

	/**
	 * 上传叮嘱图片
	 * 
	 * @param mContext
	 * @param img
	 * @param localUrl
	 * @param operationResult
	 */
	public void uploadImg(Context mContext, String mAccessToken, String localUrl, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("img", localUrl);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, true,
				localUrl);
		task.executeProxy(Constants.HTTP_HOST + "/Exhort/uploadImg");
	}

	/**
	 * 获取一个老人的所有叮嘱
	 * 
	 * @param mContext
	 * @param pension_areaid
	 * @param operationResult
	 */

	public void getExhortByOld(Context mContext, String mAccessToken, int old_id, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("old_id", old_id + "");
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Exhort/getExhortByOld");
	}

	public void logout(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/logout");
	}

	/**
	 * 下载头像图片
	 * 
	 * @param mContext
	 * @param localUrl
	 * @param operationResult
	 */
	public void loadingPhoto(Context mContext, String mAccessToken, String localUrl, String remoteUrl,
			IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.GET, mAccessToken, null, operationResult, true,
				localUrl);
		task.executeProxy(remoteUrl);
	}

	/**
	 * 
	 * Function:获取版本升级信息
	 * 
	 * @param mContext
	 * @param operationResult
	 */
	public void getUpgradeInfo(Context mContext, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, null, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Index/checkVersion");
	}

	/**
	 * 提交建议
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param type
	 *            int, 类型，1-建议，2-投诉，3-其他
	 * @param content
	 *            内容
	 * @param operationResult
	 */

	public void commitAdvice(Context mContext, String mAccessToken, String type, String content,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type);
		params.put("content", content);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/feedback");

	}

	public void getOutsideWarning(Context mContext, String mAccessToken, String warning_ids,
			IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("alarm_ids", warning_ids);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/multiGetAlarm");

	}

	/**
	 * 
	 * Function: 获取老人状态信息
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 * @param pension_areaid
	 */
	public void getUserStatusInfo(Context mContext, String mAccessToken, int pension_areaid, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pension_areaid", pension_areaid + "");
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/getOldLocation");
	}
	
	/**
	 * 
	 * Function: 获取老人状态信息
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 */
	public void getUserStatusInfo(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/getOldLocation");
	}

	/**
	 * 
	 * Function: 获取所有未回复的叮嘱信息
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 */
	public void getConcern(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Exhort/getMyExhort");
	}

	/**
	 * 
	 * Function: 上传关于叮嘱的回复
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 * @param exhort_id
	 * @param reply_content
	 * @param reply_images
	 */
	public void updateConcern(Context mContext, String mAccessToken, int exhort_id, String reply_images,
			String reply_content, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("exhort_id", exhort_id + "");
		params.put("reply_content", reply_content);
		params.put("reply_images", reply_images);

		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/Exhort/reply");
	}

	/**
	 * 
	 * Function: 上传异常信息
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param logtext
	 * @param operationResult
	 */

	public void uploadException(Context mContext, String mAccessToken, String log_text, IOperationResult operationResult) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("logtext", log_text);
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, params, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/User/errorLog");
	}
	
	/**
	 * 
	 * Function: 监控屏拉取活动统计
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 */
	public void getActive(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/TV/ActiveStatistic");
	}
	
	/**
	 * 
	 * Function: 拉取养老院各区域统计数据
	 * 
	 * @param mContext
	 * @param mAccessToken
	 * @param operationResult
	 */
	public void getAreaActive(Context mContext, String mAccessToken, IOperationResult operationResult) {
		NetAsyncTask task = new NetAsyncTask(mContext, ConnectWay.POST, mAccessToken, null, operationResult, null);
		task.executeProxy(Constants.HTTP_HOST + "/TV/AreaStatistic");
	}

}
