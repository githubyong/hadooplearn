package org.robby.web.demo;

import com.opensymphony.xwork2.ActionSupport;

public class TestAction extends ActionSupport{
	private String str = "test action str";

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
	public String Test(){
		return SUCCESS;
	}
}
