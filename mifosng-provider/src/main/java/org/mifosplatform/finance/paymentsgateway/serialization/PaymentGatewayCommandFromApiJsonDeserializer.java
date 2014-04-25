package org.mifosplatform.finance.paymentsgateway.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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
public class PaymentGatewayCommandFromApiJsonDeserializer {

	
	/**
	 * The parameters supported for this command.
	 */
	 
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("amount","timestamp", "msisdn","name", 
				"service", "receipt", "reference","transaction","dateFormat","locale","remarks","status","CUSTOMERREFERENCEID",
				"TXNID","COMPANYNAME","STATUS","AMOUNT","MSISDN","TYPE","OBSPAYMENTTYPE"));
	
    private final FromJsonHelper fromApiJsonHelper;
    
    @Autowired
    public PaymentGatewayCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
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
				dataValidationErrors).resource("paymentgateway");

		final JsonElement element = fromApiJsonHelper.parse(json);
	
		final String OBSPAYMENTTYPE = fromApiJsonHelper.extractStringNamed("OBSPAYMENTTYPE", element);
		
		if(OBSPAYMENTTYPE.equalsIgnoreCase("MPesa")){
			
			final String reference = fromApiJsonHelper.extractStringNamed("reference", element);
			baseDataValidator.reset().parameter("reference").value(reference).notBlank().notExceedingLengthOf(30);
			final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
			baseDataValidator.reset().parameter("amount").value(amount).notBlank();
			final String receipt = fromApiJsonHelper.extractStringNamed("receipt", element);
			baseDataValidator.reset().parameter("receipt").value(receipt).notBlank();
			
		}else if (OBSPAYMENTTYPE.equalsIgnoreCase("TigoPesa")) {
			
			final String keyId = fromApiJsonHelper.extractStringNamed("CUSTOMERREFERENCEID", element);
			baseDataValidator.reset().parameter("CUSTOMERREFERENCEID").value(keyId).notBlank().notExceedingLengthOf(30);
			final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("AMOUNT", element);
			baseDataValidator.reset().parameter("AMOUNT").value(amount).notBlank();
			final String transactionId = fromApiJsonHelper.extractStringNamed("TXNID", element);
			baseDataValidator.reset().parameter("TXNID").value(transactionId).notBlank();
			
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

	public void validateForUpdate(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("paymentgateway");

		final JsonElement element = fromApiJsonHelper.parse(json);
	
		final String status = fromApiJsonHelper.extractStringNamed("status", element);
		baseDataValidator.reset().parameter("status").value(status).notBlank();
		
		final String remarks = fromApiJsonHelper.extractStringNamed("remarks", element);
		baseDataValidator.reset().parameter("remarks").value(remarks).notBlank().notExceedingLengthOf(500);
		
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

}
