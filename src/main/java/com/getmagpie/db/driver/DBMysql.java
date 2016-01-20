package com.getmagpie.db.driver;

public class DBMysql extends DBPostgresql {
	public DBMysql(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
