package org.mifosplatform.scheduledjobs.uploadstatus.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.scheduledjobs.uploadstatus.service.UploadStatusWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddUploadStatusCommandHandler implements NewCommandSourceHandler {
	
	private final UploadStatusWritePlatformService uploadStatusWritePlatformService;
	
	
	@Autowired
	public AddUploadStatusCommandHandler(final UploadStatusWritePlatformService uploadStatusWritePlatformService) {
		this.uploadStatusWritePlatformService = uploadStatusWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		//return this.inventoryItemDetailsWritePlatformService.addItem(command);
		return null;
	}

}
