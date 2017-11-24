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
 * Servlet implementation class AddComment
 */
@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddComment() {
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
			int commentid = Integer.parseInt(request.getParameter("parentcommentid"));
			String text = request.getParameter("commentcontent");
			
			if(commentid == -1){
				int postid = Integer.parseInt(request.getParameter("postid"));
				System.out.println("Adding comment: " + text);
				out.print(DbHandler.addComment(uid, postid, text));
			}
			else{
				System.out.println("Adding comment: " + text);
				out.print(DbHandler.addSubComment(uid, commentid, text));
			}
		}
	}

}
