package org.mifosplatform.logistics.ownedhardware.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface OwnedHardwareWritePlatformService {

	
	public CommandProcessingResult createOwnedHardware(JsonCommand command, Long clientId);

	public CommandProcessingResult updateOwnedHardware(JsonCommand command,Long entityId);
	
	public CommandProcessingResult deleteOwnedHardware(Long entityId);
}
