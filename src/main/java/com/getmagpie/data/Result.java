package com.getmagpie.data;

import com.getmagpie.util.ComUtil;

public class Result {
	private Boolean status;	
	private String message;
	
	public Result(Boolean status){
		this.status = status;
	}
	
	public Result(Boolean status, String message){		
		this.message = message;
		this.status = status;
	}
	
	public Boolean getStatus(){
		return status;
	}
	
	public String getMessage(){
		return message;
	}
	
	public static Result notFound(String text){
		return new Result(false, ComUtil.replace("`{0}` not found!", new String[]{text}));
	}
	
	public static Result failure(){
		return new Result(false);
	}
	
	public static Result failure(String message){
		return new Result(false, message);
	}
	
	public static Result failure(String message, String[] params){
		return new Result(false, ComUtil.replace(message, params));
	}

	public static Result success(){
		return new Result(true);
	}
	
	public static Result success(String message){
		return new Result(true, message);
	}
	
	public static Result success(String message, String[] params){
		return new Result(true, ComUtil.replace(message, params));
	}
}
