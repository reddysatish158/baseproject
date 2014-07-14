package org.mifosplatform.organisation.groupsDetails.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface GroupsDetailsWritePlatformService {

	CommandProcessingResult addGroup(JsonCommand command);
	
	CommandProcessingResult addProvision(JsonCommand command);
	
	CommandProcessingResult generateStatment(JsonCommand command, Long entityId);
}
