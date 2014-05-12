package org.mifosplatform.crm.userchat.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.userchat.service.UserChatWriteplatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserChatMessageCommandHandler implements NewCommandSourceHandler {

	 private final UserChatWriteplatformService writePlatformService;

	    @Autowired
	    public UpdateUserChatMessageCommandHandler(final UserChatWriteplatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }

		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
	       return this.writePlatformService.updateUserChatMessage(command,command.entityId());
		}
}
