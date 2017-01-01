package com.college.scheduler.DB;
import java.sql.Connection;

public interface DAO {
	public Connection createConection();
	public void closeConection();
	public void generateDefaultData();
	public void dropTables();
}