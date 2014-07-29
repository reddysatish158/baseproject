package org.mifosplatform.workflow.eventvalidation.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface EventValidationWritePlatformService {

	CommandProcessingResult createEventValidation(JsonCommand command);

	CommandProcessingResult deleteEventValidation(Long entityId);


}
