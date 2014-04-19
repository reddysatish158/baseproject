package org.mifosplatform.infrastructure.jobs.api;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.jobs.data.JobDetailData;
import org.mifosplatform.infrastructure.jobs.data.JobDetailHistoryData;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistory;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistoryRepository;
import org.mifosplatform.infrastructure.jobs.exception.NoLogFileFoundException;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mifosplatform.infrastructure.jobs.service.SchedulerJobRunnerReadService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.data.BillingMessageData;
import org.mifosplatform.organisation.message.service.BillingMesssageReadPlatformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.portfolio.plan.data.SystemData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/jobs")
@Component
public class SchedulerJobApiResource {

	private final String resourceNameForPermission = "SCHEDULERJOB";
    private final SchedulerJobRunnerReadService schedulerJobRunnerReadService;
    private final JobRegisterService jobRegisterService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer;
    private final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer;
    private final ToApiJsonSerializer<ScheduleJobData> serializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
    private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;
    private final PlanReadPlatformService planReadPlatformService;
    private final PlatformSecurityContext context;

    @Autowired
    public SchedulerJobApiResource(final SchedulerJobRunnerReadService schedulerJobRunnerReadService,final JobRegisterService jobRegisterService,
    		final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
    		final ToApiJsonSerializer<ScheduleJobData> serializer,final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer,
    		final SheduleJobReadPlatformService sheduleJobReadPlatformService,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
    		final BillingMesssageReadPlatformService billingMesssageReadPlatformService,final PlanReadPlatformService planReadPlatformService,
    		final PlatformSecurityContext context) {
    	
        this.schedulerJobRunnerReadService = schedulerJobRunnerReadService;
        this.jobRegisterService = jobRegisterService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.jobHistoryToApiJsonSerializer = jobHistoryToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.serializer=serializer;
        this.sheduleJobReadPlatformService=sheduleJobReadPlatformService;
        this.billingMesssageReadPlatformService=billingMesssageReadPlatformService;
        this.planReadPlatformService=planReadPlatformService;
        this.context = context;
    }
    @Autowired
	private ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository;
    
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String addNewJob(final String apiRequestBodyAsJson) { 
    	
    	final CommandWrapper commandRequest=new CommandWrapperBuilder().addNewJob().withJson(apiRequestBodyAsJson).build();
	   final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	  return this.toApiJsonSerializer.serialize(result);
	  
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo) {
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
        List<JobDetailData> jobDetailDatas = this.schedulerJobRunnerReadService.findAllJobDeatils();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailDatas, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, @Context final UriInfo uriInfo) {
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
        JobDetailData jobDetailData = this.schedulerJobRunnerReadService.retrieveOne(jobId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailData, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateData(@Context final UriInfo uriInfo) {
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
         List<ScheduleJobData> jobDetailData = this.schedulerJobRunnerReadService.retrieveJobDetails();
        ScheduleJobData jobData=new ScheduleJobData(jobDetailData);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.serializer.serialize(settings, jobData, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }
    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}/" + SchedulerJobApiConstants.JOB_RUN_HISTORY)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveHistory(@Context final UriInfo uriInfo, @PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder) {
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
        final SearchParameters searchParameters = SearchParameters.forPagination(offset, limit, orderBy, sortOrder);
        Page<JobDetailHistoryData> jobhistoryDetailData = this.schedulerJobRunnerReadService.retrieveJobHistory(jobId, searchParameters);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.jobHistoryToApiJsonSerializer.serialize(settings, jobhistoryDetailData,
                SchedulerJobApiConstants.JOB_HISTORY_RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response executeJob(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId,
            @QueryParam(SchedulerJobApiConstants.COMMAND) final String commandParam) {
    	File file = null;
		String fileUploadLocation = FileUtils.generateLogFileDirectory();
		File filelocation = new File(fileUploadLocation);			
		if(!filelocation.isDirectory()){
			filelocation.mkdirs();
		}
		for (JobName status : JobName.values()) {
			String name=status.toString();
			file = new File(fileUploadLocation+ File.separator + name);			
			if(!file.isDirectory()){
				file.mkdirs();
			}
	     }
		
        Response response = Response.status(400).build();
        if (is(commandParam, SchedulerJobApiConstants.COMMAND_EXECUTE_JOB)) {
            this.jobRegisterService.executeJob(jobId);
            response = Response.status(202).build();
        } else {
            throw new UnrecognizedQueryParamException(SchedulerJobApiConstants.COMMAND, commandParam);
        }
        return response;
    }

    
    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}/" + SchedulerJobApiConstants.JOB_PARAMETERS)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveJobParameters(@Context final UriInfo uriInfo, @PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId) {
        
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
    	JobDetailData jobDetailData = this.schedulerJobRunnerReadService.retrieveOne(jobId);
        JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(jobDetailData.getDisplayName());
        List<SystemData> provisionSysData = this.planReadPlatformService.retrieveSystemData();
        jobDetailData=handleTemplateData(jobDetailData);
        
           jobDetailData.setJobParameters(data);
           jobDetailData.setProvisionSysData(provisionSysData);
        		
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailData, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }
    
    private JobDetailData handleTemplateData(JobDetailData jobDetailData) {
    	
    	
    	      List<ScheduleJobData> queryData=this.sheduleJobReadPlatformService.getJobQeryData();
    	      jobDetailData.setQueryData(queryData);
    	      if(jobDetailData.getName().equalsIgnoreCase(SchedulerJobApiConstants.JOB_MESSANGER)){
    	    	  
    	    	  final Collection<BillingMessageData> templateData = this.billingMesssageReadPlatformService.retrieveAllMessageTemplates();
    	    	  jobDetailData.setMessageData(templateData);
		       }
    	
		return jobDetailData;
	}


	@PUT
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}/"+SchedulerJobApiConstants.JOB_PARAMETERS)
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateJobParametersl(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateJobParametersDetail(jobId) //
                .withJson(jsonRequestBody) //
                .build(); //
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.toApiJsonSerializer.serialize(result);
    }

	
	@PUT
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateJobDetail(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateJobDetail(jobId) //
                .withJson(jsonRequestBody) //
                .build(); //
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        if (result.getChanges() != null
                && (result.getChanges().containsKey(SchedulerJobApiConstants.jobActiveStatusParamName) || result.getChanges().containsKey(
                        SchedulerJobApiConstants.cronExpressionParamName))) {
            jobRegisterService.rescheduleJob(jobId);
        }
        return this.toApiJsonSerializer.serialize(result);
    }
	
    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
    
    @DELETE
	@Path("{jobId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String deleteSingleJob(@PathParam("jobId") final Long jobId) {
	 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteJob(jobId).build();
     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
     return this.toApiJsonSerializer.serialize(result);

	} 
    
    @GET
	@Path("printlog/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response logFile(@PathParam("id") final Long id) {
    	ScheduledJobRunHistory scheduledJobRunHistory = this.scheduledJobRunHistoryRepository.findOne(id);
		String printFilePath = scheduledJobRunHistory.getFilePath();
		if(printFilePath!=null){
		File file = new File(printFilePath);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""+ printFilePath + "\"");
       		response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return response.build();
		
		}else{
			throw new NoLogFileFoundException();
		
		}
    }
}
