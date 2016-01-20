package com.getmagpie.db;

import com.getmagpie.data.Result;
import com.getmagpie.db.driver.*;
import com.google.gson.Gson;

//mongodb://magpie-dev:magpie-dev@ds059634.mongolab.com:59634/magpie-dev
//jdbc:mysql://localhost:3306/dbname?user=username&password=pw
//jdbc:postgresql://hostname:port/dbname?user=username&password=pw

public class Database {
	private static Database db;
	private DBAbstract driver = null;	
				
	public static Database getInstance(){
		if(db == null)
			db = new Database();
		return db;
	}
	
	public Result connect(String url){		
		if(url.startsWith("mongodb://")){			
			driver = new DBMongo();		
			return driver.connect(url);
		}
		else if(url.startsWith("jdbc:mysql://")){
			driver = new DBMysql();
			return driver.connect(url);
		}
		else if(url.startsWith("jdbc:postgresql://")){
			driver = new DBPostgresql();
			return driver.connect(url);
		}
		
		return Result.failure();
	}
	
	public Result count(String json){
		try {
			Gson gson = new Gson(); 
			Query query = gson.fromJson(json, Query.class);
			return driver.count(query.getQuery(), query.getParams());
		} 
		catch (Exception e) {
			return Result.failure(e.getMessage());
		}
	}
	
	public int getCnt(){
		return driver.getCnt();
	}
	
	public Result find(String json){
		try {
			Gson gson = new Gson(); 
			Query query = gson.fromJson(json, Query.class);
			return driver.find(query.getQuery(), query.getParams());			
		} 
		catch (Exception e) {
			return Result.failure(e.getMessage());
		}
	}
	
	public String getValue(String key){
		return driver.getValue(key);
	}
	
	public Result close(){
		return driver.close();
	}
}
