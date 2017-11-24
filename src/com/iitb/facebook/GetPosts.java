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
 * Servlet implementation class GetPosts
 */
@WebServlet("/GetPosts")
public class GetPosts extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPosts() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();	
		response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) 
		{
			obj.put("status", false);
			obj.put("message", "Invalid session");
			out.print(obj);
			out.close();
			return;
		}
		else 
		{
			int offset = 0;
			int limit = 1000;
			if(request.getParameter("offset") != null)
				offset = Integer.parseInt(request.getParameter("offset"));
			
			if(request.getParameter("limit") != null)
				limit = Integer.parseInt(request.getParameter("limit"));
			
			int uid = (int)request.getSession().getAttribute("uid");
			obj.put("status", true);
			System.out.println("Offset: " + offset + " Limit: " + limit);
			obj.put("data",DbHandler.getPosts(uid, offset, limit));
			out.print(obj);
			out.close();
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
