package com.magicare.smartnurse.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author:scott Function:Http请求类 Date:2014年5月12日
 */
public class NetUtil {

	private static final String TAG = "NetUtil";

	public static final int MAX_CONNECTIONS = 10;
	public static final int DEFAULT_SOCKET_TIMEOUT = 120 * 1000;
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
	public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ENCODING_GZIP = "gzip";
	public static final String LOG_TAG = "AsyncHttpClient";
	public static final int TIME_OUT = DEFAULT_SOCKET_TIMEOUT;

	// private static boolean hasOutNetWork = false;
	//
	// public static void isNetWorkConnected(final Handler mhander) {
	// new Thread() {
	// public void run() {
	// for (int i = 0; i < 3; i++) {
	// try {
	// String ipAddress = "119.75.217.56";
	// Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ipAddress);
	// int status;
	// status = p.waitFor();
	// if (status == 0) {
	// hasOutNetWork = true;
	// } else {
	// hasOutNetWork = false;
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }
	// if (mhander != null) {
	// mhander.sendEmptyMessage(5);
	// }
	// };
	// }.start();
	//
	// }

	/**
	 * 判断网络是否连接
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetWorkConnected(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = manager.getActiveNetworkInfo();
		if (network != null && network.isConnected()) {
			if (network.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	public static boolean is3GConnectivity(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		// WIFI的描述信息：NetworkInfo
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (networkInfo != null)
			return networkInfo.isConnected();
		return false;
	}

	/**
	 * WIFI是否处于连接状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// WIFI的描述信息：NetworkInfo
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (networkInfo != null)
			return networkInfo.isConnected();
		return false;
	}

	/**
	 * Mobile(apn)是否处于连接状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAPNConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null)
			return networkInfo.isConnected();
		return false;
	}

	/**
	 * 
	 * Functioin:GET方式请求数据
	 * 
	 * @param url
	 *            :请求的路径
	 * @return ：请求响应的JSON字符串
	 */
	public static String sendGet(Context mContext, String url, String mAccessToken) {
		HttpGet httpGet = new HttpGet();
		HttpClient httpClient = getHttpClient(mContext);
		try {
			if (!TextUtils.isEmpty(mAccessToken)) {
				// 登录和注册的时候，没有accessToken认证，所以需要提供一个默认的请求认证
				httpGet.addHeader(Constants.ACCESS_TOKEN, mAccessToken);
			}
			httpGet.setURI(new URI(url));
			Log.d(TAG, "GET REQEUST URL:" + url + "  mAccessToken=" + mAccessToken);
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (null != httpResponse) {
				int code = httpResponse.getStatusLine().getStatusCode();
				String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				Log.d(TAG, "GET RESPONSE CODE=" + code + ", result=" + result);
				if (code == HttpStatus.SC_OK) {
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "error:" + e.getMessage());
			return null;
		}

		return null;
	}

	/**
	 * 
	 * Function:POST请求方式 ， 参数是键值对形式
	 * 
	 * @param mContext
	 *            ：上下文对象
	 * @param url
	 *            ：请求地址
	 * @param params
	 *            ：请求参数
	 * @return
	 * 
	 */
	public static String sendPost(Context mContext, String url, String mAccessToken, Map<String, String> params) {

		Log.d(TAG, "POST REQUEST url:" + url + "  mAccessToken=" + mAccessToken);
		String str_result = "";
		HttpPost post = new HttpPost(url);

		if (!TextUtils.isEmpty(mAccessToken)) {
			// 登录和注册的时候，没有accessToken认证，所以需要提供一个默认的请求认证
			post.addHeader(Constants.ACCESS_TOKEN, mAccessToken);
		}
		// 需要把参数放到NameValuePair
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		try {

			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					paramPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			// 对请求参数进行编码，得到实体数据
			UrlEncodedFormEntity entitydata = new UrlEncodedFormEntity(paramPairs, "UTF-8");
			// 构造一个请求路径
			// 设置请求实体
			post.setEntity(entitydata);
			// 浏览器对象
			DefaultHttpClient client = new DefaultHttpClient();
			// 执行post请求
			HttpResponse response = client.execute(post);
			// 从状态行中获取状态码，判断响应码是否符合要求
			Log.d(TAG, "POST RESPONSE CODE:" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				str_result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "POST RESPONSE result:" + str_result);
		return str_result;

	}

	/**
	 * 
	 * Function:POST请求方式 ，请求参数是JSON形式。
	 * 
	 * @param mContext
	 *            :上下文对象
	 * @param url
	 *            ：请求的地址
	 * @param json
	 *            ：请求参数
	 * @return
	 * 
	 */
	public static String sendPost(Context mContext, String url, String mAccessToken, String json) {
		String result = "";
		HttpPost post = new HttpPost(url);
		if (!TextUtils.isEmpty(mAccessToken)) {
			// 登录和注册的时候，没有accessToken认证，所以需要提供一个默认的请求认证
			post.addHeader(Constants.ACCESS_TOKEN, mAccessToken);
		}
		Log.d(TAG, "POST REQUEST URL:" + url + "  mAccessToken=" + mAccessToken);
		try {

			List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
			paramPairs.add(new BasicNameValuePair("contact", json));

			UrlEncodedFormEntity entitydata = new UrlEncodedFormEntity(paramPairs, "UTF-8");
			// 构造一个请求路径
			// 设置请求实体
			post.setEntity(entitydata);
			Log.d(TAG, "POST REQUEST URL=" + url + ",  REQUEST JSON=" + json);
			HttpClient httpClient = getHttpClient(mContext);
			HttpResponse response = httpClient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			Log.d(TAG, "POST RESPONSE CODE:" + response.getStatusLine().getStatusCode());
			if (HttpStatus.SC_OK == code) {
				result = EntityUtils.toString(response.getEntity());
				Log.d(TAG, "POST RESPONSE CODE=" + code + ", POST RESPONSE result=" + result);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "error message:" + e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * Functioin:将map类型的参数转换为JSON字符串
	 * 
	 * @param params
	 *            :请求的参数
	 * @return：转换后的json字符串
	 */
	private static String getParamsToJson(Map<String, Object> params) {

		StringBuilder json_sb = new StringBuilder();
		json_sb.append("{");
		Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			if ((entry.getValue() instanceof Integer) || (entry.getValue() instanceof Float)
					|| (entry.getValue() instanceof Double)) {
				json_sb.append("\"" + entry.getKey() + "\":" + entry.getValue() + ",");
			} else {
				json_sb.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",");
			}
		}
		String json = json_sb.toString();
		json = json.substring(0, json.length() - 1);
		json += "}";
		return json;

	}

	/**
	 * 
	 * Functioin:对参数进行编码
	 * 
	 * @param params
	 * @return
	 */
	private static String encodeParameters(String url, Map<String, String> params) {

		StringBuffer buf = new StringBuffer();
		buf.append(url);
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			try {

				if (!buf.toString().contains("?")) {
					buf.append("?" + URLEncoder.encode(entry.getKey().toString(), "UTF-8")).append("=")
							.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));

				} else {
					buf.append("&" + URLEncoder.encode(entry.getKey().toString(), "UTF-8")).append("=")
							.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				}
				index++;
			} catch (java.io.UnsupportedEncodingException neverHappen) {

			}
		}

		return buf.toString();

	}

	private static HttpClient getHttpClient(Context mContext) {

		KeyStore trustStore;
		SSLSocketFactory sf = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf = new MySSLSocketFactory(trustStore);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new DefaultHttpClient();
		}

		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, TIME_OUT);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(MAX_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(httpParams, MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		/* 设置请求参数 */
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", sf, 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		HttpClient httpClient = new DefaultHttpClient(cm, httpParams);

		try {

			WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				// 获取当前正在使用的APN接入
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				Cursor mCursor = mContext.getContentResolver().query(uri, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录
					String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						HttpHost proxy = new HttpHost(proxyStr, 80);
						httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
					}
					mCursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new DefaultHttpClient();
		}

		return httpClient;
	}

	/*
	 * 
	 * POST/logsys/home/uploadIspeedLog!doDefault.html HTTP/1.1
	 * 
	 * 　　Accept: text/plain, 　　Accept-Language: zh-cn 　　Host: 192.168.24.56
	 * 　　Content
	 * -Type:multipart/form-data;boundary=-----------------------------7d
	 * b372eb000e2 　　User-Agent: WinHttpClient 　　Content-Length: 3693
	 * 　　Connection: Keep-Alive
	 * 
	 * 　　-------------------------------7db372eb000e2
	 * 
	 * 　　Content-Disposition: form-data; name="file"; filename="kn.jpg"
	 * 
	 * 　　Content-Type: image/jpeg
	 * 
	 * 　　(此处省略jpeg文件二进制数据...）
	 * 
	 * 　　-------------------------------7db372eb000e2--
	 */
	/**
	 * 
	 * Function:上传文件到服务器
	 * 
	 * @param localUrl
	 *            :本地文件的地址
	 * @param remoteUrl
	 *            ：远程服务器地址
	 * @return
	 * 
	 */
	public static String updateFile(String localUrl, String remoteUrl, String mAccessToken, Map<String, String> params) {
		LogUtil.info("smarhit", "localUrl" + localUrl + "   remoteUrl" + remoteUrl 
				+ "  mAccessToken" + mAccessToken +"  params" +params.get("img"));
		
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String result = "";
		try {
			URL url = new URL(remoteUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			if (!TextUtils.isEmpty(mAccessToken)) {
				conn.setRequestProperty(Constants.ACCESS_TOKEN, mAccessToken);
			}
			File updateFile = new File(localUrl);
			if (updateFile != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				LogUtil.info("lhw", "updateFile!=null");
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);

				// StringBuilder stringBuilder = new StringBuilder();
				// if (params != null && !params.isEmpty()) {
				// for (Map.Entry<String, String> entry : params.entrySet()) {
				// stringBuilder.append(entry.getKey()).append("=")
				// .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				// }
				// }
				//
				// byte[] myData = stringBuilder.toString().getBytes();
				//
				// dos.write(myData);

				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				if(params.get("img")!=null && !params.get("img").equals("")){
					sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + updateFile.getName() + "\""
							+ LINE_END);
				}else{
					sb.append("Content-Disposition: form-data; name=\"avatar\"; filename=\"" + updateFile.getName() + "\""
							+ LINE_END);
				}
				sb.append("Content-Type: application/octet-stream; charset=UTF-8" + LINE_END);
				sb.append(LINE_END);
				LogUtil.info("lhw", "sb="+sb.toString());
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(updateFile);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);

				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				if (res == 200) {
					result = changeInputStream(conn.getInputStream(), "UTF-8");
					Log.d(TAG, "POST RESPONSE CODE=" + res + ", POST RESPONSE result=" + result);
					return result;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String changeInputStream(InputStream inputStream, String encode) {
		// 内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	// public static String getAuthorization() {
	// byte[] bytes = (Constants.HTTP_CLIENT_KEY + ":" +
	// Constants.HTTP_CLIENT_SECRET).getBytes();
	// return "Basic " + Base64.encode(bytes, 0, bytes.length);
	// }

	// 使用ifmodified请求头，实现图片的缓存
	public static String loadingBitmap(final String localAddress, final String remoteAddress, String mAccessToken) {
		try {
			LogUtil.info("smarhit", "下载头像：url=" + remoteAddress);
			URL url = new URL(remoteAddress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			// 获取当前程序的缓存文件夹位置,在该文件夹创建缓存文件
			LogUtil.info("smarhit", "本地头像：url=" + localAddress);
			File cacheFile = new File(localAddress);
			if (cacheFile.exists()) { // 如果存在缓存文件
				conn.setIfModifiedSince(cacheFile.lastModified()); // 设置最后修改时间
			}

			int code = conn.getResponseCode();
			LogUtil.info("smarhit", "本地头像：code=" + code);
			if (code == 200) { // 响应200代表需要读取网络
				// 从网络读取数据生成字节数组
				byte[] data = parseData(conn.getInputStream());
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				// 把Bitmap图片压缩保存到cacheFile中
				// FileUtils.saveBitmap(bitmap, userbean.getOld_sn());
				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));
			} else if (code == 304) { // 响应304代表需要读取缓存
				// 从文件解码为图片
				LogUtil.info("smarhit", "本地头像304：url=" + localAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "ok";
	}

	/**
	 * 读取指定输入流中的数据, 返回一个字节数组
	 * 
	 * @param in
	 *            包含数据的输入流
	 * @return 所有数据组成的字节数组
	 */
	public static byte[] parseData(InputStream is) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1)
				baos.write(buffer, 0, len);
			byte[] data = baos.toByteArray();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (baos != null) {
						try {
							baos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}

		}
		return null;
	}

}
