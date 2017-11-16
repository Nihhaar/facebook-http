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
		try(Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM password WHERE email = ?;");)
		{
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
				int uid = result.getInt(1);
				request.getSession(true).setAttribute("uid", uid);
				obj.put("status",true);				
				obj.put("data", uid);
			}
			else{						
				obj.put("status",false);
				obj.put("message", "Authentication Failed");					
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static boolean createAccount(HttpServletRequest request, String firstName, String surName, String email, String password, long time, char gender){
		boolean flag = true;
		try(Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement preparedStmt = conn.prepareStatement("SELECT uid FROM fbuser WHERE email = ?;");
			PreparedStatement preparedStmt1 = conn.prepareStatement("INSERT INTO fbuser (firstname, surname, email, birthday, gender) VALUES (?, ?, ?, ?, ?);");
			PreparedStatement preparedStmt2 = conn.prepareStatement("INSERT INTO password VALUES (?, ?, ?);");)
		{
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
	
	public static int createpost(int uid, String postText, boolean hasImg)
	{
		int postid = 0;
		try(Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pStmt = conn.prepareStatement("INSERT INTO post(uid, timestamp, text, likes, hasImage) values(?, CURRENT_TIMESTAMP, ?, ?, ?) RETURNING postid;");)
		{   
			pStmt.setInt(1, uid);
			pStmt.setString(2, postText);
			pStmt.setInt(3, 0);
			pStmt.setBoolean(4, hasImg);
			ResultSet rset = pStmt.executeQuery();
			rset.next();
			postid = rset.getInt(1);
		}catch (Exception sqle)
		{
			sqle.printStackTrace();
		}
		return postid;
	}
	
	public static JSONObject insertImage(int postid, String imagePath){
		JSONObject obj = new JSONObject();
		try(Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pStmt = conn.prepareStatement("INSERT INTO image(postid, imgpath) VALUES(?, ?);");)
		{   
			pStmt.setInt(1, postid);
			pStmt.setString(2, imagePath);
			
			if(pStmt.executeUpdate() > 0)
			{
				obj.put("status", true);
				obj.put("data","Uploaded Image Path");				
			}
			else
			{
				obj.put("status",false);
				obj.put("message", "Unable to upload image path");
			}	
		}catch (Exception sqle)
		{
			sqle.printStackTrace();
		}
		return obj;
	}

}
