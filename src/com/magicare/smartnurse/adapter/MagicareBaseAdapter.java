package com.magicare.smartnurse.adapter;

import java.util.List;

import com.magicare.smartnurse.utils.LogUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author scott
 * 
 *         Function:@param <T>适配器基类
 */
public abstract class MagicareBaseAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mList;
	protected LayoutInflater mInflater;

	public MagicareBaseAdapter(Context mContext, List<T> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList != null){
			return mList.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return makeView(position, convertView, parent);
	}

	protected abstract View makeView(int position, View convertView, ViewGroup parent);
}