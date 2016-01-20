package com.getmagpie.util;

import java.util.ArrayList;
import java.util.List;
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
	
	public static List<String> matcher(String reg, String text){
		List<String> matches = new ArrayList<String>();		
		Matcher m = Pattern.compile(reg).matcher(text);
        if(m.find()) {
        	if(m.groupCount()>0){
        		for(int i = 1;  i<=m.groupCount(); i++){
        			 matches.add(m.group(i));
        		}
        	}
        }
        
		return matches;
	}
	
	public static String replace(String template, String[] params){
		for (int i=0; i<params.length; i++) {
			template = template.replace("{" + i + "}", params[i]);
        }
		return template;
	}
}
