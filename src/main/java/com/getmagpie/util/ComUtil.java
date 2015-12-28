package com.getmagpie.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComUtil {
	public static Boolean isValid(String reg, String text){
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(text);
		return  matcher.matches();
	}
	
	public static Boolean isNumberic(String text){
		return isValid("^\\s*[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?\\s*$", text);
	}
	
	public static String replace(String template, String[] params){
		for (int i=0; i<params.length; i++) {
			template = template.replace("{" + i + "}", params[i]);
        }
		return template;
	}
	
	public static void main(String args[]){
		System.out.println(ComUtil.isValid("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?", "https://stackoverflow.com/questions/1600291/validating-url-in-java"));
	}
}
