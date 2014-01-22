/*package org.mifosplatform.billing.promotioncodes.serialization;

public class PromotionCodeCommandFromApiJsonDeserializer {

}
*/
package org.mifosplatform.billing.promotioncodes.serialization;


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
public final class PromotionCodeCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("promotionCode","description","durationType",
    		"duration","discountType","discountRate","startDate","locale","dateFormat"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public PromotionCodeCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("promotioncodes");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String promotioncode = fromApiJsonHelper.extractStringNamed("promotionCode", element);
        baseDataValidator.reset().parameter("promotioncode").value(promotioncode).notBlank();
     
        final String description = fromApiJsonHelper.extractStringNamed("description", element);
        baseDataValidator.reset().parameter("description").value(description).notBlank();
        
        final String durationtype=fromApiJsonHelper.extractStringNamed("durationType", element);
        baseDataValidator.reset().parameter("durationtype").value(durationtype).notBlank();
        
        final Long duration=fromApiJsonHelper.extractLongNamed("duration", element);
        baseDataValidator.reset().parameter("duration").value(duration).notBlank();
        
        final String discounttype=fromApiJsonHelper.extractStringNamed("discountType", element);
        baseDataValidator.reset().parameter("discounttype").value(discounttype).notBlank();
        
        final BigDecimal discountRate=fromApiJsonHelper.extractBigDecimalWithLocaleNamed("discountRate", element);
        baseDataValidator.reset().parameter("discountRate").value(discountRate).notBlank();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }
    
/*    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("eventactionmapping");

        final JsonElement element = fromApiJsonHelper.parse(json);
        
        final Long id=fromApiJsonHelper.extractLongNamed("id", element);
        baseDataValidator.reset().parameter("id").value(id).notBlank();
        
        final String event = fromApiJsonHelper.extractStringNamed("event", element);
        baseDataValidator.reset().parameter("event").value(event).notBlank();
     
        final String action = fromApiJsonHelper.extractStringNamed("action", element);
        baseDataValidator.reset().parameter("action").value(action).notBlank();
        
        final String process=fromApiJsonHelper.extractStringNamed("process", element);
        baseDataValidator.reset().parameter("process").value(process).notBlank();
        

        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }
*/

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}