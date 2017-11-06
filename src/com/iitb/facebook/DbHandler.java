package com.iitb.facebook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.iitb.facebook.Utils;

import org.json.JSONObject;

public class DbHandler {
	
	private static String connString = "jdbc:postgresql://localhost:5432/postgres";
	private static String userName = "nihhaar";
	private static String passWord = "";
	
	public static JSONObject authenticate(String email, String password, HttpServletRequest request){		
		JSONObject obj = new JSONObject();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "SELECT * FROM password WHERE email = ?;";
			System.out.println(email);
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, email);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			String storedPassword = (result.getString("password")); 
			boolean flag = true;
			if(storedPassword == null) flag = false;
			if(flag){
				flag = Utils.validatePassword(password, storedPassword);
			}
			if(flag){
				String uid = result.getString(1);
				request.getSession(true).setAttribute("uid", uid);
				obj.put("status",true);				
				obj.put("data", uid);
			}
			else{						
				obj.put("status",false);
				obj.put("message", "Authentication Failed");					
			}
			preparedStmt.close();
			conn.close();
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static boolean createAccount(HttpServletRequest request, String firstName, String surName, String email, String password, long time, char gender){
		boolean flag = true;
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String update1 = "INSERT INTO fbuser (firstname, surname, email, birthday, gender) VALUES (?, ?, ?, ?, ?);";
			String query = "SELECT uid FROM fbuser WHERE email = ?;";
			String update2 = "INSERT INTO password VALUES (?, ?, ?);";
			
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			PreparedStatement preparedStmt1 = conn.prepareStatement(update1);
			PreparedStatement preparedStmt2 = conn.prepareStatement(update2);
			
			preparedStmt1.setString(1, firstName);
			preparedStmt1.setString(2, surName);
			preparedStmt1.setString(3, email);
			preparedStmt1.setDate(4, new java.sql.Date(time));
			preparedStmt1.setString(5, String.valueOf(gender));
			preparedStmt1.executeUpdate();
			
			preparedStmt.setString(1, email);
			ResultSet rset = preparedStmt.executeQuery();
			rset.next();
			int uid = rset.getInt(1);

			String genPwd = Utils.generateStrongPasswordHash(password);
			preparedStmt2.setInt(1, uid);
			preparedStmt2.setString(2, email);
			preparedStmt2.setString(3, genPwd);
			preparedStmt2.executeUpdate();
			if(flag){
				request.getSession(true).setAttribute("uid", uid);
			}
		} 
		catch(Exception e){
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

}
