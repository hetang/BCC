package com.sdsu.bcc.database.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CategoryInformation implements Serializable {
	private int id;
	private String type;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
