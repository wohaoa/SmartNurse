package com.magicare.smartnurse.bean;

import java.io.Serializable;
import java.util.List;

public class UpdateHealthEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String old_sn;
	private List<HealthBean> data;

	public String getOld_sn() {
		return old_sn;
	}

	public void setOld_sn(String old_sn) {
		this.old_sn = old_sn;
	}

	public List<HealthBean> getData() {
		return data;
	}

	public void setData(List<HealthBean> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "UpdateHealthEntity [old_sn=" + old_sn + ", data=" + data + "]";
	}

}
