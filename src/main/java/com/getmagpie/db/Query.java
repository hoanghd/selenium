package com.getmagpie.db;

import java.util.Map;

public class Query {
	private String query;
	private Map<String, String> params;
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String query){
		this.query = query;
	}
	
	public Map<String, String> getParams(){
		return params;
	}
	
	public void setParams(Map<String, String> params){
		this.params = params;
	}
}
