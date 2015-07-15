package com.magicare.smartnurse.activity;

import com.magicare.smartnurse.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 意见反馈界面
 * 
 * @author 波
 * 
 */
public class AdviceActivity extends BaseActivity implements OnClickListener {
	Button btn_back;
	TextView tv_title;
	Button btn_advice; // 建议
	Button btn_complain;// 投诉
	Button btn_other;// 其他

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice);
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_advice = (Button) findViewById(R.id.btn_advice);
		btn_complain = (Button) findViewById(R.id.btn_complain);
		btn_other = (Button) findViewById(R.id.btn_other);
		btn_back.setOnClickListener(this);
		btn_advice.setOnClickListener(this);
		btn_complain.setOnClickListener(this);
		btn_other.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_advice:
			Bundle bundle = new Bundle();
			bundle.putInt("type", 1);
			changeView(EditAdviceActivity.class, bundle);
			finish();
			break;
		case R.id.btn_complain:
			Bundle bundle1 = new Bundle();
			bundle1.putInt("type", 2);
			changeView(EditAdviceActivity.class, bundle1);
			finish();
			break;
		case R.id.btn_other:
			Bundle bundle2 = new Bundle();
			bundle2.putInt("type", 3);
			changeView(EditAdviceActivity.class, bundle2);
			finish();
			break;

		default:
			break;
		}
	}
}
