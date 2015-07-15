package com.magicare.smartnurse.bean;

import java.io.Serializable;

/**
 * 
 * @author scott
 * 
 *         Function:激光推送的extras信息
 */
public class JpushExtrasBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String alarm_id;// 报警id
	private String alarm_time;// 报警时间戳
	private int alarm_type; // 报警类型，1-SOS，2-跌倒，3-离开监控
	private String old_name; // 老人姓名
	private String station_detail;// 报警位置

	public String getAlarm_id() {
		return alarm_id;
	}

	public void setAlarm_id(String alarm_id) {
		this.alarm_id = alarm_id;
	}

	public String getAlarm_time() {
		return alarm_time;
	}

	public void setAlarm_time(String alarm_time) {
		this.alarm_time = alarm_time;
	}

	public int getAlarm_type() {
		return alarm_type;
	}

	public void setAlarm_type(int alarm_type) {
		this.alarm_type = alarm_type;
	}

	public String getOld_name() {
		return old_name;
	}

	public void setOld_name(String old_name) {
		this.old_name = old_name;
	}

	public String getStation_detail() {
		return station_detail;
	}

	public void setStation_detail(String station_detail) {
		this.station_detail = station_detail;
	}

	@Override
	public String toString() {
		return "JpushExtrasBean [alarm_id=" + alarm_id + ", alarm_time=" + alarm_time + ", alarm_type=" + alarm_type
				+ ", old_name=" + old_name + ", station_detail=" + station_detail + "]";
	}

}
