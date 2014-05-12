package org.mifosplatform.cms.eventorder.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface EventOrderWriteplatformService {

	CommandProcessingResult createEventOrder(JsonCommand command);

	CommandProcessingResult updateEventOrderPrice(JsonCommand command);

}
