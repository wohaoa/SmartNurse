package com.magicare.smartnurse.bean;

import java.io.Serializable;
import java.util.List;

public class ExhortBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String list;
	private int count;
	
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
