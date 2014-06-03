package org.mifosplatform.organisation.groupsDetails.serialization;

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
public class GroupsDetailsCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("id", "groupName", "groupAddress", "groupDescription",
					"attribute1", "attribute2", "attribute3", "attribute4","attribute4","status"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public GroupsDetailsCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForCreate(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("groupsdetails");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String groupName = fromApiJsonHelper.extractStringNamed(
				"groupName", element);
		baseDataValidator.reset().parameter("groupName").value(groupName)
				.notBlank();

		final String groupAddress = fromApiJsonHelper.extractStringNamed(
				"groupAddress", element);
		baseDataValidator.reset().parameter("groupAddress")
				.value(groupAddress).notBlank();
		
		final String attribute1 = fromApiJsonHelper.extractStringNamed(
				"attribute1", element);
		baseDataValidator.reset().parameter("attribute1")
				.value(attribute1).notBlank();
		
		final String attribute2 = fromApiJsonHelper.extractStringNamed(
				"attribute2", element);
		baseDataValidator.reset().parameter("attribute2")
				.value(attribute2).notBlank();
		
		final String attribute3 = fromApiJsonHelper.extractStringNamed(
				"attribute3", element);
		baseDataValidator.reset().parameter("attribute3")
				.value(attribute3).notBlank();
		
		final String attribute4 = fromApiJsonHelper.extractStringNamed(
				"attribute4", element);
		baseDataValidator.reset().parameter("attribute4")
				.value(attribute4).notBlank();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	public void validateForCreateProvision(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("groupsdetails");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String groupName = fromApiJsonHelper.extractStringNamed(
				"groupName", element);
		baseDataValidator.reset().parameter("groupName").value(groupName)
				.notBlank();
		
		final String attribute1 = fromApiJsonHelper.extractStringNamed(
				"attribute1", element);
		baseDataValidator.reset().parameter("attribute1")
				.value(attribute1).notBlank();
		
		final String attribute2 = fromApiJsonHelper.extractStringNamed(
				"attribute2", element);
		baseDataValidator.reset().parameter("attribute2")
				.value(attribute2).notBlank();
		
		final String attribute3 = fromApiJsonHelper.extractStringNamed(
				"attribute3", element);
		baseDataValidator.reset().parameter("attribute3")
				.value(attribute3).notBlank();
		
		final String attribute4 = fromApiJsonHelper.extractStringNamed(
				"attribute4", element);
		baseDataValidator.reset().parameter("attribute4")
				.value(attribute4).notBlank();

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
