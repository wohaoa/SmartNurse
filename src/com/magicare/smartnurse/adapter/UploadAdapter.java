package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.HealthBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UploadAdapter extends MagicareBaseAdapter<HealthBean> {

	public UploadAdapter(Context mContext, List<HealthBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.upload_item, null);
			holder = new ViewHolder();
			holder.tv_collecttime = (TextView) convertView.findViewById(R.id.tv_collecttime);
			holder.tv_data = (TextView) convertView.findViewById(R.id.tv_data);
			holder.tv_oldid = (TextView) convertView.findViewById(R.id.tv_oldid);
			holder.tv_stauts = (TextView) convertView.findViewById(R.id.tv_stauts);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HealthBean bean = (HealthBean) mList.get(position);
		if (bean != null) {
			holder.tv_data.setText(bean.getData_id());
			holder.tv_collecttime.setText(bean.getCollect_time());
			holder.tv_oldid.setText(bean.getOld_sn());
			holder.tv_stauts.setText(bean.getIsUpdate() == 1 ? "已上传" : "未上传");
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_data;
		public TextView tv_collecttime;
		public TextView tv_oldid;
		public TextView tv_stauts;
	}

}
