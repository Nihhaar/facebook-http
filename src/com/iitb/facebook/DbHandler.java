package com.iitb.facebook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.iitb.facebook.Utils;

import org.json.JSONArray;
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
	
	public static JSONArray getPosts(int uid, int offset, int limit){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement postSt = conn.prepareStatement("SELECT firstname, postid, timestamp, fbuser.uid, text, hasImage FROM post, fbuser WHERE post.uid IN (SELECT uid2 FROM follows WHERE uid1 = ?) AND post.uid = fbuser.uid ORDER BY timestamp ASC OFFSET ? LIMIT ?");
		)
		{	
			postSt.setInt(1, uid);
			postSt.setInt(2, offset);
			postSt.setInt(3, limit);
			ResultSet rs = postSt.executeQuery();
			json = Utils.ResultSetConverter(rs);
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static String getImagePath(int postid){
		String imgpath = null;
		try (
			    Connection conn = DriverManager.getConnection(connString, userName, passWord);
				PreparedStatement postSt = conn.prepareStatement("SELECT imgpath FROM image WHERE postid = ?");
			)
			{	
				postSt.setInt(1, postid);
				ResultSet rs = postSt.executeQuery();
				rs.next();
				imgpath = rs.getString("imgpath");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return imgpath;
	}
	
	public static JSONArray getComments(int postid, int offset, int limit){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement postSt = conn.prepareStatement("SELECT commentid, firstname, timestamp, text, replies FROM fbuser, comments WHERE fbuser.uid = comments.uid AND comments.postid = ? ORDER BY timestamp ASC OFFSET ? LIMIT ?");
		)
		{	
			postSt.setInt(1, postid);
			postSt.setInt(2, offset);
			postSt.setInt(3, limit);
			ResultSet rs = postSt.executeQuery();
			json = Utils.ResultSetConverter(rs);
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONArray getSubComments(int commentid){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement postSt = conn.prepareStatement("SELECT firstname, text FROM subcomments,fbuser WHERE subcomments.commentid = ? AND subcomments.uid = fbuser.uid ORDER BY timestamp ASC");
		)
		{	
			postSt.setInt(1, commentid);
			ResultSet rs = postSt.executeQuery();
			json = Utils.ResultSetConverter(rs);
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static JSONObject addComment(int uid, int postid, String text){
		JSONObject obj = new JSONObject();
		try (
		    Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pSt = conn.prepareStatement("INSERT INTO comments(uid, postid, timestamp, text) VALUES (?, ?, CURRENT_TIMESTAMP, ?);");
		)
		{	
			pSt.setInt(1, uid);
			pSt.setInt(2, postid);
			pSt.setString(3, text);
			if(pSt.executeUpdate() > 0)
			{
				obj.put("status", true);
				obj.put("data","Added comment");				
			}
			else
			{
				obj.put("status",false);
				obj.put("message", "Unable to add comment");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONObject addSubComment(int uid, int commentid, String text){
		JSONObject obj = new JSONObject();
		try (
		    Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pSt1 = conn.prepareStatement("INSERT INTO subcomments(uid, commentid, timestamp, text) VALUES (?, ?, CURRENT_TIMESTAMP, ?);");
			PreparedStatement pSt2 = conn.prepareStatement("UPDATE comments SET replies = replies + 1 WHERE commentid = ?");
		)
		{	
			pSt1.setInt(1, uid);
			pSt1.setInt(2, commentid);
			pSt1.setString(3, text);
			pSt2.setInt(1, commentid);
			if(pSt1.executeUpdate() > 0)
			{
				if(pSt2.executeUpdate() > 0){
					obj.put("status", true);
					obj.put("data","Added subcomment");
				}
			}
			else
			{
				obj.put("status",false);
				obj.put("message", "Unable to add subcomment");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONArray getSuggestions(String query){
		JSONArray jsonarr = new JSONArray();
		query = "%" + query + "%";
		try (
			    Connection conn = DriverManager.getConnection(connString, userName, passWord);
				PreparedStatement pSt = conn.prepareStatement("SELECT firstname, uid FROM fbuser WHERE firstname ILIKE ? OR surname ILIKE ? OR email ILIKE ?;");
			)
			{	
				pSt.setString(1, query);
				pSt.setString(2, query);
				pSt.setString(3, query);
				ResultSet rs = pSt.executeQuery();
				while(rs.next()){
					JSONObject obj = new JSONObject();
					obj.put("value", rs.getString("firstname"));
					obj.put("data", rs.getInt("uid"));
					jsonarr.put(obj);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonarr;
	}
	
	public static String getUserName(int userid){
		String name = "User";
		try (
			    Connection conn = DriverManager.getConnection(connString, userName, passWord);
				PreparedStatement pSt = conn.prepareStatement("SELECT firstname FROM fbuser WHERE uid = ?");
			)
			{	
				pSt.setInt(1, userid);
				ResultSet rs = pSt.executeQuery();
				rs.next();
				name = rs.getString("firstname");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public static JSONObject deauth(HttpServletRequest request)
	{
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) {
			obj.put("status", false);
			obj.put("message", "Invalid Session");
			return obj;
		} 
		else 
		{
			request.getSession(false).invalidate();
			obj.put("status", true);
			obj.put("data", "sucessfully logged out");
			return obj;
		}
	}

}
