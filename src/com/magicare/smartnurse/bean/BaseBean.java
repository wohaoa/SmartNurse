package com.magicare.smartnurse.bean;

import java.io.Serializable;

/**
 * 
 * @author scott
 * 
 *         Function:实体对象的基类
 * @param <T>：泛型类型
 */
public class BaseBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int status;
	private String info;
	private String data;

	public BaseBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "BaseBean [status=" + status + ", info=" + info + ", data=" + data + "]";
	}

}
