package com.magicare.smartnurse.activity;

import com.magicare.smartnurse.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 投影TV
 * 
 * @author 波
 * 
 */
public class TvFragment extends Fragment {
	View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = View.inflate(getActivity(), R.layout.fragment_tv, null);
		return mView;
	}
}
