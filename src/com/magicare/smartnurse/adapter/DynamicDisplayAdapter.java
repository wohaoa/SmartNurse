package com.magicare.smartnurse.adapter;

import java.io.File;
import java.util.List;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.view.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DynamicDisplayAdapter extends MagicareBaseAdapter<UserBean> {

	private boolean existsWarning;

	public DynamicDisplayAdapter(Context mContext, List<UserBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.dynamic_display_item, null);
			holder = new ViewHolder();
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_battery = (ImageView) convertView.findViewById(R.id.tv_battery);

			convertView.setTag(holder);
		} else {     
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < mList.size()) {
			UserBean bean = mList.get(position);

			String photopath = FileUtils.SDPATH + bean.getOld_id() + ".JPEG";
			File file = new File(photopath);
			if (file.exists()) {
				Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
				bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
				holder.civ_photo.setImageBitmap(bitmap);
			}

			if (existsWarning) {
				holder.tv_name.setTextColor(Color.parseColor("#f1f1f1"));
				holder.tv_battery.setImageResource(R.drawable.icon_battery_low_dark);
			} else {
				holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.tv_name_color));
				holder.tv_battery.setImageResource(R.drawable.icon_battery_low);
			}
			if (bean.getBattery() > 0 && bean.getBattery() < 30) {
				holder.tv_battery.setVisibility(View.VISIBLE);
			}else{
				holder.tv_battery.setVisibility(View.GONE);
			}
			holder.tv_name.setText(bean.getName());

			List<WarningBean> list_warning = bean.getList_warning();

			if (list_warning != null && list_warning.size() > 0) {
//				LogUtil.info("smarhit", "name=" + bean.getName() + "  list_warning.size()=" + list_warning.size());
				WarningBean warningBean = list_warning.get(0);
//				holder.civ_photo.setBorderColor(Color.parseColor("#FF5722"));
//				holder.civ_photo.setInnerColor(Color.parseColor("#FF5722"));
//				holder.civ_photo.setOutColor(Color.parseColor("#FF5722"));
				holder.civ_photo.setOutColor(mContext.getResources().getColor(R.color.warn_out_color));
				holder.civ_photo.setInnerColor(mContext.getResources().getColor(R.color.warn_out_color));
				holder.civ_photo.setBorderColor(mContext.getResources().getColor(R.color.warn_out_color));
				if (warningBean.getAlarm_type() == 1) {
					holder.iv_status.setImageResource(R.drawable.ic_alarm_circle);
				} else if (warningBean.getAlarm_type() == 2) {
					holder.iv_status.setImageResource(R.drawable.ic_fall_circle);
				} else if (warningBean.getAlarm_type() == 3) {
					holder.iv_status.setImageResource(R.drawable.ic_out_circle);
				}else{
					holder.iv_status.setImageResource(R.drawable.ic_alarm_circle);
				}

			} else {
//				LogUtil.info("smarhit", "name=" + bean.getName());
				holder.civ_photo.setBorderColor(Color.parseColor("#ffffff"));
				holder.civ_photo.setInnerColor(Color.parseColor("#ffffff"));
//				holder.civ_photo.setOutColor(Color.parseColor("#82D136"));
				holder.civ_photo.setOutColor(mContext.getResources().getColor(R.color.photo_out_color));
				if (bean.getCurrentStatus().equals("活动")) {
					holder.iv_status.setImageResource(R.drawable.ic_sport_circle);
				} else if (bean.getCurrentStatus().equals("睡眠")) {
					holder.iv_status.setImageResource(R.drawable.ic_sleep_circle);
				} else if (bean.getCurrentStatus().equals("静止")) {
					holder.iv_status.setImageResource(R.drawable.ic_static_circle);
				}else{
					holder.iv_status.setImageResource(R.drawable.ic_sport_circle);
				}
			}

		}
		return convertView;
	}

	private static class ViewHolder {
		public CircleImageView civ_photo;
		public ImageView iv_status;
		public ImageView tv_battery;
		public TextView tv_name;
	}

	public void exisitWarning(boolean isExist) {
		existsWarning = isExist;
		notifyDataSetChanged();
	}
}
