package org.mifosplatform.logistics.agent.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class AgentItemSaleCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("agentId","itemId","purchaseDate","orderQuantity",
    		"chargeAmount","taxPercantage","dateFormat","locale","chargeCode","unitPrice","purchaseFrom","purchaseBy"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public AgentItemSaleCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("itemsale");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String purchaseFrom = fromApiJsonHelper.extractStringNamed("purchaseFrom", element);
        baseDataValidator.reset().parameter("purchaseFrom").value(purchaseFrom).notNull();
        
        final String purchaseBy = fromApiJsonHelper.extractStringNamed("purchaseBy", element);
        baseDataValidator.reset().parameter("purchaseBy").value(purchaseBy).notNull();
        final String chargeCode = fromApiJsonHelper.extractStringNamed("chargeCode", element);
        baseDataValidator.reset().parameter("chargeCode").value(chargeCode).notNull();

        final BigDecimal unitPrice = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("unitPrice", element);
        baseDataValidator.reset().parameter("unitPrice").value(unitPrice).notNull();

        final String itemId = fromApiJsonHelper.extractStringNamed("itemId", element);
        baseDataValidator.reset().parameter("itemId").value(itemId).notNull();
        final LocalDate purchaseDate = fromApiJsonHelper.extractLocalDateNamed("purchaseDate", element);
        baseDataValidator.reset().parameter("purchaseDate").value(purchaseDate).notBlank();
        final Long orderQuantity = fromApiJsonHelper.extractLongNamed("orderQuantity", element);
        baseDataValidator.reset().parameter("orderQuantity").value(orderQuantity).notNull().integerGreaterThanZero();
        final BigDecimal chargeAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("chargeAmount", element);
        baseDataValidator.reset().parameter("chargeAmount").value(chargeAmount).notNull();
        final BigDecimal taxAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("taxAmount", element);
        baseDataValidator.reset().parameter("taxAmount").value(chargeAmount).notNull();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}