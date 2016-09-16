package org.robby.web.action;

public class FileAttr {
	private String name;
	private String size;
	private String dt;
	
	public FileAttr(String name, String size, String dt) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.size = size;
		this.dt = dt;
	}
	
	public FileAttr() {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	
	
}
