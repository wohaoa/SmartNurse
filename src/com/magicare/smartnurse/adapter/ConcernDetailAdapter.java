package com.magicare.smartnurse.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.utils.FileUtils;

public class ConcernDetailAdapter extends MagicareBaseAdapter<ConcernBean> {

	private Bitmap cacheBitmap;

	public ConcernDetailAdapter(Context mContext, List<ConcernBean> mList) {
		super(mContext, mList);
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.concern_detail_item, null);
			holder = new ViewHolder();
			holder.tv_child_concern = (TextView) convertView.findViewById(R.id.tv_child_concern);
			holder.tv_reply = (TextView) convertView.findViewById(R.id.tv_reply);
			holder.iv_reply = (ImageView) convertView.findViewById(R.id.iv_reply);
			holder.lv1_reply = (LinearLayout) convertView.findViewById(R.id.lv1_reply);
			holder.lv2_reply = (LinearLayout) convertView.findViewById(R.id.lv2_reply);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ConcernBean bean = mList.get(position);
		holder.tv_child_concern.setText(bean.getContent());
		cacheBitmap = FileUtils.getCacheBitmap(bean.getOld_id() + "rice");

		if (bean.getStatus() == 0) { // 未回复
			holder.lv1_reply.setVisibility(View.GONE);
			holder.lv2_reply.setVisibility(View.GONE);
		} else { // 已回复
			if (bean.getReply_content() != null && !bean.getReply_content().equals("")) { // 回复文字
				holder.tv_reply.setText(bean.getReply_content());
				holder.lv1_reply.setVisibility(View.VISIBLE);
				holder.lv2_reply.setVisibility(View.GONE);
			} else { // 回复图片
				if (cacheBitmap != null) {
					holder.iv_reply.setImageBitmap(cacheBitmap);
				} else {
					holder.iv_reply.setImageResource(R.drawable.icon_zhyl);
				}
				holder.lv2_reply.setVisibility(View.VISIBLE);
				holder.lv1_reply.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView tv_child_concern;
		public TextView tv_reply;
		public ImageView iv_reply;
		public LinearLayout lv1_reply, lv2_reply;
	}

}
