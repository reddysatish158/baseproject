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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.billing.message.data.BillingMessageData;
import org.mifosplatform.billing.message.service.BillingMesssageReadPlatformService;
import org.mifosplatform.billing.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.billing.scheduledjobs.data.JobParameterData;
import org.mifosplatform.billing.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatus;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusRepository;
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
import org.mifosplatform.infrastructure.jobs.service.JobRegisterService;
import org.mifosplatform.infrastructure.jobs.service.SchedulerJobRunnerReadService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/jobs")
@Component
public class SchedulerJobApiResource {

    private final SchedulerJobRunnerReadService schedulerJobRunnerReadService;
    private final JobRegisterService jobRegisterService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer;
    private final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer;
    private final ToApiJsonSerializer<ScheduleJobData> serializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
    private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;

    @Autowired
    public SchedulerJobApiResource(final SchedulerJobRunnerReadService schedulerJobRunnerReadService,
            final JobRegisterService jobRegisterService, final ToApiJsonSerializer<JobDetailData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,final ToApiJsonSerializer<ScheduleJobData> serializer,
            final ToApiJsonSerializer<JobDetailHistoryData> jobHistoryToApiJsonSerializer,final SheduleJobReadPlatformService sheduleJobReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final BillingMesssageReadPlatformService billingMesssageReadPlatformService) {
        this.schedulerJobRunnerReadService = schedulerJobRunnerReadService;
        this.jobRegisterService = jobRegisterService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.jobHistoryToApiJsonSerializer = jobHistoryToApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.serializer=serializer;
        this.sheduleJobReadPlatformService=sheduleJobReadPlatformService;
        this.billingMesssageReadPlatformService=billingMesssageReadPlatformService;
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
        List<JobDetailData> jobDetailDatas = this.schedulerJobRunnerReadService.findAllJobDeatils();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailDatas, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{" + SchedulerJobApiConstants.JOB_ID + "}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam(SchedulerJobApiConstants.JOB_ID) final Long jobId, @Context final UriInfo uriInfo) {
        JobDetailData jobDetailData = this.schedulerJobRunnerReadService.retrieveOne(jobId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, jobDetailData, SchedulerJobApiConstants.JOB_DETAIL_RESPONSE_DATA_PARAMETERS);
    }

    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateData(@Context final UriInfo uriInfo) {
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
		file = new File(fileUploadLocation);
		
		if(!file.isDirectory()){
			file.mkdirs();
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
        
    	JobDetailData jobDetailData = this.schedulerJobRunnerReadService.retrieveOne(jobId);
        JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(jobDetailData.getDisplayName());
        jobDetailData=handleTemplateData(jobDetailData);
        
           jobDetailData.setJobParameters(data);
        		
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
	@Path("printlog")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response logFile(@QueryParam("path") final String path) {
    	//ScheduledJobRunHistory scheduledJobRunHistory = this.scheduledJobRunHistoryRepository.findOne(id);
		//String printFilePath = scheduledJobRunHistory.getFilePath();
		File file = new File(path);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""+ path + "\"");
       		response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return response.build();
	}

}
