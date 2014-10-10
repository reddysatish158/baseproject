package org.mifosplatform.infrastructure.jobs.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.jobs.data.JobDetailDataValidator;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetail;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetailRepository;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistory;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistoryRepository;
import org.mifosplatform.infrastructure.jobs.domain.SchedulerDetail;
import org.mifosplatform.infrastructure.jobs.domain.SchedulerDetailRepository;
import org.mifosplatform.infrastructure.jobs.exception.JobNotFoundException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.scheduledjobs.scheduledjobs.domain.ScheduleJobs;
import org.mifosplatform.scheduledjobs.scheduledjobs.domain.ScheduledJobRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedularWritePlatformServiceJpaRepositoryImpl implements SchedularWritePlatformService {

    private final ScheduledJobDetailRepository scheduledJobDetailsRepository;

    private final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository;

    private final SchedulerDetailRepository schedulerDetailRepository;

    private final JobDetailDataValidator dataValidator;
    
    private final ScheduledJobRepository scheduledJobRepository;
    
    private final PlatformSecurityContext context;
    

    @Autowired
    public SchedularWritePlatformServiceJpaRepositoryImpl(final ScheduledJobDetailRepository scheduledJobDetailsRepository,
            final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository, final JobDetailDataValidator dataValidator,
            final SchedulerDetailRepository schedulerDetailRepository,final ScheduledJobRepository scheduledJobRepository,
            final PlatformSecurityContext context) {
        this.scheduledJobDetailsRepository = scheduledJobDetailsRepository;
        this.scheduledJobRunHistoryRepository = scheduledJobRunHistoryRepository;
        this.schedulerDetailRepository = schedulerDetailRepository;
        this.dataValidator = dataValidator;
        this.scheduledJobRepository=scheduledJobRepository;
        this.context=context;
    }

    @Override
    public List<ScheduledJobDetail> retrieveAllJobs() {
        return scheduledJobDetailsRepository.findAll();
    }

    @Override
    public ScheduledJobDetail findByJobKey(final String jobKey) {
        return scheduledJobDetailsRepository.findByJobKey(jobKey);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails, final ScheduledJobRunHistory scheduledJobRunHistory) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
        this.scheduledJobRunHistoryRepository.save(scheduledJobRunHistory);
    }

    @Override
    public Long fetchMaxVersionBy(final String jobKey) {
        Long version = 0L;
        Long versionFromDB = this.scheduledJobRunHistoryRepository.findMaxVersionByJobKey(jobKey);
        if (versionFromDB != null) {
            version = versionFromDB;
        }
        return version;
    }

    @Override
    public ScheduledJobDetail findByJobId(Long jobId) {
        return this.scheduledJobDetailsRepository.findByJobId(jobId);
    }

    @Override
    @Transactional
    public void updateSchedulerDetail(final SchedulerDetail schedulerDetail) {
        this.schedulerDetailRepository.save(schedulerDetail);
    }

    @Override
    public SchedulerDetail retriveSchedulerDetail() {
        SchedulerDetail schedulerDetail = null;
        List<SchedulerDetail> schedulerDetailList = schedulerDetailRepository.findAll();
        if (schedulerDetailList != null) {
            schedulerDetail = schedulerDetailList.get(0);
        }
        return schedulerDetail;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateJobDetail(final Long jobId, final JsonCommand command) {
    	
        dataValidator.validateForUpdate(command.json());
        
        final ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);
        if (scheduledJobDetail == null) { throw new JobNotFoundException(String.valueOf(jobId)); }
        final Map<String, Object> changes = scheduledJobDetail.update(command);
        if (!changes.isEmpty()) {
            this.scheduledJobDetailsRepository.saveAndFlush(scheduledJobDetail);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(jobId) //
                .with(changes) //
                .build();

    }

    @Transactional
    @Override
    public boolean processJobDetailForExecution(String jobKey, String triggerType) {
        boolean isStopExecution = false;
        final ScheduledJobDetail scheduledJobDetail = scheduledJobDetailsRepository.findByJobKeyWithLock(jobKey);
        if(scheduledJobDetail!=null){
        if (scheduledJobDetail.isCurrentlyRunning()
                || (triggerType == SchedulerServiceConstants.TRIGGER_TYPE_CRON && (scheduledJobDetail.getNextRunTime().after(new Date())))) {
            isStopExecution = true;
        }
        final SchedulerDetail schedulerDetail = retriveSchedulerDetail();
        if (triggerType == SchedulerServiceConstants.TRIGGER_TYPE_CRON && schedulerDetail.isSuspended()) {
            scheduledJobDetail.updateTriggerMisfired(true);
            isStopExecution = true;
        } else if (!isStopExecution) {
            scheduledJobDetail.updateCurrentlyRunningStatus(true);
        }
        scheduledJobDetailsRepository.save(scheduledJobDetail);
        return isStopExecution;
        
        }else{
        	return isStopExecution;
        }
    }

	@Override
	public CommandProcessingResult createNewJob(JsonCommand command) {
		
		try
		{

			dataValidator.validateForCreate(command.json());
			
			ScheduledJobDetail scheduledJobDetail=ScheduledJobDetail.fromJson(command);
			
			this.scheduledJobDetailsRepository.save(scheduledJobDetail);
			
			ScheduleJobs  scheduleJob=this.scheduledJobRepository.findByBatchName(scheduledJobDetail.getJobName());
			
			if(scheduleJob!=null){
				scheduleJob.update();
				this.scheduledJobRepository.save(scheduleJob);
			}
			
			
			
			return new CommandProcessingResult(scheduledJobDetail.getId());
			 
			
		}catch(DataIntegrityViolationException exception)
		{
			return  null;
		}
	}

	@Override
	public CommandProcessingResult deleteJob(Long jobId) {
		
		try
		{
			
		    final ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);
	        if (scheduledJobDetail == null) { throw new JobNotFoundException(String.valueOf(jobId)); }
	        
	        this.scheduledJobDetailsRepository.delete(scheduledJobDetail);
	        
	        ScheduleJobs scheduleJobs=this.scheduledJobRepository.findByBatchName(scheduledJobDetail.getJobName());
	        
	        if(scheduleJobs!=null){
	        	scheduleJobs.updateActiveState();
	        	this.scheduledJobRepository.save(scheduleJobs);
	        }
	        
	        
	        return new CommandProcessingResult(jobId);	
		}catch(DataIntegrityViolationException exception){
			return null;
		}
		
	
	}

	@Override
	public CommandProcessingResult updateJobParametersDetail(Long jobId,JsonCommand command) {

        dataValidator.validateForUpdateJobParameters(command.json());
        AppUser user= this.context.authenticatedUser();
    	Long id=user.getId();
        final ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);
        
        scheduledJobDetail.updateJobParamters(command,id);
      
        this.scheduledJobDetailsRepository.saveAndFlush(scheduledJobDetail);
      
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(jobId) //
                .with(null) //
                .build();

    
	}

}
