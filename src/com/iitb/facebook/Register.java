package com.iitb.facebook;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
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
		JSONObject obj = new JSONObject();
		String firstName = request.getParameter("fname");
		String surName = request.getParameter("sname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		int dd = Integer.parseInt(request.getParameter("dd"));
		int mm = getMonth(request.getParameter("mm").toUpperCase());
		if(mm == 0)
			return;
		int yyyy = Integer.parseInt(request.getParameter("yyyy"));

		Calendar cal = Calendar.getInstance();
		cal.set(yyyy, mm, dd);
		long time = cal.getTimeInMillis();
		
		char gender = request.getParameter("gender").charAt(0);
		if(gender != 'M' && gender != 'F'){
			return;
		}
		
		boolean ret = DbHandler.createAccount(request, firstName, surName, email, password, time, gender);
		if(ret){
			obj.put("status",true);
		}
		else{
			obj.put("status",false);
		}
		
		response.getWriter().print(obj);
	}
	
	private int getMonth(String MMM){
		if(MMM.equals("JAN"))
			return 1;
		else if(MMM.equals("FEB"))
			return 2;
		else if(MMM.equals("MAR"))
			return 3;
		else if(MMM.equals("APR"))
			return 4;
		else if(MMM.equals("MAY"))
			return 5;
		else if(MMM.equals("JUN"))
			return 6;
		else if(MMM.equals("JUL"))
			return 7;
		else if(MMM.equals("AUG"))
			return 8;
		else if(MMM.equals("SEP"))
			return 9;
		else if(MMM.equals("OCT"))
			return 10;
		else if(MMM.equals("NOV"))
			return 11;
		else if(MMM.equals("DEC"))
			return 12;
		
		return 0;
	}

}
