package com.magicare.smartnurse.bean;


/**
 * 所有老人当前活动状况
 * 
 * @author rice
 * 
 */
public class ActiveBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	private int pension_id;// 养老院id
	private int stop_num; // 静止人数
	private int sleep_num; // 睡眠人数
	private int move_num; // 活动人数
	private String data_time;// 整十分钟的时刻点
	private long data_timestamp;// 整10分钟的时刻点的时间戳

	public int getPension_id() {
		return pension_id;
	}

	public void setPension_id(int pension_id) {
		this.pension_id = pension_id;
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

	public String getData_time() {
		return data_time;
	}

	public void setData_time(String data_time) {
		this.data_time = data_time;
	}

	public long getData_timestamp() {
		return data_timestamp;
	}

	public void setData_timestamp(long data_timestamp) {
		this.data_timestamp = data_timestamp;
	}

	@Override
	public String toString() {
		return "ActiveBean [pension_id=" + pension_id + ", stop_num=" + stop_num + ", sleep_num=" + sleep_num
				+ ", move_num=" + move_num + ", data_time=" + data_time + ", data_timestamp=" + data_timestamp + "]";
	}

}