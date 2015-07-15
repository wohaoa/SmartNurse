package com.magicare.smartnurse.net;

/**
 * 
 * @author:scott
 * 
 *               Function:Http请求的回调接口
 * 
 *               Date:2014年5月12日
 */
public interface IOperationResult {
	public void operationResult(boolean isSuccess, String json, String errors);
}
