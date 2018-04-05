package com.suko.service.dowser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import com.suko.DAO.business.Document;
import com.suko.DAO.dataBase.DocumentGraphDB;
import com.suko.service.Admin;

public abstract class DowserBatch extends ThreadPoolTaskScheduler implements Runnable {

	protected static final long serialVersionUID = 1L;
	protected static final String BATCH = "Batch";
	protected static final String LAST_PROCESS_DATE = "_last_run_date";
	protected static final String DATE_FORMAT = "YYYY-MM-dd HH:mm:ss";
	protected Date startDate;
	protected Date endate;
	protected Date lastProcessDate;
	protected Date nextRunDate;
	protected Iterator<Document> iterator;
	protected int totalItem;
	protected int nbItemPocessed=0;
	protected String cronExpression;
	protected StringBuilder log=new StringBuilder();
	protected String language;
	private int maxResult;
	
	public DowserBatch(String name, String language, String cronExpression) {
		this.setThreadNamePrefix(name);
		this.setCronExpression(cronExpression);
		this.language=language;
		this.initialize();
		this.cronExpression = Admin.getParam(BATCH, name,"Cron_Expression", "Cron expression for "+name,cronExpression).getValue();
		this.maxResult=Integer.parseInt(Admin.getParam(BATCH, name,"Max_Result", "Max result for "+name,"50").getValue());
	    CronTrigger trigger = new CronTrigger(this.cronExpression);
	    SimpleTriggerContext triggerContext = new SimpleTriggerContext();
	    triggerContext.update(null, null, new Date());
	    this.nextRunDate= trigger.nextExecutionTime(triggerContext);
		this.schedule(this,trigger);
	}
	
	@Override
	public void run(){
		this.setEndate(null);
		this.nextRunDate=null;
		this.totalItem=0;
		this.nbItemPocessed=0;
		this.log=new StringBuilder();
		this.setStartDate(new Date()); //current date
		addLog("Batch " + this.getThreadNamePrefix() + " started");
		try{
			Date d = getLastRunDate();
			addLog("update date of the last document get : "+d);
			List<Document> result = initIterator(d,this.maxResult);
			if(result!=null ){
				this.totalItem= result.size();
				this.iterator = result.iterator();
				addLog("Iterator has "+this.totalItem+" items");
			    while(iterator.hasNext()){
			    	Document doc = iterator.next();
			    	addLog(itemProcessor(doc));
			    	if(d.equals(doc.getUpdate())){
			    		d.setTime(doc.getUpdate().getTime()+60000);
			    		saveProgression(d);
			    	}else{
			    		saveProgression(doc.getUpdate());
			    	}
			    }
			}else{
		    	addLog("no result");
		    }
		    CronTrigger trigger = new CronTrigger(this.cronExpression);
		    SimpleTriggerContext triggerContext = new SimpleTriggerContext();
		    triggerContext.update(null, null, new Date());
		    this.nextRunDate= trigger.nextExecutionTime(triggerContext);
		    
		}catch(Exception e){
			addLog(e.getMessage());
		}
		this.setEndate(new Date());
		addLog("Batch " + this.getThreadNamePrefix() + " ended");
	}

	protected Date getLastRunDate() {
		if(this.lastProcessDate!=null){
			return this.lastProcessDate;
		}else{
			DocumentGraphDB dgb = new DocumentGraphDB();
			Date d = dgb.getLastUpdateInSuko(this.getThreadNamePrefix());
			dgb.close();
			return d;
		}
	}
	
	protected void saveProgression(Date docLastUpdateDate){
		this.nbItemPocessed++;
		this.lastProcessDate=docLastUpdateDate;
	}
	
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public String getProgress(){
		return  this.nbItemPocessed+"/"+this.totalItem;
	}

	/**
	 * @return the cronExpression
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * @param cronExpression the cronExpression to set
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @return the endate
	 */
	public Date getEndate() {
		return endate;
	}

	/**
	 * @param endate the endate to set
	 */
	public void setEndate(Date endate) {
		this.endate = endate;
	}
	
	public Date getNextRunDate(){
		return this.nextRunDate;
	}
	
	public String getLog(){
		return this.log.toString();
	}
	
	public void addLog(String message){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.log.insert(0,message);
		this.log.insert(0,"] ");
		this.log.insert(0,sdf.format(new Date()));
		this.log.insert(0,"[");
		this.log.insert(0,"\n");
		logger.debug(message);
	}

	//TO OVERWRITE
	/**
	 * ItemProcessor : analyse a document
	 * @param next
	 */
	protected abstract String itemProcessor(Document next) throws Exception;
	/**
	 * iniIterator : iterator should be sort by the last update date.
	 * @throws Exception 
	 * 
	 */
	protected abstract List<Document> initIterator(Date lastRunDate, int maxResult) throws Exception;
	


}
