package org.mifosplatform.portfolio.servicemapping.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.servicemapping.service.ServiceMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateServiceMappingCommandHandler implements NewCommandSourceHandler{
	
	private final ServiceMappingWritePlatformService serviceMappingWritePlatformService;
	@Autowired
	public CreateServiceMappingCommandHandler(final ServiceMappingWritePlatformService serviceMappingWritePlatformService) {
		this.serviceMappingWritePlatformService = serviceMappingWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return serviceMappingWritePlatformService.createServiceMapping(command);
	}

}
