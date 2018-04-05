package com.suko.service;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import com.suko.DAO.business.Parameter;
import com.suko.DAO.dataBase.ParameterGraphDB;

public class Admin {
	
	private static final Logger logger = LogManager.getLogger(Search.class);
	/**
	 * @param question
	 * @param language
	 * @return result object
	 */
	public static Parameter getParam(String category, String subCategory, String name, String description, String defaultValue){	
	    ParameterGraphDB parameterGraphDB = new ParameterGraphDB(); //init graph instance
	    Parameter parameter = new Parameter();
	    parameter.setCategory(category);
	    parameter.setSubCategory(subCategory);
	    parameter.setName(name);
	    parameter.setValue(defaultValue);
	    parameter.setDescription(description);
	    try{
	    	Parameter result=parameterGraphDB.matchParameterGraphDatabase(parameter);
	    	parameterGraphDB.close();
	    	return result;
	    }catch (NoSuchRecordException e){
	    	logger.debug(e.getMessage());
	    	parameterGraphDB.mergeParameterGraphDatabase(parameter);
	    	parameterGraphDB.close();
	    	return parameter;
		}
	}
	
	public static Map<String, Map<String, List<Parameter>>> getAllParam(){
		ParameterGraphDB parameterGraphDB = new ParameterGraphDB(); 
		Map<String, Map<String, List<Parameter>>> result= parameterGraphDB.matchAllParameterGraphDatabase();
		parameterGraphDB.close();
		return result;
	}
	
	public static void setParam(Parameter parameter){
		ParameterGraphDB parameterGraphDB = new ParameterGraphDB(); 
		parameterGraphDB.mergeParameterGraphDatabase(parameter);
		parameterGraphDB.close();
	}
}
