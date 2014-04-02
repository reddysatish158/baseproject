package org.mifosplatform.billing.provisioning.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class ProvisioningCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> provisioningsupportedParameters = new HashSet<String>(Arrays.asList("id","provisioningSystem","commandName","status",
    		"commandParameters","commandParam","paramType","paramDefault","groupName","ipAddress","serviceName","vLan","planName","orderId","clientId",
    		"macId"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ProvisioningCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

	public void validateForProvisioning(String json){
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, provisioningsupportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");

        final JsonElement element = fromApiJsonHelper.parse(json);
        final String provisioningSystem = fromApiJsonHelper.extractStringNamed("provisioningSystem", element);
        baseDataValidator.reset().parameter("provisioningSystem").value(provisioningSystem).notBlank();
        
        final String commandName = fromApiJsonHelper.extractStringNamed("commandName", element);
        baseDataValidator.reset().parameter("commandName").value(commandName).notBlank();
        
        final JsonArray commandArray=fromApiJsonHelper.extractJsonArrayNamed("commandParameters",element);
 
		   if(commandArray!=null){
	         for (JsonElement jsonelement : commandArray) {
	
		         final String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
		         baseDataValidator.reset().parameter("commandParam").value(commandParam).notBlank();   
		         
		         final String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);
		         baseDataValidator.reset().parameter("paramType").value(paramType).notBlank();	     
		  }
		   }
        
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	
   private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	                "Validation errors exist.", dataValidationErrors); }
	        }
	 
	 
	  public void validateForAddProvisioning(String json) {

if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, provisioningsupportedParameters);

final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("Provisioning");

final JsonElement element = fromApiJsonHelper.parse(json);
final String serviceName = fromApiJsonHelper.extractStringNamed("serviceName", element);
baseDataValidator.reset().parameter("serviceName").value(serviceName).notBlank();

final String groupName = fromApiJsonHelper.extractStringNamed("groupName", element);
baseDataValidator.reset().parameter("groupName").value(groupName).notBlank();

final String ipAddress = fromApiJsonHelper.extractStringNamed("ipAddress", element);
baseDataValidator.reset().parameter("ipAddress").value(ipAddress).notBlank();   

final String vLan = fromApiJsonHelper.extractStringNamed("vLan", element);
baseDataValidator.reset().parameter("vLan").value(vLan).notBlank();	     



throwExceptionIfValidationWarningsExist(dataValidationErrors);
}


		
	
}