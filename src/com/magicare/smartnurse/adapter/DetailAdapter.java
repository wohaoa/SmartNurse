package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.HealthBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailAdapter extends MagicareBaseAdapter<HealthBean> {

	public DetailAdapter(Context mContext, List<HealthBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.detail_item, null);
			holder = new ViewHolder();
			holder.tv_collecttime = (TextView) convertView
					.findViewById(R.id.tv_collecttime);
			holder.tv_collecter = (TextView) convertView
					.findViewById(R.id.tv_collecter);
			holder.tv_blood = (TextView) convertView
					.findViewById(R.id.tv_blood);
			holder.tv_weight = (TextView) convertView
					.findViewById(R.id.tv_weight);
			holder.tv_bloodsugar = (TextView) convertView
					.findViewById(R.id.tv_bloodsugar);
			holder.tv_heartrate = (TextView) convertView
					.findViewById(R.id.tv_heartrate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HealthBean bean = (HealthBean) mList.get(position);
		if (bean != null) {
			holder.tv_collecttime.setText(bean.getCollect_time());
			holder.tv_collecter.setText(bean.getNurse_name());
			holder.tv_weight.setText(bean.getWeight() + "");
			holder.tv_blood.setText(bean.getSystolic_pressure() + "/"
					+ bean.getDiastolic_pressure());
			holder.tv_heartrate.setText(bean.getHeart_rate()+"");
			holder.tv_bloodsugar.setText(bean.getBlood_sugar() + "");
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_collecttime;
		public TextView tv_collecter;
		public TextView tv_weight;
		public TextView tv_blood;
		public TextView tv_heartrate;
		public TextView tv_bloodsugar;

	}

}
