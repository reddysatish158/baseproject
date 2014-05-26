package org.mifosplatform.portfolio.client.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ClientCardDetailsWritePlatformService {

	CommandProcessingResult addClientCardDetails(Long clientId,JsonCommand command);

	CommandProcessingResult updateClientCardDetails(JsonCommand command);

	CommandProcessingResult deleteClientCardDetails(JsonCommand command);
	
	

}
