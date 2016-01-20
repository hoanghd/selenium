package com.getmagpie.db.driver;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.getmagpie.data.Result;
import com.mongodb.*;

public class DBMongo implements DBAbstract{
	private DB db;	
	private MongoClient mongoClient;		
	public DBObject row = null;
	public int cnt = 0;
	
	public DBMongo(){
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
	    mongoLogger.setLevel(Level.SEVERE);
	}
		
	@SuppressWarnings("deprecation")
	public Result connect(String url) {
		try {
			MongoClientURI uri = new MongoClientURI(url);			
			mongoClient = new MongoClient(uri);
			db = mongoClient.getDB(uri.getDatabase());
		    
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
	}

	public Result count(String table, Map<String, String> params) {
		try {
			BasicDBObject query = new BasicDBObject();
			for(Entry<String, String> entry : params.entrySet()) {
				query.append(entry.getKey(), entry.getValue());
			}
			
			DBCollection coll = db.getCollection(table);			
			cnt = (int)coll.count(query);
			
			return Result.success();	
		} 
		catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
	}

	public Result find(String table, Map<String, String> params) {
		try {
			BasicDBObject query = new BasicDBObject();
			for(Entry<String, String> entry : params.entrySet()) {
				query.append(entry.getKey(), entry.getValue());
			}
			
			DBCollection coll = db.getCollection(table);
			row = coll.findOne(query);
						
			if(row == null)
				return Result.failure("Not found!");
			else			
				return Result.success();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
	}

	public Result close() {
		try {
			if(mongoClient != null)
				mongoClient.close();
			
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
	}

	public int getCnt() {		
		return cnt;
	}

	public String getValue(String key) {
		String[] keys = key.split("\\.");
		
		if(keys.length>0 && row != null){
			return getValue(row, keys);
		}
		
		return null;
	}
	
	private String getValue(DBObject data, String[] keys){
		String key = keys[0];	
		Object val = null;
		
		if(key.matches("[0-9]+") && data.toString().trim().startsWith("[")){
			int i = Integer.valueOf(key);
			BasicDBList list = (BasicDBList)data;
			 
			if(list.size()<i)
				return null;
			 
			val = list.get(i);			
		} else {
			val = data.get(key);
		}
		
		if(val != null && keys.length>1){
			return getValue((DBObject)val, Arrays.copyOfRange(keys, 1, keys.length));
		}
		
		return (val == null) ? null : val.toString().trim();
	}
}
