package org.mifosplatform.cms.mediadevice.handler;

import org.mifosplatform.cms.mediadevice.service.MediaDeviceWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateMediaDeviceCrashDetailsCommandHandler implements NewCommandSourceHandler{

	private final MediaDeviceWritePlatformService mediaDeviceWritePlatformService;
	 @Autowired
	    public UpdateMediaDeviceCrashDetailsCommandHandler(final MediaDeviceWritePlatformService mediaDeviceWritePlatformService){
		 this.mediaDeviceWritePlatformService = mediaDeviceWritePlatformService;
	 }
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.mediaDeviceWritePlatformService.updateMediaDetailsCrashStatus(command.entityId(),command);
	}

}
