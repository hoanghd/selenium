package com.getmagpie.util;
import java.util.Arrays;

import com.getmagpie.data.Result;

public class QueryUtil {
	//DB.CONNECT
	//DB.CLOSE
	//DB.SELECT|COUNT|COMPARE
	//DB.SELECT
	//DB.FIELD(field_name)|COMPARE
	//DB.FIELD(field_name)|MATCH
	//DB.FIELD(field_name)|COMPARE(\\|TEXT|\\|HTML|\\|VAL)
	//DB.FIELD(field_name)|MATCH(\\|TEXT|\\|HTML|\\|VAL)	
	private static String allCmd = "^(DB\\.(CONNECT|CLOSE|SELECT|FIELD).*?|GOTO|BROWSER|URL(\\|EQUALS|\\|MATCH)|WAIT|SCREENSHOT|EXIT|SWITCHTOFRAME|SWITCHTODEFAULT|SET|SENDKEYS|SUBMIT|CLICK|COMPARE(\\|SIZE|\\|TEXT|\\|HTML|\\|VAL|\\|DISABLED|\\|SRC|\\|TYPE|\\|PLACEHOLDER|\\|LARGEROREQ|\\|LARGER)|MATCH(\\|TEXT|\\|HTML|\\|VAL))$";
	private static final String[] JUSTNOSEL = new String[] {"URL|EQUALS", "URL|MATCH", "GOTO", "BROWSER", "WAIT", "SCREENSHOT", "EXIT"};
	
	public static Result row(String cmd){
		return row(cmd, "", "");
	}

	public static Result row(String cmd, String val){
		return row(cmd, val, "");
	}
	
	public static Result row(String cmd, String val, String selector){
		if(cmd.isEmpty()){
			return Result.failure("`Command` is empty!");
		}
		else if(!ComUtil.isValid(allCmd, cmd)){
			return Result.failure("`" + cmd + "` is not a valid command");		
		}
		else if(selector.isEmpty() && !Arrays.asList(JUSTNOSEL).contains(cmd) && !cmd.startsWith("DB.")){
			return Result.failure("`Object` is empty!");
		}
		else {
			try{
				return CmdUtil.exec(cmd, selector, val);
			}
			catch(Exception ex){
				return Result.failure(ex.getMessage());
			}
		}
	}
}
