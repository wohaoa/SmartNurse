package com.magicare.smartnurse.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.view.CircleImageView;

public class SerachAdapter extends MagicareBaseAdapter<UserBean> {

	public SerachAdapter(Context mContext, List<UserBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	public void updateListView(List<UserBean> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.gridview_item, null);
			holder.iv_poto = (CircleImageView) convertView.findViewById(R.id.iv_photo);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < mList.size()) {
			UserBean userBean = mList.get(position);

			String photopath = FileUtils.SDPATH + userBean.getOld_id() + ".JPEG";
			File file = new File(photopath);
			if (file.exists()) {
				Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
				bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
				holder.iv_poto.setImageBitmap(bitmap);
			}
			holder.tv_name.setText(userBean.getName());
		}

		return convertView;
	}

	private static class ViewHolder {
		private TextView tv_name;
		private CircleImageView iv_poto;

	}

}
