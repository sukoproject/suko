package com.sukoWeb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suko.DAO.business.Parameter;
import com.suko.service.Admin;

public class Config extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response )	throws ServletException, IOException {	
		Map<String, Map<String, List<Parameter>>> parameters = Admin.getAllParam();
		String categorySelected = (String) request.getParameter("category");
		String subCategorySelected = (String) request.getParameter("subCategory");
		request.getSession().setAttribute("categorySelected",categorySelected);
		request.getSession().setAttribute("subCategorySelected",subCategorySelected);
		if(categorySelected!=null && !categorySelected.isEmpty() && parameters.containsKey(categorySelected)){
			Map<String, List<Parameter>> mapSubCategory = parameters.get(categorySelected);
			if(subCategorySelected!=null && !subCategorySelected.isEmpty() && mapSubCategory.containsKey(subCategorySelected)){
				List<Parameter> paramaterOfCategory = mapSubCategory.get(subCategorySelected);
				request.setAttribute( "parameters", paramaterOfCategory );
			}
		}
		List<List<String>> listCategory =getListCategory(parameters,categorySelected,subCategorySelected);
		request.setAttribute( "listCategory", listCategory );
		this.getServletContext().getRequestDispatcher( "/WEB-INF/config.jsp" ).forward( request, response );
	}
	
	private List<List<String>> getListCategory(Map<String, Map<String, List<Parameter>>> parameters,String categorySelected, String subCategorySelected) {
		List<List<String>> listCategory = new ArrayList<List<String>>();
		//Category
		for(Entry<String, Map<String, List<Parameter>>> category : parameters.entrySet()){
			//Sub Category
			List<String> listSubCategory = new ArrayList<String>();	
			listSubCategory.add(category.getKey());
			Map<String, List<Parameter>> allSubCategory = category.getValue();
			for(Entry<String, List<Parameter>> subCategory : allSubCategory.entrySet()){
				listSubCategory.add(subCategory.getKey());
			}
			listCategory.add(listSubCategory);
		}
		return listCategory;
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		Map<String, Map<String, List<Parameter>>> parameters = Admin.getAllParam();
		String categorySelected = (String) request.getSession().getAttribute("categorySelected");
		String subCategorySelected = (String) request.getSession().getAttribute("subCategorySelected");
		//Category
		if(categorySelected!=null && !categorySelected.isEmpty() && parameters.containsKey(categorySelected)){
			Map<String, List<Parameter>> allSubCategory = parameters.get(categorySelected);
			//SubCategory
			if(subCategorySelected!=null && !subCategorySelected.isEmpty() && allSubCategory.containsKey(subCategorySelected)){
				List<Parameter> paramaterOfCategory = allSubCategory.get(subCategorySelected);
				for(Parameter p:paramaterOfCategory){
					String value = (String) request.getParameter(p.getName());
					if(value != null && !value.isEmpty()){
						p.setValue(value);
						Admin.setParam(p);
					}
				}
			}		
		}
		List<List<String>> listCategory =getListCategory(parameters,categorySelected,subCategorySelected);
		request.setAttribute( "listCategory", listCategory );
		this.getServletContext().getRequestDispatcher( "/WEB-INF/config.jsp" ).forward( request, response );	
    }
}
