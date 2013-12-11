package org.mifosplatform.billing.clientprospect.serialization;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;


@Component
public class ClientProspectCommandFromApiJsonDeserializer {

	
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("id","sourceOfPublicityInt","preferredPlanInt","city","sourceOther","prospectType","firstName","middleName","lastName","homePhoneNumber","workPhoneNumber","mobileNumber","email","sourceOfPublicity","preferredCallingTime","note","address","streetArea","cityDistrict","state","country","locale","preferredPlan","status","statusRemark","callStatus","assignedTo","notes","isDeleted","zipCode"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	ClientProspectCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper){
		this.fromApiJsonHelper = fromApiJsonHelper;
		
	}
	
	
	public void validateForCreate(final String json) {
		  if(StringUtils.isBlank(json)){
			  throw new InvalidJsonException();
		  }
		  
		   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	       fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client.prospect");
	        final JsonElement element = fromApiJsonHelper.parse(json);
	        
	        final Integer prospectType = fromApiJsonHelper.extractIntegerWithLocaleNamed("prospectType", element);
		    baseDataValidator.reset().parameter("prospectType").value(prospectType).notBlank().integerGreaterThanZero();
		     
	        final String firstName = fromApiJsonHelper.extractStringNamed("firstName", element);
	        baseDataValidator.reset().parameter("firstName").value(firstName).notBlank().notExceedingLengthOf(50);
	        
	        final String lastName = fromApiJsonHelper.extractStringNamed("lastName", element);
	        baseDataValidator.reset().parameter("lastName").value(lastName).notBlank().notExceedingLengthOf(50);
	        
	        final String homePhoneNumber = fromApiJsonHelper.extractStringNamed("homePhoneNumber", element);
	        baseDataValidator.reset().parameter("homePhoneNumber").value(homePhoneNumber).notBlank().notExceedingLengthOf(20);
	        final String workPhoneNumber = fromApiJsonHelper.extractStringNamed("workPhoneNumber", element);
	        baseDataValidator.reset().parameter("workPhoneNumber").value(workPhoneNumber).notBlank().notExceedingLengthOf(20);
	        final String mobileNumber = fromApiJsonHelper.extractStringNamed("mobileNumber", element);
	        baseDataValidator.reset().parameter("mobileNumber").value(mobileNumber).notBlank().notExceedingLengthOf(20);
	        
	        final String email = fromApiJsonHelper.extractStringNamed("email", element);
	        baseDataValidator.reset().parameter("email").value(email).notBlank().notExceedingLengthOf(100);
	        
	        final String address = fromApiJsonHelper.extractStringNamed("address", element);
	        baseDataValidator.reset().parameter("address").value(address).notBlank().notExceedingLengthOf(100);
	        final String streetArea = fromApiJsonHelper.extractStringNamed("streetArea", element);
	        baseDataValidator.reset().parameter("streetArea").value(streetArea).notBlank().notExceedingLengthOf(100);
	        final String cityDistrict = fromApiJsonHelper.extractStringNamed("cityDistrict", element);
	        baseDataValidator.reset().parameter("cityDistrict").value(cityDistrict).notBlank().notExceedingLengthOf(100);
	        final String state = fromApiJsonHelper.extractStringNamed("state", element);
	        baseDataValidator.reset().parameter("state").value(state).notBlank().notExceedingLengthOf(100);
	        final String country = fromApiJsonHelper.extractStringNamed("country", element);
	        baseDataValidator.reset().parameter("country").value(country).notBlank().notExceedingLengthOf(100);
	        
	        final String zipCode = fromApiJsonHelper.extractStringNamed("zipCode", element);
	        baseDataValidator.reset().parameter("zipCode").value(zipCode).notBlank().notExceedingLengthOf(45);
	        
	        final String sourceOfPublicity = fromApiJsonHelper.extractStringNamed("sourceOfPublicity", element);
	        baseDataValidator.reset().parameter("sourceOfPublicity").value(sourceOfPublicity).notExceedingLengthOf(50);
	        final String preferredPlan = fromApiJsonHelper.extractStringNamed("preferredPlan", element);
	        baseDataValidator.reset().parameter("preferredPlan").value(preferredPlan).notExceedingLengthOf(50);
	        
	        final String note = fromApiJsonHelper.extractStringNamed("note", element);
	        baseDataValidator.reset().parameter("note").value(note).notBlank().notExceedingLengthOf(255);


	        
	        final String middleName = fromApiJsonHelper.extractStringNamed("middleName", element);
	        baseDataValidator.reset().parameter("middleName").value(middleName).notExceedingLengthOf(50);


			
			
	        
			throwExceptionIfValidationWarningsExist(dataValidationErrors);
	  }
	 public void validateForUpdate(final String json) {
		  if(StringUtils.isBlank(json)){
			  throw new InvalidJsonException();
		  }
		  
		   final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	       fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client.prospect");
	        final JsonElement element = fromApiJsonHelper.parse(json);
	        
	        if(fromApiJsonHelper.parameterExists("type", element)){
	        	baseDataValidator.reset().parameter("type").value(fromApiJsonHelper.extractBooleanNamed("type", element)).notBlank();
	        }
	        
	        
	        
	        
	        
	       final Long assignedTo = fromApiJsonHelper.extractLongNamed("assignedTo", element);
	        final Long callStatus = fromApiJsonHelper.extractLongNamed("callStatus", element);
	        final String notes = fromApiJsonHelper.extractStringNamed("notes", element);
	        final String preferredCallingTime = fromApiJsonHelper.extractStringNamed("preferredCallingTime", element);
	        
	        
	        
	        baseDataValidator.reset().parameter("callStatus").value(callStatus).notNull();
	        baseDataValidator.reset().parameter("assignedTo").value(assignedTo).notBlank();
	        baseDataValidator.reset().parameter("preferredCallingTime").value(preferredCallingTime).notBlank();
	        baseDataValidator.reset().parameter("notes").value(notes).notBlank();
			
		
			
			
	        
			throwExceptionIfValidationWarningsExist(dataValidationErrors);
	  }
	
	  private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	                "Validation errors exist.", dataValidationErrors); }
	  }	
}
