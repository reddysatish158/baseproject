package org.mifosplatform.billing.selfcare.handler;


import org.mifosplatform.billing.selfcare.service.SelfCareWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSelfcareUDPCommandHandler implements NewCommandSourceHandler{
	private final SelfCareWritePlatformService selfCareWritePlatformService;

	@Autowired
	public UpdateSelfcareUDPCommandHandler(final SelfCareWritePlatformService selfCareWritePlatformService) {
		this.selfCareWritePlatformService=selfCareWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return selfCareWritePlatformService.updateSelfCareUDPassword(command);
	}
	
}
