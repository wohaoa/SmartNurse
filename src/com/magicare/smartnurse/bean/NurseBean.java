package com.magicare.smartnurse.bean;

import java.io.Serializable;

public class NurseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int nurse_id;// 护士的id(int)
	private String nurse_name; // 护士的名字(string)
	private String mobile;// 护士手机号(string)
	private int pension_id;// 养老院id(int)
	private int pension_areaid; // 养老院区域id(int)
	private String avatar_url;// 头像url(string)
	private String note;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNurse_id() {
		return nurse_id;
	}

	public void setNurse_id(int nurse_id) {
		this.nurse_id = nurse_id;
	}

	public String getNurse_name() {
		return nurse_name;
	}

	public void setNurse_name(String nurse_name) {
		this.nurse_name = nurse_name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getPension_id() {
		return pension_id;
	}

	public void setPension_id(int pension_id) {
		this.pension_id = pension_id;
	}

	public int getPension_areaid() {
		return pension_areaid;
	}

	public void setPension_areaid(int pension_areaid) {
		this.pension_areaid = pension_areaid;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "NurseBean [id=" + id + ", nurse_id=" + nurse_id + ", nurse_name=" + nurse_name + ", mobile=" + mobile
				+ ", pension_id=" + pension_id + ", pension_areaid=" + pension_areaid + ", avatar_url=" + avatar_url
				+ ", note=" + note + "]";
	}

}
