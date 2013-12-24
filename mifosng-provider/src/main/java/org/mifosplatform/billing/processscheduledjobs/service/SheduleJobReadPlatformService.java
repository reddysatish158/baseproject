package org.mifosplatform.billing.processscheduledjobs.service;

import java.util.List;

import org.mifosplatform.billing.scheduledjobs.data.JobParameterData;
import org.mifosplatform.billing.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.infrastructure.jobs.data.JobQueryData;

public interface SheduleJobReadPlatformService {

	//List<ScheduleJobData> retrieveSheduleJobDetails();

	List<Long> getClientIds(String query);

	Long getMessageId(String processParam);



	List<ScheduleJobData> retrieveSheduleJobParameterDetails(String paramValue);

	JobParameterData getJobParameters(String jobName);

	List<ScheduleJobData> getJobQeryData();

	String retrieveMessageData(Long id);
	
	List<ScheduleJobData> retrieveSheduleJobDetails(String paramValue);
	

}
