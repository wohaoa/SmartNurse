package com.magicare.smartnurse.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.magicare.smartnurse.R;

/**
 * 连接说明页面
 * @author Melon
 *
 */
public class ConnectActivity extends BaseActivity implements OnClickListener{
	Button btn_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}
}
