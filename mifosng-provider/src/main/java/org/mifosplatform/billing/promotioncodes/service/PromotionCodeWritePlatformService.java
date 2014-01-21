package org.mifosplatform.billing.promotioncodes.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PromotionCodeWritePlatformService {

	CommandProcessingResult createPromotionCode(JsonCommand command);

/*	CommandProcessingResult updateEventActionMapping(Long id, JsonCommand command);

	CommandProcessingResult deleteEventActionMapping(Long id);*/

}
