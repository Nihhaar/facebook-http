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
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession(false) != null){
			/* Session already exists, redirect him to home */
			System.out.println("Session already exists! Redirecting to homepage");
			JSONObject obj = new JSONObject();
			obj.put("status",true);				
			response.getWriter().print(obj);
		}
		else{
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();	
			out.print(DbHandler.authenticate(email, password, request));
		}
	}

}
