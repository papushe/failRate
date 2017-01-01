package com.college.scheduler.GUI;
import com.college.scheduler.DB.*;
import com.mysql.jdbc.PreparedStatement;
import net.proteanit.sql.DbUtils;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.*;
import java.awt.*;

public class Scheduler extends JFrame {
	private JPanel contentPane,ClassPanel,PhonePanel;
	private JLabel lblClass,lblLecturer, lblCourse,lblClassFloor, lblClassNumber, lblClassBuildingNumber,lblCourseId, lblCourseName,lblLecturerDobYear, lblLecturerDobDay;
	private JLabel lblCourseYear, lblCourseHours,lblCourseSemester, lblLecturerId, lblLecturerAddressCity, lblLecturerAddressStreet, lblLecturerFirstName, lblLecturerLastName, lblLecturerDobMonth;
	private JLabel lblLecturerIdPhone,lblPhoneNumber,lblAdditionalQueries, lblNesting,lblCorreleted,joinExampleCourseLocation, lblClassNo,lblResult,lblPhone;
	private JTable tableClass, tableLecturer, tableCourse,tablePhone,additionalTable;
	private JButton btnCreateConection,btnCloseConection,btnGenerateDefaultData, btnRunCorreletedQuery, btnRunPhoneQuery, btnRunNestedQuery, btnRunJoinCourseQuery, btnRunLecturerQuery, btnRunClassQuery;
	private JButton btnRunJoinClassDetails,btnRangeDates;
	private JScrollPane classScrollPane, LecturerScrollPane, courseScrollPane,phoneScrollPane,ResultscrollPane;
	private JTextPane ClassFloor, ClassNumber, ClassBuildingNumber, CourseId, CourseName, CourseYear, CourseHours, CourseSemester,LecturerId, LecturerAddressCity, LecturerAddressStreet;
	private JTextPane LecturerFirstName, LecturerLastName, LecturerDOBMonth, LecturerDOBYear, LecturerDOBDay, PhoneNumber, PhoneLecturerId,CourseClassNo,LectureridclassTextPane, startTimeTextpane;
	private JButton btnRunCourseQuery, btnRunJoinLectureQuery;
	String[] query = {"Choose an action","Add","Update", "Delete"};
	String[] phonequery = {"Choose an action","Add", "Delete"};
	String[] houers={"Hr", "8","9","10","11","12","13","14","15","16","17","18","19","20"};
	public enum days {Day,Sun,Mon,Tue,Wed,Thu,Fri};
	private JComboBox ClassComboBox, LecturerComboBox, CourseComboBox, PhoneComboBox;
	public String courseQuery , classQuery, lecturerQuery, phoneQuery,phonePK;
	public String courseAdd = "insert into Course (courseId, year, courseName, semester, hours, classNumber, lecturerId, courseDay,startTime)  values (? ,?, ?, ?, ?, ?, ?, ?,?)", 
						classAdd = "insert into Class (classNumber, floor, buildingNumber) values (? ,?, ?)", 
			     		lecturerAdd = "insert into Lecturer (lecturerId, address_city, address_street, name_first, name_last, DOB_month,DOB_year,DOB_day) values (? ,?, ?, ?, ?, ?, ?, ?)",
						phoneAdd = "insert into Phone (phoneNumber, lecturerId)  values (? ,?)";
	public String courseUpdate = "UPDATE Course set courseId = ?, year = ?, courseName = ?, semester=?, hours=?, classNumber =?, lecturerId=?, courseDay=?, startTime=? WHERE courseId=?",
						lecturerUpdate = "UPDATE Lecturer set lecturerId = ?, address_city = ?, address_street = ?, name_first=?, name_last=?, DOB_month=?,DOB_year=?,DOB_day=?  WHERE lecturerId=?",
						classUpdate = "UPDATE Class set classNumber = ?, floor = ?, buildingNumber=? WHERE classNumber=?";
	public String classDelete ="delete from Class WHERE classNumber = ?",
						courseDelete ="delete from Course WHERE courseId = ?",
						lecturerDelete ="delete from Lecturer WHERE lecturerId = ?",
						phoneDelete ="delete from Phone WHERE phoneNumber = ?";
	public String nestedSubQuery = "SELECT * FROM Phone as p WHERE lecturerId IN (SELECT lecturerId FROM Lecturer AS l WHERE address_city LIKE '%Tel Aviv%');";
	public String joinLocation ="SELECT Course.courseId, Course.courseName, Class.classNumber, Class.floor, Class.buildingNumber FROM Course INNER JOIN Class ON Course.classNumber=Class.classNumber";		
	public String joinLecture ="SELECT Course.courseId, Course.courseName, Class.classNumber, Lecturer.name_first, Lecturer.name_last FROM Course INNER JOIN Class ON Course.classNumber=Class.classNumber INNER JOIN Lecturer ON Lecturer.lecturerId=Course.lecturerId";
	public String correlated = "SELECT DISTINCT courseName FROM Course AS C WHERE C.semester='A' AND C.courseDay='Sun' AND exists( SELECT* FROM Course as T WHERE T.semester='B' AND T.courseDay='Mon' AND C.courseName=T.courseName);";
	public String classDetails = "SELECT Course.courseId, Course.courseName, Course.lecturerId, Lecturer.name_first, Lecturer.name_last from Course LEFT JOIN Lecturer ON Course.lecturerId = Lecturer.lecturerId WHERE Course.classNumber = 2001;";
	public String lecturerDetail = "SELECT  Class.classNumber, Course.courseName, Course.courseId, Course.startTime, Course.hours FROM Class INNER JOIN Course ON Course.lecturerId=123456781 AND Class.classNumber=course.classNumber;";
	public String scheduler="SELECT Course.year, Course.courseName, Course.semester,Course.courseDay,Course.startTime,Course.classNumber,Lecturer.name_first,Lecturer.name_last FROM Course LEFT JOIN Lecturer ON Course.lecturerId=Lecturer.lecturerId ORDER BY year;";
	
	
	public String rangeDates2="SELECT Lecturer.name_first, Lecturer.name_last, Course.courseName FROM Lecturer INNER JOIN Course on Lecturer.lecturerId = Course.lecturerId WHERE not ((courseDay+0 <= ? and startTime < ? || Course.courseDay < ?) OR (courseDay+0 = ? and startTime >= ? || courseDay > ?))";
	public String rangeDates="SELECT Lecturer.name_first, Lecturer.name_last, Course.courseName FROM Lecturer INNER JOIN Course on Lecturer.lecturerId = Course.lecturerId WHERE not ((courseDay+0 <= 1 and startTime < 10 || Course.courseDay < 1) OR (courseDay+0 = 3 and startTime >= 10 || courseDay > 3))";
	
	DAOImpl DAO = new DAOImpl();
	Connection conn=null;
	int classPK,lecturerPK,coursePK;

	Enum<days> firstDays;
	Enum<days> secondDays;
	public String firstHouers, secondHouers;
	PreparedStatement preparedStmtAdd, preparedStmtUpdate, preparedStmtDelete,additionalQueries, preparedStmtDaysAndHouers;
	private JLabel joinExampleLecture;
	private JLabel lblCourseDay;
	private JTextPane CourseDay;
	private JButton btnRunJoinLecturerStart;
	private JLabel lblLecturerDetails;
	private JPanel coursePanel;
	private JButton btnScheduler;
	private JLabel lblWeeklyScheduler;
	private JButton btnDropTables;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scheduler frame = new Scheduler();
					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
	});
	}	
	public Scheduler() throws SQLException {
		setResizable(false);
		setTitle("College Scheduler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1400, 800);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(224, 255, 255));
		contentPane.setPreferredSize(new Dimension(1, 1));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
			
		//Create Connection
		btnCreateConection = new JButton("Create Conection");
		btnCreateConection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			conn =	DAO.createConection();
				try {			
					String queryClass = "select * from Class";
					PreparedStatement pstClass;
					pstClass = (PreparedStatement)conn.prepareStatement(queryClass);
					ResultSet rsClass=pstClass.executeQuery();
					tableClass.setModel(DbUtils.resultSetToTableModel(rsClass));
					String queryLecturer = "select * from Lecturer";
					PreparedStatement pstLecturer = (PreparedStatement) conn.prepareStatement(queryLecturer);
					ResultSet rsLecturer=pstLecturer.executeQuery();
					tableLecturer.setModel(DbUtils.resultSetToTableModel(rsLecturer));
					String queryCourse = "select * from Course";
					PreparedStatement pstCourse = (PreparedStatement)conn.prepareStatement(queryCourse);
					ResultSet rsCourse=pstCourse.executeQuery();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rsCourse));
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement)conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					conn.commit();
					
					btnCloseConection.setEnabled(true);
					btnCreateConection.setEnabled(false);
					btnRunClassQuery.setEnabled(true);
					btnRunCorreletedQuery.setEnabled(true);
					btnRunCourseQuery.setEnabled(true);
					btnRunJoinCourseQuery.setEnabled(true);
					btnRunLecturerQuery.setEnabled(true);
					btnRunPhoneQuery.setEnabled(true);
					btnRunNestedQuery.setEnabled(true);
					btnGenerateDefaultData.setEnabled(true);
					btnRunJoinLectureQuery.setEnabled(true);
					btnRunJoinClassDetails.setEnabled(true);
					btnRunJoinLecturerStart.setEnabled(true);
					btnRangeDates.setEnabled(true);
					btnScheduler.setEnabled(true);
					btnDropTables.setEnabled(true);
					JOptionPane.showMessageDialog(null, "Connected database successfully");
					
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnCreateConection.setBounds(3, 6, 150, 29);
		contentPane.add(btnCreateConection);
		
		//Close Connection
		btnCloseConection = new JButton("Close Conection");
		btnCloseConection.setEnabled(false);
		btnCloseConection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DAO.closeConection();
				btnCloseConection.setEnabled(false);
				btnCreateConection.setEnabled(true);
				btnRunClassQuery.setEnabled(false);
				btnRunCorreletedQuery.setEnabled(false);
				btnRunCourseQuery.setEnabled(false);
				btnRunJoinCourseQuery.setEnabled(false);
				btnRunLecturerQuery.setEnabled(false);
				btnRunPhoneQuery.setEnabled(false);
				btnRunNestedQuery.setEnabled(false);
				btnGenerateDefaultData.setEnabled(false);
				btnRunJoinLectureQuery.setEnabled(false);
				btnRunJoinClassDetails.setEnabled(false);
				btnRunJoinLecturerStart.setEnabled(false);
				btnRangeDates.setEnabled(false);
				btnScheduler.setEnabled(false);
				btnDropTables.setEnabled(false);
				JOptionPane.showMessageDialog(null, "Close Connection to database successfully...");
			}
		});
		btnCloseConection.setBounds(458, 6, 150, 29);
		contentPane.add(btnCloseConection);
		
		//Generate Default Data
		btnGenerateDefaultData = new JButton("Generate Default Data");
		btnGenerateDefaultData.setEnabled(false);
		btnGenerateDefaultData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DAO.generateDefaultData();
				try{
					String queryClass = "select * from Class";
					PreparedStatement pstClass = (PreparedStatement)conn.prepareStatement(queryClass);
					ResultSet rsClass=pstClass.executeQuery();
					tableClass.setModel(DbUtils.resultSetToTableModel(rsClass));
					String queryLecturer = "select * from Lecturer";
					PreparedStatement pstLecturer = (PreparedStatement) conn.prepareStatement(queryLecturer);
					ResultSet rsLecturer=pstLecturer.executeQuery();
					tableLecturer.setModel(DbUtils.resultSetToTableModel(rsLecturer));		
					String query = "select * from Course";
					PreparedStatement pst = (PreparedStatement)conn.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rs));
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement)conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					conn.commit();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					JOptionPane.showMessageDialog(null,"Generate defalut data was performed successfully");
					btnGenerateDefaultData.setEnabled(false);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, ex);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnGenerateDefaultData.setBounds(147, 6, 174, 29);
		contentPane.add(btnGenerateDefaultData);
		
		//drop tables
		btnDropTables = new JButton("Drop Tables");
		btnDropTables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DAO.dropTables();
				try {
					DefaultTableModel model = new DefaultTableModel();
					tableClass.setModel(model);
					tableLecturer.setModel(model);
					tableCourse.setModel(model);
					tablePhone.setModel(model);
					additionalTable.setModel(model);
					conn.commit();
					JOptionPane.showMessageDialog(null, "Drop Tables data was performed successfully");
					btnDropTables.setEnabled(false);
					// btnCloseConection.setEnabled(false);
					btnGenerateDefaultData.setEnabled(false);
					btnCreateConection.setEnabled(true);
					lblResult.setText("");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnDropTables.setEnabled(false);
		btnDropTables.setBounds(314, 6, 150, 29);
		contentPane.add(btnDropTables);

		//Class
		ClassComboBox = new JComboBox(query);
		ClassComboBox.setSelectedIndex(1);
		ClassComboBox.setSelectedItem(query[0]);
		ClassComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassComboBox.addActionListener(this);
				if(e.getSource() == ClassComboBox){
					JComboBox JCB = (JComboBox)e.getSource();
					classQuery = (String)JCB.getSelectedItem();
				}
			}
		});
		ClassComboBox.setBounds(1145, 495, 230, 25);
		contentPane.add(ClassComboBox);
		tableClass = new JTable();
		tableClass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indexUpdaeTable = tableClass.getSelectedRow();
				classPK =Integer.parseInt( tableClass.getModel().getValueAt(indexUpdaeTable, 0).toString());
				ClassNumber.setText(tableClass.getModel().getValueAt(indexUpdaeTable, 0).toString());
				ClassFloor.setText(tableClass.getModel().getValueAt(indexUpdaeTable, 1).toString());
				ClassBuildingNumber.setText(tableClass.getModel().getValueAt(indexUpdaeTable,2).toString());
			}
		});
		tableClass.setSelectionForeground(Color.BLACK);
		tableClass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		tableClass.setSelectionBackground(Color.GREEN);
		classScrollPane = new JScrollPane();
		classScrollPane.setViewportView(tableClass);
		classScrollPane.setBounds(1145, 555, 250, 219);
		contentPane.add(classScrollPane);
		lblClass = new JLabel("Class");
		lblClass.setFont(new Font("Courier", Font.PLAIN, 20));
		lblClass.setBounds(1151, 526, 96, 29);
		contentPane.add(lblClass);
		lblClassFloor = new JLabel("Class floor");
		lblClassFloor.setBounds(1149, 398, 84, 16); 
		contentPane.add(lblClassFloor);
		lblClassNumber = new JLabel("Class number (P)");
		lblClassNumber.setBounds(1149, 374, 136, 16);
		contentPane.add(lblClassNumber);
		lblClassBuildingNumber = new JLabel("Class building no.");
		lblClassBuildingNumber.setBounds(1149, 422, 136, 16);
		contentPane.add(lblClassBuildingNumber);
		ClassFloor = new JTextPane(); 
		ClassFloor.setBounds(1273, 394, 117, 20);
		contentPane.add(ClassFloor);
		ClassNumber = new JTextPane();
		ClassNumber.setBounds(1273, 370, 117, 20);
		contentPane.add(ClassNumber);
		ClassBuildingNumber = new JTextPane();
		ClassBuildingNumber.setBounds(1273, 418, 117, 20);
		contentPane.add(ClassBuildingNumber);
		btnRunClassQuery = new JButton("Run Query");
		btnRunClassQuery.setEnabled(false);
		btnRunClassQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					preparedStmtAdd = (PreparedStatement) conn.prepareStatement(classAdd);
					preparedStmtAdd.setInt(1, Integer.parseInt(ClassNumber.getText()));
					preparedStmtAdd.setInt(2, Integer.parseInt(ClassFloor.getText()));
					preparedStmtAdd.setInt(3, Integer.parseInt(ClassBuildingNumber.getText()));	
					preparedStmtUpdate = (PreparedStatement) conn.prepareStatement(classUpdate);
					preparedStmtUpdate.setInt(1, Integer.parseInt(ClassNumber.getText()));
					preparedStmtUpdate.setInt(2, Integer.parseInt(ClassFloor.getText()));
					preparedStmtUpdate.setInt(3, Integer.parseInt(ClassBuildingNumber.getText()));
					preparedStmtUpdate.setInt(4, classPK);
					preparedStmtDelete = (PreparedStatement) conn.prepareStatement(classDelete);
					preparedStmtDelete.setInt(1, classPK);	
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null,e2);
				}
				switch (classQuery){
				case "Add": try {
					preparedStmtAdd.executeUpdate();
					String queryClass = "select * from Class";
					PreparedStatement pstClass = (PreparedStatement)conn.prepareStatement(queryClass);
					ResultSet rsClass=pstClass.executeQuery();
					conn.commit();
					tableClass.setModel(DbUtils.resultSetToTableModel(rsClass));
					JOptionPane.showMessageDialog(null,"Adding was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Update": try {
					preparedStmtUpdate.executeUpdate();
					String queryClass = "select * from Class";
					PreparedStatement pstClass = (PreparedStatement)conn.prepareStatement(queryClass);
					ResultSet rsClass=pstClass.executeQuery();
					conn.commit();
					tableClass.setModel(DbUtils.resultSetToTableModel(rsClass));
					String queryCourse = "select * from Course";
					PreparedStatement pstCourse = (PreparedStatement)conn.prepareStatement(queryCourse);
					ResultSet rsCourse=pstCourse.executeQuery();
					conn.commit();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rsCourse));
					JOptionPane.showMessageDialog(null,"Updating was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Delete":  try {
					preparedStmtDelete.executeUpdate();
					String queryClass = "select * from Class";
					PreparedStatement pstClass = (PreparedStatement)conn.prepareStatement(queryClass);
					ResultSet rsClass=pstClass.executeQuery();
					conn.commit();
					tableClass.setModel(DbUtils.resultSetToTableModel(rsClass));					
					JOptionPane.showMessageDialog(null,"Deleting was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				}
			}
		});
		btnRunClassQuery.setBounds(1220, 526, 100, 29);
		contentPane.add(btnRunClassQuery);
		lblClassNo = new JLabel("ClassNo");
		lblClassNo.setBounds(290, 446, 58, 16);
		contentPane.add(lblClassNo);
		tableClass.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		ClassPanel = new JPanel();
		ClassPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		ClassPanel.setBounds(1145, 358, 250, 198);
		ClassPanel.setBackground(new Color(176, 224, 230));
		contentPane.add(ClassPanel);
		
		//Lecturer
		LecturerComboBox = new JComboBox(query);
		LecturerComboBox.setSelectedIndex(1);
		LecturerComboBox.setSelectedItem(query[0]);
		LecturerComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LecturerComboBox.addActionListener(this);
				if(e.getSource() == LecturerComboBox){
					JComboBox JCB = (JComboBox)e.getSource();
					lecturerQuery = (String)JCB.getSelectedItem();
				}
			}
		});
		LecturerComboBox.setBounds(515, 495, 230, 25);
		contentPane.add(LecturerComboBox);
		tableLecturer = new JTable();
		tableLecturer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indexUpdaeTable = tableLecturer.getSelectedRow();
				lecturerPK =Integer.parseInt( tableLecturer.getModel().getValueAt(indexUpdaeTable, 0).toString());
				LecturerId.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 0).toString());
				LecturerAddressCity.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 1).toString());
				LecturerAddressStreet.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable,2).toString());
				LecturerFirstName.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 3).toString());
				LecturerLastName.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 4).toString());
				LecturerDOBMonth.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 5).toString());
				LecturerDOBYear.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 6).toString());
				LecturerDOBDay.setText(tableLecturer.getModel().getValueAt(indexUpdaeTable, 7).toString());
			}
		});
		tableLecturer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		tableLecturer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableLecturer.setSelectionBackground(Color.GREEN);
		tableLecturer.setSelectionForeground(Color.BLACK);
		LecturerScrollPane = new JScrollPane();
		LecturerScrollPane.setViewportView(tableLecturer);
		LecturerScrollPane.setBounds(515, 555, 624, 219);
		contentPane.add(LecturerScrollPane);
		lblLecturer = new JLabel("Lecturer");
		lblLecturer.setFont(new Font("Courier", Font.PLAIN, 20));
		lblLecturer.setBounds(521, 526, 136, 29);
		contentPane.add(lblLecturer);
		LecturerId = new JTextPane(); 
		LecturerId.setBounds(680, 370, 117, 20);
		contentPane.add(LecturerId);
		LecturerAddressCity = new JTextPane();
		LecturerAddressCity.setBounds(680, 394, 117, 20);
		contentPane.add(LecturerAddressCity);
		LecturerAddressStreet = new JTextPane();
		LecturerAddressStreet.setBounds(680, 418, 117, 20);
		contentPane.add(LecturerAddressStreet);
		LecturerFirstName = new JTextPane();
		LecturerFirstName.setBounds(680, 442, 117, 20);
		contentPane.add(LecturerFirstName);
		LecturerLastName = new JTextPane();
		LecturerLastName.setBounds(1015, 370, 117, 20);
		contentPane.add(LecturerLastName);
		LecturerDOBMonth = new JTextPane();
		LecturerDOBMonth.setBounds(1015, 394, 117, 20);
		contentPane.add(LecturerDOBMonth);
		LecturerDOBYear = new JTextPane();
		LecturerDOBYear.setBounds(1015, 418, 117, 20);
		contentPane.add(LecturerDOBYear);
		LecturerDOBDay = new JTextPane();
		LecturerDOBDay.setBounds(1015, 442, 117, 20);
		contentPane.add(LecturerDOBDay);
		lblLecturerId = new JLabel("Lecturer id (P)");
		lblLecturerId.setBounds(519, 374, 108, 16);
		contentPane.add(lblLecturerId);
		lblLecturerAddressCity = new JLabel("Lecturer address city");
		lblLecturerAddressCity.setBounds(519, 398, 136, 16);
		contentPane.add(lblLecturerAddressCity);
		lblLecturerAddressStreet = new JLabel("Lecturer address street");
		lblLecturerAddressStreet.setBounds(519, 422, 150, 16);
		contentPane.add(lblLecturerAddressStreet);
		lblLecturerFirstName = new JLabel("Lecturer first name");
		lblLecturerFirstName.setBounds(519, 446, 136, 16);
		contentPane.add(lblLecturerFirstName);
		lblLecturerLastName = new JLabel("Lecturer last name");
		lblLecturerLastName.setBounds(870, 374, 117, 16);
		contentPane.add(lblLecturerLastName);
		lblLecturerDobMonth = new JLabel("Lecturer DOB month");
		lblLecturerDobMonth.setBounds(870, 398, 136, 16);
		contentPane.add(lblLecturerDobMonth);
		lblLecturerDobYear = new JLabel("Lecturer DOB year");
		lblLecturerDobYear.setBounds(870, 422, 136, 16);
		contentPane.add(lblLecturerDobYear);
		lblLecturerDobDay = new JLabel("Lecturer DOB day");
		lblLecturerDobDay.setBounds(870, 446, 117, 16);
		contentPane.add(lblLecturerDobDay);
		btnRunLecturerQuery = new JButton("Run Query");
		btnRunLecturerQuery.setEnabled(false);
		btnRunLecturerQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					preparedStmtAdd = (PreparedStatement) conn.prepareStatement(lecturerAdd);
					preparedStmtAdd.setInt(1, Integer.parseInt(LecturerId.getText()));
					preparedStmtAdd.setString (2, LecturerAddressCity.getText());
					preparedStmtAdd.setString(3, LecturerAddressStreet.getText());
					preparedStmtAdd.setString(4, LecturerFirstName.getText());
					preparedStmtAdd.setString(5, LecturerLastName.getText());
					preparedStmtAdd.setInt(6, Integer.parseInt(LecturerDOBMonth.getText()));
					preparedStmtAdd.setInt(7, Integer.parseInt(LecturerDOBYear.getText()));
					preparedStmtAdd.setInt(8, Integer.parseInt(LecturerDOBDay.getText()));
					preparedStmtUpdate = (PreparedStatement) conn.prepareStatement(lecturerUpdate);
					preparedStmtUpdate.setInt(1, Integer.parseInt(LecturerId.getText()));
					preparedStmtUpdate.setString (2, LecturerAddressCity.getText());
					preparedStmtUpdate.setString(3, LecturerAddressStreet.getText());
					preparedStmtUpdate.setString(4, LecturerFirstName.getText());
					preparedStmtUpdate.setString(5, LecturerLastName.getText());
					preparedStmtUpdate.setInt(6, Integer.parseInt(LecturerDOBMonth.getText()));
					preparedStmtUpdate.setInt(7, Integer.parseInt(LecturerDOBYear.getText()));
					preparedStmtUpdate.setInt(8, Integer.parseInt(LecturerDOBDay.getText()));
					preparedStmtUpdate.setInt(9, lecturerPK);
					preparedStmtDelete = (PreparedStatement) conn.prepareStatement(lecturerDelete);
					preparedStmtDelete.setInt(1, lecturerPK);
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null,e2);
				}
				switch (lecturerQuery){
				case "Add": try {
					preparedStmtAdd.executeUpdate();
					String queryLecturer = "select * from Lecturer";
					PreparedStatement pstLecturer = (PreparedStatement) conn.prepareStatement(queryLecturer);
					ResultSet rsLecturer=pstLecturer.executeQuery();
					conn.commit();
					tableLecturer.setModel(DbUtils.resultSetToTableModel(rsLecturer));
					JOptionPane.showMessageDialog(null,"Adding was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Update": try {
					preparedStmtUpdate.executeUpdate();
					String queryLecturer = "select * from Lecturer";
					PreparedStatement pstLecturer = (PreparedStatement) conn.prepareStatement(queryLecturer);
					ResultSet rsLecturer=pstLecturer.executeQuery();
					conn.commit();
					tableLecturer.setModel(DbUtils.resultSetToTableModel(rsLecturer));
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement) conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					conn.commit();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					JOptionPane.showMessageDialog(null,"Updating was  performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Delete": try {
					preparedStmtDelete.executeUpdate();
					String queryLecturer = "select * from Lecturer";
					PreparedStatement pstLecturer = (PreparedStatement) conn.prepareStatement(queryLecturer);
					ResultSet rsLecturer=pstLecturer.executeQuery();
					conn.commit();
					tableLecturer.setModel(DbUtils.resultSetToTableModel(rsLecturer));
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement) conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					conn.commit();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					JOptionPane.showMessageDialog(null,"Deleting was  performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				}
			}
		});
		btnRunLecturerQuery.setBounds(625, 526, 100, 29);
		contentPane.add(btnRunLecturerQuery);
		
		//Course
		CourseComboBox = new JComboBox(query);
		CourseComboBox.setSelectedIndex(1);
		CourseComboBox.setSelectedItem(query[0]);
		CourseComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CourseComboBox.addActionListener(this);
				if(e.getSource() == CourseComboBox){
					JComboBox JCB = (JComboBox)e.getSource();
					courseQuery = (String)JCB.getSelectedItem();
				}
			}
		});
		CourseComboBox.setBounds(5, 495, 230, 25);
		contentPane.add(CourseComboBox);
		
		tableCourse = new JTable();
		tableCourse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableCourse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indexUpdaeTable = tableCourse.getSelectedRow();	
				coursePK =Integer.parseInt( tableCourse.getModel().getValueAt(indexUpdaeTable, 0).toString());
				CourseId.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 0).toString());
				CourseYear.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 1).toString());
				CourseName.setText(tableCourse.getModel().getValueAt(indexUpdaeTable,2).toString());
				CourseSemester.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 3).toString());
				CourseHours.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 4).toString());
				CourseClassNo.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 5).toString());
				LectureridclassTextPane.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 6).toString());
				CourseDay.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 7).toString());
				startTimeTextpane.setText(tableCourse.getModel().getValueAt(indexUpdaeTable, 8).toString());
			}
		});
		tableCourse.setSelectionForeground(Color.BLACK);
		tableCourse.setSelectionBackground(Color.GREEN);
		tableCourse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		courseScrollPane = new JScrollPane();
		courseScrollPane.setViewportView(tableCourse);
		courseScrollPane.setBounds(3, 555, 507, 219);
		contentPane.add(courseScrollPane);
		lblCourse = new JLabel("Course");
		lblCourse.setFont(new Font("Courier", Font.PLAIN, 20));
		lblCourse.setBounds(7, 526, 96, 29);
		contentPane.add(lblCourse);
		btnRunCourseQuery = new JButton("Run Query");
		btnRunCourseQuery.setEnabled(false);
		btnRunCourseQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					preparedStmtAdd = (PreparedStatement) conn.prepareStatement(courseAdd);
					preparedStmtAdd.setInt(1, Integer.parseInt(CourseId.getText()));
					preparedStmtAdd.setString (2, CourseYear.getText());
					preparedStmtAdd.setString(3, CourseName.getText());
					preparedStmtAdd.setString(4, CourseSemester.getText());
					preparedStmtAdd.setInt(5, Integer.parseInt(CourseHours.getText()));
					preparedStmtAdd.setInt(6, Integer.parseInt(CourseClassNo.getText()));
					preparedStmtAdd.setInt(7, Integer.parseInt(LectureridclassTextPane.getText()));
					preparedStmtAdd.setString(8, CourseDay.getText());
					preparedStmtAdd.setInt(9, Integer.parseInt(startTimeTextpane.getText()));
					preparedStmtUpdate = (PreparedStatement) conn.prepareStatement(courseUpdate);
					preparedStmtUpdate.setInt(1, Integer.parseInt(CourseId.getText()));
					preparedStmtUpdate.setString (2, CourseYear.getText());
					preparedStmtUpdate.setString(3, CourseName.getText());
					preparedStmtUpdate.setString(4, CourseSemester.getText());
					preparedStmtUpdate.setInt(5, Integer.parseInt(CourseHours.getText()));
					preparedStmtUpdate.setInt(6, Integer.parseInt(CourseClassNo.getText()));
					preparedStmtUpdate.setInt(7, Integer.parseInt(LectureridclassTextPane.getText()));
					preparedStmtUpdate.setString(8, CourseDay.getText());
					preparedStmtUpdate.setInt(9, Integer.parseInt(startTimeTextpane.getText()));
					preparedStmtUpdate.setInt(10, coursePK);
					preparedStmtDelete = (PreparedStatement) conn.prepareStatement(courseDelete);
					preparedStmtDelete.setInt(1, coursePK);
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null,e2);
				}
				switch (courseQuery){
				case "Add": try {
					preparedStmtAdd.executeUpdate();
					String query = "select * from Course";
					PreparedStatement pst;
					pst = (PreparedStatement)conn.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					conn.commit();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rs));	
					JOptionPane.showMessageDialog(null,"Adding was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Update": try {
					preparedStmtUpdate.executeUpdate();
					String query = "select * from Course";
					PreparedStatement pst;
					pst = (PreparedStatement)conn.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					conn.commit();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rs));
					JOptionPane.showMessageDialog(null,"Updating was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Delete": try {
					preparedStmtDelete.executeUpdate();
					String query = "select * from Course";
					PreparedStatement pst;
					pst = (PreparedStatement)conn.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					conn.commit();
					tableCourse.setModel(DbUtils.resultSetToTableModel(rs));
					JOptionPane.showMessageDialog(null,"Deleting was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				}
			}
		});
		btnRunCourseQuery.setBounds(95, 526, 100, 29);
		contentPane.add(btnRunCourseQuery);
		CourseId = new JTextPane();
		CourseId.setBounds(117, 370, 117, 20);
		contentPane.add(CourseId);
		CourseName = new JTextPane();
		CourseName.setBounds(117, 418, 117, 20);
		contentPane.add(CourseName);
		CourseYear = new JTextPane();
		CourseYear.setBounds(117, 394, 117, 20);
		contentPane.add(CourseYear);	
		CourseHours = new JTextPane();
		CourseHours.setBounds(387, 466, 117, 20);
		contentPane.add(CourseHours);
		CourseSemester = new JTextPane();
		CourseSemester.setBounds(117, 442, 117, 20);
		contentPane.add(CourseSemester);
		lblCourseId = new JLabel("Course id (P)"); 
		lblCourseId.setBounds(7, 374, 84, 16);
		contentPane.add(lblCourseId);	
		lblCourseName = new JLabel("Course name");
		lblCourseName.setBounds(7, 422, 84, 16);
		contentPane.add(lblCourseName);	
		lblCourseYear = new JLabel("Course year");
		lblCourseYear.setBounds(7, 398, 84, 16);
		contentPane.add(lblCourseYear);		
		lblCourseHours = new JLabel("Hours");
		lblCourseHours.setBounds(290, 470, 42, 16);
		contentPane.add(lblCourseHours);
		lblCourseSemester = new JLabel("Course semester");
		lblCourseSemester.setBounds(7, 446, 117, 16);
		contentPane.add(lblCourseSemester);
		CourseClassNo = new JTextPane();
		CourseClassNo.setBounds(387, 442, 117, 20);
		contentPane.add(CourseClassNo);

		//Phone
		phoneScrollPane = new JScrollPane();
		phoneScrollPane.setBounds(1145, 135, 250, 219);
		contentPane.add(phoneScrollPane);
		tablePhone = new JTable();
		phoneScrollPane.setViewportView(tablePhone);
		tablePhone.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePhone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indexUpdaeTable = tablePhone.getSelectedRow();
				phonePK = tablePhone.getModel().getValueAt(indexUpdaeTable, 0).toString();
				PhoneNumber.setText(tablePhone.getModel().getValueAt(indexUpdaeTable, 0).toString());
				PhoneLecturerId.setText(tablePhone.getModel().getValueAt(indexUpdaeTable, 1).toString());
			}
		});
		tablePhone.setSelectionForeground(Color.BLACK);
		tablePhone.setSelectionBackground(Color.GREEN);
		btnRunPhoneQuery = new JButton("Run Query");
		btnRunPhoneQuery.setEnabled(false);
		btnRunPhoneQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					preparedStmtAdd = (PreparedStatement) conn.prepareStatement(phoneAdd);
					preparedStmtAdd.setString(1, PhoneNumber.getText());
					preparedStmtAdd.setInt(2, Integer.parseInt(PhoneLecturerId.getText()));
					preparedStmtDelete = (PreparedStatement) conn.prepareStatement(phoneDelete);
					preparedStmtDelete.setString(1, phonePK);
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null,e2);
				}
				switch (phoneQuery){
				case "Add": try {
					preparedStmtAdd.executeUpdate();
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement)conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					conn.commit();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					JOptionPane.showMessageDialog(null,"Adding was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				case "Delete":  try {
					preparedStmtDelete.executeUpdate();
					String queryPhone = "select * from Phone";
					PreparedStatement pstPhone = (PreparedStatement)conn.prepareStatement(queryPhone);
					ResultSet rsPhone=pstPhone.executeQuery();
					conn.commit();
					tablePhone.setModel(DbUtils.resultSetToTableModel(rsPhone));
					JOptionPane.showMessageDialog(null,"Deleting was performed successfully");
					clearField();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null,e1);
						try {
							JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
							conn.rollback(); // back to previous state 
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(null, e2);
						}
					};
				break;
				}

			}
		});
		btnRunPhoneQuery.setBounds(1220, 108, 100, 29);
		contentPane.add(btnRunPhoneQuery);
		lblPhone = new JLabel("Phone");
		lblPhone.setFont(new Font("Courier", Font.PLAIN, 20));
		lblPhone.setBounds(1151, 108, 96, 29);
		contentPane.add(lblPhone);
		PhoneComboBox = new JComboBox(phonequery);
		PhoneComboBox.setSelectedIndex(1);
		PhoneComboBox.setSelectedItem(query[0]);
		PhoneComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PhoneComboBox.addActionListener(this);
				if(e.getSource() == PhoneComboBox){
					JComboBox JCB = (JComboBox)e.getSource();
					phoneQuery = (String)JCB.getSelectedItem();
				}
			}
		});
		PhoneComboBox.setBounds(1145, 78, 230, 25);
		contentPane.add(PhoneComboBox);
		lblPhoneNumber = new JLabel("Phone Number (P)");
		lblPhoneNumber.setBounds(1149, 20, 117, 16);
		contentPane.add(lblPhoneNumber);
		lblLecturerIdPhone = new JLabel("Lecturer id");
		lblLecturerIdPhone.setBounds(1149, 44, 108, 16);
		contentPane.add(lblLecturerIdPhone);
		PhoneNumber = new JTextPane();
		PhoneNumber.setBounds(1273, 16, 117, 20);
		contentPane.add(PhoneNumber);
		PhoneLecturerId = new JTextPane();
		PhoneLecturerId.setBounds(1273, 40, 117, 20);
		contentPane.add(PhoneLecturerId);
		PhonePanel = new JPanel();
		PhonePanel.setBorder(new LineBorder(Color.DARK_GRAY));
		PhonePanel.setBackground(new Color(176, 224, 230));
		PhonePanel.setBounds(1145, 6, 250, 131);
		contentPane.add(PhonePanel);
			
		//Additional
		btnRunJoinCourseQuery = new JButton("Run");
		btnRunJoinCourseQuery.setEnabled(false);
		btnRunJoinCourseQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(joinLocation);
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(joinLocation);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Join Location");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Joined Course was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnRunJoinCourseQuery.setBounds(370, 121, 50, 29);
		contentPane.add(btnRunJoinCourseQuery);
		lblCorreleted = new JLabel("Correleted example: Course in Sun 'A' & Mon 'B'");
		lblCorreleted.setBounds(11, 98, 308, 16);
		contentPane.add(lblCorreleted);
		btnRunCorreletedQuery = new JButton("Run");
		btnRunCorreletedQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(correlated);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(correlated);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Correlated");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Correlation was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnRunCorreletedQuery.setEnabled(false);
		btnRunCorreletedQuery.setBounds(370, 93, 50, 29);
		contentPane.add(btnRunCorreletedQuery);
		ResultscrollPane = new JScrollPane();
		ResultscrollPane.setBounds(418, 70, 721, 284);
		contentPane.add(ResultscrollPane);
		additionalTable = new JTable();
		ResultscrollPane.setViewportView(additionalTable);
		additionalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		additionalTable.setSelectionForeground(Color.BLACK);
		additionalTable.setSelectionBackground(Color.GREEN);
		lblResult = new JLabel("");																
		lblResult.setFont(new Font("Courier", Font.PLAIN, 20));
		lblResult.setBounds(423, 40, 185, 29);
		contentPane.add(lblResult);
		joinExampleCourseLocation = new JLabel("Join example: Course Location");
		joinExampleCourseLocation.setBounds(11, 126, 310, 16);
		contentPane.add(joinExampleCourseLocation);
		lblAdditionalQueries = new JLabel("Additional Queries");
		lblAdditionalQueries.setFont(new Font("Courier", Font.PLAIN, 20));
		lblAdditionalQueries.setBounds(13, 47, 224, 16);
		contentPane.add(lblAdditionalQueries);
		lblNesting = new JLabel("Nesting: Phones of lecturers who live in Tel-aviv");
		lblNesting.setBounds(11, 234, 316, 16);
		contentPane.add(lblNesting);
		btnRunNestedQuery = new JButton("Run");
		btnRunNestedQuery.setEnabled(false);
		btnRunNestedQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(nestedSubQuery);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(nestedSubQuery);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Nested");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Nested was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnRunNestedQuery.setBounds(370, 229, 50, 29);
		contentPane.add(btnRunNestedQuery);
		
		JLabel lblLectureridclass = new JLabel("Lecturer Id");
		lblLectureridclass.setBounds(290, 422, 67, 16);
		contentPane.add(lblLectureridclass);
		
		LectureridclassTextPane = new JTextPane();
		LectureridclassTextPane.setBounds(387, 418, 117, 20);
		contentPane.add(LectureridclassTextPane);
		
		joinExampleLecture = new JLabel("Join example: Lecture");
		joinExampleLecture.setBounds(11, 153, 310, 16);
		contentPane.add(joinExampleLecture);
		
		btnRunJoinLectureQuery = new JButton("Run");
		btnRunJoinLectureQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(joinLecture);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(joinLecture);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Join Lecture");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Join Lecture was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnRunJoinLectureQuery.setEnabled(false);
		btnRunJoinLectureQuery.setBounds(370, 148, 50, 29);
		contentPane.add(btnRunJoinLectureQuery);
		
		lblCourseDay = new JLabel("Course day");
		lblCourseDay.setBounds(290, 398, 76, 16);
		contentPane.add(lblCourseDay);
		
		CourseDay = new JTextPane();
		CourseDay.setBounds(387, 394, 117, 20);
		contentPane.add(CourseDay);
		
		JPanel lecturerPanel = new JPanel();
		lecturerPanel.setBackground(new Color(176, 224, 230));
		lecturerPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		lecturerPanel.setBounds(515, 358, 624, 198);
		contentPane.add(lecturerPanel);
		
		btnRunJoinClassDetails = new JButton("Run");
		btnRunJoinClassDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(classDetails);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(classDetails);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Class Details");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Join Class Details was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}	
			}
		});
		btnRunJoinClassDetails.setEnabled(false);
		btnRunJoinClassDetails.setBounds(370, 175, 50, 29);
		contentPane.add(btnRunJoinClassDetails);
		
		JLabel lblJoinExampleCourse = new JLabel("Class 2001 Details (Lectureres, Courses)");
		lblJoinExampleCourse.setBounds(11, 180, 310, 16);
		contentPane.add(lblJoinExampleCourse);
		
		startTimeTextpane = new JTextPane();
		startTimeTextpane.setBounds(387, 370, 117, 20);
		contentPane.add(startTimeTextpane);
		
		JLabel lbStartTime = new JLabel("Start TIme");
		lbStartTime.setBounds(290, 374, 76, 16);
		contentPane.add(lbStartTime);
		
		btnRunJoinLecturerStart = new JButton("Run");
		btnRunJoinLecturerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(lecturerDetail);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(lecturerDetail);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Lecturer Detail");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Join Lecturer Details was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnRunJoinLecturerStart.setEnabled(false);
		btnRunJoinLecturerStart.setBounds(370, 202, 50, 29);
		contentPane.add(btnRunJoinLecturerStart);
		
		lblLecturerDetails = new JLabel("Lecturer id 123456781 details (Classes, Courses)");
		lblLecturerDetails.setBounds(11, 207, 310, 16);
		contentPane.add(lblLecturerDetails);
		
		coursePanel = new JPanel();
		coursePanel.setBackground(new Color(176, 224, 230));
		coursePanel.setBorder(new LineBorder(Color.DARK_GRAY));
		coursePanel.setBounds(3, 358, 507, 198);
		contentPane.add(coursePanel);
		
		JComboBox comboBox = new JComboBox(days.values());
		comboBox.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		comboBox.setSelectedIndex(1);
		comboBox.setSelectedItem(days.Day);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.addActionListener(this);
				if(e.getSource() == comboBox){
					JComboBox JCB = (JComboBox)e.getSource();
					firstDays = (Enum<days>)JCB.getSelectedItem();
				
				}
			}
		});
		comboBox.setBounds(95, 259, 75, 25);
		contentPane.add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox(houers);
		comboBox_1.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		comboBox_1.setSelectedIndex(1);
		comboBox_1.setSelectedItem(houers[0]);
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox_1.addActionListener(this);
				if(e.getSource() == comboBox_1){
					JComboBox JCB = (JComboBox)e.getSource();
					firstHouers = (String)JCB.getSelectedItem();
				}
			}
		});
		comboBox_1.setBounds(165, 259, 65, 25);
		contentPane.add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox(days.values());
		comboBox_2.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		comboBox_2.setSelectedIndex(1);
		comboBox_2.setSelectedItem(days.Day);
		comboBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox_2.addActionListener(this);
				if(e.getSource() == comboBox_2){
					JComboBox JCB = (JComboBox)e.getSource();
					secondDays = (Enum<days>)JCB.getSelectedItem();
				}
			}
		});
		comboBox_2.setBounds(235, 259, 75, 25);
		contentPane.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox(houers);
		comboBox_3.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		comboBox_3.setSelectedIndex(1);
		comboBox_3.setSelectedItem(houers[0]);
		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox_3.addActionListener(this);
				if(e.getSource() == comboBox_3){
					JComboBox JCB = (JComboBox)e.getSource();
					secondHouers = (String)JCB.getSelectedItem();
				}
			}
		});
		comboBox_3.setBounds(305, 259, 65, 25);
		contentPane.add(comboBox_3);
		
		btnRangeDates = new JButton("Run");
		btnRangeDates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				try {				
					preparedStmtDaysAndHouers = (PreparedStatement) conn.prepareStatement(rangeDates2);
					preparedStmtDaysAndHouers.setInt(1, firstDays.ordinal());
					preparedStmtDaysAndHouers.setInt(2, Integer.parseInt(firstHouers));
					preparedStmtDaysAndHouers.setInt(3, firstDays.ordinal());
					preparedStmtDaysAndHouers.setInt(4, secondDays.ordinal());
					preparedStmtDaysAndHouers.setInt(5, Integer.parseInt(secondHouers));
					preparedStmtDaysAndHouers.setInt(6, secondDays.ordinal());
					preparedStmtDaysAndHouers.execute();					
					ResultSet rsAdditional=preparedStmtDaysAndHouers.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					JOptionPane.showMessageDialog(null,"The action was performed successfully");
					lblResult.setText("Range Dates");
				} catch (SQLException e3) {
					JOptionPane.showMessageDialog(null, e3);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}	
			}
		});
		btnRangeDates.setEnabled(false);
		btnRangeDates.setBounds(370, 256, 50, 29);
		contentPane.add(btnRangeDates);
		
		JLabel lblCoursesBetweenSun = new JLabel("Courses from                                 - ");
		lblCoursesBetweenSun.setBounds(11, 261, 241, 16);
		contentPane.add(lblCoursesBetweenSun);
		
		btnScheduler = new JButton("Run");
		btnScheduler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					additionalQueries = (PreparedStatement) conn.prepareStatement(scheduler);					
					PreparedStatement pstAdditional = (PreparedStatement)conn.prepareStatement(scheduler);
					ResultSet rsAdditional=pstAdditional.executeQuery();
					conn.commit();
					additionalTable.setModel(DbUtils.resultSetToTableModel(rsAdditional));
					lblResult.setText("Scheduler");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
				try {
					additionalQueries.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null,"Scheduler details was performed successfully");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1);
					try {
						JOptionPane.showMessageDialog(null, "An error was occurred - now everything is ok");
						conn.rollback(); // back to previous state 
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(null, e2);
					}
				}
			}
		});
		btnScheduler.setEnabled(false);
		btnScheduler.setForeground(Color.BLUE);
		btnScheduler.setBackground(Color.BLUE);
		btnScheduler.setBounds(370, 65, 50, 29);
		contentPane.add(btnScheduler);
		
		lblWeeklyScheduler = new JLabel("Weekly Scheduler");
		lblWeeklyScheduler.setForeground(Color.BLUE);
		lblWeeklyScheduler.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		lblWeeklyScheduler.setBounds(11, 70, 310, 16);
		contentPane.add(lblWeeklyScheduler);
	}
	public void clearField(){
		ClassFloor.setText("");
		ClassNumber.setText("");
		ClassBuildingNumber.setText("");
		LecturerId.setText("");
		LecturerAddressCity.setText("");
		LecturerAddressStreet.setText("");
		LecturerFirstName.setText("");
		LecturerLastName.setText("");
		LecturerDOBMonth.setText("");
		LecturerDOBYear.setText("");
		LecturerDOBDay.setText("");
		CourseId.setText("");
		CourseYear.setText("");
		CourseName.setText("");
		CourseSemester.setText("");
		CourseHours.setText("");
		CourseClassNo.setText("");
		PhoneNumber.setText("");
		PhoneLecturerId.setText("");
		startTimeTextpane.setText("");
		CourseDay.setText("");
		LectureridclassTextPane.setText("");
	}
}