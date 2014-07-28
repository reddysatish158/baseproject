package org.mifosplatform.provisioning.processscheduledjobs.service;

import java.util.List;

import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;

public interface SheduleJobReadPlatformService {

	//List<ScheduleJobData> retrieveSheduleJobDetails();

	List<Long> getClientIds(String query);

	Long getMessageId(String processParam);



	List<ScheduleJobData> retrieveSheduleJobParameterDetails(String paramValue);

	JobParameterData getJobParameters(String jobName);

	List<ScheduleJobData> getJobQeryData();

	String retrieveMessageData(Long id);
	
	List<ScheduleJobData> retrieveSheduleJobDetails(String paramValue);

	List<Long> getBillIds(String query);
	

}
