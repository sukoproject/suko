package com.suko.DAO.dataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;

import com.suko.DAO.business.Parameter;

/**
 * @author Jeremy
 *
 */
public class ParameterGraphDB extends GraphDB {
	
	private static final Logger logger = LogManager.getLogger(ParameterGraphDB.class);
	
	public ParameterGraphDB(){
		super();
	}
	
	/**
	 * @param parameter
	 */
	public void mergeParameterGraphDatabase(Parameter parameter){

		//--REQUEST----------	
		//create reverse relationships and delete link on the same node
		run("MERGE(p:Parameter{"
				+"category:\""+parameter.getCategory()+"\""
				+",subCategory:\""+parameter.getSubCategory()+"\""
				+",name:\""+parameter.getName()+"\""
				+"})"
				+" ON CREATE SET p.value =\"" +parameter.getValue()+"\",p.description =\""+parameter.getDescription()+"\""
				+" ON MATCH SET  p.value =\"" +parameter.getValue()+"\""
			);
		
	}
	
	/**
	 * @param parameter
	 * @return
	 * @throws NoSuchRecordException
	 */
	public Parameter matchParameterGraphDatabase(Parameter parameter) throws NoSuchRecordException{

		//--REQUEST----------	
		StatementResult result = run("MATCH(p:Parameter) where "
				+"p.category=\""+parameter.getCategory()+"\""
				+" and p.subCategory=\""+parameter.getSubCategory()+"\""
				+" and p.name=\""+parameter.getName()+"\""
				+" return p.category,p.subCategory,p.name,p.value,p.description");
		return getParameterfromResultGraphDataBase(result.single());
	}
	
	/**
	 * @return
	 * @throws NoSuchRecordException
	 */
	public Map<String, Map<String, List<Parameter>>> matchAllParameterGraphDatabase() throws NoSuchRecordException{
		logger.debug("Add Parameter");
		//--REQUEST----------	
		StatementResult result = run("MATCH(p:Parameter) "
				+" return p.category,p.subCategory,p.name,p.value,p.description");
		Map<String, Map<String, List<Parameter>>> mapParameters =  new HashMap<String, Map<String, List<Parameter>>>();
		Parameter parameter;
		while(result.hasNext()){
			parameter = getParameterfromResultGraphDataBase(result.next());//getParameter
			if(mapParameters.containsKey(parameter.getCategory())){	//category exist?
				Map<String, List<Parameter>> subMapParameters = mapParameters.get(parameter.getCategory());
				if(subMapParameters.containsKey(parameter.getSubCategory())){//subCategory Exist?		
					List<Parameter> listParameters = subMapParameters.get(parameter.getSubCategory());
					listParameters.add(parameter);
					subMapParameters.replace(parameter.getSubCategory(), listParameters);
				}else{//create sub category
					List<Parameter> listParameters =  new ArrayList<Parameter>();
					listParameters.add(parameter);
					subMapParameters.put(parameter.getSubCategory(), listParameters);
					mapParameters.put(parameter.getCategory(), subMapParameters);
				}
				mapParameters.replace(parameter.getCategory(), subMapParameters);
				
			}else{//create category
				Map<String, List<Parameter>> subMapParameters =  new HashMap<String,List<Parameter>>();
				List<Parameter> listParameters = new ArrayList<Parameter>();
				listParameters.add(parameter);
				subMapParameters.put(parameter.getSubCategory(), listParameters);
				mapParameters.put(parameter.getCategory(), subMapParameters);
			}
			
			
		}
		return mapParameters;
	}
	
	/**
	 * @param line
	 * @return
	 */
	private Parameter getParameterfromResultGraphDataBase(Record line){
		Parameter p = new Parameter();
		p.setCategory(line.get(0).toString().replaceAll("\"", ""));
		p.setSubCategory(line.get(1).toString().replaceAll("\"", ""));
		p.setName(line.get(2).toString().replaceAll("\"", ""));
		p.setValue(line.get(3).toString().replaceAll("\"", ""));
		p.setDescription(line.get(4).toString().replaceAll("\"", ""));
		return p;
		
	}
}
