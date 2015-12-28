package com.getmagpie.data;

import java.util.Arrays;

import com.getmagpie.util.ComUtil;

public class Value {
	private static final String[] operators = new String[] {"=", "!=", ">", ">=", "<", "<="};
	private String operator;
	private String value;
	
	public Value(String operator, String value){		
		this.operator = operator;
		this.value = value;
	}

	public String getOperator(){
		return operator;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setOperator(String operator){
		this.operator = operator;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public Result compareTo(Double compareTo){		
		Double compareValue = Double.valueOf(value);
		
		int rev = Double.compare(compareTo, compareValue);
		
		if(operator.equals("=")){
			if(rev == 0)
				return Result.success(String.valueOf(compareTo));
		}
		else if(operator.equals("!=")){
			if(rev != 0)
				return Result.success(String.valueOf(compareTo));
		}
		else if(operator.equals(">")){
			if(rev > 0)
				return Result.success(String.valueOf(compareTo));
		}
		else if(operator.equals(">=")){
			if(rev >= 0)
				return Result.success(String.valueOf(compareTo));
		}
		else if(operator.equals("<")){
			if(rev < 0)
				return Result.success(String.valueOf(compareTo));
		}
		else if(operator.equals("<=")){
			if(rev <= 0)
				return Result.success(String.valueOf(compareTo));
		}
		
		return Result.failure(compareTo + "");
	}
	
	public Result compareTo(String compareTo){
		if(operator.equals("=")){
			if(value.equals(compareTo))
				return Result.success(compareTo);
		}
		else if(operator.equals("!=")){
			if(!value.equals(compareTo))
				return Result.success(String.valueOf(compareTo));
		}
		else{
			if(ComUtil.isNumberic(compareTo) && ComUtil.isNumberic(value) ){
				return compareTo(Double.valueOf(compareTo));
			}
		}
		
		return Result.failure(compareTo);
	}
	
	public static Value parse(String text){
		if(Arrays.asList(operators).contains(text.substring(0, 2))){
			return new Value(text.substring(0, 2), text.substring(2));
		}
		else if(Arrays.asList(operators).contains(text.substring(0, 1))){
			return new Value(text.substring(0, 1), text.substring(1));
		}
		else {
			return new Value(null, text);
		}
	}
}
