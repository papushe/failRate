package com.college.scheduler.DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

//Data Access Object implement
public class DAOImpl implements DAO {
	
	static final private String USER = "root";
	static final private String PASS = "shely9188";
	static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final private String DB_URL = "jdbc:mysql://localhost/Scheduler?useSSL=false";
	private static Connection conn = null;
	private static Statement stmt = null;

	public Connection createConection() {
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			
			String Class =
					"CREATE TABLE IF NOT EXISTS Class " +
					" (classNumber INTEGER not NULL, " +
					" floor INTEGER not NULL, " +
					" buildingNumber INTEGER not NULL, " +
					" CONSTRAINT pk_classNumber PRIMARY KEY (classNumber)); ";
			
			String Lecturer =
					"CREATE TABLE IF NOT EXISTS Lecturer " +
					"(lecturerId INTEGER not NULL, " +
					" address_city VARCHAR(255), " +
					" address_street VARCHAR(255), " +
					" name_first VARCHAR(255), " +
					" name_last VARCHAR(255), " +
					" DOB_month INTEGER not NULL, " +
					" DOB_year INTEGER not NULL, " +
					" DOB_day INTEGER not NULL, " +
					" CONSTRAINT pk_lecturerId PRIMARY KEY (lecturerId)); ";
			
			String Course =
					"CREATE TABLE IF NOT EXISTS Course " +
					"(courseId INTEGER not NULL PRIMARY KEY, " +
					" year VARCHAR(255), " +
					" courseName VARCHAR(255), " +
					" semester VARCHAR(255), " +
					" hours INTEGER not NULL, " +
					" classNumber INTEGER not NULL, " +
					" lecturerId INTEGER not NULL, " + 
					" courseDay ENUM('Sun','Mon','Tue','Wed','Thu','Fri', 'Sat'), " +
					" startTime INTEGER not NULL, " +
					" CONSTRAINT fk_classNumber " +
					" foreign key (classNumber) references Class(classNumber) " +
					" ON DELETE CASCADE " + 
					" ON UPDATE CASCADE, " +
					" CONSTRAINT fk_lecturerId1 " +
					" foreign key (lecturerId) references Lecturer(lecturerId) " +
					" ON DELETE CASCADE " + 
					" ON UPDATE CASCADE); ";
			
			String Phone =
					"CREATE TABLE IF NOT EXISTS Phone " +
					"(phoneNumber VARCHAR(255) PRIMARY KEY, " +
					" lecturerId INTEGER not NULL, " +
					" CONSTRAINT fk_lecturerId2 " +
					" foreign key (lecturerId) references Lecturer(lecturerId) " +
					" ON DELETE CASCADE " + 
					" ON UPDATE CASCADE);";
			
			String triggerUpperCase="CREATE TRIGGER upperCase BEFORE INSERT ON Course FOR EACH ROW SET NEW.courseName = UPPER(NEW.courseName)";
			
			conn.setAutoCommit(false); // transaction block start
			stmt.executeUpdate(Class); // data is not committed yet
			stmt.executeUpdate(Lecturer); // data is not committed yet
			stmt.executeUpdate(Course); // data is not committed yet
			stmt.executeUpdate(Phone);// data is not committed yet
			stmt.executeUpdate(triggerUpperCase);
			conn.commit(); // transaction block end
			
			}catch(SQLException se){
				JOptionPane.showMessageDialog(null, se);
				try {
					JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
					conn.rollback(); // back to previous state 
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e);
				}
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, e);
				}
		return conn;
	}
	@Override
	public void closeConection() {
		try{
			if(stmt!=null)
				conn.close();
			}catch(SQLException se){
				JOptionPane.showMessageDialog(null, se);
			}
		try{
			if(conn!=null)
				conn.close();
			}catch(SQLException e){
				JOptionPane.showMessageDialog(null, e);
				}
	}
	public void generateDefaultData(){		
		String insertClasses = "INSERT INTO Class (classNumber, floor, buildingNumber) " +
				"VALUES (2001, 2, 1),(2002, 2, 1),(3001, 3, 1 ),(3003, 3, 1),(4001, 4,1),(4004, 4, 1),(5001, 5, 1),(5005, 5, 1),(6001, 6, 1),(6006, 5, 1); ";
		
		String insertCourses = "INSERT INTO Course (courseId, year, courseName, semester, hours, classNumber, lecturerId, courseDay, startTime) " +
				"VALUES (1211, 'First', 		'AlgebraA', 		'A', 2, 2001, 123456789, 'Sun', 8),"
					 + "(1231, 'Second', 	'AlgebraA', 		'B', 2, 2002, 123456781, 'Mon', 9),"
					 + "(1232, 'Second', 	'HistoryA', 		'A', 1, 3001, 123456782, 'Sun', 10),"
					 + "(1233, 'Third', 	'HistoryB', 		'B', 2, 3003, 123456783, 'Mon', 12),"
					 + "(1234, 'First', 	'ComputersA', 		'B', 3, 2001, 123456784, 'Tue', 10),"
					 + "(1235, 'Second', 	'ComputersB', 		'C', 3, 4004, 123456785, 'Tue', 8),"
					 + "(1236, 'First', 	'CommunicationA', 	'C', 3 ,2001, 123456786, 'Wed', 8),"
					 + "(1237, 'Third', 	'CommunicationB', 	'C', 3 ,5005, 123456787, 'Thu', 12),"
					 + "(1238, 'First', 	'LiteratureA', 		'A', 2, 6001, 123456788, 'Thu', 16),"
					 + "(1239, 'Third',	 	'LiteratureB', 		'A', 3, 2001, 123456799, 'Thu', 20); ";
			
		String insertLecturers = "INSERT INTO Lecturer (lecturerId, address_city, address_street, name_first, name_last, DOB_month,DOB_year,DOB_day) " +
				"VALUES (123456789, 'Tel Aviv',  'Almog',    	'Moshe', 'Peretz',    11, 1985, 2),"
					 + "(123456781, 'Tel Aviv',  'Tenenbum', 	'Eyal',   'golan',    12, 1999, 3),"
					 + "(123456782, 'Rehovot',   'Dalia',    	'Amit',   'Shely',    10, 1980, 9),"
					 + "(123456783, 'Tel Aviv',  'Mamila',   	'Naor',   'Haimov',   2,  1970, 11),"
					 + "(123456784, 'Rehovot',   'Motzkin',  	'Tal',    'Kot',      5,  1970, 31),"
					 + "(123456785, 'Ramat gan', 'Dizingof', 	'Shlomi', 'Lougassi', 11, 1970, 22),"
					 + "(123456786, 'Ramle', 	 'Azrieli',     'Terry',  'Meir',     9,  1973, 27),"
					 + "(123456787, 'Afula', 	 'Aloof Sade',  'Naama',  'Lapidot',  4,  1979, 12),"
					 + "(123456788, 'Sitriyya',  'Bograshov', 	'Shlomi', 'Shloosh',  1,  2000, 21),"
					 + "(123456799, 'Ramat gan', 'Neve tzedek', 'Tomer',  'Trump',    6,  1986, 10); ";
		
		String insertPhone = "INSERT INTO Phone (phoneNumber, lecturerId) " +
				"VALUES ('050692234', 123456789),"
				           + "('050692235', 123456781),"
				           + "('050692236', 123456782),"
				           + "('050692237', 123456783),"
				           + "('050692238', 123456784),"
				           + "('050692239', 123456785),"
				           + "('050692231', 123456786),"
				           + "('050692232', 123456787),"
				           + "('050692233', 123456788),"
				           + "('050692214', 123456799); ";
		
		try {
			stmt.executeUpdate(insertClasses);
			stmt.executeUpdate(insertLecturers);
			stmt.executeUpdate(insertCourses);
			stmt.executeUpdate(insertPhone);
			
			conn.commit();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
			try {
				JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
				conn.rollback(); // back to previous state 
			} catch (SQLException e2) {
				JOptionPane.showMessageDialog(null, e2);
			}
		}
	}
	public void dropTables() {
		String dropTable="DROP TABLE IF EXISTS Scheduler.Phone,Scheduler.Course, Scheduler.Class, Scheduler.Lecturer";
		try {
			stmt.executeUpdate(dropTable);
			conn.commit();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
			try {
				JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
				conn.rollback(); // back to previous state 
			} catch (SQLException e2) {
				JOptionPane.showMessageDialog(null, e2);
			}
		}
	}
}