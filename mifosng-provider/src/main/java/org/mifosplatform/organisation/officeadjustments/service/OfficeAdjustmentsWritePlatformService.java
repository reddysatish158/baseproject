package org.mifosplatform.organisation.officeadjustments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface OfficeAdjustmentsWritePlatformService {
	
	CommandProcessingResult createOfficeAdjustment(JsonCommand command);
}
