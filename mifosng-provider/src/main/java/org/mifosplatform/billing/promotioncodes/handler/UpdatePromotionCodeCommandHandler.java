package org.mifosplatform.billing.promotioncodes.handler;

import org.mifosplatform.billing.promotioncodes.service.PromotionCodeWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePromotionCodeCommandHandler implements NewCommandSourceHandler {

	private final PromotionCodeWritePlatformService writePlatformService;

    @Autowired
    public UpdatePromotionCodeCommandHandler(PromotionCodeWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.updatePromotionCode(command.entityId(), command);
    }
}