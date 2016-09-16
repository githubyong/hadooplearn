package org.robby.web.action;

import java.util.Map;

import org.robby.web.tool.RedisTool;
import org.robby.web.tool.StringTool;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport{
	private String name;
	private String password;
	private String msg;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String login() {
		System.out.println("name->" + name);
		System.out.println("password->" + password);
		
		msg = "";
		//if(name.equals("admin") && password.equals("admin")){
		if(RedisTool.loginUser(name, password)){
			Map map = ActionContext.getContext().getSession();
			map.put("USERNAME", name);
			msg = "登陆成功";
			return SUCCESS;
		}else{
			msg = "登陆失败";
		}
		System.out.println("name->" + name);
		System.out.println("password->" + password);

		return SUCCESS;
	}
	
	public String register() {
		System.out.println("name->" + name);
		System.out.println("password->" + password);
		
		if(!StringTool.checkStr(name) || !StringTool.checkStr(password)){
			msg = "非法字符，注册失败";
			return SUCCESS;
		}
		
		if(RedisTool.registerUser(name, password)){
			msg = "注册成功";
		}else{
			msg = "注册失败";
		}
		
		return SUCCESS;
	}
	
	public String logout() {
		System.out.println("logout action");
		Map map = ActionContext.getContext().getSession();
		map.remove("USERNAME");
		msg = "注销成功";
		return SUCCESS;
		
	}

}
