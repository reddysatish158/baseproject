package org.mifosplatform.cms.epgprogramguide.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface EpgProgramGuideWritePlatformService {

	
	public CommandProcessingResult createEpgProgramGuide(JsonCommand command, Long id);
}
