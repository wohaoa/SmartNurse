package com.magicare.smartnurse.adapter;

import java.io.File;
import java.util.List;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.ConfigManager;
import com.magicare.smartnurse.utils.Constants;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.view.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WarningDealWithAdapter extends MagicareBaseAdapter<WarningBean> {

	private FeedListener mFeedListener;
	private boolean mShowButton = true; // 如果从TV监控大屏进入的话，则不显示button

	public interface FeedListener {
		public void onWarningFeed(WarningBean bean);
	}

	public void setOnFeedListener(FeedListener listener) {
		this.mFeedListener = listener;
	}

	private UserDetailListener mUserDetailListener;

	public interface UserDetailListener {
		public void userDetail(WarningBean bean);
	}

	public void setOnUserDetailListener(UserDetailListener listener) {
		this.mUserDetailListener = listener;
	}

	private ResolveListener mResolveListener;

	public interface ResolveListener {
		public void onResolve(WarningBean bean);
	}

	public void setOnResolveListener(ResolveListener listener) {
		this.mResolveListener = listener;
	}

	public WarningDealWithAdapter(Context mContext, List<WarningBean> mList, boolean showButton) {
		super(mContext, mList);
		this.mShowButton = showButton;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.already_dealwith_item, null);
			holder = new ViewHolder();
			holder.layout_warn_type = (LinearLayout) convertView.findViewById(R.id.layout_warn_type);
			holder.iv_warning_type = (ImageView) convertView.findViewById(R.id.iv_warning_type);
			holder.tv_warning_type = (TextView) convertView.findViewById(R.id.tv_warning_type);
			holder.layout_user = (LinearLayout) convertView.findViewById(R.id.layout_user);
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_warn_time = (TextView) convertView.findViewById(R.id.tv_warn_time);
			holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
			holder.btn_feedback = (Button) convertView.findViewById(R.id.btn_feedback);
			if(mShowButton){
				holder.btn_feedback.setVisibility(View.VISIBLE);
			}else{
				holder.btn_feedback.setVisibility(View.GONE); // 如果从TV监控大屏进入的话，则不显示button
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final WarningBean bean = mList.get(position);
		holder.tv_warn_time.setText(bean.getAlarm_time());
		holder.tv_name.setText(bean.getOld_name());
		holder.tv_location.setText(bean.getStation_detail());

		String photopath = FileUtils.SDPATH + bean.getOld_id() + ".JPEG";
		File file = new File(photopath);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
			holder.civ_photo.setImageBitmap(bitmap);
		}

		if (bean.getAlarm_status() == 0) {
			holder.btn_feedback.setBackgroundResource(R.drawable.monitor_btn_dealwith_selector);
			holder.btn_feedback.setTextColor(Color.parseColor("#ffffff"));
			holder.btn_feedback.setText("处理");
			if (bean.getAlarm_type() == 1) {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#ff6838"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.active_warn_color1));
				holder.iv_warning_type.setImageResource(R.drawable.ic_alarm_dark);
				holder.tv_warning_type.setText(ConfigManager.getStringValue(mContext, ConfigManager.WARNING_NAME));
			} else if (bean.getAlarm_type() == 2) {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#f5a623"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.fall_warn));
				holder.iv_warning_type.setImageResource(R.drawable.ic_fall_dark);
				holder.tv_warning_type.setText("摔倒报警");
			} else {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#4f99da"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.out_area_warn));
				holder.iv_warning_type.setImageResource(R.drawable.ic_out_dark);
				holder.tv_warning_type.setText("不在服务区");
			}
			holder.btn_feedback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mResolveListener != null) {
						mResolveListener.onResolve(bean);
					}
				}
			});
		} else {
			holder.btn_feedback.setBackgroundResource(R.drawable.monitor_btn_feed_selector);
			holder.btn_feedback.setTextColor(Color.parseColor("#333333"));
			holder.btn_feedback.setText("反馈");
			if (bean.getAlarm_type() == 1) {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#a1a1a1"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.deal_warn_color));
				holder.iv_warning_type.setImageResource(R.drawable.ic_alarm_white);
				holder.tv_warning_type.setText(ConfigManager.getStringValue(mContext, ConfigManager.WARNING_NAME));
			} else if (bean.getAlarm_type() == 2) {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#a1a1a1"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.deal_warn_color));
				holder.iv_warning_type.setImageResource(R.drawable.ic_fall_white);
				holder.tv_warning_type.setText("摔倒报警");
			} else {
//				holder.layout_warn_type.setBackgroundColor(Color.parseColor("#a1a1a1"));
				holder.layout_warn_type.setBackgroundColor(mContext.getResources().getColor(R.color.deal_warn_color));
				holder.iv_warning_type.setImageResource(R.drawable.ic_out_white);
				holder.tv_warning_type.setText("不在服务区");
			}

			holder.btn_feedback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mFeedListener != null) {
						mFeedListener.onWarningFeed(bean);
					}
				}
			});

//			holder.layout_user.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (mUserDetailListener != null) {
//						mUserDetailListener.userDetail(bean);
//					}
//				}
//			});

		}

		return convertView;
	}

	private static class ViewHolder {
		public LinearLayout layout_warn_type;
		public ImageView iv_warning_type;
		public TextView tv_warning_type;
		public LinearLayout layout_user;
		public CircleImageView civ_photo;
		public TextView tv_name;
		public TextView tv_warn_time;
		public TextView tv_location;
		public Button btn_feedback;
	}

}