package com.getmagpie.util;
import static io.github.seleniumquery.SeleniumQuery.jQuery;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.getmagpie.data.Result;
import com.getmagpie.data.Value;
import com.getmagpie.db.Database;

import io.github.seleniumquery.SeleniumQueryObject;

public class CmdUtil {
	public static long waitUntilTimeout = 30000;
	
	public static Result exec(String cmd, String selector, String val){
		System.out.print("[" + cmd + "]\t> " + selector + " | " + val);
		
		if(cmd.equals("BROWSER")){
			if(!selector.isEmpty() && selector.startsWith("http")){
				hub(val, selector);
			}
			else{
				browser(val, selector);
			}
		}
		else if(cmd.startsWith("GOTO")){
			jQuery.url(val);
		}
		else if(cmd.startsWith("SWITCHTOFRAME")){
			toFrame(selector);
		}
		else if(cmd.startsWith("SWITCHTODEFAULT")){
			toDefault();
		}
		else if(cmd.equals("WAIT")){
			if(!val.isEmpty()){
				try {					
					Thread.sleep(Double.valueOf(val).intValue() * 1000);				
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
		else if(cmd.equals("EXIT")){
			jQuery.quit();
		}
		else if(cmd.equals("SCREENSHOT")){
			screenshot(val);
		}
		else if(cmd.equals("SET")){
			return setVal(selector, val);
		}
		else if(cmd.equals("SENDKEYS")){
			return sendKeys(selector, val);
		}
		else if(cmd.equals("SUBMIT")){
			return submit(selector, val);
		}
		else if(cmd.equals("CLICK")){
			return click(selector, val);
		}
		else if(cmd.startsWith("URL")){
			String[] parts = cmd.split(Pattern.quote("|"));
			return url((parts.length>1 ? parts[1] : null), val);
		}
		else if(cmd.startsWith("COMPARE")){
			String[] parts = cmd.split(Pattern.quote("|"));
			return compare(selector, (parts.length>1 ? parts[1] : null), val);
		}
		else if(cmd.startsWith("MATCH")){
			String[] parts = cmd.split(Pattern.quote("|"));
			return match(selector, (parts.length>1 ? parts[1] : null), val);
		}
		else if(cmd.equals("DB.CONNECT")){
			return Database.getInstance().connect(val);			
		}
		else if(cmd.equals("DB.CLOSE")){
			return Database.getInstance().close();
		}
		else if(cmd.startsWith("DB.SELECT|COUNT")){
			Result rs = Database.getInstance().count(val);
			if(rs.getStatus()){
				Value value = Value.parse(val);
				return value.compareTo(Double.valueOf(String.valueOf(Database.getInstance().getCnt())));
			}
			return rs;
		}
		else if(cmd.equals("DB.SELECT")){
			return Database.getInstance().find(val);
		}
		else if(cmd.startsWith("DB.FIELD")){
			List<String> params = ComUtil.matcher("DB\\.FIELD\\((.+)\\)\\|(.+)\\|?(.+)?", cmd);
			String dbVal = Database.getInstance().getValue(params.get(0));
			
			if(params.get(2) == null || params.get(2).isEmpty()){				
				if(params.get(1).equals("COMPARE")){
					Value vl = Value.parse(val);
					return vl.compareTo(dbVal);
				} else {
					if(ComUtil.isValid(dbVal, val)){
						return Result.success(val);
					} else {
						Result.failure(val);
					}
				}				
			} else {
				if(params.get(1).equals("COMPARE")){
					return compare(selector, params.get(2), val+dbVal);
				} else {
					return match(selector, params.get(2), val+dbVal);
				}
			}
		}
		else if(cmd.startsWith("DROPDOWN")){
			List<String> params = ComUtil.matcher("DROPDOWN\\|(.+)", cmd);
			return dropdown(selector, params.get(0), val);
		}
		else if(cmd.equals("ISCHECKED")){
			return isChecked(selector, val);
		}
		else if(cmd.equals("CHECKED")){
			return checked(selector, val);
		}
		else if(cmd.equals("ISHIDDEN")){
			return isHidden(selector, val);
		}
		
		return Result.success();
	}
	
	public static Result isHidden(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else {
			if(val.trim().toLowerCase().equals("true") && !sel.get(0).isDisplayed()){
				return Result.success();
			}
			else if(val.trim().toLowerCase().equals("false") && sel.get(0).isDisplayed()){
				return Result.success();
			}
		}
		
		return Result.failure();
	}
	
	public static Result checked(String selector, String text){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else {			
			for(int i = 0; i<sel.size(); i++){
				WebElement elm = sel.get(i);
				if(elm.getAttribute("value").equals(text) && !elm.isSelected()){
					elm.click();
					return Result.success();
				}
			}
		}
		return Result.failure();
	}
	
	public static Result isChecked(String selector, String text){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else {			
			for(int i = 0; i<sel.size(); i++){
				WebElement elm = sel.get(i);
				if(elm.isSelected() && elm.getAttribute("value").equals(text)){
					return Result.success();
				}
			}
		}
		
		return Result.failure();
	}
	
	public static Result dropdown(String selector, String type, String text){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else if(sel.size() == 1){
			Select dropdown = new Select(sel.get(0));
						
			if(type.equals("TEXT")) {
				dropdown.selectByVisibleText(text);
			}
			else if(type.equals("VALUE")){
				dropdown.selectByValue(text);
			}
			else if(type.equals("INDEX")){
				dropdown.selectByIndex(Integer.valueOf(text));
			}
			
			return Result.success();
		}
		else if(sel.size() > 1){
			return Result.failure("`{0}` Too many items in returns", new String[]{selector});
		}
		return Result.failure();
	}
	
	public static void toFrame(String selector){
		jQuery.driver().get().switchTo().frame(selector);
	}
	
	public static void toDefault(){
		jQuery.driver().get().switchTo().defaultContent();
	}
	
	public static void screenshot(String path){
		try {			
			WebDriver driver = jQuery.driver().get();			
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(path), true);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Result url(String type, String text){		
		String url = jQuery.url().toString();
		
		if(type.equals("EQUALS")) {
			if(url.equals(text))
				return Result.success(url);
		}
		else if(type.equals("MATCH")){
			if(ComUtil.isValid(text, url)){				
				return Result.success(url);
			}
		}
		
		return Result.failure(url);
	}
	
	public static Result match(String selector, String type, String text){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		String val = "";
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else if(sel.size() == 1){			
			if(type.equals("TEXT")) {
				val = sel.text();
			}
			else if(type.equals("HTML")){
				val = sel.html();
			}
			else if(type.equals("VAL")){
				val = sel.val();
			}
			
			if(ComUtil.isValid(text, val))
				return Result.success(val);
		}
		else if(sel.size() > 1){
			return Result.failure("`{0}` Too many items in returns", new String[]{selector});
		}
		
		return Result.failure(val);
	}
	
	public static Result compare(String selector, String type, String text){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		sel.get(0).sendKeys();
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			Value val = Value.parse(text);
			
			if(type.equals("SIZE")){
				if(!ComUtil.isNumberic(val.getValue())){
					return Result.failure("`{0}` must be a number.", new String[]{text});
				}
				else {					
					return val.compareTo(Double.valueOf(String.valueOf(sel.size())));
				}
			}
			else if(sel.size() == 1){	
				if(type.equals("TEXT")){
					return val.compareTo(sel.text().trim());
				}
				else if(type.equals("HTML")){
					return val.compareTo(sel.html());
				}
				else if(type.equals("VAL")){
					return val.compareTo(sel.val());
				}
				else if (type.equals("DISABLED")) {
					return val.compareTo(sel.attr("disabled"));
				}
				else if (type.equals("SRC")) {
					return val.compareTo(sel.attr("src"));
				}
				else if (type.equals("TYPE")) {
					return val.compareTo(sel.attr("type"));
				}
				else if (type.equals("PLACEHOLDER")) {
					return val.compareTo(sel.attr("placeholder"));
				}
			}
			else if(sel.size() > 1){
				return Result.failure("`{0}` Too many items in returns", new String[]{selector});
			}
		}
		
		return Result.failure();
	}
	
	public static Result click(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.click();
		}
		
		return Result.success();
	}
	
	public static Result submit(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.submit();
		}
		
		return Result.success();
	}
	
	public static Result sendKeys(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.get(0).sendKeys(val);
		}
		
		return Result.success();
	}
	public static Result setVal(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector).waitUntil(waitUntilTimeout).is(":visible").then();
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{			
			sel.val(val);
		}
		
		return Result.success();
	}
	
	public static void hub(String type, String hub){
		WebDriver driver = null;
		DesiredCapabilities capability = null;
		
		if(type.equals("Firefox")){
			capability = DesiredCapabilities.firefox();
		}
		else if(type.equals("Chrome")){
			capability = DesiredCapabilities.chrome();
		}
		else if(type.equals("Safari")){
			capability = DesiredCapabilities.safari();
		}
		
		try {
			if(capability != null){
				driver = new RemoteWebDriver(new URL(hub), capability);
				jQuery.driver().use(driver);
				jQuery.driver().get().manage().window().maximize();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void browser(String type, String bit){		
		if(type.equals("Firefox")){
			jQuery.driver().useFirefox();
			jQuery.driver().get().manage().window().maximize();
		}
		else if(type.equals("Chrome")){
			if(OSUtil.isWindows())
				jQuery.driver().useChrome().withPathToChromeDriver("./driver/window/chromedriver.exe");
			else if(OSUtil.isMac())
				jQuery.driver().useChrome().withPathToChromeDriver("./driver/macos/chromedriver");
			else 
				jQuery.driver().useChrome().withPathToChromeDriver("./driver/linux/chromedriver");
			
			jQuery.driver().get().manage().window().maximize();
		}
		else if(type.equals("Safari")){
			WebDriver driver = new SafariDriver();
			jQuery.driver().use(driver);
			jQuery.driver().get().manage().window().maximize();
		}
	}
}
