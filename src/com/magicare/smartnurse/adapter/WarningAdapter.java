package com.magicare.smartnurse.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.utils.ConfigManager;

public class WarningAdapter extends MagicareBaseAdapter<WarningBean> {

	public WarningAdapter(Context mContext, List<WarningBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.warning_item, null);
			holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
			holder.tv_adress = (TextView) convertView
					.findViewById(R.id.tv_adress);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_handle = (TextView) convertView
					.findViewById(R.id.tv_handle);
			holder.tv_feedtime = (TextView) convertView
					.findViewById(R.id.tv_feedtime);
			holder.tv_handler = (TextView) convertView
					.findViewById(R.id.tv_handler);
			holder.tv_detail = (TextView) convertView
					.findViewById(R.id.tv_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		WarningBean bean = mList.get(position);
		if (bean != null) {
			holder.tv_type.setText(getTypeStr(holder.tv_type,
					bean.getAlarm_type()));
			holder.tv_adress.setText(bean.getStation_detail() + "");
			holder.tv_time.setText(bean.getAlarm_time() + "");
			holder.tv_feedtime.setText(bean.getFeed_time() + "");
			holder.tv_handler.setText(bean.getFeed_nurse() + "");
			holder.tv_detail.setText(bean.getFeedback() + "");
			holder.tv_handle.setText(bean.getResolve_time() + "");
		}

		return convertView;
	}

	public String getTypeStr(TextView textView, int type) {
		String str = null;
		Drawable mClearDrawable = null;

		if (type == 1) {
			str = ConfigManager.getStringValue(mContext, ConfigManager.WARNING_NAME);
			mClearDrawable = mContext.getResources().getDrawable(
					R.drawable.ic_alarm);
			mClearDrawable.setBounds(0, 0, 36, 36);
//			textView.setTextColor(Color.parseColor("#ff6838"));
			textView.setTextColor(mContext.getResources().getColor(R.color.active_warn_color1));
		} else if (type == 2) {
			str = "摔倒报警";
			mClearDrawable = mContext.getResources().getDrawable(
					R.drawable.ic_fall);
			mClearDrawable.setBounds(0, 0, 36, 36);
//			textView.setTextColor(Color.parseColor("#f5a623"));
			textView.setTextColor(mContext.getResources().getColor(R.color.fall_warn));
		} else if (type == 3) {
			str = "走失报警";
			mClearDrawable = mContext.getResources().getDrawable(
					R.drawable.ic_out);
			mClearDrawable.setBounds(0, 0, 36, 36);
//			textView.setTextColor(Color.parseColor("#4f99da"));
			textView.setTextColor(mContext.getResources().getColor(R.color.out_area_warn));
		}
		textView.setPadding(30, 0, 0, 0);
		textView.setCompoundDrawablePadding(-40);

		textView.setCompoundDrawables(mClearDrawable,
				textView.getCompoundDrawables()[1],
				textView.getCompoundDrawables()[1],
				textView.getCompoundDrawables()[3]);
		return str;
	}

	private static class ViewHolder {
		public TextView tv_type;
		public TextView tv_adress;
		public TextView tv_time;
		public TextView tv_handle;
		public TextView tv_feedtime;
		public TextView tv_handler;
		public TextView tv_detail;
	}

}
