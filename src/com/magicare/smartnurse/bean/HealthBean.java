package com.magicare.smartnurse.bean;

import java.util.List;

/**
 * 老人健康信息
 * 
 * @author 波
 * 
 */
public class HealthBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String data_id;// 健康数据的唯一标识
	private int old_id;// 老人真是编号
	private String old_sn; // 老人的编号
	private String collect_time; // 采集时间
	private float weight; // 体重
	private float systolic_pressure; // 收缩压
	private float diastolic_pressure;// 舒张压
	private int heart_rate; // 心率
	private float blood_sugar;// 血糖
	private String nurse_name; // 采集人
	private int nurse_id;// 护士的ID
	private int isUpdate = 0;// 是否上传至服务器 isUpdate=1：表示已上传，isUpdate=0表示未上传

	private UserBean user;// 老人信息

	public HealthBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getOld_id() {
		return old_id;
	}

	public void setOld_id(int old_id) {
		this.old_id = old_id;
	}

	public String getData_id() {
		return data_id;
	}

	public void setData_id(String data_id) {
		this.data_id = data_id;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getOld_sn() {
		return old_sn;
	}

	public void setOld_sn(String old_sn) {
		this.old_sn = old_sn;
	}

	public String getCollect_time() {
		return collect_time;
	}

	public void setCollect_time(String collect_time) {
		this.collect_time = collect_time;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getSystolic_pressure() {
		return systolic_pressure;
	}

	public void setSystolic_pressure(float systolic_pressure) {
		this.systolic_pressure = systolic_pressure;
	}

	public float getDiastolic_pressure() {
		return diastolic_pressure;
	}

	public void setDiastolic_pressure(float diastolic_pressure) {
		this.diastolic_pressure = diastolic_pressure;
	}

	public int getHeart_rate() {
		return heart_rate;
	}

	public void setHeart_rate(int heart_rate) {
		this.heart_rate = heart_rate;
	}

	public float getBlood_sugar() {
		return blood_sugar;
	}

	public void setBlood_sugar(float blood_sugar) {
		this.blood_sugar = blood_sugar;
	}

	public String getNurse_name() {
		return nurse_name;
	}

	public void setNurse_name(String nurse_name) {
		this.nurse_name = nurse_name;
	}

	public int getNurse_id() {
		return nurse_id;
	}

	public void setNurse_id(int nurse_id) {
		this.nurse_id = nurse_id;
	}

	public int getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(int isUpdate) {
		this.isUpdate = isUpdate;
	}

	@Override
	public String toString() {
		return "HealthBean [data_id=" + data_id + ", old_id=" + old_id + ", old_sn=" + old_sn + ", collect_time="
				+ collect_time + ", weight=" + weight + ", systolic_pressure=" + systolic_pressure
				+ ", diastolic_pressure=" + diastolic_pressure + ", heart_rate=" + heart_rate + ", blood_sugar="
				+ blood_sugar + ", nurse_name=" + nurse_name + ", nurse_id=" + nurse_id + ", isUpdate=" + isUpdate
				+ ", user=" + user + "]";
	}

}
