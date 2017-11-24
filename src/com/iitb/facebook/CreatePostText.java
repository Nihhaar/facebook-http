package com.iitb.facebook;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class CreatePostText
 */
@WebServlet("/CreatePostText")
public class CreatePostText extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreatePostText() {
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
	        post = request.getParameter("text");
	        int postid = DbHandler.createpost(uid, post, false);
	        if(postid>0){
	        	obj.put("status", true);
				obj.put("data","Created Post");
	        }
	        else{
	        	obj.put("status",false);
				obj.put("message", "Unable to create post");
	        }
			out.print(obj);
		}
	        
        /* Close the streams and print output */
		out.close();
	}

}
