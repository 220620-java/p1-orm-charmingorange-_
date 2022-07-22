package com.revature.pepinUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
	
	private static ConnectionUtil connUtil;
	private Properties props;
	
	//-------------------CONNECT-TO-PROPERTIES-FILE----------------------//
	
	private ConnectionUtil() {
		
		props = new Properties();
		
		InputStream propsFile = ConnectionUtil.class.getClassLoader()
				                .getResourceAsStream("database.properties");
		
		try {
			props.load(propsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//--------------------END-CONNECT-PROPERTIES-FILE-------------------//
	
	
	public static synchronized ConnectionUtil getConnectionUtil() {
		if (connUtil == null) {
			connUtil = new ConnectionUtil();
		}
		return connUtil;
	}
	
	
	public Connection getConnection() {
		
		Connection conn = null;
		String dbUrl = props.getProperty("url");
		String dbUser = props.getProperty("usr");
		String dbPass = props.getProperty("psw");
		
		//System.out.println("URL: " + dbUrl);
		//System.out.println("USR: " + dbUser);
		//System.out.println("PSW: " + dbPass);
		
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
		} catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return conn;
		
	}
	

}
