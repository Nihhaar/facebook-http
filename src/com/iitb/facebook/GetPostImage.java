package com.iitb.facebook;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

/**
 * Servlet implementation class GetPostImage
 */
@WebServlet("/GetPostImage")
public class GetPostImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPostImage() {
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
			int postid = Integer.parseInt(request.getParameter("postid"));
			String imgpath = DbHandler.getImagePath(postid);
			if(imgpath != null){
				BufferedImage image = null;
				String imageString = "";
				try 
				{
					System.out.println("/home/nihhaar/fbdata" + imgpath);
				    image = ImageIO.read(new File("/home/nihhaar/fbdata" + imgpath));
				    ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    ImageIO.write(image, "jpeg", baos);
				    baos.flush();
				    imageString = DatatypeConverter.printBase64Binary(baos.toByteArray());
				    baos.close();
				} 
				catch (IOException e) 
				{
				    e.printStackTrace();
				}
				
				obj.put("status", true);
				obj.put("data", imageString);
			}
			else{
				obj.put("status", false);
				obj.put("data", "Image not found");
			}
			
			out.print(obj);
			out.close();
			return;
		}
	}

}
