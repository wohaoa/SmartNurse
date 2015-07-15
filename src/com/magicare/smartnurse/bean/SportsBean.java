package com.magicare.smartnurse.bean;

import com.magicare.smartnurse.utils.DateUtil;

/**
 * 老人运动信息
 * 
 * @author 波
 * 
 */
public class SportsBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String data_id;// 数据的唯一标示
	private int old_id;// 老人ID

	private String start_time;// 数据开始时间
	private String end_time;// 数据结束时间
	private int time_value;// 时间之差(开始和结束时间之间的刻度数)
	private int mode;// 模式，255-运动，254睡眠
	private int state;// 0-在线，1-离线
	private int step; // 步数
	private float calorie;// 消耗的卡路里
	private float meter; // 米
	private float sleep_0;// 深睡时间（分钟）
	private float sleep_1; // 浅睡时间
	private float sleep_2; // 辗转反侧
	private float sleep_quantity;// 睡眠质量指数

	public String getData_id() {
		return data_id;
	}

	public void setData_id(String data_id) {
		this.data_id = data_id;
	}

	public int getOld_id() {
		return old_id;
	}

	public void setOld_id(int old_id) {
		this.old_id = old_id;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getTime_value() {
		return time_value;
	}

	public void setTime_value(int time_value) {
		this.time_value = time_value;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public float getCalorie() {
		return calorie;
	}

	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}

	public float getMeter() {
		return meter;
	}

	public void setMeter(float meter) {
		this.meter = meter;
	}

	public float getSleep_0() {
		return sleep_0;
	}

	public void setSleep_0(float sleep_0) {
		this.sleep_0 = sleep_0;
	}

	public float getSleep_1() {
		return sleep_1;
	}

	public void setSleep_1(float sleep_1) {
		this.sleep_1 = sleep_1;
	}

	public float getSleep_2() {
		return sleep_2;
	}

	public void setSleep_2(float sleep_2) {
		this.sleep_2 = sleep_2;
	}

	public float getSleep_quantity() {
		return sleep_quantity;
	}

	public void setSleep_quantity(float sleep_quantity) {
		this.sleep_quantity = sleep_quantity;
	}

	@Override
	public String toString() {
		return "SportsBean [data_id=" + data_id + ", old_id=" + old_id + ", start_time=" + start_time + ", end_time="
				+ end_time + ", time_value=" + time_value + ", mode=" + mode + ", state=" + state + ", step=" + step
				+ ", calorie=" + calorie + ", meter=" + meter + ", sleep_0=" + sleep_0 + ", sleep_1=" + sleep_1
				+ ", sleep_2=" + sleep_2 + ", sleep_quantity=" + sleep_quantity + "]";
	}

}