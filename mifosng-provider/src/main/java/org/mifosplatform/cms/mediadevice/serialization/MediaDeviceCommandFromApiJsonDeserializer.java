package org.mifosplatform.cms.mediadevice.serialization;

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

import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class MediaDeviceCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("deviceId", "clientId", "status"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public MediaDeviceCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForUpdateDeviceDetails(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("mediadevices");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final Long deviceId = fromApiJsonHelper.extractLongNamed(
				"deviceId", element);
		baseDataValidator.reset().parameter("deviceId").value(deviceId)
		.notBlank();
		
		final Long clientId = fromApiJsonHelper.extractLongNamed(
				"clientId", element);
		baseDataValidator.reset().parameter("clientId").value(clientId)
		.notBlank();
		
		final String status = fromApiJsonHelper.extractStringNamed(
				"status", element);
		baseDataValidator.reset().parameter("status").value(status)
		.notBlank();
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}
	
	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

	}
	
}
