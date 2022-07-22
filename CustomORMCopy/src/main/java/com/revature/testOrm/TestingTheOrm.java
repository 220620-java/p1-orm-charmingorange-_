package com.revature.testOrm;

import com.revature.orm.*;
import com.revature.testClasses.*;

public class TestingTheOrm {
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		
		
		System.out.println("hello WOdfdfdfRLD!");
		
		Orm_Impl execute = new Orm_Impl();
		
		
	    //String test = execute.printTable("users");
	    
	    //System.out.println("HERE WE GO \n\n" + test);
		
		
		//execute.printTable("has_account");
		
		//execute.printTable("account");
		
		//execute.printTable("boobs");
		
		// ----------------------------
		
		
		
		// TESTING UPDATE
		
		//users mike = new users(18, "eedyVersio", "newestPoopuiuiuiuiuiuiuiuiuihghghghr");
		
		//System.out.println("NEW THINGY: " + mike.user_id + " " + mike.user_name + " " + mike.user_pass);
		
		//execute.UpdateOBJ(mike, "18");
		
		// ---------------------//
		
		// TESTING INSERT
		
		//users joe = new users(545, "pooper", "scooper");
		
		//execute.InsertToTable(joe);
		
		// ----------------------//
		
		// TESTING DELETE
		
		//execute.printTable("users");
		
		//execute.DeleteOBJ(joe, "22");
		
	    //execute.printTable("users");
		
	}

}
