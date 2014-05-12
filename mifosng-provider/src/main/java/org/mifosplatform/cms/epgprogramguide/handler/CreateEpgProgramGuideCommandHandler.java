package org.mifosplatform.cms.epgprogramguide.handler;

import org.mifosplatform.cms.epgprogramguide.service.EpgProgramGuideWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CreateEpgProgramGuideCommandHandler implements NewCommandSourceHandler {

	private EpgProgramGuideWritePlatformService epgProgramGuideWritePlatformService;
	
	@Autowired
	public CreateEpgProgramGuideCommandHandler(final EpgProgramGuideWritePlatformService epgProgramGuideWritePlatformService) {
		this.epgProgramGuideWritePlatformService = epgProgramGuideWritePlatformService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.epgProgramGuideWritePlatformService.createEpgProgramGuide(command,command.entityId());
	}

}

