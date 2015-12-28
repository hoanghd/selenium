package com.getmagpie.util;
import static io.github.seleniumquery.SeleniumQuery.jQuery;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.getmagpie.data.Result;
import com.getmagpie.data.Value;

import io.github.seleniumquery.SeleniumQueryObject;

public class CmdUtil {
	public static Result exec(String cmd, String selector, String val){
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
		else if(cmd.equals("EXIT")){
			jQuery.quit();
		}
		else if(cmd.equals("SCREENSHOT")){
			screenshot(val);
		}
		else if(cmd.equals("SET")){
			return setVal(selector, val);
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
		
		return Result.success();
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
		SeleniumQueryObject sel = jQuery(selector);
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
		SeleniumQueryObject sel = jQuery(selector);
		
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
					return val.compareTo(sel.text());
				}
				else if(type.equals("HTML")){
					return val.compareTo(sel.html());
				}
				else if(type.equals("VAL")){
					return val.compareTo(sel.val());
				}
			}
			else if(sel.size() > 1){
				return Result.failure("`{0}` Too many items in returns", new String[]{selector});
			}
		}
		
		return Result.failure();
	}
	
	public static Result click(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector);
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.click();
		}
		
		return Result.success();
	}
	
	public static Result submit(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector);
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.submit();
		}
		
		return Result.success();
	}
	
	public static Result setVal(String selector, String val){
		SeleniumQueryObject sel = jQuery(selector);
		
		if(sel.size() == 0){
			return Result.notFound(selector);
		}
		else{
			sel.val(val);
		}
		
		return Result.success();
	}
	
	public static void hub(String type, String hub){
		System.out.println(hub);
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
