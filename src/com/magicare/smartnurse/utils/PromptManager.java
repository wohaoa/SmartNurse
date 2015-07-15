package com.magicare.smartnurse.utils;

import com.magicare.smartnurse.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author:scott
 * 
 *               Function:提示框工具类
 * 
 *               Date:2014年5月12日
 */
public class PromptManager {
	// 滚动条
	private static ProgressDialog dialog;

	// 当测试阶段时true
	private static final boolean isShow = true;

	// 提示框
	// Toast
	public static void showProgressDialog(Context context) {
		dialog = new ProgressDialog(context);
		dialog.setMessage("请等候，数据加载中……");
		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				closeProgressDialog();

			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				closeProgressDialog();
			}
		});
		dialog.show();

	}

	public static void showProgressDialog(Context context, String message) {
		dialog = new ProgressDialog(context);
		dialog.setMessage(message);
		dialog.show();

	}

	public static void closeProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 当判断当前手机没有网络时使用
	 * 
	 * @param context
	 */
	public static void showNoNetWork(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(R.string.app_name).setMessage("当前无网络")
				.setPositiveButton("设置", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到系统的网络设置界面
						Intent intent = new Intent();
						intent.setClassName("com.android.settings",
								"com.android.settings.Settings");
						context.startActivity(intent);

					}
				}).setNegativeButton("知道了", null).show();

	}

	/**
	 * 退出系统
	 * 
	 * @param context
	 */
	public static void showExitSystem(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher)
				//
				.setTitle(R.string.app_name)
				//
				.setMessage("是否退出应用")
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process
								.myPid());
						// 多个Activity——懒人听书：没有彻底退出应用
						// 将所有用到的Activity都存起来，获取全部，干掉
						// BaseActivity——onCreated——放到容器中
					}
				})//
				.setNegativeButton("取消", null)//
				.show();

	}

	public static void showToast(Context context, String msg) {
		Toast toast = new Toast(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toast, null);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(view);
		TextView tv_contnet = (TextView) view.findViewById(R.id.tv_content);
		tv_contnet.setText(msg);
		toast.show();
	}

	static Toast toast;
	public static void showToast(Context context, boolean isSuccess, String msg) {
		if (toast == null) {
			toast = new Toast(context);
		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toast, null);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
		if (isSuccess) {
			iv_icon.setImageResource(R.drawable.ic_right);
		} else {
			iv_icon.setImageResource(R.drawable.ic_wrong);
		}
		TextView tv_contnet = (TextView) view.findViewById(R.id.tv_content);
		tv_contnet.setText(msg);
		toast.show();
	}

	public static void showErrorToast(Context context) {
		showToast(context, "网络错误，没获取到数据，请检查你的网络连接状态!");
	}

	public static void showToast(Context context, int msgResId) {
		showToast(context, context.getResources().getString(msgResId));
	}

	/**
	 * 测试用 在正式投入市场：删
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToastTest(Context context, String msg) {
		if (isShow) {
			showToast(context, msg);
		}
	}

	/**
	 * 显示错误提示框
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showErrorDialog(Context context, String msg) {
		new AlertDialog.Builder(context)//
				.setIcon(R.drawable.ic_launcher)//
				.setTitle(R.string.app_name)//
				.setMessage(msg)//
				.setNegativeButton("网络错误", null)//
				.show();
	}

}
