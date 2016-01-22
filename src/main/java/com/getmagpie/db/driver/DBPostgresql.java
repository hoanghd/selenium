package com.getmagpie.db.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.getmagpie.data.Result;
import com.getmagpie.util.ComUtil;

public class DBPostgresql implements DBAbstract {
	protected int cnt = 0;
	protected Connection conn;
	protected Map<String, String> row;
		
	public DBPostgresql(){
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Result connect(String url) {		
		try {
			conn = DriverManager.getConnection(url);
			return Result.success();
		} catch (SQLException e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
	}

	public Result count(String sql, Map<String, String> params) {
		try {
			PreparedStatement stmt = null;
			Statement st = null;
			ResultSet rs = null;
			
			if(params.isEmpty()) {
				stmt = conn.prepareStatement(sql);
				
                for(Entry<String, String> entry : params.entrySet()) {
                	int key = Integer.valueOf(entry.getKey());
                	List<String> value = ComUtil.matcher("\\[([a-z0-9]+)\\](.+)", entry.getValue());
                	if(value.size() == 2){
                		if(value.get(0).equals("string")){
                			stmt.setString(key, value.get(1));
                		}
                		else if(value.get(0).equals("int")){
                			stmt.setInt(key, Integer.valueOf(value.get(1)));
                		}
                		else if(value.get(0).equals("float")){
                			stmt.setFloat(key, Float.valueOf(value.get(1)));                			
                		}
                		else if(value.get(0).equals("date")){
                			if(value.get(1).equals("now()")){
                				stmt.setDate(key, new java.sql.Date(System.currentTimeMillis()));
                			} 
                			else{
                				stmt.setDate(key, java.sql.Date.valueOf(value.get(1)));
                			}
                		}
                		else if(value.get(0).equals("timestamp")){
                			if(value.get(1).equals("now()")){
                				stmt.setTimestamp(key, new java.sql.Timestamp(System.currentTimeMillis()));
                			} 
                			else{
                				stmt.setTimestamp(key, java.sql.Timestamp.valueOf(value.get(1)));
                			}
                		}
                	}	
                }
                
                rs = stmt.executeQuery();   
                
			} else {
				st = conn.createStatement();
				rs = st.executeQuery(sql);
			}
			
			if(rs != null){
				if(rs.next()){
					cnt = rs.getInt(1);
				}
			}
			
			rs.close();			
			if(st != null)
				st.close();
			
			if(stmt != null)
				stmt.close();
			
			return Result.success();
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return Result.failure(e.getMessage());
	    }
	}

	public Result find(String sql, Map<String, String> params) {
		try {		  
			row = new HashMap<String, String>();
			
			PreparedStatement stmt = null;
			Statement st = null;
			ResultSet rs = null;
			
			if(!params.isEmpty()) {
				stmt = conn.prepareStatement(sql);
				
                for(Entry<String, String> entry : params.entrySet()) {
                	int key = Integer.valueOf(entry.getKey());
                	List<String> value = ComUtil.matcher("\\[([a-z0-9]+)\\](.+)", entry.getValue());
                	if(value.size() == 2){
                		if(value.get(0).equals("string")){
                			stmt.setString(key, value.get(1));
                		}
                		else if(value.get(0).equals("int")){
                			stmt.setInt(key, Integer.valueOf(value.get(1)));
                		}
                		else if(value.get(0).equals("date")){
                			if(value.get(1).equals("now()")){
                				stmt.setDate(key, new java.sql.Date(System.currentTimeMillis()));
                			} 
                			else{
                				stmt.setDate(key, java.sql.Date.valueOf(value.get(1)));
                			}
                		}
                		else if(value.get(0).equals("timestamp")){
                			if(value.get(1).equals("now()")){
                				stmt.setTimestamp(key, new java.sql.Timestamp(System.currentTimeMillis()));
                			} 
                			else{
                				stmt.setTimestamp(key, java.sql.Timestamp.valueOf(value.get(1)));
                			}
                		}
                	}	
                }
                
                rs = stmt.executeQuery();   
                
			} else {
				st = conn.createStatement();
				rs = st.executeQuery(sql);
			}
	      
			if(rs != null){
				if(rs.next()){
					ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
					int count = metaData.getColumnCount();
					for(int i=1; i<=count; i++){	
						row.put(metaData.getColumnLabel(i).toLowerCase(), rs.getString(i));
					}
				}
			}
	      
			rs.close();			
			if(st != null)
				st.close();
			
			if(stmt != null)
				stmt.close();
	      
			return Result.success();
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return Result.failure(e.getMessage());
	    }
	}

	public Result close() {
		try {
			if(conn != null)
				conn.close();
			
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
		return ((row != null) ? row.get(key) : "");
	}
}
