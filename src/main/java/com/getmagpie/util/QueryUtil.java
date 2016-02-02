package com.getmagpie.util;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Keys;

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
	private static String allCmd = "^(ISHIDDEN|CHECKED|ISCHECKED|DROPDOWN(\\|TEXT|\\|VALUE|\\|INDEX)|DB\\.(CONNECT|CLOSE|SELECT|FIELD).*?|GOTO|BROWSER|URL(\\|EQUALS|\\|MATCH)|WAIT|SCREENSHOT|EXIT|SWITCHTOFRAME|SWITCHTODEFAULT|SET|SENDKEYS|SUBMIT|CLICK|COMPARE(\\|SIZE|\\|TEXT|\\|HTML|\\|VAL|\\|DISABLED|\\|SRC|\\|TYPE|\\|PLACEHOLDER|\\|LARGEROREQ|\\|LARGER)|MATCH(\\|TEXT|\\|HTML|\\|VAL))$";
	private static final String[] JUSTNOSEL = new String[] {"URL|EQUALS", "URL|MATCH", "GOTO", "BROWSER", "WAIT", "SCREENSHOT", "EXIT"};	
	public static final Map<String, Keys> KEYS;
    static {
    	KEYS = new HashMap<String, Keys>();
    	KEYS.put("NULL", Keys.NULL);
		KEYS.put("CANCEL", Keys.CANCEL);
		KEYS.put("HELP", Keys.HELP);
		KEYS.put("BACK_SPACE", Keys.BACK_SPACE);
		KEYS.put("TAB", Keys.TAB);
		KEYS.put("CLEAR", Keys.CLEAR);
		KEYS.put("RETURN", Keys.RETURN);
		KEYS.put("ENTER", Keys.ENTER);
		KEYS.put("SHIFT", Keys.SHIFT);
		KEYS.put("LEFT_SHIFT", Keys.LEFT_SHIFT);
		KEYS.put("CONTROL", Keys.CONTROL);
		KEYS.put("LEFT_CONTROL", Keys.LEFT_CONTROL);
		KEYS.put("ALT", Keys.ALT);
		KEYS.put("LEFT_ALT", Keys.LEFT_ALT);
		KEYS.put("PAUSE", Keys.PAUSE);
		KEYS.put("ESCAPE", Keys.ESCAPE);
		KEYS.put("SPACE", Keys.SPACE);
		KEYS.put("PAGE_UP", Keys.PAGE_UP);
		KEYS.put("PAGE_DOWN", Keys.PAGE_DOWN);
		KEYS.put("END", Keys.END);
		KEYS.put("HOME", Keys.HOME);
		KEYS.put("LEFT", Keys.LEFT);
		KEYS.put("ARROW_LEFT", Keys.ARROW_LEFT);
		KEYS.put("UP", Keys.UP);
		KEYS.put("ARROW_UP", Keys.ARROW_UP);
		KEYS.put("RIGHT", Keys.RIGHT);
		KEYS.put("ARROW_RIGHT", Keys.ARROW_RIGHT);
		KEYS.put("DOWN", Keys.DOWN);
		KEYS.put("ARROW_DOWN", Keys.ARROW_DOWN);
		KEYS.put("INSERT", Keys.INSERT);
		KEYS.put("DELETE", Keys.DELETE);
		KEYS.put("SEMICOLON", Keys.SEMICOLON);
		KEYS.put("EQUALS", Keys.EQUALS);
		KEYS.put("NUMPAD0", Keys.NUMPAD0);
		KEYS.put("NUMPAD1", Keys.NUMPAD1);
		KEYS.put("NUMPAD2", Keys.NUMPAD2);
		KEYS.put("NUMPAD3", Keys.NUMPAD3);
		KEYS.put("NUMPAD4", Keys.NUMPAD4);
		KEYS.put("NUMPAD5", Keys.NUMPAD5);
		KEYS.put("NUMPAD6", Keys.NUMPAD6);
		KEYS.put("NUMPAD7", Keys.NUMPAD7);
		KEYS.put("NUMPAD8", Keys.NUMPAD8);
		KEYS.put("NUMPAD9", Keys.NUMPAD9);
		KEYS.put("MULTIPLY", Keys.MULTIPLY);
		KEYS.put("ADD", Keys.ADD);
		KEYS.put("SEPARATOR", Keys.SEPARATOR);
		KEYS.put("SUBTRACT", Keys.SUBTRACT);
		KEYS.put("DECIMAL", Keys.DECIMAL);
		KEYS.put("DIVIDE", Keys.DIVIDE);
		KEYS.put("F1", Keys.F1);
		KEYS.put("F2", Keys.F2);
		KEYS.put("F3", Keys.F3);
		KEYS.put("F4", Keys.F4);
		KEYS.put("F5", Keys.F5);
		KEYS.put("F6", Keys.F6);
		KEYS.put("F7", Keys.F7);
		KEYS.put("F8", Keys.F8);
		KEYS.put("F9", Keys.F9);
		KEYS.put("F10", Keys.F10);
		KEYS.put("F11", Keys.F11);
		KEYS.put("F12", Keys.F12);
		KEYS.put("META", Keys.META);
		KEYS.put("COMMAND", Keys.COMMAND);
		KEYS.put("ZENKAKU_HANKAKU", Keys.ZENKAKU_HANKAKU);
    }
	
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
				ex.printStackTrace();
				return Result.failure(ex.getMessage());
			}
		}
	}
}
