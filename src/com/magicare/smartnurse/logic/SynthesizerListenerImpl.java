package com.magicare.smartnurse.logic;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 
 * Function:科大讯飞朗读的回调接口空实现
 * 
 * @author scott TIME:2015年3月20日
 */
public class SynthesizerListenerImpl implements SynthesizerListener {

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompleted(SpeechError arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub

	}

}
