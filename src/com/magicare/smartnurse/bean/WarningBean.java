package com.magicare.smartnurse.bean;

import java.io.Serializable;

/**
 * 未处理报警信息和 未反馈报警信息
 * 
 * @author 波
 * 
 */
public class WarningBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// 数据表中的ID
	private String alarm_id; // 报警信息id
	private String alarm_time; // 报警时间
	private String station_detail;// 报警的基站地址
	private int alarm_type;// 报警类型，1-SOS，2-跌倒，3-离开监控
	private int alarm_status; // 0未处理 1未反馈
	private String resolve_time; // 处理时间(string)
	private String feed_time;// 反馈的时间(string)
	private String feed_nurse; // 反馈的护士(string)
	private String feedback;// 对于反馈结果的文字处理(string)
	private int old_id;// 老人ID
	private String old_sn;// 老人编码(string)
	private String nurse_id; // 护士id
	private String old_name;// 老人姓名
	// 这两个是激光推送必要的字段
	private int type;// 1-报警，2-报警已处理，3-区域活动
	private String active_mod = "活动"; // 活动类型，活动、睡眠、静止
	private boolean isOutside;// 是否是外区域来的老人

	public boolean isOutside() {
		return isOutside;
	}

	public void setOutside(boolean isOutside) {
		this.isOutside = isOutside;
	}

	public int getOld_id() {
		return old_id;
	}

	public void setOld_id(int old_id) {
		this.old_id = old_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getActive_mod() {
		return active_mod;
	}

	public void setActive_mod(String active_mod) {
		this.active_mod = active_mod;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getStation_detail() {
		return station_detail;
	}

	public void setStation_detail(String station_detail) {
		this.station_detail = station_detail;
	}

	public int getAlarm_type() {
		return alarm_type;
	}

	public void setAlarm_type(int alarm_type) {
		this.alarm_type = alarm_type;
	}

	public int getAlarm_status() {
		return alarm_status;
	}

	public void setAlarm_status(int alarm_status) {
		this.alarm_status = alarm_status;
	}

	public String getResolve_time() {
		return resolve_time;
	}

	public void setResolve_time(String resolve_time) {
		this.resolve_time = resolve_time;
	}

	public String getFeed_time() {
		return feed_time;
	}

	public void setFeed_time(String feed_time) {
		this.feed_time = feed_time;
	}

	public String getFeed_nurse() {
		return feed_nurse;
	}

	public void setFeed_nurse(String feed_nurse) {
		this.feed_nurse = feed_nurse;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getOld_sn() {
		return old_sn;
	}

	public void setOld_sn(String old_sn) {
		this.old_sn = old_sn;
	}

	public String getNurse_id() {
		return nurse_id;
	}

	public void setNurse_id(String nurse_id) {
		this.nurse_id = nurse_id;
	}

	public String getOld_name() {
		return old_name;
	}

	public void setOld_name(String old_name) {
		this.old_name = old_name;
	}

	@Override
	public String toString() {
		return "WarningBean [id=" + id + ", alarm_id=" + alarm_id + ", alarm_time=" + alarm_time + ", station_detail="
				+ station_detail + ", alarm_type=" + alarm_type + ", alarm_status=" + alarm_status + ", resolve_time="
				+ resolve_time + ", feed_time=" + feed_time + ", feed_nurse=" + feed_nurse + ", feedback=" + feedback
				+ ", old_id=" + old_id + ", old_sn=" + old_sn + ", nurse_id=" + nurse_id + ", old_name=" + old_name
				+ ", type=" + type + ", active_mod=" + active_mod + "]";
	}

}
