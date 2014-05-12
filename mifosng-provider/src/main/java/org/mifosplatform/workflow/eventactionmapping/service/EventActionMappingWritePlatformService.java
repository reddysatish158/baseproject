package org.mifosplatform.workflow.eventactionmapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface EventActionMappingWritePlatformService {

	CommandProcessingResult createEventActionMapping(JsonCommand command);

	CommandProcessingResult updateEventActionMapping(Long id, JsonCommand command);

	CommandProcessingResult deleteEventActionMapping(Long id);

}
