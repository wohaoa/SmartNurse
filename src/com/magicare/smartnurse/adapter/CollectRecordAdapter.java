package com.magicare.smartnurse.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.utils.DateUtil;

public class CollectRecordAdapter extends MagicareBaseAdapter<HealthBean> {

	private CollectionListener mListener;
	private int type; // 表示0 表示采集页面 1表示查询页面进来的

	public interface CollectionListener {
		public void goCollection(HealthBean bean);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setOnCollectionListener(CollectionListener collectionListener) {
		this.mListener = collectionListener;
	}

	public CollectRecordAdapter(Context mContext, List<HealthBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.collect_record_item, null);
			holder.tv_collecttime = (TextView) convertView.findViewById(R.id.tv_collect_time);
			holder.tv_nursename = (TextView) convertView.findViewById(R.id.tv_nursename);
			holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
			holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
			holder.tv_weight = (TextView) convertView.findViewById(R.id.tv_weight);
			holder.tv_bloodpressure = (TextView) convertView.findViewById(R.id.tv_bloodpressure);
			holder.tv_heartrate = (TextView) convertView.findViewById(R.id.tv_heartrate);
			holder.tv_bloodsugar = (TextView) convertView.findViewById(R.id.tv_bloodsugar);
			holder.btn_retest = (Button) convertView.findViewById(R.id.btn_retest);
			holder.lin = (LinearLayout) convertView.findViewById(R.id.line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final HealthBean bean = (HealthBean) mList.get(position);

		holder.tv_collecttime.setText(bean.getCollect_time());
		holder.tv_nursename.setText(bean.getNurse_name());
		if (bean.getUser() != null) {
			holder.tv_username.setText(bean.getUser().getName());
			holder.tv_userId.setText(bean.getUser().getOld_sn());
		}
		holder.tv_weight.setText(bean.getWeight() + "");
		holder.tv_bloodpressure.setText(bean.getSystolic_pressure() + "/" + bean.getDiastolic_pressure());
		holder.tv_heartrate.setText(bean.getHeart_rate() + "");
		holder.tv_bloodsugar.setText(bean.getBlood_sugar() + "");
		if (type == 0) {
			holder.lin.setVisibility(View.VISIBLE);
		} else {
			holder.lin.setVisibility(View.GONE);
		}

		//仅24小时内的采集记录可以重新采集，按钮名称改为“重新采集”
		if (DateUtil.DateToString(new Date(), "yyyy-MM-dd").equals(DateUtil.DateToString(DateUtil.StringTolong(bean.getCollect_time()), "yyyy-MM-dd"))) {
			holder.btn_retest.setVisibility(View.VISIBLE);
		} else {
			holder.btn_retest.setVisibility(View.INVISIBLE);
		}

		holder.btn_retest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null) {
					mListener.goCollection(bean);
				}
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_collecttime;
		public TextView tv_nursename;
		public TextView tv_userId;
		public TextView tv_username;
		public TextView tv_weight;
		public TextView tv_bloodpressure;
		public TextView tv_heartrate;
		public TextView tv_bloodsugar;
		public Button btn_retest;
		public LinearLayout lin;

	}

}
