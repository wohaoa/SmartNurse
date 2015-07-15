package com.magicare.smartnurse.activity;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.BaseBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.net.HttpClientUtil;
import com.magicare.smartnurse.net.IOperationResult;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.PromptManager;
import com.magicare.smartnurse.view.ClearEditText;

import android.content.Intent;
import android.graphics.Paint.Join;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class PasswordFragment extends Fragment implements OnClickListener {

	View mView;
	ClearEditText et_oldpassword;
	ClearEditText et_newpassword;
	ClearEditText et_confirmpassword;
	Button btn_pwd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = View.inflate(getActivity(), R.layout.fragement_password, null);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		btn_pwd = (Button) mView.findViewById(R.id.btn_pwd);
		et_oldpassword = (ClearEditText) mView.findViewById(R.id.et_oldpassword);
		et_newpassword = (ClearEditText) mView.findViewById(R.id.et_newpassword);
		et_confirmpassword = (ClearEditText) mView.findViewById(R.id.et_confirmpassword);
		btn_pwd.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		alertPwd();
	}

	public void alertPwd() {

		String old_pwd = et_oldpassword.getText().toString();
		String new_pwd = et_newpassword.getText().toString();
		String confirm_pwd = et_confirmpassword.getText().toString();

		if (TextUtils.isEmpty(old_pwd)) {
			PromptManager.showToast(getActivity(), false, "亲，原密码不能为空");
			return;
		}

		if (TextUtils.isEmpty(new_pwd)) {
			PromptManager.showToast(getActivity(), false, "亲，新密码不能为空");
			return;
		}

		if (TextUtils.isEmpty(confirm_pwd)) {
			PromptManager.showToast(getActivity(), false, "亲，确认密码不能为空");
			return;
		}

		if (!(new_pwd.equals(confirm_pwd))) {
			PromptManager.showToast(getActivity(), false, "亲，两次密码不一致");
			return;
		}

		PromptManager.showProgressDialog(getActivity(), "请稍后....");

		HttpClientUtil client = HttpClientUtil.getInstance();
		client.alterPassword(getActivity(), ConfigManager.getStringValue(getActivity(), Constants.ACCESS_TOKEN),
				old_pwd, new_pwd, new IOperationResult() {

					@Override
					public void operationResult(boolean isSuccess, String json, String errors) {

						if (isSuccess) {
							PromptManager.closeProgressDialog();
							if (TextUtils.isEmpty(json) || !json.startsWith("{")) {
								PromptManager.showToast(getActivity(), false, "数据为空，请检查您的网络，重新操作一次！");
							} else {
								BaseBean baseBean = JSON.parseObject(json, BaseBean.class);
								if (baseBean.getStatus() == 0) {
									Intent intent = new Intent(getActivity(), LoginActivity.class);
									startActivity(intent);
									getActivity().finish();
									ConfigManager.setBooleanValue(getActivity(), ConfigManager.LOGIN_SUCCESS, false);
								}
								PromptManager.showToast(getActivity(), false, baseBean.getInfo());
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getActivity(), false, errors);
						}
					}
				});
	}
}
