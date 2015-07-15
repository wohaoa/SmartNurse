package com.magicare.smartnurse.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.adapter.ConcernDetailAdapter;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.CircleImageView;

public class ConcernActivity extends BaseActivity {
	private ListView lv_concern;
	private TextView child_name;
	private Button concernHistory;
	private ConcernBean concern;
	private CircleImageView iv_photo;
	private Button btn_back;
	private Button send_concern;
	private Button add_image;
	private EditText et_concern;
	private Context mContext;
	private Bitmap reply_img;
	private UserBean userBean;

	private final int REFRESH_CONCERN = 0;
	private final int REFRESH_IMAGE = 1;
	private final int REPLY_IMAGE = 2;
	public static final String action = "ConcernActivity action";
	private ConcernDetailAdapter mAdapter;
	private List<ConcernBean> concernlist = new ArrayList<ConcernBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concern);
		mContext = ConcernActivity.this;

		lv_concern = (ListView) findViewById(R.id.lv_concern);
		child_name = (TextView) findViewById(R.id.child_name);
		concernHistory = (Button) findViewById(R.id.concern_history);
		iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
		btn_back = (Button) findViewById(R.id.btn_back);
		send_concern = (Button) findViewById(R.id.send_concern);
		add_image = (Button) findViewById(R.id.add_image);
		et_concern = (EditText) findViewById(R.id.et_concern);

		btn_back.setOnClickListener(this);
		send_concern.setOnClickListener(this);
		add_image.setOnClickListener(this);
		concernHistory.setOnClickListener(this);

		et_concern.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					add_image.setVisibility(View.GONE);
					send_concern.setVisibility(View.VISIBLE);

				} else {
					add_image.setVisibility(View.VISIBLE);
					send_concern.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		initConcern();
	}

	/**
	 * 叮嘱详情初始化
	 */
	private void initConcern() {
		concern = (ConcernBean) getIntent().getSerializableExtra("concernbean");

		String photopath = FileUtils.SDPATH + concern.getOld_id() + ".JPEG";
		File file = new File(photopath);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap,
					(int) (10 * 1.6f));
			iv_photo.setImageBitmap(bitmap);
		}

		DBUser dbuser = DBUser.getInstance(ConcernActivity.this);
		dbuser.open();
		userBean = dbuser.getUserInfoById(concern.getOld_id() + "");
		dbuser.close();

		child_name.setText(userBean.getName() + "之"
				+ userBean.getChild_relation() + concern.getChild_name()
				+ "的叮嘱");

		concernlist.add(concern);
		mAdapter = new ConcernDetailAdapter(ConcernActivity.this, concernlist);
		lv_concern.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.concern_history:
			Intent intent = new Intent(this, ConcernHistory.class);
			intent.putExtra("userbean", userBean);
			startActivity(intent);
			break;
		case R.id.send_concern:
			if (!TextUtils.isEmpty(et_concern.getText())) {
				concern.setReply_content(et_concern.getText().toString());
				concern.setStatus(1);
			}
			concernlist.clear();
			concernlist.add(concern);
			mHandler.sendEmptyMessage(REFRESH_CONCERN);
			break;

		case R.id.add_image:
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				new PopupWindows(ConcernActivity.this, lv_concern);
			} else {
				PromptManager.showToast(getApplicationContext(), false,
						"sdcard已拔出，不能选择照片");
			}
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case REFRESH_CONCERN: // 在线程中刷新叮嘱详情界面
				Log.e("dingzhu_concernactivity", "");
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				} else {
					mAdapter = new ConcernDetailAdapter(ConcernActivity.this,
							concernlist);
					lv_concern.setAdapter(mAdapter);
				}

				updateConcern();
				/*
				 * Intent intent = new Intent(action); intent.putExtra("data",
				 * "");
				 * 
				 * ConcernActivity.this.sendBroadcast(intent);
				 */
				break;

			case REFRESH_IMAGE:
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				} else {
					mAdapter = new ConcernDetailAdapter(ConcernActivity.this,
							concernlist);
					lv_concern.setAdapter(mAdapter);
				}
				updateConcernImg(); // 上传图片
				break;

			// case REPLY_IMAGE:
			// replyImg(); //返回叮嘱图片路径到一条叮嘱中
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * Function:上传叮嘱回复信息
	 * 
	 */
	private void updateConcern() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.updateConcern(mContext,
				ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				concern.getExhort_id(), concern.getReply_images(),
				concern.getReply_content(), new IOperationResult() {
					@Override
					public void operationResult(boolean isSuccess, String json,
							String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json)
									|| !json.startsWith("{")) {
								PromptManager.showToast(
										getApplicationContext(), false,
										"数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json,
										BaseBean.class);
								if (baseBean.getStatus() == 0) {
									et_concern.setText("");
									PromptManager.showToast(mContext, "上传文字成功");
								} else {
									PromptManager.showToast(mContext, false,
											errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});

	}

	/**
	 * 
	 * Function:上传叮嘱回复图片
	 * 
	 */
	private void updateConcernImg() {
		String img = FileUtils.SDPATH + userBean.getOld_id() + "rice.JPEG";
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.uploadImg(this, ConfigManager.getStringValue(
				getApplicationContext(), Constants.ACCESS_TOKEN), img,
				new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json,
							String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json)
									|| !json.startsWith("{")) {
								PromptManager.showToast(
										getApplicationContext(), false,
										"上传图片失败，请检查网络!");
							} else {
								BaseBean baseBean = JSON.parseObject(json,
										BaseBean.class);
								if (baseBean.getStatus() == 0) {
									JSONObject object = JSONObject
											.parseObject(baseBean.getData());
									// String img_id =
									// object.getString("img_id");
									// String img_url =
									// object.getString("img_url");
									LogUtil.info("lhw",
											"object=" + object.toJSONString());
									concern.setReply_images("["
											+ object.toJSONString() + "]");
									replyImg();// 上传图片链接
								} else {
									PromptManager.showToast(
											getApplicationContext(), false,
											baseBean.getInfo());
								}
							}
						} else {
							PromptManager.showToast(getApplicationContext(),
									false, errors);
						}
					}
				});
	}

	/**
	 * 
	 * Function:上传叮嘱图片链接
	 * 
	 */
	private void replyImg() {
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.updateConcern(mContext,
				ConfigManager.getStringValue(mContext, Constants.ACCESS_TOKEN),
				concern.getExhort_id(), concern.getReply_images(),
				concern.getReply_content(), new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json,
							String errors) {
						if (isSuccess) {
							if (TextUtils.isEmpty(json)
									|| !json.startsWith("{")) {
								PromptManager.showToast(mContext, false,
										"数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json,
										BaseBean.class);
								if (baseBean.getStatus() == 0) {
									PromptManager.showToast(mContext, "上传图片成功");
								} else {
									PromptManager.showToast(mContext, false,
											errors);
								}
							}
						} else {
							PromptManager.showToast(mContext, false, errors);
						}
					}
				});

	}

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private String path = "";
	private Uri photoUri;

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});
		}

	}

	public void photo() {
		try {
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			String sdcardState = Environment.getExternalStorageState();
			String sdcardPathDir = android.os.Environment
					.getExternalStorageDirectory().getPath() + "/tempImage/";
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				// 有sd卡，是否有myImage文件夹
				File fileDir = new File(sdcardPathDir);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				// 是否有headImg文件
				file = new File(sdcardPathDir + userBean.getOld_id()
						+ "rice.JPEG");
			}
			if (file != null) {
				path = file.getPath();
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == -1) {// 拍照
				savePhoto(null);
			}
			break;
		case RESULT_LOAD_IMAGE:
			if (resultCode == RESULT_OK && null != data) {// 相册返回
				Uri uri = data.getData();
				if (uri != null) {
					savePhoto(uri);
				}
			}
			break;

		}
	}

	private void savePhoto(Uri uri) {
		try {
			if (!FileUtils.isFileExist("")) {
				FileUtils.createSDDir("");
			}
			if (uri != null) {
				// reply_img =
				// BitmpUtils.getLoacalBitmap(getRealPathFromURI(uri));
				reply_img = BitmpUtils
						.revitionImageSize(getRealPathFromURI(uri));
			} else {
				// reply_img = BitmpUtils.getLoacalBitmap(path);
				reply_img = BitmpUtils.revitionImageSize(path);
			}
			FileUtils.saveBitmap(reply_img, userBean.getOld_id() + "rice");

			concernlist.clear();
			concern.setStatus(1);
			concernlist.add(concern);
			mHandler.sendEmptyMessage(REFRESH_IMAGE);

			String sdcardPathDir = android.os.Environment
					.getExternalStorageDirectory().getPath() + "/tempImage/";
			FileUtils.deleteDir(sdcardPathDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

}
