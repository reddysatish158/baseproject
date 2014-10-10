package org.mifosplatform.infrastructure.jobs.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SchedulerJobApiConstants {

    public static final String JOB_RESOURCE_NAME = "schedulerjob";
    // response parameters
    public static final String jobIdentifierParamName = "jobId";
    public static final String displayNameParamName = "displayName";
    public static final String nextRunTimeParamName = "nextRunTime";
    public static final String initializingErrorParamName = "initializingError";
    public static final String jobActiveStatusParamName = "active";
    public static final String currentlyRunningParamName = "currentlyRunning";
    public static final String lastRunHistoryObjParamName = "lastRunHistory";

    public static final String versionParamName = "version";
    public static final String jobRunStartTimeParamName = "jobRunStartTime";
    public static final String jobRunEndTimeParamName = "jobRunEndTime";
    public static final String statusParamName = "status";
    public static final String jobRunErrorMessageParamName = "jobRunErrorMessage";
    public static final String triggerTypeParamName = "triggerType";
    public static final String jobRunErrorLogParamName = "jobRunErrorLog";
    public static final String cronExpressionParamName = "cronExpression";
    public static final String cronDescriptionParamName = "cronDescription";
    public static final String schedulerStatusParamName = "active";
    public static final String schedulerJobParamName = "jobName";
    public static final String schedulerJobLogFile="runFilePath";
    
    public static final String JOB_INVOICE = "Invoice";
    public static final String JOB_GENERATE_STATEMENT = "Generate Statment";
    public static final String JOB_MESSANGER = "Messanger";
    public static final String JOB_AUTO_EXIPIRY= "Auto Exipiry";
    public static final String jobReportName = "reportName";
    public static final String jobIsDynamic = "isDynamic";
    public static final String jobProcessdate = "processDate";
    public static final String jobDueDate = "dueDate";
    public static final String jobPromotionalMessage = "promotionalMessage";
    public static final String jobMessageTemplate= "messageTemplate";
    public static final String jobExipiryDate= "exipiryDate";
    public static final String jobHistoryId="historyId";
    public static final String jobisAutoRenewal="isAutoRenewal";
    public static final String JOB_REPORTEMAIL="Report Email";
    public static final String JOB_EmailId="emailId";
    public static final String JOB_MiddleWare="Middleware";
    public static final String JOB_ProvSystem="ProvSystem";
    public static final String JOB_URL="URL";
    public static final String JOB_Username="Username";
    public static final String JOB_Password="Password";
    
    
    
    public static final Set<String> JOB_DETAIL_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(jobIdentifierParamName,
            displayNameParamName, nextRunTimeParamName, initializingErrorParamName, cronExpressionParamName, jobActiveStatusParamName,
            currentlyRunningParamName, lastRunHistoryObjParamName,schedulerJobParamName,cronDescriptionParamName,jobIsDynamic,
            schedulerJobLogFile,jobHistoryId));

    public static final Set<String> JOB_HISTORY_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(versionParamName,
            jobRunStartTimeParamName, jobRunEndTimeParamName, statusParamName, jobRunErrorMessageParamName, triggerTypeParamName,
            jobRunErrorLogParamName));

    public static final Set<String> JOB_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(displayNameParamName,
            jobActiveStatusParamName, cronExpressionParamName,cronDescriptionParamName,jobIsDynamic,"isAutoRenewal"));
    
    public static final Set<String> JOB_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(displayNameParamName,
            jobActiveStatusParamName, cronExpressionParamName,schedulerJobParamName,cronDescriptionParamName));
    


    public static final Set<String> CREATE_REQUEST_JOB_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(jobProcessdate,jobDueDate,jobPromotionalMessage,
    		jobMessageTemplate,jobExipiryDate,"locale","dateFormat","isDynamic","jobName","reportName","isAutoRenewal",schedulerJobParamName,jobReportName,
    		JOB_REPORTEMAIL,JOB_EmailId,JOB_ProvSystem,JOB_URL,JOB_Username,JOB_Password,JOB_MiddleWare));



    public static final Set<String> SCHEDULER_DETAIL_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(schedulerStatusParamName));

    public static final String COMMAND_EXECUTE_JOB = "executeJob";
    public static final String COMMAND_STOP_SCHEDULER = "stop";
    public static final String COMMAND_START_SCHEDULER = "start";
    public static final String COMMAND = "command";
    public static final String JOB_ID = "jobId";
    public static final String JOB_RUN_HISTORY = "runhistory";
    public static final String JOB_PARAMETERS = "jopparameters";
    public static final String SCHEDULER_STATUS_PATH = "scheduler";
}
