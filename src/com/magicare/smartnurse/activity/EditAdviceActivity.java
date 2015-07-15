package com.magicare.smartnurse.activity;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PromptManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditAdviceActivity extends BaseActivity implements OnClickListener {
	Button btn_back;
	EditText et_advice;
	Button btn_commmit;
	int type;
	String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editadvice);
		type = getIntent().getBundleExtra("bundle").getInt("type");
		LogUtil.info("-----------" + type + "--------------");
		btn_back = (Button) findViewById(R.id.btn_back);
		et_advice = (EditText) findViewById(R.id.et_advice);
		btn_commmit = (Button) findViewById(R.id.btn_commmit);
		btn_back.setOnClickListener(this);
		btn_commmit.setOnClickListener(this);
		et_advice.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.toString().length() > 0) {
					btn_commmit.setEnabled(true);
					btn_commmit.setBackgroundResource(R.drawable.btn_green_rectangle_selector);
				} else {
					btn_commmit.setEnabled(false);
					btn_commmit.setBackgroundResource(R.drawable.btn_feedback_pressed);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);

		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_commmit:
			content = et_advice.getText().toString();
			commitAdvice();
			break;
		default:
			break;
		}
	}

	public void commitAdvice() {
		PromptManager.showProgressDialog(this, "正在提交反馈....");
		HttpClientUtil client = HttpClientUtil.getInstance();
		client.commitAdvice(this, ConfigManager.getStringValue(getApplicationContext(), Constants.ACCESS_TOKEN), type
				+ "", content, new IOperationResult() {
			@Override
			public void operationResult(boolean isSuccess, String json, String errors) {
				// TODO Auto-generated method stub

				if (isSuccess) {
					if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
						PromptManager.showToast(getApplicationContext(), false, "数据为空，请检查您的网络，重新操作一次！");
					} else {
						BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
						if (baseBean.getStatus() == 0) {
							PromptManager.showToast(getApplicationContext(), true, "反馈成功");
							finish();
						} else {
							PromptManager.showToast(getApplicationContext(), false, baseBean.getInfo());
						}
					}

				} else {

					PromptManager.showToast(getApplicationContext(), false, errors);
				}
				PromptManager.closeProgressDialog();

			}
		});
	}
}