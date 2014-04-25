package org.mifosplatform.portfolio.association.service;

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

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public class HardwareAssociationCommandFromApiJsonDeserializer {


	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("planId", "orderId", "serialNo","provisionNum","associationId","serialNo","provisionNum"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public HardwareAssociationCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	
	public void validateForCreate(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("association");

		final JsonElement element = fromApiJsonHelper.parse(json);
        
		/*		
		final Long planId = fromApiJsonHelper.extractLongNamed("planId", element);
        baseDataValidator.reset().parameter("planId").value(planId).notNull();
      
		*/
		
		final String serialNo = fromApiJsonHelper.extractStringNamed("provisionNum", element);
		 baseDataValidator.reset().parameter("serialNo").value(serialNo).notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.association.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
	
}
