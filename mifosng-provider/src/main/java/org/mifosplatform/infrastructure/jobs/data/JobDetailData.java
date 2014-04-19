package org.mifosplatform.infrastructure.jobs.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.organisation.message.data.BillingMessageData;
import org.mifosplatform.portfolio.plan.data.SystemData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;

public class JobDetailData {

    @SuppressWarnings("unused")
    private final Long jobId;

    @SuppressWarnings("unused")
    private final String displayName;
    
    @SuppressWarnings("unused")
    private final String name;

    @SuppressWarnings("unused")
    private final Date nextRunTime;

    @SuppressWarnings("unused")
    private final String initializingError;
    
    @SuppressWarnings("unused")
    private final String cronExpression;
    
    @SuppressWarnings("unused")
    private final String cronDescription;

    @SuppressWarnings("unused")
    private final boolean active;

    @SuppressWarnings("unused")
    private final boolean currentlyRunning;

    @SuppressWarnings("unused")
    private final JobDetailHistoryData lastRunHistory;
    
    @SuppressWarnings("unused")
    private  List<ScheduleJobData> queryData;
    
    @SuppressWarnings("unused")
    private   Collection<BillingMessageData> billingMessageDatas;

	private JobParameterData jobparameters;

	private Long historyId;
	
	private List<SystemData> provisionSysData;

    public JobDetailData(final Long jobId, final String displayName, String name, final Date nextRunTime, final String initializingError,
            final String cronExpression,final boolean active, final boolean currentlyRunning, final JobDetailHistoryData lastRunHistory, 
            String cronDescription, Long historyId) {
        this.jobId = jobId;
        this.displayName = displayName;
        this.nextRunTime = nextRunTime;
        this.initializingError = initializingError;
        this.cronExpression = cronExpression;
        this.active = active;
        this.lastRunHistory = lastRunHistory;
        this.currentlyRunning = currentlyRunning;
        this.cronDescription=cronDescription;
        this.name=name;
        this.queryData=null;
        this.billingMessageDatas=null;
        this.historyId=historyId;
    }

	public Long getJobId() {
		return jobId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Date getNextRunTime() {
		return nextRunTime;
	}

	public String getInitializingError() {
		return initializingError;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public String getCronDescription() {
		return cronDescription;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCurrentlyRunning() {
		return currentlyRunning;
	}

	public JobDetailHistoryData getLastRunHistory() {
		return lastRunHistory;
	}

	public String getName() {
		return name;
	}

	public void setQueryData(List<ScheduleJobData> queryData) {

		this.queryData=queryData;
		
	}

	public void setMessageData(Collection<BillingMessageData> templateData) {
		
		this.billingMessageDatas=templateData;
		
	}

	public void setJobParameters(JobParameterData data) {
		this.jobparameters=data;
		
	}

	public List<SystemData> getProvisionSysData() {
		return provisionSysData;
	}

	public void setProvisionSysData(List<SystemData> provisionSysData) {
		this.provisionSysData = provisionSysData;
	}
    
	
    
}
