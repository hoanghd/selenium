package com.getmagpie.db.driver;

import com.getmagpie.data.Result;

import java.util.Map;

public interface DBAbstract {
	public Result connect(String url);

	public Result count(String sql, Map<String, String> params);
	
	public Result find(String sql, Map<String, String> params);
	
	public Result close();
	
	public int getCnt();
	
	public String getValue(String key);
}
