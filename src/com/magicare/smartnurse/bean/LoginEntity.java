package com.magicare.smartnurse.bean;

import java.io.Serializable;

public class LoginEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String access_token;
	private NurseBean nurse;
	private RegionBean pension_area;
	private PensionBean pension;

	public PensionBean getPension() {
		return pension;
	}

	public void setPension(PensionBean pension) {
		this.pension = pension;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public NurseBean getNurse() {
		return nurse;
	}

	public void setNurse(NurseBean nurse) {
		this.nurse = nurse;
	}

	public RegionBean getPension_area() {
		return pension_area;
	}

	public void setPension_area(RegionBean pension_area) {
		this.pension_area = pension_area;
	}

	@Override
	public String toString() {
		return "LoginEntity [nurse=" + nurse + ", pension_area=" + pension_area + "]";
	}

}
