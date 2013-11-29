package org.mifosplatform.infrastructure.jobs.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.jobs.api.SchedulerJobApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class JobDetailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public JobDetailDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        boolean atLeastOneParameterPassedForUpdate = false;
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SchedulerJobApiConstants.JOB_UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SchedulerJobApiConstants.JOB_RESOURCE_NAME);
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.displayNameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String displayName = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.displayNameParamName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.displayNameParamName).value(displayName).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.cronExpressionParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String cronExpression = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.cronExpressionParamName,
                    element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.cronExpressionParamName).value(cronExpression).notBlank()
                    .validateCronExpression();
        }
        
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.cronDescriptionParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String crondescription = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.cronDescriptionParamName,
                    element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.cronDescriptionParamName).value(crondescription).notBlank();
                    
        }
        
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobActiveStatusParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String status = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.jobActiveStatusParamName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobActiveStatusParamName).value(status).notBlank()
                    .validateForBooleanValue();
        }

        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

	public void validateForCreate(String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        boolean atLeastOneParameterPassedForUpdate = false;
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SchedulerJobApiConstants.JOB_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SchedulerJobApiConstants.JOB_RESOURCE_NAME);
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.displayNameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String displayName = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.displayNameParamName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.displayNameParamName).value(displayName).notBlank();
        }
        
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.schedulerJobParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String jobName = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.schedulerJobParamName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.schedulerJobParamName).value(jobName).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.cronExpressionParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String cronExpression = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.cronExpressionParamName,
                    element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.cronExpressionParamName).value(cronExpression).notBlank()
                    .validateCronExpression();
        }
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobActiveStatusParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String status = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.jobActiveStatusParamName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobActiveStatusParamName).value(status).notBlank()
                    .validateForBooleanValue();
        }

        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

	public void validateForUpdateJobParameters(String json) {
		
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        boolean atLeastOneParameterPassedForUpdate = false;
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SchedulerJobApiConstants.CREATE_REQUEST_JOB_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SchedulerJobApiConstants.JOB_RESOURCE_NAME);
        
        final String jobName = this.fromApiJsonHelper.extractStringNamed("jobName", element);
        if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobReportName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String jobReportName = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.jobReportName, element);
            baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobReportName).value(jobReportName).notBlank();
        }
        if(jobName.equalsIgnoreCase(SchedulerJobApiConstants.JOB_INVOICE)){
        	
        	if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobProcessdate, element)) {
                atLeastOneParameterPassedForUpdate = true;
                final LocalDate processDate = this.fromApiJsonHelper.extractLocalDateNamed(SchedulerJobApiConstants.jobProcessdate, element);
                baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobProcessdate).value(processDate).notBlank();
            }
        }else if(jobName.equalsIgnoreCase(SchedulerJobApiConstants.JOB_MESSANGER)){
        	
        	if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobMessageTemplate, element)) {
                atLeastOneParameterPassedForUpdate = true;
                final String messageTemplate = this.fromApiJsonHelper.extractStringNamed(SchedulerJobApiConstants.jobMessageTemplate, element);
                baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobProcessdate).value(messageTemplate).notBlank();
            }
        }else if(jobName.equalsIgnoreCase(SchedulerJobApiConstants.JOB_GENERATE_STATEMENT)){
        	
        	if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobDueDate, element)) {
                atLeastOneParameterPassedForUpdate = true;
                final LocalDate dueDate = this.fromApiJsonHelper.extractLocalDateNamed(SchedulerJobApiConstants.jobDueDate, element);
                baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobDueDate).value(dueDate).notBlank();
            }
        	
        }else if(jobName.equalsIgnoreCase(SchedulerJobApiConstants.JOB_AUTO_EXIPIRY)){
        	
        	if (this.fromApiJsonHelper.parameterExists(SchedulerJobApiConstants.jobExipiryDate, element)) {
                atLeastOneParameterPassedForUpdate = true;
                final LocalDate exipiryDate = this.fromApiJsonHelper.extractLocalDateNamed(SchedulerJobApiConstants.jobExipiryDate, element);
                baseDataValidator.reset().parameter(SchedulerJobApiConstants.jobExipiryDate).value(exipiryDate).notBlank();
            }
        	
        }
       
        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

}
