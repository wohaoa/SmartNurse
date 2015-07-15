package com.magicare.smartnurse.bean;

/**
 * 养老院各区域统计数据
 * 
 * @author rice
 * 
 */
public class AreaActiveBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	private int pension_areaid;// 养老院区域id
	private int stop_num; // 静止人数
	private int sleep_num; // 睡眠人数
	private int move_num; // 活动人数
	private int alarm_num;// 报警人数
	private int people_num; // 区域人数
	private String area_name;// 区域名字

	public int getPension_areaid() {
		return pension_areaid;
	}

	public void setPension_areaid(int pension_areaid) {
		this.pension_areaid = pension_areaid;
	}

	public int getAlarm_num() {
		return alarm_num;
	}

	public void setAlarm_num(int alarm_num) {
		this.alarm_num = alarm_num;
	}

	public int getPeople_num() {
		return people_num;
	}

	public void setPeople_num(int people_num) {
		this.people_num = people_num;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public int getStop_num() {
		return stop_num;
	}

	public void setStop_num(int stop_num) {
		this.stop_num = stop_num;
	}

	public int getSleep_num() {
		return sleep_num;
	}

	public void setSleep_num(int sleep_num) {
		this.sleep_num = sleep_num;
	}

	public int getMove_num() {
		return move_num;
	}

	public void setMove_num(int move_num) {
		this.move_num = move_num;
	}

	@Override
	public String toString() {
		return "AreaActiveBean [pension_areaid=" + pension_areaid + ", stop_num=" + stop_num + ", sleep_num="
				+ sleep_num + ", move_num=" + move_num + ", alarm_num=" + alarm_num + ", people_num=" + people_num
				+ ", area_name=" + area_name + "]";
	}

}