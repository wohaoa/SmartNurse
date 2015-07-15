package com.magicare.smartnurse.bean;

import java.io.Serializable;

public class ConcernBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;// 数据表中的ID
	private int old_id;// 老人ID
	private String content;	//提问内容
	private String child_name;	//子女姓名
	private String create_time;	//叮嘱提问时间
	private String reply_time;  //回复时间
	private String replyer_name;  //回复人
	private String reply_content; //回复内容
	private String reply_images;	//回复图片
	private int exhort_id;	//叮嘱编号
	private int status;	//1是已回复,0是未回复
	
	
	public String getReply_time() {
		return reply_time;
	}
	public void setReply_time(String reply_time) {
		this.reply_time = reply_time;
	}
	public String getReplyer_name() {
		return replyer_name;
	}
	public void setReplyer_name(String replyer_name) {
		this.replyer_name = replyer_name;
	}
	public String getReply_content() {
		return reply_content;
	}
	public void setReply_content(String reply_content) {
		this.reply_content = reply_content;
	}
	public String getReply_images() {
		return reply_images;
	}
	public void setReply_images(String reply_images) {
		this.reply_images = reply_images;
	}
	public int getExhort_id() {
		return exhort_id;
	}
	public void setExhort_id(int exhort_id) {
		this.exhort_id = exhort_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getChild_name() {
		return child_name;
	}
	public void setChild_name(String child_name) {
		this.child_name = child_name;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOld_id() {
		return old_id;
	}
	public void setOld_id(int old_id) {
		this.old_id = old_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "ConcernBean [id=" + id + ", child_name=" + child_name + ", reply_time=" + reply_time + 
				", replyer_name=" + replyer_name + ", status=" + status + ",  old_id=" + old_id + 
				", create_time=" + create_time + ", exhort_id=" + exhort_id + 
				", content=" + content + ", + reply_content="+ reply_content + "]";
	}
}
