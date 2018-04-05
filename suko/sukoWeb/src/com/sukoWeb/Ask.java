package com.sukoWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suko.service.Search;

public class Ask extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response )	throws ServletException, IOException {	
		String question = (String) request.getParameter("question");
		String result = Search(question);
		request.setAttribute( "result", result );
		this.getServletContext().getRequestDispatcher( "/WEB-INF/ask.jsp" ).forward( request, response );
	}
	
	private String Search(String question){
		if(question!= null && !question.isEmpty()){
			return Search.SearchPerson(question,"EN",null).toString(); 
		}
		return "";
	}
}
