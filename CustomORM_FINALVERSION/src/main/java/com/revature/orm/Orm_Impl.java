package com.revature.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

import com.revature.pepinUtil.ConnectionUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Orm_Impl implements Orm_Interface {

	private ConnectionUtil connUtil = ConnectionUtil.getConnectionUtil();

	// -----------------------------------------------------------------------

	public boolean checkRowExists(String colData, String colName, String tableName) {

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select exists (select from " + tableName + " where " + colName + " = '" + colData + "')";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				String result = resultSet.getString(1);

				if (result.equals("t")) {
					return true;
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// ----------------------------------------------------------------------------------

	public boolean checkColumnExists(String col, String tableName) {

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			return false;
		}

		String[] colList = getAllColumns(tableName);

		for (String colCheck : colList) {
			if (col.equals(colCheck)) {
				return true;
			}
		}

		System.out.println("ERROR - COLUMN: " + col + " DOES NOT EXISTS");

		return false;
	}

	// ----------------------------------------------------------------------------------

	public boolean checkTableExist(String tableName) {

		boolean result = false;

		try (Connection conn = connUtil.getConnection()) {

			// CHANGE table_schema to your schema name

			String sql = "select exists (select from information_schema.tables where table_schema LIKE 'bankapp' and table_type LIKE 'BASE TABLE' and table_name  = '"
					+ tableName + "')";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				String check = resultSet.getString(1);

				if (check.equals("t")) {

					result = true;

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	// ----------------------------------------------------------------------------------

	public int getNumColumn(String tableName) {

		// Make Sure Table exists

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			return 0;
		}

		// --------------------------------------

		int num = 0;

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select count(*) from information_schema.columns where table_name = '" + tableName + "'";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				num = resultSet.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return num;

	}

	// ------------------------------------------------------------------------------------

	public void printAllColumns(String tableName) {

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			return;
		}

		System.out.println("Columns of " + tableName + "\n---------------------");

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select column_name from information_schema.columns where table_schema = 'bankapp' and table_name = '"
					+ tableName + "'";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			int counter = 1;

			while (resultSet.next()) {

				String colName = resultSet.getString(1);

				System.out.println("Column " + counter + ": " + colName);

				counter++;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// ------------------------------------------------------------------------------------------

	public String[] getAllColumns(String tableName) {

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			String[] nothing = { "" };
			return nothing;
		}

		String[] colArray = new String[getNumColumn(tableName)];

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select column_name from information_schema.columns where table_schema = 'bankapp' and table_name = '"
					+ tableName + "'";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			int counter = 0;

			while (resultSet.next()) {

				String colName = resultSet.getString(1);

				colArray[counter] = colName;

				counter++;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// -----------------------------------

		return colArray;
	}

	// --------------------------------------------------------------------------------------------------

	public int getMaxColLength(String col, String tableName) {

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			return 0;
		}

		// CHECK COLUMN EXIST

		if (!checkColumnExists(col, tableName)) {
			System.out.println("ERROR: Column does not exist");
			return 0;
		}

		int result = 0;

		try (Connection conn = connUtil.getConnection()) {

			// SELECT 'my_table', 'name', MAX(LENGTH(name)) FROM my_table

			String sql = "SELECT MAX(LENGTH(CAST(" + col + " AS TEXT))) FROM " + tableName;

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				result = resultSet.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// -----------------------------------

		return result;

	}

	// ---------------------------------------------------------------------------------------------

	public boolean checkInputColumn(String col, String tableName, int length) {

		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: Table does not exist");
			return false;
		}

		// CHECK COLUMN EXIST

		if (!checkColumnExists(col, tableName)) {
			System.out.println("ERROR: Column does not exist");
			return false;
		}

		// String dataType = "";

		int maxChar = 0;

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select data_type, character_maximum_length from information_schema.columns where column_name = '"
					+ col + "' and table_name = '" + tableName + "'";

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				// dataType = resultSet.getString(1);

				maxChar = resultSet.getInt(2);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// -----------------------------

		if (maxChar != 0 && length > maxChar) {
			System.out.println("INVALID INPUT FOR " + col + "\nMAX LENGTH IS: " + maxChar + " CHARACTERS");
			return false;
		}

		return true;

	}

	// -------------------------------------------------------------------------------------------------------

	public int[] getColSpacing(String tableName) {

		// Return column spacing for all columns in Integer Array

		String[] colArray = getAllColumns(tableName);

		int[] colSpacing = new int[getNumColumn(tableName)];

		int colLength;

		int colDataLength;

		int index = 0;

		for (String s : colArray) {
			colLength = s.length();
			colDataLength = getMaxColLength(s, tableName);

			if (colLength > colDataLength) {
				colSpacing[index] = colLength + 4;
			} else {
				colSpacing[index] = colDataLength + 4;
			}

			index++;

		}

		// --------------------------------------------------

		return colSpacing;

	}

	// -------------------------------------------------------------------------------------------------------------

	public String printTable(String tableName) {
		
        // Display something in initial console.
        System.out.println("Console: inside printTable");
 
        // Preserve current console
        PrintStream previousConsole = System.out;
		
        // Set the standard output to use newConsole.
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
        
        //---------------------------------------------------
	
		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: " + tableName + " table does not exist");
			return "";
		}

		// ----------------------------------------------

		System.out.println("\nRetrieving " + tableName + " data...\n");

		String[] colArray = getAllColumns(tableName);

		int[] colSpacing = getColSpacing(tableName);

		int counter = 0;

		// ------------------------------------------

		int colSpacingSum = 0;

		for (int woo : colSpacing) {
			colSpacingSum = colSpacingSum + woo;
		}

		System.out.println("\n-------------------------------------------------\n");

		System.out.printf("%s", StringUtils.center(tableName, colSpacingSum));

		System.out.println("\n");

		for (String i : colArray) {
			System.out.printf("%s", StringUtils.center(i, colSpacing[counter]));
			counter++;
		}

		System.out.println("\n");

		// ----------------------------------------------

		String firstColumn = colArray[0];

		int numC = getNumColumn(tableName);

		try (Connection conn = connUtil.getConnection()) {

			String sql = "select * from " + tableName + " order by " + firstColumn;

			Statement stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(sql);

			while (resultSet.next()) {

				int counter2 = 1;
				int colArrayNum = 0;
				while (counter2 <= numC) {

					String x = "";

					x = x.concat(resultSet.getString(counter2));

					System.out.printf("%s", StringUtils.center(x, colSpacing[colArrayNum]));

					counter2++;
					colArrayNum++;

				}

				System.out.println("");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// --------------------------------------------------
	
		System.out.println("\n-------------------------------------------------\n");
		
		//Display output of newConsole.
		
		previousConsole.println(newConsole.toString()); 
		
        // Restore back the standard console output.
        System.setOut(previousConsole);
 
        return newConsole.toString();
        
        // Test print to console.
        // System.out.println("Console: back to Normal sysout");
        //System.out.println(newConsole.toString());

	}

	// ---------------------------------------------------------------------------------------------------------------

	// - - - - - - - - - - - - - - - - START CRUD METHODS - - - - - - - - - - - - 

	// UPDATE

	public void UpdateOBJ(Object obj, String id) throws IllegalArgumentException, IllegalAccessException {
		
		// CHECK IF TABLE EXISTS
		
		// CLASS NAME AND TABLE NAME HAVE TO MATCH
				
		String tableName = obj.getClass().getSimpleName().toString();
				
		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: " + tableName + " table does not exist\nCLASS NAME AND TABLE NAME HAVE TO MATCH\nCANNOT INSERT OBJECT");
			return;
		}
		
		// CHECK IF ROW EXISTS
		
		String []cols = getAllColumns(tableName);
		
		if (!checkRowExists(id, cols[0], tableName)) {
			System.out.println("ERROR: CAN'T FIND RECORD TO UPDATE\n");
			return;
		}
		
		try (Connection conn = connUtil.getConnection()) {

			Field[] fields = obj.getClass().getDeclaredFields();// array of fields of the object.
			//System.out.println("NUT: " + obj.getClass().getSimpleName());

			//System.out.println("NUT 2: " + fields[1].toString());

			fields[1].setAccessible(true); // setting the field to accessible.
			//System.out.println("hello >:D");
			//System.out.println(fields[1].get(obj)); // object data EX:lilly in array given index.

			// String colName = fields[1].get(obj).toString();//index 1 will always be the
			// table name
			// user.setTable("users"); //this will be done in the orm. it will be just set
			// table with the table name being passed in.
			// System.out.println(user.getTable());//prints out the table in users. this is
			// the table name user.getTable();

			// ----------------------------------------------------------------------------------------------------------------------------------
			// GRAB THE CLASS NAME AS A STRING.

			// user.setTable("users"); //this will be done in the orm. it will be just set
			// table with the table name being passed in.
			// System.out.println(user.getTable());//prints out the table in users. this is
			// the table name user.getTable();
			// ArrayList<String> columns = GetAllColumns("table");//array of column
			// EX:username
			// String [] columns =
			// getAllColumns(obj.getClass().getSimpleName().toString());// this will get all
			// of the columns

			String[] columns = getAllColumns(obj.getClass().getSimpleName().toString());// this will get
																						// all
																						// of the
																						// columns
			// this will pass in fields at the index of 1 because that will always be the
			// table name/

			//System.out.println("OUTPUT HERE :d");
			//System.out.println(obj.getClass().getSimpleName().toString());
			//for (String temp : columns) {
			//	System.out.println(temp);
			//}
			// BUILDING SQL STATEMENT
			String update = "Update" + " " + obj.getClass().getSimpleName().toString() + " ";
			String set = " SET ";
			String where = " WHERE " + columns[0] + " = " + id;

			for (int j = 1; j < columns.length; j++) {

				if (j < columns.length - 1) {
					fields[j].setAccessible(true);
					set = set + " " + columns[j] + "  = " + "'" + fields[j].get(obj).toString() + "'" + ",";
					
                    String check1 = fields[j].get(obj).toString();
					
					boolean checker = checkInputColumn(columns[j], obj.getClass().getSimpleName().toString(), check1.length());
					
					if(checker == false) {
						System.out.println("FAILED UPDATE");
						return;
					}
					

				} else {
					fields[j].setAccessible(true);
					set = set + " " + columns[j] + "  = " + "'" + fields[j].get(obj).toString() + "'";
					
					String check2 = fields[j].get(obj).toString();
					
					boolean checker2 = checkInputColumn(columns[j], obj.getClass().getSimpleName().toString(), check2.length());
					
					if(checker2 == false) {
						System.out.println("FAILED UPDATE");
						return;
					}

				}

			}

			// FINISHING STATEMENT TOUCHES.
			// set=set.substring(20,set.length());
			
			String sql = update + " " + " " + set + " " + where + ";";
			
			//System.out.println(sql);

			Statement st = conn.createStatement();
			st.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
			// Error_Log(e.toString());
		}

	}

	// -------------------------------------------------------------------------------------------------

	// INSERT

	public void InsertToTable(Object obj) throws IllegalArgumentException, IllegalAccessException {
		
		// CHECK IF TABLE EXISTS
		
		// CLASS NAME AND TABLE NAME HAVE TO MATCH
		
		String tableName = obj.getClass().getSimpleName().toString();
		
		if (!checkTableExist(tableName)) {
			System.out.println("ERROR: " + tableName + " table does not exist\nCLASS NAME AND TABLE NAME HAVE TO MATCH\nCANNOT INSERT OBJECT");
			return;
		}
		
		try (Connection conn = connUtil.getConnection()) {

			// the orm repository will contain a annotation for the table and then you can
			// convert that annotation to a string,
			// object.getclass().getName(); //you need to substring this . there is an
			// example of this at the orm . in the parsed object class.
			// checks to see if the object is a string or not
			// if(obj.getClass() != String.class) {
			// look for a table with the string name.
			// get table method(obj )

			// creates a field array and grabs the fields of the object and inserts it into
			// the array.
			// could use this to validate the fields in the table in the database.

			// ie if fields[0](username)== database table column name ;

			// we would need to create a method to find the type of each field. in this case
			// if its a string.
			// var f1 = fields[0];
			// field minipulation grabs data from the field array

			Field[] fields = obj.getClass().getDeclaredFields();// array of fields of the object.

			fields[1].setAccessible(true); // setting the field to accessable.
			
			//System.out.println(fields[1].get(obj)); // object data EX:lilly in array given index.

			// String colName = fields[1].get(obj).toString();//index 1 will always be the
			// table name

			// need to create some type of get table and do the object.get table. and the
			// set table.
			// getting tables .

			// user.setTable("users"); //this will be done in the orm. it will be just set
			// table with the table name being passed in.
			// System.out.println(user.getTable());//prints out the table in users. this is
			// the table name user.getTable();
			// ArrayList<String> columns = GetAllColumns("table");//array of column
			// EX:username

			String[] columns = getAllColumns(obj.getClass().getSimpleName().toString());
			// this will pass in fields at the index of 1 because that will always be the
			// table name/

			String insertInto = "";
			String values = "";
			String colName = obj.getClass().getSimpleName().toString();
			// we will have to make the first field in the database and the models be the
			// table name for this to work.

			// building statement;
			insertInto = insertInto + "INSERT INTO" + " " + obj.getClass().getSimpleName().toString()
					+ " (";

			for (int i = 1; i < getNumColumn(colName); i++) {

				if (i < getNumColumn(colName) - 1) {

					fields[i].setAccessible(true);
					values = values + "'" + fields[i].get(obj).toString() + "'" + ", ";
					
					String check1 = fields[i].get(obj).toString();
					
					boolean checker = checkInputColumn(columns[i], colName, check1.length());
					
					if(checker == false) {
						System.out.println("FAILED INSERT");
						return;
					}
					

				} else {
					fields[i].setAccessible(true);
					values = values + " " + "'" + fields[i].get(obj).toString() + "'";
					
					String check2 = fields[i].get(obj).toString();
					
					boolean checker2 = checkInputColumn(columns[i], colName, check2.length());
					
					if(checker2 == false) {
						System.out.println("FAILED INSERT");
						return;
					}
					
				}

			}

			for (int i = 1; i < getNumColumn(colName); i++) {
				// sets accessibility to access the data.

				fields[i].setAccessible(true);
				if (i < getNumColumn(colName) - 1) {
					insertInto = insertInto + columns[i] + ",";// column name

				} else {
					insertInto = insertInto + columns[i];
					fields[i].setAccessible(true);

				}

			}

			insertInto = insertInto + ") VALUES" + "( ";
			values = values + " );";

			String sql = insertInto + values;

			//System.out.println(sql);

			// String sql = "INSERT INTO users (username, pwd, first_name, last_name,
			// address) " //
			// + " VALUES ('" + obj + "', '" + user.getPassword() + "'" //
			// + ", '" + user.f1 .equals(obj).getFirstName() + "'" + ", '" +
			// user.getLastName() + "'" //
			// + ", '" + user.getAddress() + "')";
			// String sql = insertInto;
			// System.out.println(sql);

			Statement st = conn.createStatement();
			st.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
			// Error_Log(e.toString());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------

	// DELETE

	public void DeleteOBJ(Object obj, String id) throws IllegalArgumentException, IllegalAccessException {
		
		// CHECK IF ROW EXISTS
		
		String tableName = obj.getClass().getSimpleName().toString();
		
		String []cols = getAllColumns(tableName);
		
		if (!checkRowExists(id, cols[0], tableName)) {
			System.out.println("ERROR: CAN'T FIND RECORD TO DELETE\n");
			return;
		}

		try (Connection conn = connUtil.getConnection()) {
			
			// the orm repository will contain a annotation for the table and then you can
			// convert that annotation to a string,
			// object.getclass().getName(); //you need to substring this . there is an
			// example of this at the orm . in the parsed object class.
			// checks to see if the object is a string or not
			// if(obj.getClass() != String.class) {
			// look for a table with the string name.
			// get table method(obj )

			// creates a field array and grabs the fields of the object and inserts it into
			// the array.
			// could use this to validate the fields in the table in the database.

			// ie if fields[0](username)== database table column name ;

			// we would need to create a method to find the type of each field. in this case
			// if its a string.
			// var f1 = fields[0];
			// field manipulation grabs data from the field array
			
			Field[] fields = obj.getClass().getDeclaredFields();// array of fields of the object.
			// var f2 = fields[1].toString();
			
			fields[1].setAccessible(true); // setting the field to accessible.
			
			//System.out.println("hello >:D");
			//System.out.println(fields[1].get(obj)); // object data EX:lilly in array given index.
			
			String colName = obj.getClass().getSimpleName().toString();

			// need to create some type of get table and do the object.get table. and the
			// set table.
			// getting tables .

			// user.setTable("users"); //this will be done in the orm. it will be just set
			// table with the table name being passed in.
			// System.out.println(user.getTable());//prints out the table in users. this is
			// the table name user.getTable();
			// ArrayList<String> columns = GetAllColumns("table");//array of column
			// EX:username
			
			String[] columns = getAllColumns(colName);// this will become orm.get all columns
			
			// this will pass in fields at the index of 1 because that will always be the
			// table name/

			String deleteFrom = "";
			String where = " WHERE ";

			// we will have to make the first field in the database and the models be the
			// table name for this to work.

			// building delete statement;
			deleteFrom = deleteFrom + "DELETE FROM" + " " + obj.getClass().getSimpleName().toString()
					+ " ";

			//fields[2].setAccessible(true);
			where = where + " " + columns[0] + " = " + id;
			String sql = deleteFrom + where;
			System.out.println(sql);

			Statement st = conn.createStatement();
			st.executeUpdate(sql);
			
			System.out.println("\nSUCCESSFUL DELETE\n");

		} catch (SQLException e) {
			e.printStackTrace();
			// Error_Log(e.toString());
		}

	}

	// -------------------------------------------------------------------------------

}
