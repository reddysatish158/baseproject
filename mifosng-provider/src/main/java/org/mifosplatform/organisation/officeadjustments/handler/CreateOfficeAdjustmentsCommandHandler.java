package org.mifosplatform.organisation.officeadjustments.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.officeadjustments.service.OfficeAdjustmentsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOfficeAdjustmentsCommandHandler implements 	NewCommandSourceHandler {

	private final OfficeAdjustmentsWritePlatformService writePlatformService;
	  
	  @Autowired
	    public CreateOfficeAdjustmentsCommandHandler(OfficeAdjustmentsWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.createOfficeAdjustment(command);
	}

}
