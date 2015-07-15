package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PopAdapter extends MagicareBaseAdapter<String> {

	public PopAdapter(Context mContext, List<String> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.pop_item, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.tv_lable);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(mList.get(position));
		return convertView;
	}

	private static class ViewHolder {

		TextView textView;
	}

}
