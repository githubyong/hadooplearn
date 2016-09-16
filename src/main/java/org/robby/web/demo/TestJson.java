package org.robby.web.demo;

import org.robby.web.action.FileAttr;

import com.google.gson.Gson;

public class TestJson {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileAttr fa = new FileAttr("test.xml", "10M", "2014");
		Gson gson = new Gson();
		System.out.println(gson.toJson(fa));
		String str = gson.toJson(fa);
		
		System.out.println(str);
		FileAttr fa1 = gson.fromJson(str, FileAttr.class);
	}
}
