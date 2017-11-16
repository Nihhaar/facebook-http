package com.iitb.facebook;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

/**
 * Servlet implementation class CreatePost
 */
@WebServlet("/CreatePost")
@MultipartConfig(	fileSizeThreshold=1024*1024*10, 	// 10 MB 
					maxFileSize=1024*1024*50,      		// 50 MB
					maxRequestSize=1024*1024*100)   	// 100 MB
public class CreatePost extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreatePost() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");	
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) 
		{
			obj.put("status", false);
			obj.put("message", "Invalid session");
			out.print(obj);
			return;
		}
		else 
		{
			int uid = (int)request.getSession().getAttribute("uid");
			String post = "";
	        
	        Part postText = request.getPart("text");
	        Part imgPart = request.getPart("picture");
	        
	        if(postText != null){
	        	InputStream ps = postText.getInputStream();
	        	post = Utils.convertStreamToString(ps);
	        	ps.close();
        	}
	        
	        if(imgPart == null){
	        	obj.put("status", false);
				obj.put("data","Image has to be present");
				out.print(obj);
	        	return;
	        }
	        else{
	        	int postid = DbHandler.createpost(uid, post, true);
	        	if(postid > 0){
	        		obj.put("status", true);
					obj.put("data","Created Post");
					
					String imgsuffix = "/posts/" + String.valueOf(postid) + Utils.randomName() + Utils.imgMimeType;
					imgPart.write(Utils.imgData + imgsuffix);
		        	DbHandler.insertImage(postid, imgsuffix);
	        	}
	        	else{
	        		obj.put("status",false);
					obj.put("message", "Unable to create post");
	        	}
	        }
	        
	        /* Close the streams and print output */
			out.close();
			out.print(obj);
		}
	}

}
