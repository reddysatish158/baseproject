package org.mifosplatform.cms.mediadevice.handler;

import org.mifosplatform.cms.mediadevice.service.MediaDeviceWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMediaDeviceDetailsCommandHandler  implements NewCommandSourceHandler {

	private final MediaDeviceWritePlatformService mediaDeviceWritePlatformService;
	 @Autowired
	    public UpdateMediaDeviceDetailsCommandHandler(final MediaDeviceWritePlatformService mediaDeviceWritePlatformService){
		 this.mediaDeviceWritePlatformService = mediaDeviceWritePlatformService;
	 }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.mediaDeviceWritePlatformService.updateMediaDetailsStatus(command);
	}

}
