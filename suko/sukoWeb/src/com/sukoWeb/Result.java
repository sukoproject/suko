package com.sukoWeb;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.Strings;
import com.suko.DAO.business.Person;
import com.suko.service.Search;

public class Result extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response )	throws ServletException, IOException {	
		showResult(request,response);
		
	}
	
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{	
		showResult(request,response);	
    }

	private void showResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		com.suko.DAO.business.Result result=null;
		String question = (String) request.getParameter("question");
		String language = "EN";//TODO:Manage lang
		List<Person> listPerson = null;
		List<String> listKnowledges =null;
		
		if(Strings.isNullOrEmpty(question)){
			result =  (com.suko.DAO.business.Result) request.getSession().getAttribute("result");
			if(result!=null){
				question=result.getQuestion();
				language=result.getLanguage();
				listPerson = result.getPerfectMatchedPersons();
				listKnowledges= result.getSkills();
				String removeSkill = (String) request.getParameter("remove");
				if(removeSkill!=null){
					listKnowledges.remove(removeSkill);
				}
				String addedSkill = (String) request.getParameter("addSkill");
				if(addedSkill!=null){
					listKnowledges.add(addedSkill);
				}	
			}
		}
		result = Search.SearchPerson(question,language,listKnowledges);
		request.getSession().setAttribute("result", result);
		listPerson = result.getPerfectMatchedPersons();
		request.setAttribute( "listPerson", listPerson );
		listKnowledges= result.getSkills();
		request.getSession().setAttribute("question",question);
		request.setAttribute( "listknowledges", listKnowledges );
		this.getServletContext().getRequestDispatcher( "/WEB-INF/result.jsp" ).forward( request, response );
	}
}
