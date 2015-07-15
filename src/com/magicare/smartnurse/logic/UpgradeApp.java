package com.magicare.smartnurse.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.LoginEntity;
import com.magicare.smartnurse.bean.UpgradeBean;
import com.magicare.smartnurse.database.dao.DBWarning;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

public class UpgradeApp {

	private Context mContext;

	private HttpClientUtil httpClient;

	private String LOAD_APK_URL;

	private String str;

	public UpgradeApp(Context mContext) {
		super();
		this.mContext = mContext;
		this.httpClient = HttpClientUtil.getInstance();
	}

	public UpgradeApp(Context mContext, String str) {
		super();
		this.mContext = mContext;
		this.httpClient = HttpClientUtil.getInstance();
		this.str = str;
	}

	/**
	 * 
	 * <p>
	 * function :版本检测
	 * </p>
	 * :
	 */
	public void checkVersionCode() {

		if (versionDialog != null && versionDialog.isShowing()) {
			return;
		}

		nativeVersionCode = ConfigManager.getIntValue(mContext, ConfigManager.VERSIONCODE);
		httpClient.getUpgradeInfo(mContext, new IOperationResult() {

			@Override
			public void operationResult(boolean isSuccess, String json, String errors) {
				// TODO Auto-generated method stub

				// if (!TextUtils.isEmpty(json)) {
				// LogUtil.info("---版本升级：" + json);
				// UpgradeBean bean = JSON.parseObject(json, UpgradeBean.class);
				// if (bean.getVersionCode() > nativeVersionCode) {
				// LOAD_APK_URL = bean.getUpgradeUrl();
				// showBar();
				// }
				// }

				if (isSuccess) {
					if (!TextUtils.isEmpty(json)) {
						LogUtil.info("---版本升级：" + json);

						BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
						if (baseBean.getStatus() == 0) {
							UpgradeBean bean = JSON.parseObject(baseBean.getData(), UpgradeBean.class);

							if (bean.getVersionCode() > nativeVersionCode) {
								LOAD_APK_URL = bean.getUpgradeUrl();
								showBar();
							} else {
								if (!TextUtils.isEmpty(str)) {
									PromptManager.showToast(mContext, true, str);
								}
							}
						}
					}
				}

			}
		});

	}

	private AlertDialog versionDialog;

	/**
	 * 显示更新对话框
	 */
	public void showBar() {
		AlertDialog.Builder versionBuilder = new AlertDialog.Builder(mContext);
		versionBuilder.setTitle(mContext.getString(R.string.verisonTitle));
		versionBuilder.setMessage(mContext.getString(R.string.versionMessage));
		versionBuilder.setPositiveButton(mContext.getString(R.string.loadOk), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				new UpdateTask().execute(LOAD_APK_URL);
				versionDialog.dismiss();
			}
		});

		versionDialog = versionBuilder.create();
		versionDialog.setCancelable(false);
		versionDialog.show();
	}

	/**
	 * 更新
	 */
	public void installApk() {
		Intent upIntent = new Intent(Intent.ACTION_VIEW);
		upIntent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Constants.APPNAME)),
				"application/vnd.android.package-archive");
		// 安装界面
		mContext.startActivity(upIntent);
		// Editor edit = sp.edit();
		// edit.putBoolean("isUpdate", true);
		// edit.commit();
		// mContext.finish();

	}

	// 下载进度框
	private ProgressDialog loadDialog;

	private int nativeVersionCode;

	/**
	 * 获取apk,下载数据
	 */
	public class UpdateTask extends AsyncTask<String, Integer, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadDialog = new ProgressDialog(mContext);
			loadDialog.setTitle(mContext.getString(R.string.loadingTitle));
			loadDialog.setMessage(mContext.getString(R.string.loadingMessage));
			loadDialog.setMax(100);
			loadDialog.setIndeterminate(false);
			loadDialog.setCancelable(false);
			loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			loadDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadDialog.dismiss();
			installApk();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			loadDialog.setProgress(values[0]);
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			InputStream in = null;
			try {
				HttpResponse response = client.execute(httpGet);

				if (response.getStatusLine().getStatusCode() == 200) {
					long length = response.getEntity().getContentLength();
					in = response.getEntity().getContent();
					FileOutputStream out = null;
					File file = new File(Environment.getExternalStorageDirectory(), Constants.APPNAME);
					// 删除之前的文件
					if (file.exists()) {
						file.delete();
					}
					out = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					long count = 0;
					while ((ch = in.read(buf)) != -1) {
						out.write(buf, 0, ch);
						count += ch;
						publishProgress((int) (count * 100 / length));

					}
					out.flush();
					in.close();
					if (out != null) {
						out.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
