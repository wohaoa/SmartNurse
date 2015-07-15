package com.magicare.smartnurse.bean;

import java.io.Serializable;

/**
 * 
 * @author scott
 * 
 *         Function:反馈信息
 */
public class FeedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private int feed_id;
	private String content;

	public FeedBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeedBean(int feed_id, String content) {
		super();
		this.feed_id = feed_id;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(int feed_id) {
		this.feed_id = feed_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "FeedBean [id=" + id + ", feed_id=" + feed_id + ", content=" + content + "]";
	}

}
