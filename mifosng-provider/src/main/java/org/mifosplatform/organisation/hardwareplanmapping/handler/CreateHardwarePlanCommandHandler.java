
package org.mifosplatform.organisation.hardwareplanmapping.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateHardwarePlanCommandHandler implements NewCommandSourceHandler {

    private final HardwarePlanWritePlatformService writePlatformService;

    @Autowired
    public CreateHardwarePlanCommandHandler(final HardwarePlanWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.createHardwarePlan(command);
    }
}