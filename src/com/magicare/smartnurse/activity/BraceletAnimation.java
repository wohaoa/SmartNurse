package com.magicare.smartnurse.activity;

import org.roisoleil.gifview.GifView;


import com.magicare.smartnurse.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BraceletAnimation extends BaseActivity{
	
	private Button mBtBack;
	private GifView mGifView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bracelet_animation);
		
		initView();
	}
	
	private void initView(){
		mBtBack = (Button) findViewById(R.id.bt_back);
		mGifView = (GifView) findViewById(R.id.gifview);
		
		mGifView.setMovieResource(R.drawable.animation_bracelet);
		
		mBtBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}
