package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.bean.UserBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class UserAdapter extends MagicareBaseAdapter<UserBean> implements SectionIndexer {

	public UserAdapter(Context mContext, List<UserBean> mList) {
		super(mContext, mList);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ?ListView???????,????????ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<UserBean> list) {
		mList = list;
		notifyDataSetChanged();
	}

	@Override
	protected View makeView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final UserBean mContent = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
//			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
//			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// ??position?????????Char ascii?
		int section = getSectionForPosition(position);

		// ???????????????Char??? ??????????
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		viewHolder.tvTitle.setText(mList.get(position).getName());

		return convertView;
	}

	private final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * ??ListView??????????????Char ascii?
	 */
	public int getSectionForPosition(int position) {
		return mList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * ?????????Char ascii????????????????
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * ???????????????#???
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// ??????????????????
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}