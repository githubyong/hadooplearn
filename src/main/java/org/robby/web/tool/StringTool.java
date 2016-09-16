package org.robby.web.tool;

public class StringTool {
	public static boolean checkStr(String s){
		for(int i=0; i<s.length(); i++){
			char ch = s.charAt(i);
			if(ch>='0' && ch<='9')
				continue;
			if(ch>='a' && ch<='z')
				continue;
			if(ch>='A' && ch<='Z')
				continue;
			
			return false;
		}
		return true;
	}
}
