package org.mifosplatform.portfolio.servicemapping.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
public class ServiceMappingCommandFromApiJsonDeserializer {
	
	final private Set<String> supportedParameters = new HashSet<String>(Arrays.asList("serviceId","serviceIdentification","status","image","category","subCategory"));
	private final FromJsonHelper fromApiJsonHelper;  
	
	@Autowired
	public ServiceMappingCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper){
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	 public void validateForCreate(final String json) {
	        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("ServiceMapping");

	        final JsonElement element = fromApiJsonHelper.parse(json);

	        final Long serviceId = fromApiJsonHelper.extractLongNamed("serviceId", element);
	        final String serviceIdentification = fromApiJsonHelper.extractStringNamed("serviceIdentification", element);
	        final String status = fromApiJsonHelper.extractStringNamed("status", element);
	        final String image = fromApiJsonHelper.extractStringNamed("image",element);
	        final String category=fromApiJsonHelper.extractStringNamed("category", element);
	        final String subCategory=fromApiJsonHelper.extractStringNamed("subCategory", element);
	        
	        baseDataValidator.reset().parameter("serviceId").value(serviceId).notBlank();
			baseDataValidator.reset().parameter("serviceIdentification").value(serviceIdentification).notBlank();
			baseDataValidator.reset().parameter("status").value(status).notBlank();
			baseDataValidator.reset().parameter("image").value(image).notBlank();
			/*baseDataValidator.reset().parameter("category").value(category).notBlank();
			baseDataValidator.reset().parameter("subCategory").value(subCategory).notBlank();*/
		

	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	    }
	    
	    
	    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) { 
	        	
	        	throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist","Validation errors exist.",dataValidationErrors); }
	    }
	
	
}
