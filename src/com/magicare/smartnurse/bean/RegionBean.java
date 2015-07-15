package com.magicare.smartnurse.bean;

import java.io.Serializable;

/**
 * 区域信息
 * 
 * @author 波
 * 
 */

public class RegionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// 数据库表中的id
	private int pension_areaid; // 区域id
	private String pension_areasn;// 区域编号
	private String name; // 区域名字
	private String contact; // 区域联系方式
	private String telephone; // 区域电话
	private int area_oldnum; // 区域人数
	private String range; // 区域范围
	private String note; // 区域的具体地点
	private String nurse_id; // 护士Id

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPension_areaid() {
		return pension_areaid;
	}

	public void setPension_areaid(int pension_areaid) {
		this.pension_areaid = pension_areaid;
	}

	public String getPension_areasn() {
		return pension_areasn;
	}

	public void setPension_areasn(String pension_areasn) {
		this.pension_areasn = pension_areasn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public int getArea_oldnum() {
		return area_oldnum;
	}

	public void setArea_oldnum(int area_oldnum) {
		this.area_oldnum = area_oldnum;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNurse_id() {
		return nurse_id;
	}

	public void setNurse_id(String nurse_id) {
		this.nurse_id = nurse_id;
	}

	@Override
	public String toString() {
		return "RegionBean [id=" + id + ", pension_areaid=" + pension_areaid + ", pension_areasn=" + pension_areasn
				+ ", name=" + name + ", contact=" + contact + ", telephone=" + telephone + ", area_oldnum="
				+ area_oldnum + ", range=" + range + ", note=" + note + ", nurse_id=" + nurse_id + "]";
	}

}
