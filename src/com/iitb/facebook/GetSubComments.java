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
 * Servlet implementation class GetSubComments
 */
@WebServlet("/GetSubComments")
public class GetSubComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSubComments() {
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
			out.close();
			return;
		}
		else 
		{
			int commentid = Integer.parseInt(request.getParameter("commentid"));
			obj.put("status", true);
			System.out.println("GetSubComments comtid: " + commentid);
			obj.put("data",DbHandler.getSubComments(commentid));
			out.print(obj);
			out.close();
			return;
		}
	}

}
