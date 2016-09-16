package org.robby.web.action;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.robby.web.tool.RedisTool;
import org.robby.web.tool.StringTool;

import com.opensymphony.xwork2.ActionSupport;

public class SearchAction extends ActionSupport {
	String text;
	String query;

	Set<String> result;
	
	public Set<String> getResult() {
		this.result = RedisTool.zrevrange(query, 0, 5);
		
		return result;
	}

	public void setResult(Set<String> result) {
		this.result = result;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String search() {
		return SUCCESS;
	}
	
	public String exec() throws Exception {
		return SUCCESS;
	}
	
	
}
