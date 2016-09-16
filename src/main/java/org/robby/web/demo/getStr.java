package org.robby.web.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getStr {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String s = "192.168.1.103 - - [22/Sep/2014:09:44:59 -0400] \"GET /web/search.do?text=hello HTTP/1.1\" 200 5003";
		
		String regEx = "\".*=(.*?) .*\"";

		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(s);
		if (mat.find()) {
			s = mat.group(1);
			s = URLDecoder.decode(s, "UTF-8");
			
			System.out.println(s);
		}
	}

}
