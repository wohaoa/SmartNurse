package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreAdapter extends MagicareBaseAdapter<String> {

	private int position;

	public void setPosition(int position) {
		this.position = position;
	}

	public MoreAdapter(Context mContext, List<String> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.text_item, null);
			holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			holder.view = convertView.findViewById(R.id.view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == this.position) {
			holder.tv_text.setTextColor(Color.rgb(109, 179, 29));
			convertView.setBackgroundColor(Color.rgb(245, 249, 237));
			holder.view.setVisibility(View.VISIBLE);
			holder.tv_text.setTextSize(23);
		} else {
			convertView.setBackgroundColor(Color.rgb(239, 245, 234));
			holder.tv_text.setTextColor(Color.rgb(95, 109, 84));
			holder.view.setVisibility(View.INVISIBLE);
			holder.tv_text.setTextSize(20);
		}
		holder.tv_text.setText(mList.get(position));
			
		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_text;
		public View view;
	}

}
