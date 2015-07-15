package com.magicare.smartnurse.adapter;

import java.io.File;
import java.util.List;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.bean.ConcernBean;
import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.utils.BitmpUtils;
import com.magicare.smartnurse.utils.FileUtils;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.view.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConcernAdapter extends MagicareBaseAdapter<ConcernBean> {

	public ConcernAdapter(Context mContext, List<ConcernBean> mList) {
		super(mContext, mList);

	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.concern_item, null);
			holder = new ViewHolder();
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.concern_title = (TextView) convertView.findViewById(R.id.concern_title);
//			holder.iv_newphoto = (ImageView) convertView.findViewById(R.id.iv_newphoto);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ConcernBean bean = mList.get(position);

		String photopath = FileUtils.SDPATH + bean.getOld_id() + ".JPEG";
		File file = new File(photopath);
		if (file.exists()) {
			Bitmap bitmap = BitmpUtils.getLoacalBitmap(photopath);
			bitmap = BitmpUtils.createFramedPhoto(480, 480, bitmap, (int) (10 * 1.6f));
			holder.civ_photo.setImageBitmap(bitmap);
		}
		DBUser dbuser = DBUser.getInstance(mContext);
		dbuser.open();
		UserBean userBean = dbuser.getUserInfoById(bean.getOld_id() + "");
		dbuser.close();
		
		holder.concern_title.setText(userBean.getName()+"之"+userBean.getChild_relation() + bean.getChild_name()+"的叮嘱");
		holder.tv_content.setText(bean.getContent());
		holder.tv_time.setText(bean.getCreate_time());
//		if(bean.getStatus() == 0){
//			holder.iv_newphoto.setImageResource(R.drawable.icon_new);
//		}

		return convertView;
	}

	private static class ViewHolder {
		public CircleImageView civ_photo;
		public TextView tv_time;
		public TextView concern_title;
		public TextView tv_content;
		public ImageView iv_newphoto;
	}

}
