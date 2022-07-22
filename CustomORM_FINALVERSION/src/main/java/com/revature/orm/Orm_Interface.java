package com.revature.orm;

import java.util.List;

public interface Orm_Interface {
	
	// Helper Methods 
	
	// Check Exists
	
	public boolean checkRowExists(String colData, String colName, String tableName);
	
	public boolean checkColumnExists(String col, String tableName);
	
	public boolean checkTableExist(String tableName);
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Get Column Info
	
	public int getNumColumn(String tableName);
	
	public String[] getAllColumns (String tableName);
	
	public int getMaxColLength(String col, String tableName);
	
	public boolean checkInputColumn(String col, String tableName, int length);
	
	public int[] getColSpacing(String tableName);
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Output
	
	public void printAllColumns(String tableName);
	
	public String printTable(String tableName);
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	// CRUD Operations
	
	public void UpdateOBJ(Object obj, String id) throws IllegalArgumentException, IllegalAccessException;
	
	public void InsertToTable(Object obj) throws IllegalArgumentException, IllegalAccessException;
	
	public void DeleteOBJ(Object obj,String id) throws IllegalArgumentException, IllegalAccessException;
	

	
	
	/*
	
	<T> T findById(Class<T> objectClass, Object key);

    <T> List<T> findAll(Class<T> objectClass);

    //<T> List<T> findAll(Class<T> objectClass, Criteria criteria);

    <T> T create(T object);

    <T> boolean update(T object);

    <T> boolean delete(T object);
    
    */
	
	
	
	
	
	

}
