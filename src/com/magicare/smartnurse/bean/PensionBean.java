package com.magicare.smartnurse.bean;

import java.io.Serializable;

public class PensionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pension_id;
	private int word_type;
	private String name;

	public int getPension_id() {
		return pension_id;
	}

	public void setPension_id(int pension_id) {
		this.pension_id = pension_id;
	}

	public int getWord_type() {
		return word_type;
	}

	public void setWord_type(int word_type) {
		this.word_type = word_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PensionBean [pension_id=" + pension_id + ", word_type=" + word_type + ", name = " + name + "]";
	}
}
