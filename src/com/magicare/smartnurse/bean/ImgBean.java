package com.magicare.smartnurse.bean;

import java.io.Serializable;
import java.util.List;

public class ImgBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String img_id;
	private String img_url;
	
	public String getImg_id() {
		return img_id;
	}

	public void setImg_id(String img_id) {
		this.img_id = img_id;
	}

	public String getImg_url() {
		return img_url;
	}


	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
