package com.college.scheduler.DB;
//STEP 1. Import required packages
import java.sql.*;

public class Scheduler {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/Scheduler";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "shely9188";
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		
		try{
			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);
			//STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");
			
			//STEP 4: Execute a query
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();
			
			String Class =
					"CREATE TABLE Class " +
					"(floor INTEGER not NULL, " +
					" class_number INTEGER not NULL PRIMARY KEY, " +
					" building_number INTEGER not NULL); ";

			String inserClass = "INSERT INTO Class (floor, class_number, building_number) " +
								"VALUES (1, 1, 1),(2, 2, 2),(3, 3, 3),(4, 4, 4),(5, 5, 5),(6, 6, 6),(7, 7, 7),(8, 8, 8),(9, 9, 9),(10, 10, 10); ";
			
			String Course =
					"CREATE TABLE Course " +
					"(course_id INTEGER not NULL PRIMARY KEY, " +
					" year INTEGER not NULL, " +
					" course_name VARCHAR(255), " +
					" semester VARCHAR(255), " +
					" houres INTEGER not NULL, " +
					" building_number INTEGER not NULL); ";

			String Lecturer =
					"CREATE TABLE Lecturer " +
					"(lecturer_id INTEGER not NULL PRIMARY KEY, " +
					" address_city VARCHAR(255), " +
					" address_street_streetName VARCHAR(255), " +
					" name_firstName VARCHAR(255), " +
					" name_lastName VARCHAR(255), " +
					" dateOfBirth_month VARCHAR(255), " +
					" dateOfBirth_year VARCHAR(255), " +
					" dateOfBirth_day VARCHAR(255)); ";

			String Phone =
					"CREATE TABLE Phone " +
					"(phoneNumber INTEGER not NULL PRIMARY KEY, " +
					" lecturer_id INTEGER not NULL, " +
					" foreign key (lecturer_id) references Lecturer(lecturer_id)); ";
	
			String Lecture =
					"CREATE TABLE Lecture " +
					"(lecturer_id INTEGER not NULL, " +
					" course_id INTEGER not NULL, " +
					" PRIMARY KEY (lecturer_id, course_id), " +
					" foreign key (course_id) references Course(course_id)," +
					" foreign key (lecturer_id) references Lecturer(lecturer_id)); ";
			
			String Locate =
					"CREATE TABLE Locate " +
					"(class_number INTEGER not NULL, " +
					" course_id INTEGER not NULL, " +
					" PRIMARY KEY (class_number, course_id), " +
					" foreign key (course_id) references Course(course_id)," +
					" foreign key (class_number) references Class(class_number)); ";
			
			stmt.executeUpdate(Class);
			stmt.executeUpdate(inserClass);
			stmt.executeUpdate(Course);
			stmt.executeUpdate(Lecturer);
			stmt.executeUpdate(Phone);
			stmt.executeUpdate(Lecture); 
			stmt.executeUpdate(Locate);
	
			System.out.println("Created table in given database...");
			}catch(SQLException se){
				//Handle errors for JDBC
				se.printStackTrace();
				}catch(Exception e){
					//Handle errors for Class.forName
					e.printStackTrace();
				}
			finally{
				//finally block used to close resources
				try{
					if(stmt!=null)
						conn.close();
					}catch(SQLException se){
						
					}// do nothing
				try{
					if(conn!=null)
						conn.close();
					}catch(SQLException e){
						e.printStackTrace();
						}//end finally try
				}//end try
				System.out.println("Goodbye!");
			}//end main
}//end JDBCExample