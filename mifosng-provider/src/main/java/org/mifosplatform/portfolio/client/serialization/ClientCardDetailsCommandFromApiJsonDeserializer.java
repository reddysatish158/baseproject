/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.serialization;

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
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.command.ClientCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;


/**
 * Implementation of {@link FromApiJsonDeserializer} for {@link ClientCommand} 
 * 's.
 */
@Component
public class ClientCardDetailsCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final String CREDIT_CARD="CreditCard";
	private final String ACH_CARD="ACH";
	
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("clientId","name","cardNumber","routingNumber",
    		"bankAccountNumber","bankName","accountType","cardExpiryDate","id","cardType","type","cvvNumber"));
  
    private final FromJsonHelper fromApiJsonHelper;
    
    @Autowired
    public ClientCardDetailsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }
    
	public void validateForCreate(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("CardDetails");

		final JsonElement element = fromApiJsonHelper.parse(json);
	
		final String type = fromApiJsonHelper.extractStringNamed("type", element);
		baseDataValidator.reset().parameter("type").value(type)
		.notBlank().notExceedingLengthOf(50);
		
		final String Name = fromApiJsonHelper.extractStringNamed("name", element);
		baseDataValidator.reset().parameter("name").value(Name)
		.notBlank().notExceedingLengthOf(500);
		
		if(type.equalsIgnoreCase(CREDIT_CARD)){
			
			final String cardNumber = fromApiJsonHelper.extractStringNamed("cardNumber", element);
			baseDataValidator.reset().parameter("cardNumber").value(cardNumber)
			.notBlank().notExceedingLengthOf(500);
			
			final String cardType = fromApiJsonHelper.extractStringNamed("cardType", element);
			baseDataValidator.reset().parameter("cardType").value(cardType)
			.notBlank().notExceedingLengthOf(100);
			
			final String cardExpiryDate = fromApiJsonHelper.extractStringNamed("cardExpiryDate", element);
			baseDataValidator.reset().parameter("cardExpiryDate").value(cardExpiryDate)
			.notBlank().notExceedingLengthOf(300);
			
		/*	final String cvvNumber = fromApiJsonHelper.extractStringNamed("cvvNumber", element);
			baseDataValidator.reset().parameter("cvvNumber").value(cvvNumber)
			.notBlank().notExceedingLengthOf(300);*/
			
		}else if(type.equalsIgnoreCase(ACH_CARD)){
			
			final String routingNumber = fromApiJsonHelper.extractStringNamed("routingNumber", element);
			baseDataValidator.reset().parameter("routingNumber").value(routingNumber)
			.notBlank().notExceedingLengthOf(500);
			
			final String bankAccountNumber = fromApiJsonHelper.extractStringNamed("bankAccountNumber", element);
			baseDataValidator.reset().parameter("bankAccountNumber").value(bankAccountNumber)
			.notBlank().notExceedingLengthOf(500);
			
			final String bankName = fromApiJsonHelper.extractStringNamed("bankName", element);
			baseDataValidator.reset().parameter("bankName").value(bankName)
			.notBlank().notExceedingLengthOf(200);
			
			final String accountType = fromApiJsonHelper.extractStringNamed("accountType", element);
			baseDataValidator.reset().parameter("accountType").value(accountType)
			.notBlank().notExceedingLengthOf(50);
			
		}
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
