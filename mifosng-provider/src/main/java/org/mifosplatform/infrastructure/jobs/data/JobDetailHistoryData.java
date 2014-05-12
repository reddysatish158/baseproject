package org.mifosplatform.infrastructure.jobs.data;

import java.util.Date;

import org.joda.time.LocalDate;

public class JobDetailHistoryData {

    @SuppressWarnings("unused")
    private final Long version;

    @SuppressWarnings("unused")
    private final Date jobRunStartTime;

    @SuppressWarnings("unused")
    private final Date jobRunEndTime;

    @SuppressWarnings("unused")
    private final String status;

    @SuppressWarnings("unused")
    private final String jobRunErrorMessage;

    @SuppressWarnings("unused")
    private final String triggerType;

    @SuppressWarnings("unused")
    private final String jobRunErrorLog;
    
    @SuppressWarnings("unused")
    private final String runFilePath;

	private Long historyId;

    public JobDetailHistoryData(final Long version, final Date jobRunStartTime, final Date jobRunEndTime, final String status,
            final String jobRunErrorMessage, final String triggerType, final String jobRunErrorLog,final String runFilePath, Long historyId) {
        this.version = version;
        this.jobRunStartTime = jobRunStartTime;
        this.jobRunEndTime = jobRunEndTime;
        this.status = status;
        this.jobRunErrorMessage = jobRunErrorMessage;
        this.triggerType = triggerType;
        this.jobRunErrorLog = jobRunErrorLog;
        this.runFilePath=runFilePath;
        this.historyId=historyId;
    }
}