package com.college.scheduler.DB;
import java.sql.*;

import javax.swing.JOptionPane;
public class mySqlConnection {
	
	static final private String USER = "root";
	static final private String PASS = "shely9188";
	static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final private String DB_URL = "jdbc:mysql://localhost/Scheduler";
	private static Connection conn = null;
	public static Statement stmt = null;

	public static Connection dbConnector(){
		
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			return conn;
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
}
