package org.mifosplatform.portfolio.servicemapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ServiceMappingWritePlatformService {
	
	public CommandProcessingResult createServiceMapping(JsonCommand command);

	public CommandProcessingResult updateServiceMapping(Long entityId,JsonCommand command);

}
