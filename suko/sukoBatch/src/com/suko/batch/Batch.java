package com.suko.batch;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suko.service.dowser.DowserBatch;

public class Batch extends HttpServlet  {

	private List<DowserBatch> batchList = new ArrayList<DowserBatch>();
	
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config){
		try {
			initBatch(config);
			super.init(config);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initBatch(ServletConfig config) {
		Enumeration<String> parameters = config.getInitParameterNames();
		while(parameters.hasMoreElements()){
			String batchName = parameters.nextElement();
			String batchDisplayName = config.getInitParameter(batchName);
			String className = config.getInitParameter(batchName+"ClassName");
			String language = config.getInitParameter(batchName+"Language");
			String cronExpression = config.getInitParameter(batchName+"CronExpression");
			if(className!=null){
				try {
					this.batchList.add(createBatch(batchDisplayName,className,language,cronExpression));
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}	
	}

	private DowserBatch createBatch(String batchName, String className, String language, String cronExpression) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> cls = Class.forName(className);
		Constructor<?> constructor = cls.getConstructor(new Class[] { String.class, String.class, String.class });
		DowserBatch db = (DowserBatch) constructor.newInstance(new Object[] { batchName, language, cronExpression });
		return db ;
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response )	throws ServletException, IOException {	
		String batchName = (String) request.getParameter("restart");
		if(batchName!=null){
			restart(batchName);
		}
		request.setAttribute("batchList", this.batchList);
		this.getServletContext().getRequestDispatcher( "/WEB-INF/batch.jsp" ).forward( request, response );
	}
	
	public boolean restart(String batchName){
		int indexBatch = 0;
		String language = null;
		String cronExpression = null;
		String className = null;
		for(DowserBatch dBatch : this.batchList){
			if(dBatch.getThreadNamePrefix().equals(batchName)){
				cronExpression = dBatch.getCronExpression();
				className = dBatch.getClass().getName();
				dBatch.setWaitForTasksToCompleteOnShutdown(false);
				dBatch.destroy();
				try {
					this.batchList.set(indexBatch,createBatch(batchName, className, language, cronExpression));
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			indexBatch++;
		}
		return false;
	}

}
