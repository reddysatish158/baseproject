package org.mifosplatform.billing.userchat.handler;

import org.mifosplatform.billing.plan.service.PlanWritePlatformService;
import org.mifosplatform.billing.userchat.service.UserChatWriteplatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserChatMessageCommandHandler implements NewCommandSourceHandler {

    private final UserChatWriteplatformService writePlatformService;

    @Autowired
    public DeleteUserChatMessageCommandHandler(final UserChatWriteplatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.deleteUserChatMessage(command.entityId());
    }
}