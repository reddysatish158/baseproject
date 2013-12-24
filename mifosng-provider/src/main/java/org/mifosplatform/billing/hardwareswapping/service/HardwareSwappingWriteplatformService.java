package org.mifosplatform.billing.hardwareswapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwareSwappingWriteplatformService {

	CommandProcessingResult dohardWareSwapping(Long entityId,JsonCommand command);

}
